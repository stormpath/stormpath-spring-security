[![Build Status](https://api.travis-ci.org/stormpath/stormpath-spring-security.png?branch=master)](https://travis-ci.org/stormpath/stormpath-spring-security)

# Spring Security plugin for Stormpath #

Copyright &copy; 2013 Stormpath, Inc. and contributors. This project is open-source via the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).  

The `stormpath-spring-security` plugin allows a [Spring Security](http://projects.spring.io/spring-security/)-enabled application to use the [Stormpath](http://www.stormpath.com) User Management & Authentication service for all authentication and access control needs.

Pairing Spring Security with Stormpath gives you a full application security system complete with immediate user account support, authentication, account registration and password reset workflows, password security and more -
with little to no coding on your part.  

Usage documentation [is in the wiki](https://github.com/stormpath/stormpath-spring-security/wiki).

## Build Instructions ##

This project requires Maven 3 to build.  Run the following from a command prompt:

`mvn install`

## Change Log

### 0.3.0

- [Issue 5](https://github.com/stormpath/stormpath-spring-security/issues/5): Removed credentials from authentication token
- Upgraded Stormpath SDK dependency to latest release of 1.0.RC2

### 0.2.0

- Upgraded Stormpath SDK dependency to latest stable release of 0.9.2
- Added Permission support!  It is now possible to use Spring Security Granted Authorities as permissions for Stormpath Accounts or Groups by leveraging Stormpath's newly released [CustomData](http://docs.stormpath.com/rest/product-guide/#custom-data) feature.  You can add and remove permission to an Account or Group by modifying that account or group's CustomData resource.  For example:

	```java
	Account account = getAccount(); //lookup account

	//edit the permisssions assigned to the Account:
	new CustomDataPermissionsEditor(account.getCustomData())
    	.append("user:1234:edit")
	    .append("document:*")
    	.remove("printer:*:print");

	//persist the account's permission changes:
	account.save();
	```

	The same `CustomDataPermissionsEditor` can be used to assign permissions to Groups as well, and assumes 'transitive association': any permissions assigned to a Group are also 'inherited' to the Accounts in the Group.

	In other words, an account's total assigned permissions are any permissions assigned directly to the account, plus, all of the permissions assigned to any Group that contains the account.

	The `CustomDataPermissionsEditor` will save the permissions as a JSON list in the CustomData resource, under the default `springSecurityPermissions` field name, for example:

	```json
	{
    	... any other of your own custom data properties ...,

	    "springSecurityPermissions": [
    	    "perm1",
        	"perm2",
	        ...,
    	    "permN"
	    ]
	}
	```
	If you would like to change the default field name, you can call the `setFieldName` method:

	```java
	new CustomDataPermissionsEditor(account.getCustomData())
    	.setFieldName("whateverYouWantHere")
	    .append("user:1234:edit")
    	.append("document:*")
	    .remove("printer:*:print");
	```

	But you'll also need to update your `AuthenticationProvider`'s configuration to reflect the new name so it can function - the provider reads the same `CustomData` field, so they must be identical to ensure both read and write scenarios access the same field.  For example, in Spring xml configuration:

	```xml
	<bean id="groupPermissionResolver" class="com.stormpath.spring.security.provider.GroupCustomDataPermissionResolver">
		<property name="customDataFieldName" value="myApplicationPermissions" />
	</bean>
	<bean id="accountPermissionResolver" class="com.stormpath.spring.security.provider.AccountCustomDataPermissionResolver">
		<property name="customDataFieldName" value="myApplicationPermissions" />
	</bean>
	<bean id="authenticationProvider" class="com.stormpath.spring.security.provider.StormpathAuthenticationProvider">
    	<!-- etc... -->
		<property name="groupPermissionResolver" ref="groupPermissionResolver" />
    	<property name="accountPermissionResolver" ref="accountPermissionResolver" />
	</bean>
	```

- The `AuthenticationProvider` implementation now has a default `groupPermissionResolver` and `accountPermissionResolver` properties that leverage respective group or account `CustomData` to support permissions as described above.  Prior to this 0.5.0 release, there were no default implementations of these properties - you had to implement the interfaces yourself to support permissions.  Now Permissions are built in by default (although you could still provide your own custom implementations if you have custom needs of course).
- Added CacheManager/Cache bridging support.  This allows the Stormpath SDK to use the same caching mechanism that you're already using for Spring, simplifying cache configuration/setup.  For example:

	```xml
	<bean id="cacheManager" class="org.springframework.cache.support.SimpleCacheManager">
    	<property name="caches">
        	<set>
            	<bean class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean" p:name="com.stormpath.sdk.application.Application" />
	            <bean class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean" p:name="com.stormpath.sdk.account.Account" />
    	        <bean class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean" p:name="com.stormpath.sdk.group.Group" />
        	    <bean class="org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean" p:name="com.stormpath.sdk.directory.CustomData" />
	        </set>
    	</property>
	</bean>

	<!-- Stormpath integration -->
	<bean id="stormpathClient" class="com.stormpath.spring.security.client.ClientFactory" >
    	<!-- etc... -->
	    <property name="cacheManager" ref="cacheManager" />
	</bean>
	```

	If for some reason you *don't* want the Stormpath SDK to use Spring's caching mechanism, you can configure the `stormpathCacheManager` property (instead of the expected Spring-specific `cacheManager` property), which accepts a `com.stormpath.sdk.cache.CacheManager` instance instead:

	```xml
	<bean id="stormpathCacheManager" class="my.com.stormpath.sdk.cache.CacheManagerImplementation" />

	<!-- Stormpath integration -->
	<bean id="stormpathClient" class="com.stormpath.spring.security.client.ClientFactory" >
    	<!-- etc... -->
	    <property name="stormpathCacheManager" ref="stormpathCacheManager" />
	</bean>
	```

	But note that this approach requires you to set-up/configure two separate caching mechanisms.

	See ClientFactory `setCacheManager` and `setStormpathCacheManager` JavaDoc for more.

### 0.1.0

- First release