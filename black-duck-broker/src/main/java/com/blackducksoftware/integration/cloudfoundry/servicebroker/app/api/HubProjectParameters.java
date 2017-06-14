/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author jfisher
 *
 */
public final class HubProjectParameters {

    private Optional<String> projectName = Optional.empty();

    private Optional<String> codeLocation = Optional.empty();

    public HubProjectParameters(@JsonProperty(value = "project_name", required = false) Optional<String> projectName,
            @JsonProperty(value = "code_location", required = false) Optional<String> codeLocation) {
        this.projectName = projectName;
        this.codeLocation = codeLocation;
    }

    /**
     * @return the projectName
     */
    @JsonProperty(value = "project_name")
    public final Optional<String> getProjectName() {
        return projectName;
    }

    /**
     * @param projectName
     *            the projectName to set
     */
    public final void setProjectName(Optional<String> projectName) {
        this.projectName = projectName;
    }

    /**
     * @return the codeLocation
     */
    @JsonProperty(value = "code_location")
    public Optional<String> getCodeLocation() {
        return codeLocation;
    }

    /**
     * @param codeLocation
     *            the codeLocation to set
     */
    public final void setCodeLocation(Optional<String> codeLocation) {
        this.codeLocation = codeLocation;
    }
}
