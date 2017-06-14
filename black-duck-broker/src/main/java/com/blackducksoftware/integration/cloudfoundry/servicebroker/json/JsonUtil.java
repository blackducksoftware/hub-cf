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
package com.blackducksoftware.integration.cloudfoundry.servicebroker.json;

import java.io.IOException;

import com.blackducksoftware.integration.cloudfoundry.servicebroker.exception.BlackDuckServiceBrokerException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

/**
 * @author jfisher
 *
 */
public final class JsonUtil {
    public static final ObjectMapper OBJECT_MAPPER = JsonUtil.configureObjectMapper();

    private JsonUtil() {
    }

    private static ObjectMapper configureObjectMapper() {
        return configureJacksonJson(new ObjectMapper());
    }

    /**
     * Configure ObjectMapper to behave how we need it
     *
     * @param mapper
     * @return
     */
    private static ObjectMapper configureJacksonJson(ObjectMapper mapper) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.registerModule(new Jdk8Module());
        return mapper;
    }

    /**
     *
     * @return the configured ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static <T> T readValue(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonParseException e) {
            throw new BlackDuckServiceBrokerException("Improperly formatted JSON: " + json, e);
        } catch (JsonMappingException e) {
            throw new BlackDuckServiceBrokerException("Unable to parse JSON: " + json + "; Message: " + e.getLocalizedMessage(), e);
        } catch (IOException e) {
            throw new BlackDuckServiceBrokerException("IO processing exception", e);
        }
    }
}
