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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * @since 0.3.0
 */
public class AbstractService {

//    static{
////        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:**/spring-security.xml");
////        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/WEB-INF/spring-security.xml", "classpath*:**/spring-security.xml");
////        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:**/spring-security.xml");
////        FileSystemXmlApplicationContext applicationContext = new FileSystemXmlApplicationContext("/webapp/WEB-INF/spring-security.xml"); OK
////        ApplicationContext applicationContext = new XmlWebApplicationContext("classpath*:**/spring-security.xml");
//        XmlWebApplicationContext applicationContext = new XmlWebApplicationContext() {
//            public String[] getConfigLocations() {
//                return new String[]{
//                        "/WEB-INF/spring-security.xml"
////                        "file:WEB-INF/spring-security.xml",
////                        "classpath*:**/spring-security.xml"};
//                };
//            }
//        };
//        applicationContext.refresh();
////        authenticationProvider = (StormpathAuthenticationProvider) applicationContext.getAutowireCapableBeanFactory().getBean("authenticationProvider");
//        authenticationProvider = (StormpathAuthenticationProvider) applicationContext.getBean("authenticationProvider");
//    }

    @Autowired
    private StormpathAuthenticationProvider authenticationProvider;

    private Application application;

    protected StormpathAuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    protected Client getStormpathClient() {
        return getAuthenticationProvider().getClient();
    }

    protected Application getStormpathApplication() {
        if (this.application == null) {
            this.application = getStormpathClient().getResource(getAuthenticationProvider().getApplicationRestUrl(), Application.class);
        }
        return this.application;
    }

}
