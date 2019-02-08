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
package com.blackducksoftware.integration.cloudfoundry.v2.model;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author fisherj
 *
 */
public final class Catalog {
    @JsonProperty("services")
    private List<Service> services;

    public Catalog() {

    }

    public final List<Service> getServices() {
        return services;
    }

    public final void setServices(List<Service> services) {
        this.services = services;
    }

    public final Optional<Service> findFirstServiceByName(String serviceName) {
        if (serviceName == null) {
            throw new IllegalArgumentException("Must provide a service name");
        }

        if (services == null) {
            return Optional.empty();
        }

        return services.stream().filter(svc -> serviceName.equals(svc.getName())).findFirst();
    }
}
