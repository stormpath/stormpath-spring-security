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
package com.stormpath.spring.security.web.conf;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.spring.security.web.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @since 0.3.0
 */
public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private static final OverridableIni ini;

    static{
        OverridableIni defaultsIni = new OverridableIni();
        readIniData(defaultsIni, Constants.DEFAULT_CONFIG_INI_FILE);
        ini = new OverridableIni(defaultsIni);
        readIniData(ini, Constants.CUSTOM_CONFIG_INI_FILE);
        validate();
    }

    //App
    public static String getBaseURL() {
        return ini.getSection("App").get("baseURL");
    }

    //ID Site
    public static boolean isIDSiteEnabled() {
        return Boolean.parseBoolean(ini.getSection("ID Site").get("enable"));
    }

    public static String getLoginRedirectUri() {
        return ini.getSection("ID Site").get("loginRedirectUri");
    }

    public static String getLogoutRedirectUri() {
        return ini.getSection("ID Site").get("logoutRedirectUri");
    }


    private static void validate() {
        Assert.hasText(getBaseURL());
        Assert.notNull(isIDSiteEnabled());
        Assert.hasText(getLoginRedirectUri());
        Assert.hasText(getLogoutRedirectUri());
    }

//    //App
//    public static boolean isRegistrationEnabled() {
//        return Boolean.parseBoolean(ini.getSection("App").get("enableRegistration"));
//    }
//
//    public static boolean isUsernameEnabled() {
//        return Boolean.parseBoolean(ini.getSection("App").get("enableUsername"));
//    }
//
//    public static boolean isUsernameRequired() {
//        return Boolean.parseBoolean(ini.getSection("App").get("usernameRequired"));
//    }
//
//    public static boolean isGivenNameEnabled() {
//        return Boolean.parseBoolean(ini.getSection("App").get("givenNameEnabled"));
//    }
//
//    public static boolean isGivenNameRequired() {
//        return Boolean.parseBoolean(ini.getSection("App").get("givenNameRequired"));
//    }
//
//    public static boolean isMiddleNameEnabled() {
//        return Boolean.parseBoolean(ini.getSection("App").get("middleNameEnabled"));
//    }
//
//    public static boolean isMiddleNameRequired() {
//        return Boolean.parseBoolean(ini.getSection("App").get("middleNameRequired"));
//    }
//
//    public static boolean isSurnameEnabled() {
//        return Boolean.parseBoolean(ini.getSection("App").get("surnameEnabled"));
//    }
//
//    public static boolean isSurnameRequired() {
//        return Boolean.parseBoolean(ini.getSection("App").get("surnameRequired"));
//    }
//
//    public static boolean isStormpathLoginEnabled(){
//        return Boolean.parseBoolean(ini.getSection("App").get("stormpathLoginEnabled"));
//    }
//
//    //Google
//    public static boolean isGoogleEnabled() {
//        return Boolean.parseBoolean(ini.getSection("Social").get("enableGoogle"));
//    }
//
//    public static String getGoogleClientId() {
//        return GOOGLE().get("clientId");
//    }
//
//    public static String getGoogleClientSecret() {
//        return GOOGLE().get("clientSecret");
//    }
//
//    public static String getGoogleRedirectUri() {
//        return GOOGLE().get("redirectUri");
//    }
//
//    //Facebook
//    public static boolean isFacebookEnabled() {
//        return Boolean.parseBoolean(ini.getSection("Social").get("enableFacebook"));
//    }
//
//    public static String getFacebookAppId() {
//        return FACEBOOK().get("appId");
//    }
//
//    public static String getFacebookAppSecret() {
//        return FACEBOOK().get("appSecret");
//    }
//
//    public static String getFacebookRedirectUri() {
//        return FACEBOOK().get("redirectUri");
//    }
//
//    public static String getFacebookScope() {
//        return FACEBOOK().get("scope");
//    }
//
//    private static OverridableIni.Section GOOGLE() {
//        return ini.getSection("Google");
//    }
//
//    private static OverridableIni.Section FACEBOOK() {
//        return ini.getSection("Facebook");
//    }
//
//    public static boolean isSocialEnabled() {
//        return Configuration.isGoogleEnabled() || Configuration.isFacebookEnabled();
//    }
//
//
//    private static void validate() {
//        Assert.notNull(isRegistrationEnabled());
//        Assert.notNull(isUsernameEnabled());
//        Assert.notNull(isUsernameRequired());
//        Assert.notNull(isGoogleEnabled());
//        Assert.notNull(isFacebookEnabled());
//        Assert.notNull(isGivenNameEnabled());
//        Assert.notNull(isGivenNameRequired());
//        Assert.notNull(isMiddleNameEnabled());
//        Assert.notNull(isMiddleNameRequired());
//        Assert.notNull(isSurnameEnabled());
//        Assert.notNull(isSurnameRequired());
//        Assert.notNull(isStormpathLoginEnabled());
//
//        if(isGoogleEnabled()) {
//            Assert.hasText(getGoogleClientId());
//            Assert.hasText(getGoogleClientSecret());
//            Assert.hasText(getGoogleRedirectUri());
//        }
//        if(isFacebookEnabled()) {
//            Assert.hasText(getFacebookAppId());
//            Assert.hasText(getFacebookAppSecret());
//            Assert.hasText(getFacebookRedirectUri());
//            Assert.hasText(getFacebookScope());
//        }
//    }

    private static void readIniData(OverridableIni ini, String inputStreamPath) {
        Assert.hasText(inputStreamPath);
        InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(inputStreamPath);

        if (inputStream != null) {
            try {
                ini.load(inputStream);
                if (CollectionUtils.isEmpty(ini)) {
                    logger.warn("Configuration INI resource exists, but it does not contain any data.");
                }
            } catch (Exception e) {
                logger.warn("There was some problem reading configuration file " + inputStreamPath + "." + e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
    }


}
