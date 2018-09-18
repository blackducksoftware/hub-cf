/*
 * Copyright (C) 2018 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.cloudfoundry.perceiver.impl;

import static com.blackducksoftware.integration.cloudfoundry.v2.util.ApiV2Utils.createListEventsRequest;
import static com.blackducksoftware.integration.cloudfoundry.v2.util.ApiV2Utils.requestEvents;
import static com.blackducksoftware.integration.cloudfoundry.v2.util.ApiV2Utils.requestSingleServiceBinding;
import static com.blackducksoftware.integration.cloudfoundry.v3.util.ApiV3Utils.DropletResourceNotDummy;
import static com.blackducksoftware.integration.cloudfoundry.v3.util.ApiV3Utils.createListApplicationStagedDropletsRequest;
import static com.blackducksoftware.integration.cloudfoundry.v3.util.ApiV3Utils.requestCurrentApplicationDropletRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.cloudfoundry.perceiver.ApplicationProperties;
import com.blackducksoftware.integration.cloudfoundry.perceiver.PerceptorProperties;
import com.blackducksoftware.integration.cloudfoundry.perceiver.api.CfResourceData;
import com.blackducksoftware.integration.cloudfoundry.perceiver.iface.IControllerService;
import com.blackducksoftware.integration.cloudfoundry.perceiver.iface.IEventMonitorService;
import com.blackducksoftware.integration.cloudfoundry.v2.model.EventType;
import com.blackducksoftware.integration.perceptor.model.Pod;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author fisherj
 *
 */
@Service
public class CloudControllerEventMonitorService implements IEventMonitorService, IControllerService {
    private static final Logger logger = LoggerFactory.getLogger(CloudControllerEventMonitorService.class);

    private final ApplicationProperties applicationProperties;

    private final ReactorCloudFoundryClient reactorCloudFoundryClient;

    private final RestTemplate perceptorRestTemplate;

    private final PerceptorProperties perceptorProperties;

    private boolean exit = false;

    private Set<UUID> appIds = new HashSet<UUID>();

    private Instant timeLastEventCheck;

    @Autowired
    public CloudControllerEventMonitorService(ApplicationProperties applicationProperties,
            ReactorCloudFoundryClient reactorCloudFoundryClient,
            RestTemplate perceptorRestTemplate,
            PerceptorProperties perceptorProperties) {
        this.applicationProperties = applicationProperties;
        this.reactorCloudFoundryClient = reactorCloudFoundryClient;
        this.perceptorRestTemplate = perceptorRestTemplate;
        this.perceptorProperties = perceptorProperties;

        timeLastEventCheck = Instant.now(); // Get the current time
    }

    @Async
    @Override
    public void run() {
        logger.info("Startig CloudControllerEventMonitorService thread");
        logger.info("Starting with timestamp: {}", timeLastEventCheck);
        Set<String> appIdsWaitingStaged = new HashSet<>();
        while (!exit) {

            if (!appIds.isEmpty()) {
                ListEventsRequest lev = createListEventsRequest(appIds.stream().map(String::valueOf).collect(Collectors.toList()),
                        EventType.AUDIT.APP.DROPLET.CREATE, timeLastEventCheck.toString());

                // Get the set of app ids who have a "droplet create" event
                Set<String> eventAppIds = requestEvents(reactorCloudFoundryClient, lev)
                        .log()
                        .doOnComplete(() -> {
                            timeLastEventCheck = Instant.now();
                            logger.debug("Updating timeLastEventCheck to {}", timeLastEventCheck);
                        })
                        .collect(Collectors.mapping(er -> {
                            return er.getEntity().getActee();
                        }, Collectors.toSet()))
                        .block();

                // Merge that set of app ids with the ones who are still waiting for droplet to be staged
                appIdsWaitingStaged.addAll(eventAppIds);

                Flux.fromIterable(new HashSet<>(appIdsWaitingStaged))
                        .switchMap(appId -> Flux.zip(Mono.just(appId),
                                requestCurrentApplicationDropletRequest(reactorCloudFoundryClient, createListApplicationStagedDropletsRequest(appId)),
                                requestSingleServiceBinding(reactorCloudFoundryClient, ListServiceBindingsRequest.builder().applicationId(appId).build())))
                        .filter(adsb -> DropletResourceNotDummy.test(adsb.getT2()))
                        .map(adsb -> {
                            return new CfResourceData(adsb.getT3().getEntity().getServiceInstanceId(), // service
                                                                                                       // instance id
                                    adsb.getT3().getMetadata().getId(), // service binding id
                                    adsb.getT1(), // application id
                                    adsb.getT2()); // droplet resource
                        })
                        .subscribe(cfrd -> {
                            logger.debug("Processing: {}", cfrd);
                            // Remove app id from "waiting" list
                            appIdsWaitingStaged.remove(cfrd.getApplicationId());
                            // TODO PhoneHome usage stats
                            // Send to perceptor
                            sendToPerceptor(cfrd);
                        });
            } else {
                logger.debug("Skipped query for events. No application id(s) registered");
            }

            // Sleep
            try {
                Thread.sleep(applicationProperties.getEventMonitorService().getPollingPeriodSeconds() * 1000L);
            } catch (InterruptedException e) {
                // TODO jfisher Auto-generated catch block
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void exitThread() {
        logger.info("Received external call to exit");
        exit = true;
    }

    @Override
    public boolean registerId(UUID appId) {
        logger.debug("Adding app: {} to receive events", appId);
        return appIds.add(appId);
    }

    @Override
    public boolean unregisterId(UUID appId) {
        logger.debug("Removing app: {} from receiveing events", appId);
        // TODO Add code to remove any pending scans for the appId
        return appIds.remove(appId);
    }

    private void sendToPerceptor(CfResourceData cfResourceData) {
        Pod pod = cfResourceData.toPod();
        logger.debug("Sending Pod data to perceptor: {}", pod);
        URI perceptorUri;
        try {
            URI perceptorBaseUri = new URI(perceptorProperties.getBaseUrl());
            perceptorUri = new URI(perceptorBaseUri.getScheme(),
                    null,
                    perceptorBaseUri.getHost(),
                    perceptorProperties.getPort(),
                    "/pod",
                    null, null);
        } catch (URISyntaxException e) {
            logger.error("URI to perceptor not created successfully", e);
            return;
        }
        logger.debug("Using URI: {}", perceptorUri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Pod> httpEntity = new HttpEntity<>(pod, headers);
        ResponseEntity<String> dumpResponse = perceptorRestTemplate.exchange(perceptorUri, HttpMethod.POST, httpEntity, String.class);
        logger.debug("Post data to perceptor returned: {}", dumpResponse);
    }
}
