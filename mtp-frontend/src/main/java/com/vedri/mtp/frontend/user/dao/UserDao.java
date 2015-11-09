package com.vedri.mtp.frontend.user.dao;

import java.util.List;
import java.util.Optional;

import com.vedri.mtp.core.user.User;

public interface UserDao {

	Optional<User> findOne(String id);

	Optional<User> findOneByActivationKey(String activationKeyVal);

	Optional<User> findOneByResetKey(String resetKeyVal);

	Optional<User> findOneByEmail(String emailVal);

	Optional<User> findOneByLogin(String loginVal);

	List<User> findAll();

	User save(User user);

	void delete(User user);
}
