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



package com.stormpath.spring.provider

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.authc.AuthenticationRequest
import com.stormpath.sdk.authc.AuthenticationResult
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.ds.DataStore
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupList
import org.easymock.IAnswer
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

import static org.easymock.EasyMock.*
import static org.junit.Assert.*

class StormpathAuthenticationProviderTest {

    StormpathAuthenticationProvider authenticationProvider

    @Before
    void setUp() {
        authenticationProvider = new StormpathAuthenticationProvider()
    }

    @Test(expected = IllegalStateException)
    void testInitWithoutClient() {
        def token = createStrictMock(UsernamePasswordAuthenticationToken)

        replay token

        try {
            authenticationProvider.authenticate(token) //needs client and applicationUrl properties
        } finally {
            verify token
        }
    }

    @Test(expected = IllegalStateException)
    void testInitWithoutApplicationUrl() {
        def client = createStrictMock(Client)
        def token = createStrictMock(UsernamePasswordAuthenticationToken)

        replay client, token

        authenticationProvider.client = client
        try {
            authenticationProvider.authenticate(token) //needs applicationUrl property
        } finally {
            verify client, token
        }
    }

    @Test
    void testSetClient() {
        def client = createStrictMock(Client)

        replay client

        authenticationProvider.client = client

        assertSame client, authenticationProvider.client

        verify client
    }

    @Test
    void testDoGetAuthenticationInfoSuccess() {

        def appHref = 'https://api.stormpath.com/v1/applications/foo'
        def username = 'jsmith'
        def password = 'secret'
        def acctHref = 'https://api.stormpath.com/v1/accounts/123'
        def email = 'jsmith@foo.com'
        def acctGivenName = 'John'
        def acctMiddleName = 'A'
        def acctSurname = 'Smith'

        def authentication = createStrictMock(UsernamePasswordAuthenticationToken)
        def client = createStrictMock(Client)
        def dataStore = createStrictMock(DataStore)
        def app = createStrictMock(Application)
        def authenticationResult = createStrictMock(AuthenticationResult)
        def account = createStrictMock(Account)
        def groupList = createStrictMock(GroupList)
        def iterator = createStrictMock(Iterator)
        def group = createStrictMock(Group)
        def accountGrantedAuthorityResolver = createStrictMock(AccountGrantedAuthorityResolver)
        def groupGrantedAuthorityResolver = createStrictMock(GroupGrantedAuthorityResolver)
        def groupGrantedAuthority = createStrictMock(GrantedAuthority)
        def accountGrantedAuthority = createStrictMock(GrantedAuthority)
        def groupGrantedAuthoritySet = new HashSet<GrantedAuthority>()
        groupGrantedAuthoritySet.add(groupGrantedAuthority)
        def accountGrantedAuthoritySet = new HashSet<GrantedAuthority>()
        accountGrantedAuthoritySet.add(accountGrantedAuthority)

        expect(authentication.principal).andReturn username
        expect(authentication.credentials).andReturn password
        expect(client.dataStore).andStubReturn(dataStore)
        expect(dataStore.getResource(eq(appHref), same(Application))).andReturn(app)
        expect(app.authenticateAccount(anyObject() as AuthenticationRequest)).andAnswer( new IAnswer<AuthenticationResult>() {
            AuthenticationResult answer() throws Throwable {
                def authcRequest = getCurrentArguments()[0] as AuthenticationRequest

                assertEquals username, authcRequest.principals
                assertTrue Arrays.equals(password.toCharArray(), authcRequest.credentials as char[])

                return authenticationResult
            }
        })
        expect(authenticationResult.account).andReturn account
        expect(account.groups).andReturn groupList
        expect(authentication.principal).andReturn username
        expect(authentication.credentials).andReturn password
        expect(groupList.iterator()).andReturn iterator
        expect(iterator.hasNext()).andReturn true
        expect(iterator.next()).andReturn group
        expect(groupGrantedAuthorityResolver.resolveGrantedAuthorities(group)).andReturn groupGrantedAuthoritySet
        expect(iterator.hasNext()).andReturn false
        expect(accountGrantedAuthorityResolver.resolveGrantedAuthorities(account)).andReturn accountGrantedAuthoritySet

        replay authentication, client, dataStore, app, authenticationResult, account, groupList, iterator, group,
                accountGrantedAuthorityResolver, groupGrantedAuthorityResolver, groupGrantedAuthority, accountGrantedAuthority

        authenticationProvider.client = client
        authenticationProvider.applicationRestUrl = appHref
        authenticationProvider.accountGrantedAuthorityResolver = accountGrantedAuthorityResolver
        authenticationProvider.groupGrantedAuthorityResolver = groupGrantedAuthorityResolver

        def info = authenticationProvider.authenticate(authentication)

        assertTrue info instanceof UsernamePasswordAuthenticationToken
        assertTrue info.authenticated

        assertEquals username, info.principal
        assertEquals password, info.credentials
        assertEquals 2, info.authorities.size()
        assertTrue info.authorities.contains(groupGrantedAuthority)
        assertTrue info.authorities.contains(accountGrantedAuthority)

        verify authentication, client, dataStore, app, authenticationResult, account, groupList, iterator, group,
                accountGrantedAuthorityResolver, groupGrantedAuthorityResolver, groupGrantedAuthority, accountGrantedAuthority
    }

