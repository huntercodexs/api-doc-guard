package com.huntercodexs.sample.config.oauth2.filter;

import com.huntercodexs.sample.config.oauth2.security.CustomAuthenticationManager;
import com.huntercodexs.sample.config.oauth2.security.CustomClientDetailsService;
import com.huntercodexs.sample.config.oauth2.security.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableAuthorizationServer
@SuppressWarnings("deprecation")
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${oauth.server.custom.endpoint}")
    private String oauth2CustomEndpoint;
	
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserApprovalHandler userApprovalHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;

    @Autowired
    private CustomClientDetailsService customClientDetailsService;
    
	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
	
    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }
    
    @Bean
    @Autowired
    public UserApprovalHandler userApprovalHandler(TokenStore tokenStore) {
        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
        handler.setTokenStore(tokenStore);
        handler.setRequestFactory(new DefaultOAuth2RequestFactory(customClientDetailsService));
        handler.setClientDetailsService(customClientDetailsService);

        return handler;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .pathMapping("/oauth/token", oauth2CustomEndpoint+"/token" )
                .pathMapping("/oauth/check_token", oauth2CustomEndpoint+"/check_token")
                .tokenStore(this.tokenStore)
                .userApprovalHandler(this.userApprovalHandler)
                .authenticationManager(customAuthenticationManager)
                .userDetailsService(customUserDetailsService);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(customClientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security
                .allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .passwordEncoder(this.passwordEncoder);
    }
    
    @Configuration
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Value("${api.prefix}")
        private String apiPrefix;

        @Value("${springdoc.swagger-ui.path}")
        private String swaggerCustomPath;

        @Value("${springdoc.api-docs.path}")
        private String apiCustomDocsPath;

        @Override
        public void configure(final HttpSecurity http) throws Exception {
            http.authorizeRequests()

                /*NOTE: The routes below should be edited accord the current needed*/

                /*APPLICATION ROUTES TO SERVICES*/
                .antMatchers(apiPrefix+"/users/**").authenticated()

                /*OTHERS APPLICATION ROUTES TO SERVICES*/
                .antMatchers(apiPrefix+"/others/**").authenticated()

                /*APPLICATION ROUTES ALLOWED TO ALL*/
                .antMatchers(apiPrefix+"/allowed/**").authenticated()

                /*NOTE: Is not needed changes the routes below*/

                /*CUSTOM ROUTES*/
                .antMatchers(swaggerCustomPath).permitAll()
                .antMatchers(swaggerCustomPath+"/swagger-ui/**.html").permitAll()
                .antMatchers(apiPrefix+swaggerCustomPath).permitAll()
                .antMatchers(apiPrefix+swaggerCustomPath+"/swagger-ui/**.html").permitAll()
                .antMatchers(swaggerCustomizedPath()).permitAll()
                .antMatchers(apiPrefix+ swaggerCustomizedPath()).permitAll()

                /*SWAGGER ROUTES*/
                .antMatchers("/swagger/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/api-docs/**").permitAll()
                .antMatchers("/api-docs.yaml").permitAll()

                /*SWAGGER ROUTES WITH PREFIX*/
                .antMatchers(apiPrefix+"/swagger/**").permitAll()
                .antMatchers(apiPrefix+"/swagger-ui/**").permitAll()
                .antMatchers(apiPrefix+"/api-docs/**").permitAll()
                .antMatchers(apiPrefix+"/api-docs.yaml").permitAll()

                /*API-DOC-GUARD ROUTES*/
                .antMatchers("/").permitAll()
                .antMatchers("/doc-protect/**").permitAll()
                .antMatchers("/api-doc-guard/**").permitAll()
                .antMatchers("/api-docs-guard/**").permitAll()
                .antMatchers(apiCustomDocsPath).permitAll()
                .antMatchers(apiPrefix+"/doc-protect/**").permitAll()
                .antMatchers(apiPrefix+"/api-doc-guard/**").permitAll()
                .antMatchers(apiPrefix+"/api-docs-guard/**").permitAll()
                .antMatchers(apiPrefix+apiCustomDocsPath).permitAll()

                /*ACTUATOR ROUTES*/
                .antMatchers("/actuator/**").permitAll().anyRequest().authenticated();

       }

       private String swaggerCustomizedPath() {
           String[] swaggerSetCustomPath = swaggerCustomPath.split("/");
           StringBuilder swaggerCustomizedPath = new StringBuilder();
           if (swaggerSetCustomPath.length > 0) {
               for (int i = 0; i < swaggerSetCustomPath.length-1; i++) {
                   if (!swaggerSetCustomPath[i].equals("")) {
                       swaggerCustomizedPath.append("/").append(swaggerSetCustomPath[i]);
                   }
               }
           }
           swaggerCustomizedPath.append("/swagger-ui/index.html");

           System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
           System.out.println("Swagger Customized Path is: " + swaggerCustomizedPath);
           System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

           return swaggerCustomizedPath.toString();
       }
       
    }

}