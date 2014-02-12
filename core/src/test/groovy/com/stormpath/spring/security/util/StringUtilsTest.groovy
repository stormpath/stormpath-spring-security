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
package com.stormpath.spring.security.util

import org.junit.Test

import static org.junit.Assert.*

class StringUtilsTest {

    @Test
    public void splitWithNullInput() {
        String line = null;
        String[] split = StringUtils.split(line);
        assertNull(split);
    }

    @Test
    public void splitWithCommas() {
        String line = "shall,we,play,a,game?";
        String[] split = StringUtils.split(line);
        assertNotNull(split);
        assertTrue(split.length == 5);
        assertEquals("shall", split[0]);
        assertEquals("we", split[1]);
        assertEquals("play", split[2]);
        assertEquals("a", split[3]);
        assertEquals("game?", split[4]);
    }

    @Test
    public void splitWithCommasAndSpaces() {
        String line = "shall,we ,    play, a,game?";
        String[] split = StringUtils.split(line);
        assertNotNull(split);
        assertTrue(split.length == 5);
        assertEquals("shall", split[0]);
        assertEquals("we", split[1]);
        assertEquals("play", split[2]);
        assertEquals("a", split[3]);
        assertEquals("game?", split[4]);
    }

    @Test
    public void splitWithQuotedCommasAndSpaces() {
        String line = "shall, \"we, play\", a, game?";
        String[] split = StringUtils.split(line);
        assertNotNull(split);
        assertTrue(split.length == 4);
        assertEquals("shall", split[0]);
        assertEquals("we, play", split[1]);
        assertEquals("a", split[2]);
        assertEquals("game?", split[3]);
    }

    @Test
    public void splitTestWithQuotedCommas() {
        String line = "authc, test[blah], test[\"1,2,3\"], test[]";
        String[] split = StringUtils.split(line);
        assertNotNull(split);
        assertTrue(split.length == 4);
        assertEquals("authc", split[0]);
        assertEquals("test[blah]", split[1]);
        assertEquals("test[1,2,3]", split[2]);
        assertEquals("test[]", split[3]);
    }

    @Test
    public void splitWithQuotedCommasAndSpacesAndEscapedQuotes() {
        String line = "shall, \"\"\"we, play\", a, \"\"\"game?";
        String[] split = StringUtils.split(line);
        assertNotNull(split);
        assertTrue(split.length == 4);
        assertEquals("shall", split[0]);
        assertEquals("\"we, play", split[1]);
        assertEquals("a", split[2]);
        assertEquals("\"game?", split[3]);
    }
}

