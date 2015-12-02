package com.vedri.mtp.frontend.web.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SecurityUtilsTest {

	@Test
	public void testgetCurrentUserLogin() {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
		SecurityContextHolder.setContext(securityContext);
		String login = SecurityUtils.getCurrentUserLogin();
		Assert.assertEquals(login, "admin");
	}

	@Test
	public void testIsAuthenticated() {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
		SecurityContextHolder.setContext(securityContext);
		boolean isAuthenticated = SecurityUtils.isAuthenticated();
		Assert.assertTrue(isAuthenticated);
	}

	@Test
	public void testAnonymousIsNotAuthenticated() {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
		securityContext
				.setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities));
		SecurityContextHolder.setContext(securityContext);
		boolean isAuthenticated = SecurityUtils.isAuthenticated();
		Assert.assertFalse(isAuthenticated);
	}
}
