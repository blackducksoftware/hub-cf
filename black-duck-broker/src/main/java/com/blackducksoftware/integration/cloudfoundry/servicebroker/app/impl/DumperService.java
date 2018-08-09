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
package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.impl;

import static com.blackducksoftware.integration.cloudfoundry.v2.util.ApiV2Utils.createListServicePlansRequest;
import static com.blackducksoftware.integration.cloudfoundry.v2.util.ApiV2Utils.requestServiceBindings;
import static com.blackducksoftware.integration.cloudfoundry.v2.util.ApiV2Utils.requestServiceInstances;
import static com.blackducksoftware.integration.cloudfoundry.v2.util.ApiV2Utils.requestServicePlans;
import static com.blackducksoftware.integration.cloudfoundry.v3.util.ApiV3Utils.DropletResourceNotDummy;
import static com.blackducksoftware.integration.cloudfoundry.v3.util.ApiV3Utils.createGetApplicationEnvironmentRequest;
import static com.blackducksoftware.integration.cloudfoundry.v3.util.ApiV3Utils.createListApplicationStagedDropletsRequest;
import static com.blackducksoftware.integration.cloudfoundry.v3.util.ApiV3Utils.requestApplicationEnvironment;
import static com.blackducksoftware.integration.cloudfoundry.v3.util.ApiV3Utils.requestCurrentApplicationDropletRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsRequest;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.BindResource;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.CfResourceData;
import com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api.HubProjectParameters;
import com.blackducksoftware.integration.cloudfoundry.v2.model.Catalog;
import com.blackducksoftware.integration.perceptor.model.AllPods;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author fisherj
 *
 */
@Component
public class DumperService {
    private static final Logger logger = LoggerFactory.getLogger(DumperService.class);

    private final ReactorCloudFoundryClient reactorCloudFoundryClient;

    private final RestTemplate perceptorRestTemplate;

    private final ServiceInstanceService serviceInstanceService;

    private final BindingInstanceService bindingInstanceService;

    private final Catalog brokerCatalog;

    private final String perceptorBaseUrlString;

    private final int perceptorPort;

    @Autowired
    public DumperService(ReactorCloudFoundryClient reactorCloudFoundryClient,
            RestTemplate perceptorRestTemplate,
            ServiceInstanceService serviceInstanceService,
            BindingInstanceService bindingInstanceService,
            Catalog brokerCatalog,
            @Value("${perceptor.baseUrl}") String perceptorBaseUrlString,
            @Value("${perceptor.port}") int perceptorPort) {
        this.reactorCloudFoundryClient = reactorCloudFoundryClient;
        this.perceptorRestTemplate = perceptorRestTemplate;
        this.serviceInstanceService = serviceInstanceService;
        this.bindingInstanceService = bindingInstanceService;
        this.brokerCatalog = brokerCatalog;
        this.perceptorBaseUrlString = perceptorBaseUrlString;
        this.perceptorPort = perceptorPort;
    }

    @Scheduled(fixedRateString = "#{${application.dumper-service.polling-period-seconds} * 1000}")
    public void execute() throws Throwable {
        logger.debug("Starting data dump to perceptor");

        // Retrieve the Service Ids the broker knows about, if any
        Set<String> brokerServiceIds = serviceInstanceService.getServiceInstances();
        logger.debug("Retrieved the following service ids from the broker: {}", brokerServiceIds);

        // Retrieve the Binding Ids and App Ids the broker knows about, if any
        Map<String, UUID> brokerAppIdByBindingId = bindingInstanceService.toAppIdByBindingId().orElse(Collections.emptyMap());
        logger.debug("Retrieved the following binding id/app ids from broker: {}", brokerAppIdByBindingId);

        // Get the Service Instances from the CF Cloud Controller whose Plan Id matches ours
        String uniquePlanId = brokerCatalog.findFirstServiceByName("black-duck-scan")
                .orElseThrow(() -> {
                    throw new IllegalStateException("Broker Catalog does not contain entry for service: black-duck-scan");
                })
                .findFirstPlanByName("standard")
                .orElseThrow(() -> {
                    throw new IllegalStateException("Broker Catalog does not contain entry for plan: standard");
                })
                .getId().toString();
        ServicePlanResource servicePlanResource = requestServicePlans(reactorCloudFoundryClient, createListServicePlansRequest())
                .log()
                .switchIfEmpty(Mono.empty())
                .filter(spr -> uniquePlanId.equals(spr.getEntity().getUniqueId()))
                .single()
                .blockOptional()
                .orElseThrow(() -> {
                    throw new IllegalStateException("Service plan: standard, service: black-duck-scan not found");
                });

        ListServiceInstancesRequest listServiceInstancesRequest = ListServiceInstancesRequest.builder()
                .servicePlanId(servicePlanResource.getMetadata().getId())
                .build();
        Optional<List<ServiceInstanceResource>> serviceInstanceResources = requestServiceInstances(reactorCloudFoundryClient, listServiceInstancesRequest)
                .log()
                .switchIfEmpty(Mono.empty())
                .collectList()
                .blockOptional();

        serviceInstanceResources.ifPresent(siResources -> {
            if (!siResources.isEmpty()) {
                Set<String> cfServiceIds = siResources.stream().map(ServiceInstanceResource::getMetadata).map(Metadata::getId)
                        .collect(Collectors.toSet());

                // Assumes CF Cloud Controller has the full list and this just needs to backfill the broker with the
                // missing items
                reconstructBrokerServiceIdsFromCloudFoundry(brokerServiceIds, cfServiceIds);

                if (!cfServiceIds.isEmpty()) {
                    // Get the Service Bindings from the CF Cloud Controller whose Service Instance Id is in the list of
                    // Service Ids from above
                    ListServiceBindingsRequest listServiceBindingsRequest = ListServiceBindingsRequest.builder()
                            .serviceInstanceIds(cfServiceIds)
                            .build();
                    Optional<List<ServiceBindingResource>> serviceBindingResources = requestServiceBindings(reactorCloudFoundryClient,
                            listServiceBindingsRequest)
                                    .log()
                                    .onErrorResume(e -> Mono.empty())
                                    .collectList()
                                    .blockOptional();

                    // If there were service bindings present in the CF Clound Controller get the staged droplet
                    // resource
                    serviceBindingResources.ifPresent(sbResources -> {
                        Set<CfResourceData> cfResources = new HashSet<>();
                        if (!sbResources.isEmpty()) {
                            cfResources = Flux.fromStream(sbResources.stream())
                                    .flatMap(sbr -> {
                                        // Get the current droplet data for each application id
                                        ListApplicationDropletsRequest listAppDropletRequest = createListApplicationStagedDropletsRequest(
                                                sbr.getEntity().getApplicationId());
                                        return Flux.zip(Mono.just(sbr),
                                                requestCurrentApplicationDropletRequest(reactorCloudFoundryClient, listAppDropletRequest)
                                                        .log());
                                    })
                                    .collect(Collectors.mapping(sbrdroplet -> {
                                        CfResourceData cfrd = new CfResourceData();
                                        cfrd.setResourceId(sbrdroplet.getT1().getEntity().getServiceInstanceId());
                                        cfrd.setBindingId(sbrdroplet.getT1().getMetadata().getId());
                                        cfrd.setApplicationId(sbrdroplet.getT1().getEntity().getApplicationId());
                                        cfrd.setDropletData(sbrdroplet.getT2());
                                        return cfrd;
                                    }, Collectors.toSet())).block();

                            reconstructBrokerBindingInstancesFromCloudFoundry(brokerAppIdByBindingId, cfResources);

                            // Dump the data to the perceptor
                            sendToPerceptor(cfResources);
                        } else {
                            logger.debug("No service bindings found");
                        }
                    });
                } else {
                    logger.debug("No service ids found from service instance resources");
                }
            } else {
                logger.debug("No service instances found for service: black-duck-scan, plan: standard");
            }
        });
    }

