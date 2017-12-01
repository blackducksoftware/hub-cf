/*******************************************************************************
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
 *******************************************************************************/
package com.blackducksoftware.integration.cloudfoundry.servicebroker.app.api;

import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.phonehome.enums.PhoneHomeSource;
import com.blackducksoftware.integration.phonehome.enums.ThirdPartyName;

/**
 * @author jfisher
 *
 */
public final class PhoneHomeParameters {
    private final Logger logger = LoggerFactory.getLogger(PhoneHomeParameters.class);

    private final Optional<PhoneHomeSource> source;

    private final Optional<ThirdPartyName> vendor;

    public PhoneHomeParameters(@Nullable String source, @Nullable String vendor) {
        PhoneHomeSource phSrc = null;
        try {
            phSrc = PhoneHomeSource.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            logger.debug("Exception parsing PhoneHomeSource:", e);
        }

        ThirdPartyName tpName = null;
        try {
            tpName = ThirdPartyName.valueOf(vendor.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            logger.debug("Exception parsing ThirdPartyName:", e);
        }

        this.source = Optional.ofNullable(phSrc);
        this.vendor = Optional.ofNullable(tpName);

        logger.debug("Using PhoneHomeParameters: source: %s; vendor: %s", getSource().orElse(null), getVendor().orElse(null));
    }

    /**
     * @return the source
     */
    public final Optional<PhoneHomeSource> getSource() {
        return source;
    }

    /**
     * @return the vendor
     */
    public final Optional<ThirdPartyName> getVendor() {
        return vendor;
    }
}
