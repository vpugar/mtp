package com.vedri.mtp.frontend.user;

import java.util.Optional;

import com.vedri.mtp.core.user.User;

public interface UserManager {

	Optional<User> activateRegistration(String key);

	Optional<User> completePasswordReset(String newPassword, String key);

	Optional<User> requestPasswordReset(String mail);

	User createUserInformation(String login, String password, String firstName, String lastName, String email,
			String langKey);

	void updateUserInformation(String firstName, String lastName, String email, String langKey);

	void changePassword(String password);

	Optional<User> getUserWithAuthoritiesByLogin(String login);

	User getUserWithAuthorities();
}
