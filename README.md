[![Build Status](https://api.travis-ci.org/stormpath/stormpath-spring-security.png?branch=master)](https://travis-ci.org/stormpath/stormpath-spring-security)

# Spring Security plugin for Stormpath #

Copyright &copy; 2013 Stormpath, Inc. and contributors. This project is open-source via the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).  

The `stormpath-spring-security` plugin allows a [Spring Security](http://projects.spring.io/spring-security/)-enabled application to use the [Stormpath](http://www.stormpath.com) User Management & Authentication service for all authentication and access control needs.

Pairing Spring Security with Stormpath gives you a full application security system complete with immediate user account support, authentication, account registration and password reset workflows, password security and more -
with little to no coding on your part.  

## Build Instructions ##

This project requires Maven 3 to build.  Run the following from a command prompt:

`mvn install`

## Using the Plugin in your own Spring Security App ##

## Configuration ##

1. Add the stormpath-spring-security .jars to your application using Maven, Ant+Ivy, Grails, SBT or whatever
   maven-compatible tool you prefer:
   
    	<dependency>
            <groupId>com.stormpath.spring.security</groupId>
            <artifactId>stormpath-spring-security-core</artifactId>
            <version>0.1.0</version>
        </dependency>

2. Ensure you [have an API Key](http://www.stormpath.com/docs/quickstart/connect) so your application can communicate with Stormpath.  Store your API Key file somewhere secure (readable only by you), for example:

        /home/myhomedir/.stormpath/apiKey.properties

3. Configure your `spring-security.xml`. 

		<!-- # Replace this value with the file location from #2 above -->
		<beans:bean id="stormpathClient" class="com.stormpath.spring.security.client.ClientFactory" >
        	<beans:property name="apiKeyFileLocation" 	value="#{systemProperties['user.home']}/.stormpath/apiKey.properties" />
    	</beans:bean>

		<!-- By default, the Spring Security's DecisionManager is enforcing permissions to have the "ROLE_" 
		prefix. We need to change this to support any text -->
    	<beans:bean id="accessDecisionManager" class="org.springframework.security.access.vote.AffirmativeBased">
        	<beans:property name="decisionVoters">
            	<beans:list>
                	<beans:bean class="org.springframework.security.access.vote.RoleVoter">
                    	<beans:property name="rolePrefix" value=""/>
                	</beans:bean>
                	<beans:bean class="org.springframework.security.access.vote.AuthenticatedVoter"/>
	            </beans:list>
    	    </beans:property>
	    </beans:bean>

		<!-- Configure Spring Security with the Stormpath `AuthenticationProvider` -->
	    <beans:bean id="stormpathAuthenticationProvider" class="com.stormpath.spring.security.provider.StormpathAuthenticationProvider">
	        <beans:property name="client" ref="stormpathClient" />
    	    <beans:property name="applicationRestUrl" value="REPLACE_ME_WITH_YOUR_STORMPATH_APP_REST_URL"/>
	    </beans:bean>
	    
	    <http auto-config='true' access-decision-manager-ref="accessDecisionManager" >
    	    <intercept-url pattern="A_SECURED_RESOURCE" access="REPLACE_ME_WITH_YOUR_STORMPATH_GROUP_ALLOWED_TO_ACCESS_THIS_SECURED_RESOURCE />
        	<logout logout-url="/logout" logout-success-url="/logoutSuccess.jsp"/>
	    </http>

    	<authentication-manager>
        	<authentication-provider ref='stormpathAuthenticationProvider'/>
	    </authentication-manager>
        
4. Replace the `applicationRestUrl` value above with your
   [Application's Stormpath-specific REST URL](http://www.stormpath.com/docs/libraries/application-rest-url), for
   example:

		<beans:property name="applicationRestUrl" value="https://api.stormpath.com/v1/applications/someRandomIdHereReplaceMe"/>
        
5. Replace the secured `intercept-url` info above with your
   [Stormpath Group REST URL](http://docs.stormpath.com/java/product-guide/#groups) allowed to access your secured resource, for example:

        <intercept-url pattern="/secured/*" access="https://api.stormpath.com/v1/groups/d8UDkz9QPcn2z73j93m6Z" />
        
## Authentication ##

In a web application, you can use one of Spring Security's existing authentication filters to automatically handle authentication requests (e.g. [UsernamePasswordAuthenticationFilter](http://docs.spring.io/spring-security/site/docs/3.1.x/apidocs/org/springframework/security/web/authentication/UsernamePasswordAuthenticationFilter.html), [BasicAuthenticationFilter](http://docs.spring.io/spring-security/site/docs/3.1.x/apidocs/org/springframework/security/web/authentication/www/BasicAuthenticationFilter.html)) and you won't have to code anything; authentication attempts will be processed as expected by the `StormpathAuthenticationProvider` automatically.

However, if you want to execute the authentication attempt yourself (e.g. you have a more complex login form or UI technology) or your application is not a web application, this is easy as well:

Create a Spring Security `UsernamePasswordAuthenticationToken` to wrap your user's submitted username and password and then call Spring Security's `AuthenticationManager`'s `authenticate` method:
	
		//Field
		private AuthenticationManager am = new MyAuthenticationManager();
	
		...
		...
	
		//authenticate
		String username = //get from a form or request parameter (OVER SSL!)
		String password = //get from a form or request parameter (OVER SSL!)

    	UsernamePasswordToken request = new UsernamePasswordToken(username, password);
		Authentication result = am.authenticate(request);
		SecurityContextHolder.getContext().setAuthentication(result);
	
		//From that point on, the user is considered to be authenticated


that's it, a standard Spring Security authentication attempt.  If the authentication attempt fails, an `AuthenticationException` will be thrown as expected.

In Stormpath, you can add, remove and enable accounts for your application and Spring Security will reflect these changes instantly!

## Authorization ##

After an account has authenticated, you can perform standard Spring Security roles checks, e.g. `<intercept-url pattern="/**" access="hasRole('A_ROLE_NAME')" />` or `<intercept-url pattern="/**" access="isAuthenticated()" />`.

### Role checks ###

Spring Security's role concept in Stormpath is represented as a Stormpath [Group](http://docs.stormpath.com/java/product-guide/#groups).

#### Role checks with the Group `href` ####

The recommended way to perform a Spring Security role check is to use the Stormpath group's `href` property as the Spring Security role 'name'.  

While it is possible (and maybe more intuitive) to use the Group name for the role check, this secondary approach is not enabled by default and not recommended for most usages:  role names can potentially change over time (for example, someone changes the Group name in the Stormpath administration console without telling you).  If you code a role check in your source code, and that role name changes in the future, your role checks will likely fail!

Instead, it is recommended to perform role checks with a stable identifier.

You can use a Stormpath Group's `href` property as the role 'name' and check that:

	    @PreAuthorize("hasAuthority('A_SPECIFIC_GROUP_HREF')")
    	public Account post(Account account, double amount) {
    		//do something
	    }

#### Role checks with the Group `name` ####

If you still want to use a Stormpath Group's name as the Spring Security role name for role checks - perhaps because you have a high level of confidence that no one will change group names once your software is written - you can still use the Group name if you wish by adding a little configuration.

In your `spring-security.xml`, you can set the supported naming modes of what will be represented as a Spring Security role:

		<beans:bean id="groupGrantedAuthorityResolver" class="com.stormpath.spring.security.provider.DefaultGroupGrantedAuthorityResolver">
        	<beans:property name="modeNames" value="NAME" />
	    </beans:bean>

		<beans:bean id="stormpathAuthenticationProvider" class="com.stormpath.spring.security.provider.StormpathAuthenticationProvider">
        	...
			<beans:property name="groupGrantedAuthorityResolver" ref="groupGrantedAuthorityResolver" />
	    </beans:bean>

The modes (or mode names) allow you to specify which Group properties Spring Security will consider as role 'names'.  The default is `href`, but you can specify more than one if desired.  The supported modes are the following:

* *HREF*: the Group's `href` property will be considered a Spring Security role name.  This is the default mode if not configured otherwise.  Allows a Spring Security role check to look like the following: `authentication.getAuthorities().contains(new SimpleGrantedAuthority(group.getHref()))`.
* *NAME*: the Group's `name` property will be considered a Spring Security role name.  This allows a Spring Security role check to look like the following: `authentication.getAuthorities().contains(new SimpleGrantedAuthority(group.getName()))`.  This however has the downside that if you (or someone else on your team or in your company) changes the Group's name, you will have to update your role check code to reflect the new names (otherwise the existing checks are very likely to fail).
* *ID*: the Group's unique id will be considered a Spring Security role name.  The unique id is the id at the end of the Group's HREF url.  This is a deprecated mode and should ideally not be used in new applications.

#### The GroupGrantedAuthorityResolver Interface ####

If the above default role name resolution logic does not meet your needs or if you want full customization of how a Stormpath Group resolves to one or more Spring Security role names, you can implement the `GroupGrantedAuthorityResolver` interface and configure the implementation on the StormpathAuthenticationProvider:

		<beans:bean id="groupGrantedAuthorityResolver" class="com.mycompany.my.impl.MyGroupGrantedAuthorityResolver">
			...
	    </beans:bean>

		<beans:bean id="stormpathAuthenticationProvider" class="com.stormpath.spring.security.provider.StormpathAuthenticationProvider">
        	...
			<beans:property name="groupGrantedAuthorityResolver" ref="groupGrantedAuthorityResolver" />
	    </beans:bean>

