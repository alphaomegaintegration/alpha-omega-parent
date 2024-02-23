package com.alpha.omega.security.authentication;

import com.alpha.omega.security.authorization.AuthorizationService;
import com.alpha.omega.security.model.UserProfile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alpha.omega.security.utils.AOSecurityUtils.parseBasicAuthString;

public class InMemoryBasicRxAuthenticationManager  implements ReactiveAuthenticationManager {

	private static Logger logger = LogManager.getLogger(InMemoryBasicRxAuthenticationManager.class);

	private Collection<? extends UserDetails> userDetailsCollection;
	private InMemoryUserDetailsManager inMemoryUserDetailsManager;
	private DaoAuthenticationProvider daoAuthenticationProvider;
	private AuthorizationService authorizationService;
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/*
	https://stackoverflow.com/questions/53595420/correct-way-of-throwing-exceptions-with-reactor

	InMemoryUserDetailsManager
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	 */

	/*
	Create the passwords as basic auth credentials
	 */

	public InMemoryBasicRxAuthenticationManager(Collection<? extends UserDetails> userDetailsCollection) {
		this.userDetailsCollection = userDetailsCollection;
	}

	@PostConstruct
	public void init(){
		inMemoryUserDetailsManager = new InMemoryUserDetailsManager(userDetailsCollection.stream().map(usr -> usr).collect(Collectors.toSet()));
		daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
		daoAuthenticationProvider.setUserDetailsService(inMemoryUserDetailsManager);
		daoAuthenticationProvider.setUserDetailsPasswordService(inMemoryUserDetailsManager);
	}

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		logger.debug("Got authentication name  => {}",authentication.getName());
		return Mono.just(authentication);
/*		return Mono.just(authentication)
				.map(getAuthenticationUsernamePasswordAuthenticationTokenFunction())
				//.doOnNext(token -> logger.info("What are the creds here? => {} name here {}",token.getCredentials().toString(),token.getName()))
				.map(auth -> daoAuthenticationProvider.authenticate(auth))
				.map(auth -> extractUserProfile(authentication))
				.flatMap(userProfile -> Mono.zip(Mono.just(userProfile),
						Mono.just(authorizationResponseFromUserProfile(authorizationService).apply(userProfile)),
						userProfileAuthenticationFromAuthResponse()));*/
	}

	private Function<Authentication, UsernamePasswordAuthenticationToken> getAuthenticationUsernamePasswordAuthenticationTokenFunction() {
		return auth -> new UsernamePasswordAuthenticationToken(((UserProfile) auth.getPrincipal()).getName(),
				parseBasicAuthString((String) auth.getCredentials()).getT2());
	}

	private UserProfile extractUserProfile(Authentication authentication) {
		PreAuthenticationPrincipal principal = (PreAuthenticationPrincipal)authentication;
		UserProfile userProfile = (UserProfile) (principal.getPrincipal());
		return userProfile;
	}

	private String getCredentials(Authentication auth) {
		return (String) ((PreAuthenticationPrincipal) auth).getCredentials();
	}

}
