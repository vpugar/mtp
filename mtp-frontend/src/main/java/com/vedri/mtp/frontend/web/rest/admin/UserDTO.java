package com.vedri.mtp.frontend.web.rest.admin;

import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import com.vedri.mtp.core.user.User;

/**
 * A DTO representing a user, with his authorities.
 */
@Getter
@Setter
public class UserDTO {

	public static final int PASSWORD_MIN_LENGTH = 5;
	public static final int PASSWORD_MAX_LENGTH = 100;

	@Pattern(regexp = "^[a-z0-9]*$")
	@NotNull
	@Size(min = 1, max = 50)
	private String login;

	@NotNull
	@Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
	private String password;

	@Size(max = 50)
	private String firstName;

	@Size(max = 50)
	private String lastName;

	@Email
	@Size(min = 5, max = 100)
	private String email;

	private boolean activated = false;

	@Size(min = 2, max = 5)
	private String langKey;

	private Set<String> authorities;

	public UserDTO() {
	}

	public UserDTO(User user) {
		this(user.getLogin(), null, user.getFirstName(), user.getLastName(),
				user.getEmail(), user.isActivated(), user.getLangKey(),
				user.getAuthorities());
	}

	public UserDTO(String login, String password, String firstName, String lastName,
			String email, boolean activated, String langKey, Set<String> authorities) {

		this.login = login;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.activated = activated;
		this.langKey = langKey;
		this.authorities = authorities;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("login", login)
				.add("password", password)
				.add("firstName", firstName)
				.add("lastName", lastName)
				.add("email", email)
				.add("activated", activated)
				.add("langKey", langKey)
				.add("authorities", authorities)
				.toString();
	}
}
