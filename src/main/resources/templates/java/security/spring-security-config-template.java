package ${packageName}.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private static final String[] IGNORE_URLS = { "/logout", "/logout**", "/health**", "/health", "logout",
			"logout**", "/login", "/login*" };

	@Value("#[[${csrfEnabled:true}]]#")
	private boolean csrfEnabled;

	@Value("#[[${spring.profiles.active:local}]]#")
	private String environment;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
	  /*
		if ("cloud".equalsIgnoreCase(environment)) {
			http	
			    .requiresChannel()
			      .antMatchers("/login*").requiresSecure()
			    .and() 
			    .csrf().csrfTokenRepository(csrfTokenRepo()).ignoringAntMatchers(IGNORE_URLS).and()
					.addFilterAfter(siteminderFilter(), RequestHeaderAuthenticationFilter.class)
					.addFilterAfter(new ClearSessionOnSMHeaderChange(), SecurityContextPersistenceFilter.class)
					.authorizeRequests()
					.antMatchers(IGNORE_URLS).permitAll()
					.antMatchers("/**").hasRole("USER").and().formLogin().and().logout()
					.invalidateHttpSession(true).deleteCookies("JSESSIONID")
					.logoutSuccessHandler(new CustomLogoutSuccessHandler())
					
					
					;
		} else {
			http
				//.requiresChannel().anyRequest().requiresSecure()
				//.and()
				  .csrf().disable().addFilterAfter(siteminderFilter(), RequestHeaderAuthenticationFilter.class)
					.addFilterAfter(new ClearSessionOnSMHeaderChange(), SecurityContextPersistenceFilter.class)
					.authorizeRequests()
					.antMatchers(IGNORE_URLS).permitAll()
					.antMatchers("/**").hasRole("USER").and().httpBasic().and().logout()
					.invalidateHttpSession(true).deleteCookies("JSESSIONID")
					.logoutSuccessHandler(new CustomLogoutSuccessHandler());
		}
		*/
		httpSecurity
	    .csrf(AbstractHttpConfigurer::disable)
	    .addFilterAfter(siteminderFilter(), RequestHeaderAuthenticationFilter.class)
	    .addFilterAfter(new ClearSessionOnSMHeaderChange(), SecurityContextHolderFilter.class)
	    .authorizeRequests(authorize -> authorize
	    		.requestMatchers(IGNORE_URLS).permitAll()
	            .requestMatchers("/**").hasAnyRole("ADMIN", "USER")	        
	    )
	    .httpBasic(Customizer.withDefaults())
	    .logout(logout -> logout
	        .invalidateHttpSession(true)
	        .deleteCookies("JSESSIONID")
	        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
	    );
		return httpSecurity.build();
	}
    
    private CsrfTokenRepository csrfTokenRepo() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("x-csrf-token");
        return repository;
    }

    @Bean(name = "siteminderFilter")
	public RequestHeaderAuthenticationFilter siteminderFilter() throws Exception {
		ExtendedSiteMinderFilter esm = new ExtendedSiteMinderFilter();
		esm.setAuthenticationManager(authenticationManager());
		esm.setExceptionIfHeaderMissing(false);
		return esm;
	}

	@Bean 
	protected AuthenticationManager authenticationManager() throws Exception{
		final List<AuthenticationProvider> providers = new ArrayList<>();
		providers.add(springAuthenticationProvider());
		return new ProviderManager(providers);
	}
	
	public class ExtendedSiteMinderFilter extends RequestHeaderAuthenticationFilter{
		@Override
		protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
			String userPrincipal = request.getHeader("SM_USER");
			return userPrincipal;
		}
	}
	
	@Bean
	public SpringAuthenticationProvider springAuthenticationProvider() {
		return new SpringAuthenticationProvider();
	}

	@Bean
	public ClearSessionOnSMHeaderChange clearSessionOnSMHeaderChange() {
		return new ClearSessionOnSMHeaderChange();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}