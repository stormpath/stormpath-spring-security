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
package com.stormpath.spring.security.authz;

import java.util.Set;

/**
 * A {@code GrantedAuthoritiesEditor} allows one to read and manipulate (append or remove) granted authority on an underlying data
 * structure.
 * <p/>
 * The primary default implementation of this interface is the {@link CustomDataGrantedAuthoritiesEditor}, which reads and
 * modifies a Set of granted authorities stored in an {@link com.stormpath.sdk.directory.CustomData CustomData} instance.
 *
 * @see CustomDataGrantedAuthoritiesEditor
 * @since 0.1.1
 */
public interface GrantedAuthoritiesEditor {

    /**
     * Adds a Spring Security granted authority String to the associated Set of granted authority strings.
     *
     * @param grantedAuthority the granted authority string to add to the associated Set of granted authority strings.
     * @return this object for method chaining.
     */
    GrantedAuthoritiesEditor append(String grantedAuthority);

    /**
     * Removes the specified Spring Security granted authority String from the associated Set of granted authority Strings.
     *
     * @param grantedAuthority the granted authority string to remove from the associated Set of granted authority strings.
     * @return this object for method chaining.
     */
    GrantedAuthoritiesEditor remove(String grantedAuthority);

    /**
     * Returns a read-only (immutable) view of the stored granted authority strings.  An immutable empty set will be returned
     * if there are not any currently stored.
     *
     * @return an immutable view of the stored granted authority strings.
     */
    Set<String> getGrantedAuthorityStrings();
}
