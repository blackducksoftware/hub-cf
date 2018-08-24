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

/**
 * @author fisherj
 *
 */
public final class EventType {
    public static class AUDIT {
        private static final String PREFIX = "audit.";

        public static class APP {
            private static final String PREFIX = AUDIT.PREFIX + "app.";

            public static class DROPLET {
                private static final String PREFIX = APP.PREFIX + "droplet.";

                public static final String CREATE = PREFIX + "create";
            }
        }
    }
}
