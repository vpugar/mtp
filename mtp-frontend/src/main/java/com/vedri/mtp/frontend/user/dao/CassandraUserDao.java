package com.vedri.mtp.frontend.user.dao;

import static com.vedri.mtp.core.user.User.Fields.*;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.vedri.mtp.core.user.User;

/**
 * Cassandra repository for the User entity.
 */
@Repository
public class CassandraUserDao implements UserDao {

	private final Session session;

	private Mapper<User> mapper;

	private PreparedStatement findAllStmt;

	private PreparedStatement findOneByActivationKeyStmt;

	private PreparedStatement findOneByResetKeyStmt;

	private PreparedStatement insertByActivationKeyStmt;

	private PreparedStatement insertByResetKeyStmt;

	private PreparedStatement deleteByActivationKeyStmt;

	private PreparedStatement deleteByResetKeyStmt;

	private PreparedStatement findOneByLoginStmt;

	private PreparedStatement insertByLoginStmt;

	private PreparedStatement deleteByLoginStmt;

	private PreparedStatement findOneByEmailStmt;

	private PreparedStatement insertByEmailStmt;

	private PreparedStatement deleteByEmailStmt;

	@Autowired
	public CassandraUserDao(Session session) {
		this.session = session;
	}

	@PostConstruct
	public void init() {
		mapper = new MappingManager(session).mapper(User.class);

		findAllStmt = session.prepare("SELECT * FROM user");

		findOneByActivationKeyStmt = session.prepare(
				"SELECT id " +
						"FROM user_by_activation_key " +
						"WHERE activation_key = :activation_key");

		findOneByResetKeyStmt = session.prepare(
				"SELECT id " +
						"FROM user_by_reset_key " +
						"WHERE reset_key = :reset_key");

		insertByActivationKeyStmt = session.prepare(
				"INSERT INTO user_by_activation_key (activation_key, id) " +
						"VALUES (:activation_key, :id)");

		insertByResetKeyStmt = session.prepare(
				"INSERT INTO user_by_reset_key (reset_key, id) " +
						"VALUES (:reset_key, :id)");

		deleteByActivationKeyStmt = session.prepare(
				"DELETE FROM user_by_activation_key " +
						"WHERE activation_key = :activation_key");

		deleteByResetKeyStmt = session.prepare(
				"DELETE FROM user_by_reset_key " +
						"WHERE reset_key = :reset_key");

		findOneByLoginStmt = session.prepare(
				"SELECT id " +
						"FROM user_by_login " +
						"WHERE login = :login");

		insertByLoginStmt = session.prepare(
				"INSERT INTO user_by_login (login, id) " +
						"VALUES (:login, :id)");

		deleteByLoginStmt = session.prepare(
				"DELETE FROM user_by_login " +
						"WHERE login = :login");

		findOneByEmailStmt = session.prepare(
				"SELECT id " +
						"FROM user_by_email " +
						"WHERE email     = :email");

		insertByEmailStmt = session.prepare(
				"INSERT INTO user_by_email (email, id) " +
						"VALUES (:email, :id)");

		deleteByEmailStmt = session.prepare(
				"DELETE FROM user_by_email " +
						"WHERE email = :email");
	}

	@Override
	public Optional<User> findOne(String id) {
		return Optional.of(mapper.get(id));
	}

	@Override
	public Optional<User> findOneByActivationKey(String activationKeyVal) {
		BoundStatement stmt = findOneByActivationKeyStmt.bind();
		stmt.setString(activationKey.F.underscore(), activationKeyVal);
		return findOneFromIndex(stmt);
	}

	@Override
	public Optional<User> findOneByResetKey(String resetKeyVal) {
		BoundStatement stmt = findOneByResetKeyStmt.bind();
		stmt.setString(resetKey.F.underscore(), resetKeyVal);
		return findOneFromIndex(stmt);
	}

