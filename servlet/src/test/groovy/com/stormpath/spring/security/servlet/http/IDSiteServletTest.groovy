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
package com.stormpath.spring.security.servlet.http

import com.stormpath.sdk.idsite.IdSiteCallbackHandler
import com.stormpath.sdk.idsite.IdSiteResultListener
import com.stormpath.spring.security.authc.IdSiteAuthenticationToken
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider
import com.stormpath.spring.security.servlet.conf.Configuration
import com.stormpath.spring.security.servlet.service.IdSiteService
import org.junit.Test
import org.springframework.beans.factory.BeanFactory
import org.springframework.security.core.Authentication

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.easymock.EasyMock.anyObject
import static org.easymock.EasyMock.createMockBuilder
import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify

class IdSiteServletTest {

    @Test
    public void testLogin() {

        def request = createStrictMock(HttpServletRequest)
        def response = createStrictMock(HttpServletResponse)
        def idSiteService = createStrictMock(IdSiteService)
        def stormpathAuthenticationProvider = createStrictMock(StormpathAuthenticationProvider)
        def idSiteResultListener = createStrictMock(IdSiteResultListener)
        def servletContextEvent = createStrictMock(ServletContextEvent)
        def servletContext = createStrictMock(ServletContext)
        def beanFactory = createStrictMock(BeanFactory)
        def callbackUri = "http://api.stormpath.com/sso?jwtRequest=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpUXQiOoE1MDk4ODIzNTIsImp0aSI6IjUzODU2YmJmLTBlOTQtNDFmNC05OTJmLWZiYjFmZjA5MWVkZCIsImlzcyI6IjZKUVBDSVRNTzVFOEhFS042REtCVDdSNTIiLCJzdWIiOiJodHRwczovL2FwaS5zdG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW6ucy8zVHFieVoxcW83NGVETTRnVG7ySDk0IiwiY2JfdXJpIjoiaHR9cDovL2xvY2FsaG9zdDo4MDgwL2lkc2l0ZS9jYWxsYmFja0xvZ2luIn0.hBva8p4Wy9hAu5nR9euJcMRI0qR0Xkvna-GlBnMOGSQ"

        IdSiteServlet servlet = createMockBuilder(IdSiteServlet.class)
                .addMockedMethod("getBeanFactory", ServletContext).createMock();

        expect(servletContextEvent.getServletContext()).andReturn(servletContext)
        expect(servlet.getBeanFactory(servletContext)).andReturn(beanFactory)
        expect(beanFactory.getBean("idSiteService")).andReturn(idSiteService)
        expect(beanFactory.getBean("authenticationProvider")).andReturn(stormpathAuthenticationProvider)
        expect(beanFactory.containsBean("idSiteResultListener")).andReturn(true)
        expect(beanFactory.getBean("idSiteResultListener")).andReturn(idSiteResultListener)
        expect(request.getRequestURI()).andReturn("/idsite/login")
        expect(response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0"))
        expect(response.setHeader("Pragma", "no-cache"))
        expect(idSiteService.getLoginRedirectUri("http://localhost:8080/idsite/callbackLogin")).andReturn(callbackUri)
        expect(response.sendRedirect(callbackUri))

        replay servlet, request, response, idSiteService, stormpathAuthenticationProvider, idSiteResultListener,
                servletContextEvent, servletContext, beanFactory

        servlet.contextInitialized(servletContextEvent)
        servlet.doGet(request, response)

        verify servlet, request, response, idSiteService, stormpathAuthenticationProvider, idSiteResultListener,
                servletContextEvent, servletContext, beanFactory
    }

    @Test
    public void testLoginCallback() {

        def request = createStrictMock(HttpServletRequest)
        def response = createStrictMock(HttpServletResponse)
        def idSiteService = createStrictMock(IdSiteService)
        def stormpathAuthenticationProvider = createStrictMock(StormpathAuthenticationProvider)
        def idSiteResultListener = createStrictMock(IdSiteResultListener)
        def servletContextEvent = createStrictMock(ServletContextEvent)
        def servletContext = createStrictMock(ServletContext)
        def beanFactory = createStrictMock(BeanFactory)
        def callbackUri = Configuration.getLoginRedirectUri()
        def authentication = createStrictMock(Authentication)

        IdSiteServlet servlet = createMockBuilder(IdSiteServlet.class)
                .addMockedMethod("getBeanFactory", ServletContext).createMock();

        expect(servletContextEvent.getServletContext()).andReturn(servletContext)
        expect(servlet.getBeanFactory(servletContext)).andReturn(beanFactory)
        expect(beanFactory.getBean("idSiteService")).andReturn(idSiteService)
        expect(beanFactory.getBean("authenticationProvider")).andReturn(stormpathAuthenticationProvider)
        expect(beanFactory.containsBean("idSiteResultListener")).andReturn(true)
        expect(beanFactory.getBean("idSiteResultListener")).andReturn(idSiteResultListener)
        expect(request.getRequestURI()).andReturn("/idsite/callbackLogin")
        expect(request.getMethod()).andReturn("GET")
        expect(request.getHeaderNames()).andReturn(Collections.emptyEnumeration())
        expect(request.getParameterMap()).andReturn(Collections.EMPTY_MAP)
        expect(stormpathAuthenticationProvider.authenticate(anyObject(IdSiteAuthenticationToken))).andReturn(authentication)
        expect(response.sendRedirect(callbackUri))

        replay servlet, request, response, idSiteService, stormpathAuthenticationProvider, idSiteResultListener,
                servletContextEvent, servletContext, beanFactory, authentication

        servlet.contextInitialized(servletContextEvent)
        servlet.doGet(request, response)

        verify servlet, request, response, idSiteService, stormpathAuthenticationProvider, idSiteResultListener,
                servletContextEvent, servletContext, beanFactory, authentication
    }

    @Test
    public void testLogout() {

        def request = createStrictMock(HttpServletRequest)
        def response = createStrictMock(HttpServletResponse)
        def idSiteService = createStrictMock(IdSiteService)
        def stormpathAuthenticationProvider = createStrictMock(StormpathAuthenticationProvider)
        def idSiteResultListener = createStrictMock(IdSiteResultListener)
        def servletContextEvent = createStrictMock(ServletContextEvent)
        def servletContext = createStrictMock(ServletContext)
        def beanFactory = createStrictMock(BeanFactory)
        def callbackUri = "http://api.stormpath.com/sso?jwtRequest=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpUXQiOoE1MDk4ODIzNTIsImp0aSI6IjUzODU2YmJmLTBlOTQtNDFmNC05OTJmLWZiYjFmZjA5MWVkZCIsImlzcyI6IjZKUVBDSVRNTzVFOEhFS042REtCVDdSNTIiLCJzdWIiOiJodHRwczovL2FwaS5zdG9ybXBhdGguY29tL3YxL2FwcGxpY2F0aW6ucy8zVHFieVoxcW83NGVETTRnVG7ySDk0IiwiY2JfdXJpIjoiaHR9cDovL2xvY2FsaG9zdDo4MDgwL2lkc2l0ZS9jYWxsYmFja0xvZ2luIn0.hBva8p4Wy9hAu5nR9euJcMRI0qR0Xkvna-GlBnMOGSQ"

        IdSiteServlet servlet = createMockBuilder(IdSiteServlet.class)
                .addMockedMethod("getBeanFactory", ServletContext).createMock();

        expect(servletContextEvent.getServletContext()).andReturn(servletContext)
        expect(servlet.getBeanFactory(servletContext)).andReturn(beanFactory)
        expect(beanFactory.getBean("idSiteService")).andReturn(idSiteService)
        expect(beanFactory.getBean("authenticationProvider")).andReturn(stormpathAuthenticationProvider)
        expect(beanFactory.containsBean("idSiteResultListener")).andReturn(true)
        expect(beanFactory.getBean("idSiteResultListener")).andReturn(idSiteResultListener)
        expect(request.getRequestURI()).andReturn("/idsite/logout")
        expect(response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0"))
        expect(response.setHeader("Pragma", "no-cache"))
        expect(idSiteService.getLogoutRedirectUri("http://localhost:8080/idsite/callbackLogout")).andReturn(callbackUri)
        expect(response.sendRedirect(callbackUri))

        replay servlet, request, response, idSiteService, stormpathAuthenticationProvider, idSiteResultListener,
                servletContextEvent, servletContext, beanFactory

        servlet.contextInitialized(servletContextEvent)
        servlet.doGet(request, response)

        verify servlet, request, response, idSiteService, stormpathAuthenticationProvider, idSiteResultListener,
                servletContextEvent, servletContext, beanFactory
    }

    @Test
    public void testLogoutCallback() {

        def request = createStrictMock(HttpServletRequest)
        def response = createStrictMock(HttpServletResponse)
        def idSiteService = createStrictMock(IdSiteService)
        def stormpathAuthenticationProvider = createStrictMock(StormpathAuthenticationProvider)
        def idSiteResultListener = createStrictMock(IdSiteResultListener)
        def servletContextEvent = createStrictMock(ServletContextEvent)
        def servletContext = createStrictMock(ServletContext)
        def beanFactory = createStrictMock(BeanFactory)
        def callbackUri = Configuration.getLoginRedirectUri()
        def authentication = createStrictMock(Authentication)
        def idSiteCallbackHandler = createStrictMock(IdSiteCallbackHandler)

        IdSiteServlet servlet = createMockBuilder(IdSiteServlet.class)
                .addMockedMethod("getBeanFactory", ServletContext).createMock();

        expect(servletContextEvent.getServletContext()).andReturn(servletContext)
        expect(servlet.getBeanFactory(servletContext)).andReturn(beanFactory)
        expect(beanFactory.getBean("idSiteService")).andReturn(idSiteService)
        expect(beanFactory.getBean("authenticationProvider")).andReturn(stormpathAuthenticationProvider)
        expect(beanFactory.containsBean("idSiteResultListener")).andReturn(true)
        expect(beanFactory.getBean("idSiteResultListener")).andReturn(idSiteResultListener)
        expect(request.getRequestURI()).andReturn("/idsite/callbackLogout")
        expect(idSiteService.getCallbackHandler(request, idSiteResultListener)).andReturn(idSiteCallbackHandler)
        expect(idSiteCallbackHandler.getAccountResult()).andReturn(null)
        expect(response.sendRedirect(callbackUri))

        replay servlet, request, response, idSiteService, stormpathAuthenticationProvider, idSiteResultListener,
                servletContextEvent, servletContext, beanFactory, authentication, idSiteCallbackHandler

        servlet.contextInitialized(servletContextEvent)
        servlet.doGet(request, response)

        verify servlet, request, response, idSiteService, stormpathAuthenticationProvider, idSiteResultListener,
                servletContextEvent, servletContext, beanFactory, authentication, idSiteCallbackHandler
    }
}

