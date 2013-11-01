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

package com.stormpath.spring.security.provider

import org.junit.Assert
import org.junit.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

import static org.hamcrest.core.IsInstanceOf.instanceOf

class UsernamePasswordAuthenticationTokenFactoryTest {

    @Test
    void testSetAuthenticationTokenFactory() {
        def authenticationTokenFactory = new UsernamePasswordAuthenticationTokenFactory()
        def grantedAuthorityA = new SimpleGrantedAuthority("ROLE_A");
        def grantedAuthorityB = new SimpleGrantedAuthority("ROLE_B");
        def Set<GrantedAuthority> gas = new HashSet<>(2);
        gas.add(grantedAuthorityA)
        gas.add(grantedAuthorityB)
        def token = authenticationTokenFactory.createAuthenticationToken("foo", "bar", gas)
        Assert.assertNotNull token
        Assert.assertThat token, instanceOf(UsernamePasswordAuthenticationToken.class)
        Assert.assertEquals "foo", token.principal
        Assert.assertEquals "bar", token.credentials
        Assert.assertArrayEquals gas.toArray(), token.getAuthorities().toArray()
        Assert.assertEquals gas.size(), token.getAuthorities().size()
    }


}
