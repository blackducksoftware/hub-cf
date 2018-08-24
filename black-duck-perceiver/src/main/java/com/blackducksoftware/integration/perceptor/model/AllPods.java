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
package com.blackducksoftware.integration.perceptor.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class AllPods {
    @JsonProperty("Pods")
    List<Pod> pods = new ArrayList<>();

    public AllPods() {

    }

    public AllPods(List<Pod> pods) {
        this.pods = pods;
    }

    public final List<Pod> getPods() {
        return pods;
    }

    public final void setPods(List<Pod> pods) {
        this.pods = pods;
    }

    public final void addPod(Pod pod) {
        pods.add(pod);
    }
}
