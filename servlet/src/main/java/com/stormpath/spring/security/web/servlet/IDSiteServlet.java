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
package com.stormpath.spring.security.web.servlet;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.idsite.AccountResult;
import com.stormpath.sdk.idsite.IdSiteCallbackHandler;
import com.stormpath.sdk.idsite.IdSiteResultListener;
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider;
import com.stormpath.spring.security.web.conf.Configuration;
import com.stormpath.spring.security.web.conf.UrlFor;
import com.stormpath.spring.security.web.service.IDSiteService;
import com.stormpath.spring.security.web.service.IDSiteServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * @since 0.3.0
 */
//@Component("idSiteServlet")
//public class IDSiteServlet extends IDSiteServletHttpServletRequestHandler {
public class IDSiteServlet extends HttpServlet implements ApplicationContextAware {


//    private ApplicationContext applicationContext;

//    @Autowired
//    //private StormpathAuthenticationProvider authenticationProvider;
//    //@Resource(name = "idSiteService")
//    //@Qualifier("idSiteService")
//    private static IDSiteService idSiteService;
//
//    public IDSiteServlet() {
//        //ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:stormpath-root-context.xml");
//        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:**/*root-context.xml");
//        System.out.println("XXXXXXXXXX got app context: " + ctx);
//        idSiteService = (IDSiteService)ctx.getBean("idSiteService");
//        int a = 0;
//    }

//    private final ApplicationContext appContext;

//    public IDSiteServlet() {
//        appContext = new AnnotationConfigApplicationContext(IDSiteService.class);
//        this.idSiteService = appContext.getBean(IDSiteService.class);
//    }

    //    @Autowired
    //private StormpathAuthenticationProvider authenticationProvider;
    //@Resource(name = "idSiteService")
    //@Qualifier("idSiteService")
//    @Autowired
    protected IDSiteService idSiteService;

//    @Autowired
//    protected IDSiteServiceFactory idSiteServiceFactory;

    private static final Logger logger = LoggerFactory.getLogger(IDSiteServlet.class);

    // This servlet will only handle requests for these URIs
    private static final String IDSITE_LOGIN_ACTION = UrlFor.get("idsite_login.action");
    private static final String IDSITE_LOGOUT_ACTION = UrlFor.get("idsite_logout.action");

    //private static final String IDSITE_LOGIN_CALLBACK_INTERCEPT = UrlFor.get("loginRedirectCallbackURL");
    private static final String IDSITE_LOGIN_CALLBACK_ACTION = UrlFor.get("idsite_login_callback.action");
    private static final String IDSITE_LOGIN_REDIRECT_URL = Configuration.getBaseURL() + IDSITE_LOGIN_CALLBACK_ACTION;

    private static final String IDSITE_LOGOUT_CALLBACK_ACTION = UrlFor.get("idsite_logout_callback.action");
    private static final String IDSITE_LOGOUT_REDIRECT_URL = Configuration.getBaseURL() + IDSITE_LOGOUT_CALLBACK_ACTION;

//    private static final String IDSITE_LOGIN_CALLBACK_INTERCEPT_URI = "/idsite/loginCallback";
////    private static final String IDSITE_LOGIN_CALLBACK_INTERCEPT = "http://localhost:8080" + IDSITE_LOGIN_CALLBACK_INTERCEPT_URI;
//    private static final String IDSITE_LOGOUT_CALLBACK_INTERCEPT_URI = "/idsite/logoutCallback";
//    private static final String IDSITE_LOGOUT_CALLBACK_INTERCEPT = "http://localhost:8080" + IDSITE_LOGOUT_CALLBACK_INTERCEPT_URI;

    //    @Autowired
    private StormpathAuthenticationProvider authenticationProvider;

    private String callbackUri;

    /**
     * Registration requests are handled here relying on the {@link com.stormpath.spring.security.web.service.IDSiteService} to actually execute it.
     *
     * @param request  an {@link javax.servlet.http.HttpServletRequest} object that contains the request the client has made of the servlet.
     * @param response an {@link javax.servlet.http.HttpServletResponse} object that contains the response the servlet sends to the client.
     * @throws java.io.IOException            if an input or output error is detected when the servlet handles the POST request
     * @throws javax.servlet.ServletException if the request for the POST could not be handled
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (idSiteService == null) {
//            applicationContext = ContextLoader.getCurrentWebApplicationContext();

//            ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
//            ((XmlWebApplicationContext) applicationContext).getServletConfig();
//            applicationContext.getAutowireCapableBeanFactory().getBean("idSiteService");
            ServletContext servletContext = request.getSession().getServletContext();
//            //applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);


            XmlWebApplicationContext applicationContext = new XmlWebApplicationContext() {
                public String[] getConfigLocations() {
                    return new String[]{
                            "/WEB-INF/spring-security.xml",
                            "/WEB-INF/spring/appServlet/servlet-context.xml",
//                        "/WEB-INF/spring/appServlet/stormpath-servlet-context.xml",
                            "classpath*:stormpath-servlet-context.xml",
                            "/WEB-INF/spring/root-context.xml"
//                        "file:WEB-INF/spring-security.xml",
//                        "classpath*:**/spring-security.xml"};
                    };
