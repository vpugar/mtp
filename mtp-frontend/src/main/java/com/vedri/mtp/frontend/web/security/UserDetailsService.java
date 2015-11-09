package com.vedri.mtp.frontend.web.security;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vedri.mtp.frontend.user.dao.CassandraUserDao;
import com.vedri.mtp.core.user.User;

/**
 * Authenticate a user from the database.
 */
@Slf4j
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	private final CassandraUserDao cassandraUserDao;

	@Autowired
	public UserDetailsService(CassandraUserDao cassandraUserDao) {
		this.cassandraUserDao = cassandraUserDao;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String login) {
		log.debug("Authenticating {}", login);
		String lowercaseLogin = login.toLowerCase();
		Optional<User> userFromDatabase = cassandraUserDao.findOneByLogin(lowercaseLogin);
		return userFromDatabase.map(user -> {
			if (!user.isActivated()) {
				throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
			}
			Set<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
					.map(authority -> new SimpleGrantedAuthority(authority))
					.collect(Collectors.toSet());
			return new CustomUserDetails(user.getId(), lowercaseLogin,
					user.getPassword(),
					grantedAuthorities, true, true, true, true);
		}).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " +
				"database"));
	}
}
