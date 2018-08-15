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
package com.blackducksoftware.integration.cloudfoundry.v3.util;

import java.util.function.Predicate;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.DockerData;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.LifecycleType;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationDropletsRequest;
import org.cloudfoundry.client.v3.droplets.DropletResource;
import org.cloudfoundry.client.v3.droplets.DropletState;
import org.cloudfoundry.util.PaginationUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author fisherj
 *
 */
public final class ApiV3Utils {
    private static DropletResource DUMMY_DROPLET_RESOURCE = DropletResource.builder().executionMetadata("EMPTY")
            .lifecycle(Lifecycle.builder().data(DockerData.builder().build()).type(LifecycleType.DOCKER).build())
            .state(DropletState.EXPIRED).createdAt("EMPTY").id("EMPTY").build();

    private ApiV3Utils() {
        // Static class
    }

    public static ListApplicationDropletsRequest createListApplicationStagedDropletsRequest(String appId) {
        return ListApplicationDropletsRequest.builder()
                .applicationId(appId)
                .state(DropletState.STAGED)
                .build();
    }

    public static GetApplicationEnvironmentRequest createGetApplicationEnvironmentRequest(String applicationId) {
        return GetApplicationEnvironmentRequest.builder()
                .applicationId(applicationId)
                .build();
    }

    public static Flux<DropletResource> requestApplicationDroplets(CloudFoundryClient cloudFoundryClient,
            ListApplicationDropletsRequest listAppDropletsRequest) {
        return PaginationUtils.requestClientV3Resources(page -> cloudFoundryClient.applicationsV3().listDroplets(listAppDropletsRequest));
    }

    public static Mono<DropletResource> requestCurrentApplicationDropletRequest(CloudFoundryClient cloudFoundryClient,
            ListApplicationDropletsRequest listAppDropletsRequest) {
        ListApplicationDropletsRequest currentAppDropletRequest = ListApplicationDropletsRequest.builder().from(listAppDropletsRequest).current(true).build();
        return requestApplicationDroplets(cloudFoundryClient, currentAppDropletRequest).single().onErrorResume(e -> Mono.just(DUMMY_DROPLET_RESOURCE));
    }

    public static Mono<GetApplicationEnvironmentResponse> requestApplicationEnvironment(CloudFoundryClient cloudFoundryClient,
            GetApplicationEnvironmentRequest getAppEnvRequest) {
        return cloudFoundryClient.applicationsV3().getEnvironment(getAppEnvRequest);
    }

    public static Predicate<DropletResource> DropletResourceNotDummy = dr -> !DUMMY_DROPLET_RESOURCE.equals(dr);
}
