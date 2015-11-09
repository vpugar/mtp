package com.vedri.mtp.core.user;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import com.datastax.driver.mapping.annotations.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.vedri.mtp.core.support.cassandra.ColumnUtils;
import org.hibernate.validator.constraints.Email;

/**
 * A user.
 */
@Getter
@Setter
@Table(name = "user")
public class User implements Serializable {

	@PartitionKey
	private String id;

	@NotNull
	@Pattern(regexp = "^[a-z0-9]*$|(anonymousUser)")
	@Size(min = 1, max = 50)
	private String login;

	@JsonIgnore
	@NotNull
	@Size(min = 60, max = 60)
	private String password;

	@Size(max = 50)
	private String firstName;

	@Size(max = 50)
	private String lastName;

	@Email
	@Size(max = 100)
	private String email;

	private boolean activated = false;

	@Size(min = 2, max = 5)
	@Column(name = "lang_key")
	private String langKey;

	@Size(max = 20)
	@Column(name = "activation_key")
	@JsonIgnore
	private String activationKey;

	@Size(max = 20)
	@Column(name = "reset_key")
	private String resetKey;

	@Column(name = "reset_date")
	private Date resetDate;

	@JsonIgnore
	private Set<String> authorities = new HashSet<>();

	public enum Fields {
		id, login, password, firstName, lastName, email, activated, langKey, activationKey, resetKey, resetDate, authorities;

		public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		User user = (User) o;

		if (!login.equals(user.login)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return login.hashCode();
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
				.add("activationKey", activationKey)
				.add("resetKey", resetKey)
				.add("resetDate", resetDate)
				.add("authorities", authorities)
				.toString();
	}
}
