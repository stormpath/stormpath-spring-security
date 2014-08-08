/*
 * Copyright 2014 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.spring.security.web.service;

import com.stormpath.spring.security.web.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @since 0.3.0
 */
@Service(value = "idSiteService")
public class IDSiteService extends AbstractService {

    private static final Logger logger = LoggerFactory.getLogger(IDSiteService.class);

    private static IDSiteService service = null;

//    /**
//     * Let's make the constructor private so we can have a single IDSiteService.
//     */
//    private IDSiteService() {
//    }
//
//    public static IDSiteService getInstance() {
//        if(service == null) {
//            service = new IDSiteService();
//        }
//        return service;
//    }

    public String getLoginRedirectUri(String redirectUri) {
        //getStormpathApplication().newIdSiteCallbackHandler(null).;
        return getStormpathApplication().newIdSiteUrlBuilder().setCallbackUri(redirectUri).build();
    }

    public String getLogoutRedirectUri(String redirectUri) {
        return getStormpathApplication().newIdSiteUrlBuilder().setCallbackUri(redirectUri).withLogout().build();
        //return getStormpathApplication().newIdSiteUrlBuilder().setCallbackUri(redirectUri).setPath("sso/logout").build();
    }

}
