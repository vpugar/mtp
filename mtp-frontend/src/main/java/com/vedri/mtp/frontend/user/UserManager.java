package com.vedri.mtp.frontend.user;

import java.time.ZonedDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vedri.mtp.frontend.user.dao.CassandraUserDao;
import com.vedri.mtp.frontend.web.security.AuthoritiesConstants;
import com.vedri.mtp.frontend.web.security.SecurityUtils;
import com.vedri.mtp.frontend.support.random.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
public class UserManager {

	private final Logger log = LoggerFactory.getLogger(UserManager.class);

	private final PasswordEncoder passwordEncoder;

	private final CassandraUserDao cassandraUserDao;

	@Autowired
	public UserManager(PasswordEncoder passwordEncoder, CassandraUserDao cassandraUserDao) {
		this.passwordEncoder = passwordEncoder;
		this.cassandraUserDao = cassandraUserDao;
	}

	public Optional<User> activateRegistration(String key) {
		log.debug("Activating user for activation key {}", key);
		cassandraUserDao.findOneByActivationKey(key)
				.map(user -> {
                    // activate given user for the registration key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    cassandraUserDao.save(user);
                    log.debug("Activated user: {}", user);
                    return user;
                });
		return Optional.empty();
	}

	public Optional<User> completePasswordReset(String newPassword, String key) {
		log.debug("Reset user password for reset key {}", key);

		return cassandraUserDao.findOneByResetKey(key)
				.filter(user -> {
                    ZonedDateTime oneDayAgo = ZonedDateTime.now().minusHours(24);
                    return user.getResetDate().after(Date.from(oneDayAgo.toInstant()));
                })
				.map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    cassandraUserDao.save(user);
                    return user;
                });
	}

	public Optional<User> requestPasswordReset(String mail) {
		return cassandraUserDao.findOneByEmail(mail)
				.filter(user -> user.getActivated())
				.map(user -> {
                    user.setResetKey(RandomUtil.generateResetKey());
                    user.setResetDate(new Date());
                    cassandraUserDao.save(user);
                    return user;
                });
	}

	public User createUserInformation(String login, String password, String firstName, String lastName, String email,
			String langKey) {

		User newUser = new User();
		newUser.setId(UUID.randomUUID().toString());
		Set<String> authorities = new HashSet<>();
		String encryptedPassword = passwordEncoder.encode(password);
		newUser.setLogin(login);
		// new user gets initially a generated password
		newUser.setPassword(encryptedPassword);
		newUser.setFirstName(firstName);
		newUser.setLastName(lastName);
		newUser.setEmail(email);
		newUser.setLangKey(langKey);
		// new user is not active
		newUser.setActivated(false);
		// new user gets registration key
		newUser.setActivationKey(RandomUtil.generateActivationKey());
		authorities.add(AuthoritiesConstants.USER);
		newUser.setAuthorities(authorities);
		cassandraUserDao.save(newUser);
		log.debug("Created Information for User: {}", newUser);
		return newUser;
	}

	public void updateUserInformation(String firstName, String lastName, String email, String langKey) {
		cassandraUserDao.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
			u.setFirstName(firstName);
			u.setLastName(lastName);
			u.setEmail(email);
			u.setLangKey(langKey);
			cassandraUserDao.save(u);
			log.debug("Changed Information for User: {}", u);
		});
	}

	public void changePassword(String password) {
		cassandraUserDao.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
			String encryptedPassword = passwordEncoder.encode(password);
			u.setPassword(encryptedPassword);
			cassandraUserDao.save(u);
			log.debug("Changed password for User: {}", u);
		});
	}

	public Optional<User> getUserWithAuthoritiesByLogin(String login) {
		return cassandraUserDao.findOneByLogin(login).map(u -> {
			u.getAuthorities().size();
			return u;
		});
	}

	public User getUserWithAuthorities() {
		User user = cassandraUserDao.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		user.getAuthorities().size(); // eagerly load the association
		return user;
	}
}
