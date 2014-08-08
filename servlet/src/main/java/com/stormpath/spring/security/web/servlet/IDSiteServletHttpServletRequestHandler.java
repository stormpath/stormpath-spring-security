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

import com.stormpath.spring.security.web.conf.UrlFor;
import com.stormpath.spring.security.web.service.IDSiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @since 0.3.0
 */
//@Component("IDSiteServletHttpServletRequestHandler")
public class IDSiteServletHttpServletRequestHandler implements HttpRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(IDSiteServletHttpServletRequestHandler.class);

    @Autowired
    //private StormpathAuthenticationProvider authenticationProvider;
    //@Resource(name = "idSiteService")
    //@Qualifier("idSiteService")
    protected static IDSiteService idSiteService;

    public IDSiteServletHttpServletRequestHandler() {
        //ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:stormpath-root-context.xml");
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:**/*root-context.xml");
        System.out.println("XXXXXXXXXX got app context: " + ctx);
        idSiteService = (IDSiteService)ctx.getBean("idSiteService");
        int a = 0;
    }



    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.setContentType("text/html");
//        PrintWriter writer = response.getWriter();
//        writer.write("<h1>Spring Beans Injection into Java Servlets!</h1><h2>" + helloService.sayHello("World") + "</h2>");
    }

}