    private void reconstructBrokerServiceIdsFromCloudFoundry(Set<String> brokerIds, Set<String> cfIds) {
        cfIds.stream().filter(id -> !brokerIds.contains(id)).forEach(id -> {
            if (serviceInstanceService.create(id)) {
                logger.debug("Backfill service instance id: {} to broker", id);
            } else {
                logger.debug("service instance id: {} not added to broker because it was already present", id);
            }
        });
        return;
    }

    private void reconstructBrokerBindingInstancesFromCloudFoundry(Map<String, UUID> brokerAppIdByBindingId, Set<CfResourceData> cfrd) {
        cfrd.stream().filter(cfr -> !brokerAppIdByBindingId.containsValue(UUID.fromString(cfr.getApplicationId()))).forEach(cfr -> {
            // Get the env variables from CF to feed back into the broker
            Optional<GetApplicationEnvironmentResponse> cfEnv = requestApplicationEnvironment(reactorCloudFoundryClient,
                    createGetApplicationEnvironmentRequest(cfr.getApplicationId()))
                            .log()
                            .blockOptional();
            Optional<BindResource> brokerBindResource = Optional.of(new BindResource(cfr.getApplicationId(), null));
            Optional<HubProjectParameters> brokerHubProjParms = Optional.of(HubProjectParameters
                    .fromCloudFoundryEnvironment(cfEnv.map(GetApplicationEnvironmentResponse::getEnvironmentVariables).orElse(Collections.emptyMap())));
            bindingInstanceService.create(cfr.getBindingId(), cfr.getResourceId(), brokerBindResource, brokerHubProjParms);
            logger.debug("Backfill binding id/app id to broker: {}/{} for service id: {}", cfr.getBindingId(), cfr.getApplicationId(), cfr.getResourceId());
        });
        return;
    }

    private void sendToPerceptor(Set<CfResourceData> cfResources) {
        AllPods allPods = cfResources.stream()
                .filter(cfrd -> DropletResourceNotDummy.test(cfrd.getDropletData()))
                .map(CfResourceData::toPod)
                .collect(Collectors.collectingAndThen(Collectors.toList(), AllPods::new));

        if (!allPods.getPods().isEmpty()) {
            // There is data to send
            logger.debug("Sending Pods data to perceptor: {}", allPods);
            URI perceptorUri;
            try {
                URI perceptorBaseUri = new URI(perceptorBaseUrlString);
                perceptorUri = new URI(perceptorBaseUri.getScheme(),
                        null,
                        perceptorBaseUri.getHost(),
                        perceptorPort,
                        "/allpods",
                        null, null);
            } catch (URISyntaxException e) {
                logger.error("URI to perceptor not created successfully", e);
                return;
            }
            logger.debug("Using URI: {}", perceptorUri);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AllPods> httpEntity = new HttpEntity<>(allPods, headers);
            ResponseEntity<String> dumpResponse = perceptorRestTemplate.exchange(perceptorUri, HttpMethod.PUT, httpEntity, String.class);
            logger.debug("Dump data to perceptor returned: {}", dumpResponse);
        } else {
            logger.debug("No Pod data to send to perceptor");
        }
    }
}
