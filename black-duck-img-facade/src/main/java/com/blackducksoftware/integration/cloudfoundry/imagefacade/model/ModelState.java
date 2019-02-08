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
package com.blackducksoftware.integration.cloudfoundry.imagefacade.model;

/**
 * @author fisherj
 *
 */
public enum ModelState {
    READY("ModelStateReady"),
    PULLING("ModelStatePulling");

    private final String desc;

    private ModelState(String desc) {
        this.desc = desc;
    }

    private final String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return name() + "(" + getDesc() + ")";
    }
}