	@Override
	public Optional<User> findOneByEmail(String emailVal) {
		BoundStatement stmt = findOneByEmailStmt.bind();
		stmt.setString(email.F.underscore(), emailVal);
		return findOneFromIndex(stmt);
	}

	@Override
	public Optional<User> findOneByLogin(String loginVal) {
		BoundStatement stmt = findOneByLoginStmt.bind();
		stmt.setString(login.F.underscore(), loginVal);
		return findOneFromIndex(stmt);
	}

	@Override
	public List<User> findAll() {
		return mapper.map(session.execute(findAllStmt.bind())).all();
	}

	@Override
	public User save(User user) {
		User oldUser = mapper.get(user.getId());
		if (oldUser != null) {
			if (!StringUtils.isEmpty(oldUser.getActivationKey())
					&& !oldUser.getActivationKey().equals(user.getActivationKey())) {
				session.execute(
						deleteByActivationKeyStmt.bind().setString(activationKey.F.underscore(),
								oldUser.getActivationKey()));
			}
			if (!StringUtils.isEmpty(oldUser.getResetKey()) && !oldUser.getResetKey().equals(user.getResetKey())) {
				session.execute(deleteByResetKeyStmt.bind().setString(resetKey.F.underscore(), oldUser.getResetKey()));
			}
			if (!StringUtils.isEmpty(oldUser.getLogin()) && !oldUser.getLogin().equals(user.getLogin())) {
				session.execute(deleteByLoginStmt.bind().setString(login.F.underscore(), oldUser.getLogin()));
			}
			if (!StringUtils.isEmpty(oldUser.getEmail()) && !oldUser.getEmail().equals(user.getEmail())) {
				session.execute(deleteByEmailStmt.bind().setString(email.F.underscore(), oldUser.getEmail()));
			}
		}
		BatchStatement batch = new BatchStatement();
		batch.add(mapper.saveQuery(user));
		if (!StringUtils.isEmpty(user.getActivationKey())) {
			batch.add(insertByActivationKeyStmt.bind()
					.setString(activationKey.F.underscore(), user.getActivationKey())
					.setString(id.F.underscore(), user.getId()));
		}
		if (!StringUtils.isEmpty(user.getResetKey())) {
			batch.add(insertByResetKeyStmt.bind()
					.setString(resetKey.F.underscore(), user.getResetKey())
					.setString(id.F.underscore(), user.getId()));
		}
		batch.add(insertByLoginStmt.bind()
				.setString(login.F.underscore(), user.getLogin())
				.setString(id.F.underscore(), user.getId()));
		batch.add(insertByEmailStmt.bind()
				.setString(email.F.underscore(), user.getEmail())
				.setString(id.F.underscore(), user.getId()));
		session.execute(batch);
		return user;
	}

	@Override
	public void delete(User user) {
		BatchStatement batch = new BatchStatement();
		batch.add(mapper.deleteQuery(user));
		if (!StringUtils.isEmpty(user.getActivationKey())) {
			batch.add(
					deleteByActivationKeyStmt.bind().setString(activationKey.F.underscore(), user.getActivationKey()));
		}
		if (!StringUtils.isEmpty(user.getResetKey())) {
			batch.add(deleteByResetKeyStmt.bind().setString(resetKey.F.underscore(), user.getResetKey()));
		}
		batch.add(deleteByLoginStmt.bind().setString(login.F.underscore(), user.getLogin()));
		batch.add(deleteByEmailStmt.bind().setString(email.F.underscore(), user.getEmail()));
		session.execute(batch);
	}

	private Optional<User> findOneFromIndex(BoundStatement stmt) {
		ResultSet rs = session.execute(stmt);
		if (rs.isExhausted()) {
			return Optional.empty();
		}
		return Optional.ofNullable(rs.one().getString(id.F.underscore()))
				.map(id -> Optional.ofNullable(mapper.get(id)))
				.get();
	}
}
