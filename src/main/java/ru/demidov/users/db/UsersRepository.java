package ru.demidov.users.db;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import ru.demidov.users.Users;

@Component
public class UsersRepository {

	private final SessionFactory sessionFactory;

	private static final String HQL_SELECT_BY_TOKEN = """
			select u from Users u where u.token = :token
			""";
	private static final String HQL_SELECT_BY_EMAIL = """
			select u from Users u where u.email = :email
			""";
	private static final String HQL_UPDATE = """
			update Users u
			set u.password = :password,
			    u.email = :email,
			    u.enabled = :enabled,
			    u.token = :token
			where username = :username
			""";

	public UsersRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Users getUserByToken(String token) {
		var session = sessionFactory.getCurrentSession();
		var query = session.createQuery(HQL_SELECT_BY_TOKEN, Users.class);

		return (Users) query.setParameter("token", token).getSingleResult();
	}

	public Users getUserByEmail(String email) {
		var session = sessionFactory.getCurrentSession();
		var query = session.createQuery(HQL_SELECT_BY_EMAIL, Users.class);

		return (Users) query.setParameter("email", email).getSingleResult();
	}

	public Users getUserById(String username) {
		var session = sessionFactory.getCurrentSession();
		return (Users) session.get(Users.class, username);
	}

	public void update(Users user) {
		var entityManager = sessionFactory.createEntityManager();

		entityManager.createQuery(HQL_UPDATE)
				.setParameter("password", user.getPassword())
				.setParameter(":email", user.getEmail())
				.setParameter("enabled", user.getEnabled())
				.setParameter("token", user.getToken())
				.setParameter("username", user.getUsername())
				.executeUpdate();
	}
}
