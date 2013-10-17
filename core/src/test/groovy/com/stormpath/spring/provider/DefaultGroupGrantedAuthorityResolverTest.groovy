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

import com.stormpath.sdk.group.Group
import org.easymock.EasyMock
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.springframework.security.core.GrantedAuthority

import static org.hamcrest.core.IsInstanceOf.instanceOf

class DefaultGroupGrantedAuthorityResolverTest {

    DefaultGroupGrantedAuthorityResolver resolver

    @Before
    void setUp() {
        resolver = new DefaultGroupGrantedAuthorityResolver()
    }

    @Test
    void testDefaultInstance() {
        Assert.assertEquals 1, resolver.modes.size()
        Assert.assertSame DefaultGroupGrantedAuthorityResolver.Mode.HREF, resolver.modes.iterator().next()
        def modeNames = resolver.modeNames
        Assert.assertEquals 1, modeNames.size()
        Assert.assertEquals DefaultGroupGrantedAuthorityResolver.Mode.HREF.name(), modeNames.iterator().next()
    }

    @Test
    void testSetModes() {
        resolver.setModes([DefaultGroupGrantedAuthorityResolver.Mode.ID] as Set)
        Assert.assertEquals 1, resolver.modes.size()
        Assert.assertSame DefaultGroupGrantedAuthorityResolver.Mode.ID, resolver.modes.iterator().next()
    }

    @Test(expected=IllegalArgumentException)
    void testSetNullModes() {
        resolver.setModes(null)
    }

    @Test(expected=IllegalArgumentException)
    void testSetEmptyModes() {
        resolver.setModes(Collections.emptySet())
    }

    @Test
    void testSetModeNames() {
        resolver.setModeNames([DefaultGroupGrantedAuthorityResolver.Mode.ID.name()] as Set)
        Assert.assertEquals 1, resolver.modes.size()
        Assert.assertSame DefaultGroupGrantedAuthorityResolver.Mode.ID, resolver.modes.iterator().next()
    }

    @Test
    void testSetModeNamesLowerCase() {
        resolver.setModeNames([DefaultGroupGrantedAuthorityResolver.Mode.ID.name().toLowerCase()] as Set)
        Assert.assertEquals 1, resolver.modes.size()
        Assert.assertSame DefaultGroupGrantedAuthorityResolver.Mode.ID, resolver.modes.iterator().next()
    }

    @Test(expected=IllegalArgumentException)
    void testSetNullModeNames() {
        resolver.setModeNames(null)
    }

    @Test(expected=IllegalArgumentException)
    void testSetEmptyModeNames() {
        resolver.setModeNames(Collections.emptySet())
    }

    @Test(expected=IllegalArgumentException)
    void testSetInvalidModeName() {
        resolver.setModeNames(['foo'] as Set)
    }

    @Test
    void testResolveRolesWithHref() {

        def group = EasyMock.createStrictMock(Group)

        def href = 'https://api.stormpath.com/groups/foo'

        EasyMock.expect(group.href).andReturn(href)

        EasyMock.replay group

        def roleNames = resolver.resolveGrantedAuthorities(group)

        Assert.assertEquals 1, roleNames.size()
        def retrievedRole = roleNames.iterator().next()
        Assert.assertThat retrievedRole, instanceOf(GrantedAuthority.class)
        Assert.assertEquals href, retrievedRole.toString()

        EasyMock.verify group
    }

    @Test(expected=IllegalStateException)
    void testResolveRolesWithMissingHref() {

        def group = EasyMock.createStrictMock(Group)

        EasyMock.expect(group.href).andReturn null

        EasyMock.replay group

        try {
            resolver.resolveGrantedAuthorities(group)
        } finally {
            EasyMock.verify group
        }
    }

    @Test
    void testResolveRolesWithId() {

        def group = EasyMock.createStrictMock(Group)

        def href = 'https://api.stormpath.com/groups/foo'

        EasyMock.expect(group.href).andReturn(href)

        EasyMock.replay group

        resolver.modes = [DefaultGroupGrantedAuthorityResolver.Mode.ID] as Set
        def roleNames = resolver.resolveGrantedAuthorities(group)

        Assert.assertEquals 1, roleNames.size()
        def retrievedRole = roleNames.iterator().next()
        Assert.assertThat retrievedRole, instanceOf(GrantedAuthority.class)
        Assert.assertEquals 'foo', retrievedRole.toString()

        EasyMock.verify group
    }

    @Test
    void testResolveRolesWithIdAndInvalidHref() {

        def group = EasyMock.createStrictMock(Group)

        def href = 'whatever'

        EasyMock.expect(group.href).andReturn(href)

        EasyMock.replay group

        resolver.modes = [DefaultGroupGrantedAuthorityResolver.Mode.ID] as Set
        def roleNames = resolver.resolveGrantedAuthorities(group)

        Assert.assertNotNull roleNames
        Assert.assertTrue roleNames.isEmpty()

        EasyMock.verify group
    }

    @Test
    void testResolveRolesWithName() {

        def group = EasyMock.createStrictMock(Group)

        def href = 'https://api.stormpath.com/groups/foo'

        EasyMock.expect(group.href).andReturn(href)
        EasyMock.expect(group.name).andReturn('bar')

        EasyMock.replay group

        resolver.modes = [DefaultGroupGrantedAuthorityResolver.Mode.NAME] as Set
        def roleNames = resolver.resolveGrantedAuthorities(group)

        Assert.assertEquals 1, roleNames.size()
        def retrievedRole = roleNames.iterator().next()
        Assert.assertThat retrievedRole, instanceOf(GrantedAuthority.class)
        Assert.assertEquals 'bar', retrievedRole.toString()

        EasyMock.verify group
    }

}
