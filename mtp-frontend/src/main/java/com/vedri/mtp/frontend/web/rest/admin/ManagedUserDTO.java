package com.vedri.mtp.frontend.web.rest.admin;

import com.google.common.base.Objects;
import com.vedri.mtp.core.user.User;
import lombok.Getter;
import lombok.Setter;

/**
 * A DTO extending the UserDTO, which is meant to be used in the user management UI.
 */
@Getter
@Setter
public class ManagedUserDTO extends UserDTO {

	private String id;

	public ManagedUserDTO() {
	}

	public ManagedUserDTO(User user) {
		super(user);
		this.id = user.getId();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.toString();
	}
}
