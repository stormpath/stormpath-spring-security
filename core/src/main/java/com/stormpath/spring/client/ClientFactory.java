/*
 * Copyright 2013 Stormpath, Inc.
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

package com.stormpath.spring.client;

import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * A simple factory that allows a Stormpath SDK Client to be created via Spring's
 * {@link org.springframework.beans.factory.config.AbstractFactoryBean} concept.
 * <p/>
 * As this class is a simple bridge between APIs, it does not do much - all configuration properties are immediately
 * passed through to an internal {@link com.stormpath.sdk.client.ClientBuilder ClientBuilder} instance, and the
 * {@link #createInstance()} implementation merely calls {@link com.stormpath.sdk.client.ClientBuilder#build()}.
 * <h2>Usage</h2>
 * Example {@code spring.xml} configuration:
 * <p/>
 * <pre>
 * <bean id="stormpathClient" class="com.stormpath.spring.client.ClientFactory" >
 * <property name="apiKeyFileLocation" value="#{systemProperties['user.home']}/.stormpath/apiKey.properties" />
 * </bean>
 *
 * ...
 * </pre>
 *
 * @see ClientBuilder
 * @see ClientBuilder#setApiKeyFileLocation(String)
 */
public class ClientFactory extends AbstractFactoryBean<Client> {

    private ClientBuilder clientBuilder;

    public ClientFactory() {
        super();
        this.clientBuilder = new ClientBuilder();
    }

    @Override
    protected Client createInstance() throws Exception {
        return clientBuilder.build();
    }

    @Override
    public Class<?> getObjectType() {
        return Client.class;
    }

    public ClientBuilder getClientBuilder() {
        return clientBuilder;
    }

    public void setClientBuilder(ClientBuilder clientBuilder) {
        this.clientBuilder = clientBuilder;
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyFileLocation(String) setApiKeyFileLocation(location)}.
     * See that JavaDoc for expected syntax/format.
     *
     * @param apiKeyFileLocation the file, classpath or url location of the API Key {@code .properties} file to load when
     *                           constructing the API Key to use for communicating with the Stormpath REST API.
     * @see ClientBuilder#setApiKeyFileLocation(String)
     */
    public void setApiKeyFileLocation(String apiKeyFileLocation) {
        this.clientBuilder.setApiKeyFileLocation(apiKeyFileLocation);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyInputStream(java.io.InputStream) setApiKeyInputStream}.
     *
     * @param apiKeyInputStream the InputStream to use to construct a configuration Properties instance.
     * @see ClientBuilder#setApiKeyInputStream(java.io.InputStream)
     */
    public void setApiKeyInputStream(InputStream apiKeyInputStream) {
        this.clientBuilder.setApiKeyInputStream(apiKeyInputStream);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyReader(java.io.Reader) setApiKeyReader}.
     *
     * @param apiKeyReader the reader to use to construct a configuration Properties instance.
     * @see ClientBuilder#setApiKeyReader(java.io.Reader)
     */
    public void setApiKeyReader(Reader apiKeyReader) {
        this.clientBuilder.setApiKeyReader(apiKeyReader);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyProperties(java.util.Properties)}.
     *
     * @param properties the properties instance to use to load the API Key ID and Secret.
     * @see ClientBuilder#setApiKeyProperties(java.util.Properties)
     */
    public void setApiKeyProperties(Properties properties) {
        this.clientBuilder.setApiKeyProperties(properties);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeyIdPropertyName(String) setApiKeyIdPropertyName}.
     *
     * @param apiKeyIdPropertyName the name used to query for the API Key ID from a Properties instance.
     * @see ClientBuilder#setApiKeyIdPropertyName(String)
     */
    public void setApiKeyIdPropertyName(String apiKeyIdPropertyName) {
        this.clientBuilder.setApiKeyIdPropertyName(apiKeyIdPropertyName);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setApiKeySecretPropertyName(String) setApiKeySecretPropertyName}.
     *
     * @param apiKeySecretPropertyName the name used to query for the API Key Secret from a Properties instance.
     * @see ClientBuilder#setApiKeySecretPropertyName(String)
     */
    public void setApiKeySecretPropertyName(String apiKeySecretPropertyName) {
        this.clientBuilder.setApiKeySecretPropertyName(apiKeySecretPropertyName);
    }

    /**
     * Calls {@code clientBuilder.}{@link ClientBuilder#setCacheManager(com.stormpath.sdk.cache.CacheManager) setCacheManager}
     * using the specified Stormpath {@link com.stormpath.sdk.cache.CacheManager CacheManager} instance, but <b>note:</b>
     * <p/>
     *
     * @param cacheManager the Storpmath SDK-specific CacheManager to use for the Stormpath SDK Client's caching needs.
     */
    public void setCacheManager(CacheManager cacheManager) {
        this.clientBuilder.setCacheManager(cacheManager);
    }


}