    @Test(expected=AuthenticationServiceException)
    void testAuthenticateException() {

        def appHref = 'https://api.stormpath.com/v1/applications/foo'
        def client = createStrictMock(Client)
        def dataStore = createStrictMock(DataStore)
        def app = createStrictMock(Application)

        int status = 400
        int code = 400
        def msg = 'Invalid username or password.'
        def devMsg = 'Invalid username or password.'
        def moreInfo = 'mailto:support@stormpath.com'

        expect(client.dataStore).andStubReturn(dataStore)

        def error = new SimpleError(status:status, code:code, message: msg, developerMessage: devMsg, moreInfo: moreInfo)

        expect(dataStore.getResource(eq(appHref), same(Application))).andReturn app
        expect(app.authenticateAccount(anyObject() as AuthenticationRequest)).andThrow(new com.stormpath.sdk.resource.ResourceException(error))

        replay client, dataStore, app

        authenticationProvider.client = client
        authenticationProvider.applicationRestUrl = appHref

        def token = new UsernamePasswordAuthenticationToken('foo', 'bar')
        try {
            authenticationProvider.authenticate(token)
        }
        finally {
            verify client, dataStore, app
        }
    }

    @Test(expected=IllegalArgumentException)
    void testInvalidAuthenticationTokenFactory() {
        authenticationProvider.authenticationTokenFactory = null
    }

    @Test
    void testSetAuthenticationTokenFactory() {
        def authenticationTokenFactory = new UsernamePasswordAuthenticationTokenFactory()
        authenticationProvider.authenticationTokenFactory = authenticationTokenFactory
        assertEquals authenticationTokenFactory, authenticationProvider.authenticationTokenFactory
    }

    @Test
    void testSetAccountGrantedAuthorityResolver() {
        def accountGrantedAuthorityResolver = createNiceMock(AccountGrantedAuthorityResolver)
        authenticationProvider.accountGrantedAuthorityResolver = accountGrantedAuthorityResolver
        assertEquals accountGrantedAuthorityResolver, authenticationProvider.accountGrantedAuthorityResolver
    }

    @Test
    void testSetGroupGrantedAuthorityResolver() {
        def groupGrantedAuthorityResolver = createNiceMock(GroupGrantedAuthorityResolver)
        authenticationProvider.groupGrantedAuthorityResolver = groupGrantedAuthorityResolver
        assertEquals groupGrantedAuthorityResolver, authenticationProvider.groupGrantedAuthorityResolver
    }

    @Test
    void testSupports() {
        assertTrue authenticationProvider.supports(UsernamePasswordAuthenticationToken)
        assertTrue authenticationProvider.supports(RememberMeAuthenticationToken)
        assertTrue authenticationProvider.supports(AnonymousAuthenticationToken)
        assertTrue authenticationProvider.supports(Authentication)
        assertTrue authenticationProvider.supports(AbstractAuthenticationToken)
        assertFalse authenticationProvider.supports(String)
        assertFalse authenticationProvider.supports(Object)
    }

}
