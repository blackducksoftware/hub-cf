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
package com.blackducksoftware.integration.cloudfoundry.v2.util;

import java.util.Collection;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.OrderDirection;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.util.PaginationUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author fisherj
 *
 */
public final class ApiV2Utils {
    private ApiV2Utils() {
        // Static class
    }

    public static ListEventsRequest createListEventsRequest(Collection<String> appIds, String eventType, String timestamp) {
        ListEventsRequest.Builder levBuilder = ListEventsRequest.builder()
                .types(eventType)
                .actees(appIds)
                .orderDirection(OrderDirection.DESCENDING);
        if (null != timestamp) {
            levBuilder.timestamp(timestamp);
        }

        return levBuilder.build();
    }

    public static ListServicePlansRequest createListServicePlansRequest(String... uniqueId) {
        ListServicePlansRequest.Builder lspRequestBuilder = ListServicePlansRequest.builder().serviceIds(uniqueId);
        return lspRequestBuilder.build();
    }

    public static Flux<EventResource> requestEvents(CloudFoundryClient cloudFoundryClient, ListEventsRequest listEventsRequest) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.events()
                .list(listEventsRequest));
    }

    public static Flux<ServiceInstanceResource> requestServiceInstances(CloudFoundryClient cloudFoundryClient,
            ListServiceInstancesRequest listSvcInstancesRequest) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.serviceInstances().list(listSvcInstancesRequest));
    }

    public static Flux<ServiceBindingResource> requestServiceBindings(CloudFoundryClient cloudFoundryClient,
            ListServiceBindingsRequest listServiceBindingsRequest) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.serviceBindingsV2().list(listServiceBindingsRequest));
    }

    public static Mono<ServiceBindingResource> requestSingleServiceBinding(CloudFoundryClient cloudFoundryClient,
            ListServiceBindingsRequest listServiceBindingsRequest) {
        return requestServiceBindings(cloudFoundryClient, listServiceBindingsRequest).single().onErrorResume(e -> Mono.empty());
    }

    public static Flux<ServicePlanResource> requestServicePlans(CloudFoundryClient cloudFoundryClient, ListServicePlansRequest listServicePlansRequest) {
        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.servicePlans().list(listServicePlansRequest));
    }

    public static Mono<ServicePlanResource> requestSingleServicePlan(CloudFoundryClient cloudFoundryClient, String uniqueId) {
        ListServicePlansRequest listServicePlansRequest = createListServicePlansRequest(uniqueId);
        return requestServicePlans(cloudFoundryClient, listServicePlansRequest).single().onErrorResume(e -> Mono.empty());
    }
}