//                return new String[]{
////                        "/WEB-INF/spring-security.xml",
////                        "/WEB-INF/spring/appServlet/servlet-context.xml",
////                        "/WEB-INF/spring/appServlet/stormpath-servlet-context.xml",
//                        "classpath*:stormpath-servlet-context.xml"
////                        "/WEB-INF/spring/root-context.xml"
////                        "file:WEB-INF/spring-security.xml",
////                        "classpath*:**/spring-security.xml"};
//                    };
                }
            };
            applicationContext.setServletContext(servletContext);
            applicationContext.refresh();
////        authenticationProvider = (StormpathAuthenticationProvider) applicationContext.getAutowireCapableBeanFactory().getBean("authenticationProvider");
            idSiteService = (IDSiteService) applicationContext.getBean("idSiteService");
            authenticationProvider = (StormpathAuthenticationProvider) applicationContext.getBean("authenticationProvider");
//            idSiteServiceFactory = (IDSiteServiceFactory) applicationContext.getBean("idSiteServiceFactory");
        }

        callbackUri = idSiteService.getLoginRedirectUri(IDSITE_LOGIN_REDIRECT_URL);
//        if(authenticationProvider == null) {
//        }
//        idSiteServiceFactory.getIdSiteService();

        String uri = request.getRequestURI();
        //URL url = new URL(IDSITE_LOGIN_CALLBACK_INTERCEPT);
        if (uri.startsWith(IDSITE_LOGIN_ACTION)) {
            //perform login via ID Site
            logger.debug("About to redirect to the following IDSite Redirect URL: " + IDSITE_LOGIN_REDIRECT_URL);
//            String callbackUri = idSiteS            ervice.getLoginRedirectUri(IDSITE_LOGIN_REDIRECT_URL);

//            String callbackUri = IDSiteService.getInstance().getLoginRedirectUri();
            addIDSiteHeader(response);
            response.sendRedirect(callbackUri);
            //} else if (uri.startsWith(IDSITE_LOGIN_CALLBACK_INTERCEPT_URI)) {
        } else if (uri.startsWith(IDSITE_LOGIN_CALLBACK_ACTION)) {
            Application application = authenticationProvider.getClient().getResource(authenticationProvider.getApplicationRestUrl(), Application.class);
            ResultListener resultListener = new ResultListener();
//            new Thread(resultListener).start();
//            callbackHandler.setResultListener(resultListener);
            IdSiteCallbackHandler callbackHandler = application.newIdSiteCallbackHandler(request);
            callbackHandler.setResultListener(new IdSiteResultListener() {
                @Override
                public void onRegistered(AccountResult accountResult) {
                    System.out.println("Successful Id Site registration");
                }

                @Override
                public void onAuthenticated(AccountResult accountResult) {
                    System.out.println("Successful Id Site authentication");
                }

                @Override
                public void onLogout(AccountResult accountResult) {
                    System.out.println("Successful Id Site logout");
                }
            });
            Account account = callbackHandler.getAccountResult().getAccount();

            SecurityContextHolder.clearContext();
            Authentication authentication = authenticationProvider.createAuthenticationToken(account.getUsername(), "", account);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            response.sendRedirect(Configuration.getLoginRedirectUri());
        } else if (uri.startsWith(IDSITE_LOGOUT_ACTION)) {
            logger.debug("About to redirect to the following IDSite Redirect URL: " + IDSITE_LOGOUT_REDIRECT_URL);
            String callbackUri = idSiteService.getLogoutRedirectUri(IDSITE_LOGOUT_REDIRECT_URL);
//            String callbackUri = IDSiteService.getInstance().getLogoutRedirectUri();
            addIDSiteHeader(response);
            response.sendRedirect(callbackUri);
        } else if (uri.startsWith(IDSITE_LOGOUT_CALLBACK_ACTION)) {
//            Application application = authenticationProvider.getClient().getResource(authenticationProvider.getApplicationRestUrl(), Application.class);
//            AccountResult accountResult = application.newIdSiteCallbackHandler(request).getAccountResult();
//            Account account = accountResult.getAccount();
            SecurityContextHolder.clearContext();
//            Authentication authentication = authenticationProvider.createAuthenticationToken(account.getUsername(), "", account);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
            response.sendRedirect(Configuration.getLoginRedirectUri());
        }
    }

    private void addIDSiteHeader(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
    }


    private class ResultListener implements IdSiteResultListener {

        @Override
        public void onAuthenticated(AccountResult accountResult) {
            System.out.println("AAAAAAAAAA");
        }

        @Override
        public void onLogout(AccountResult accountResult) {

        }

        @Override
        public void onRegistered(AccountResult accountResult) {

        }

//        @Override
//        public void run() {
//
//        }
    }

}
