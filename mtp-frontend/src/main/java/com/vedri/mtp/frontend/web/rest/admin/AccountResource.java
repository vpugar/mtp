package com.vedri.mtp.frontend.web.rest.admin;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.vedri.mtp.frontend.user.UserManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.codahale.metrics.annotation.Timed;
import com.vedri.mtp.frontend.user.dao.CassandraUserDao;
import com.vedri.mtp.core.user.User;
import com.vedri.mtp.frontend.web.security.SecurityUtils;

/**
 * REST controller for managing the current user's account.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class AccountResource {

	private final CassandraUserDao cassandraUserDao;

	private final UserManager userManager;

	@Autowired
	public AccountResource(CassandraUserDao cassandraUserDao, UserManager userManager) {
		this.cassandraUserDao = cassandraUserDao;
		this.userManager = userManager;
	}

	/**
	 * POST /register -> register the user.
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<?> registerAccount(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) {
		return cassandraUserDao.findOneByLogin(userDTO.getLogin())
				.map(user -> new ResponseEntity<>("login already in use", HttpStatus.BAD_REQUEST))
				.orElseGet(() -> cassandraUserDao.findOneByEmail(userDTO.getEmail())
                        .map(user -> new ResponseEntity<>("e-mail address already in use", HttpStatus.BAD_REQUEST))
                        .orElseGet(() -> {
                            final User user = userManager.createUserInformation(userDTO.getLogin(),
                                    userDTO.getPassword(),
                                    userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail().toLowerCase(),
                                    userDTO.getLangKey());
                            String baseUrl = request.getScheme() + // "http"
                                    "://" + // "://"
                                    request.getServerName() + // "myhost"
                                    ":" + // ":"
                                    request.getServerPort(); // "80"

                            return new ResponseEntity<>(HttpStatus.CREATED);
                        }));
	}

	/**
	 * GET /activate -> activate the registered user.
	 */
	@RequestMapping(value = "/activate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
		return Optional.ofNullable(userManager.activateRegistration(key))
				.map(user -> new ResponseEntity<String>(HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * GET /authenticate -> check if the user is authenticated, and return its login.
	 */
	@RequestMapping(value = "/authenticate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public String isAuthenticated(HttpServletRequest request) {
		log.debug("REST request to check if the current user is authenticated");
		return request.getRemoteUser();
	}

	/**
	 * GET /account -> get the current user.
	 */
	@RequestMapping(value = "/account", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UserDTO> getAccount() {
		return Optional.ofNullable(userManager.getUserWithAuthorities())
				.map(user -> new ResponseEntity<>(new UserDTO(user), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST /account -> update the current user information.
	 */
	@RequestMapping(value = "/account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> saveAccount(@RequestBody UserDTO userDTO) {
		return cassandraUserDao
				.findOneByLogin(userDTO.getLogin())
				.filter(u -> u.getLogin().equals(SecurityUtils.getCurrentUserLogin()))
				.map(u -> {
                    userManager.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                            userDTO.getLangKey());
                    return new ResponseEntity<String>(HttpStatus.OK);
                })
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	/**
	 * POST /change_password -> changes the current user's password
	 */
	@RequestMapping(value = "/account/change_password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> changePassword(@RequestBody String password) {
		if (!checkPasswordLength(password)) {
			return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
		}
		userManager.changePassword(password);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/account/reset_password/init", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<?> requestPasswordReset(@RequestBody String mail, HttpServletRequest request) {
		return userManager.requestPasswordReset(mail)
				.map(user -> {
                    String baseUrl = request.getScheme() +
                            "://" +
                            request.getServerName() +
                            ":" +
                            request.getServerPort();
                    return new ResponseEntity<>("e-mail was sent", HttpStatus.OK);
                }).orElse(new ResponseEntity<>("e-mail address not registered", HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/account/reset_password/finish", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> finishPasswordReset(@RequestBody KeyAndPasswordDTO keyAndPassword) {
		if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
			return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
		}
		return userManager.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
				.map(user -> new ResponseEntity<String>(HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	private boolean checkPasswordLength(String password) {
		return (!StringUtils.isEmpty(password) &&
				password.length() >= UserDTO.PASSWORD_MIN_LENGTH &&
				password.length() <= UserDTO.PASSWORD_MAX_LENGTH);
	}
}
