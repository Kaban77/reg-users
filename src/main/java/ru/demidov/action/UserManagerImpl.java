package ru.demidov.action;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import ru.demidov.email.Email;
import ru.demidov.email.EmailSenderService;
import ru.demidov.interfaces.UserManager;
import ru.demidov.users.UserRegistrationResponse;
import ru.demidov.users.Users;
import ru.demidov.users.authorities.Authorities;
import ru.demidov.users.db.UsersRepository;

@Component
public class UserManagerImpl implements UserManager {

	private final SessionFactory sessionFactory;
	private final EmailSenderService emailSenderService;
	private final UsersRepository usersRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagerImpl.class);

	public UserManagerImpl(SessionFactory sessionFactory, EmailSenderService emailSenderService, UsersRepository usersRepository) {
		this.sessionFactory = sessionFactory;
		this.emailSenderService = emailSenderService;
		this.usersRepository = usersRepository;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	@Override
    public UserRegistrationResponse save(Users user, HttpServletRequest request) {
        try {
            Session session = sessionFactory.getCurrentSession();
            UserRegistrationResponse response = new UserRegistrationResponse();
            response.setUsernameCorrect(checkUsername(user.getUsername(), session));
			response.setEmailCorrect(checkEmail(user.getEmail()));

            if(!response.isUsernameCorrect() || !response.isEmailCorrect()) {
                LOGGER.info("incorect login or email");
                return response;
            }
            user.setPassword(encodeString(user.getPassword()));
            user.setToken(getToken(request));
            session.persist(user);
            session.persist(new Authorities(user.getUsername(), "ROLE_USER"));

            response.setEmailSent(sendConfirmEmail(user));
            return response;

        } catch(Exception e) {
			LOGGER.error(" save. Error " + e.getMessage(), e);
            return new UserRegistrationResponse();
        }
    }

	private boolean checkEmail(String email) {
        try {
			usersRepository.getUserByEmail(email);
            return false;
        } catch (NoResultException e) {
            return true;
        } catch (Exception e) {
			LOGGER.error("checkEmail " + e.getMessage(), e);
            return false;
        }
    }

    private boolean checkUsername (String username, Session session) {
        try {
			var user = usersRepository.getUserById(username);
            LOGGER.info("username is " + user.getUsername());
            return false;
        } catch (NullPointerException npe) {
            return true;
        } catch (Exception e) {
			LOGGER.error("Error: ", e);
            return false;
        }
    }

    private String encodeString(String string) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        return encoder.encode(string);
    }

    private String getToken(HttpServletRequest request) {
        CookieCsrfTokenRepository tokenRepository = new CookieCsrfTokenRepository();
        CsrfToken token = tokenRepository.generateToken(request);
        return token.getToken();
    }

    private boolean sendConfirmEmail(Users user) {
		var email = new Email();
		email.setSendTo(user.getEmail());
		email.setTitle("Confirmation of registration");

		String text = "Dear " + user.getUsername() + "!\nTo confirm the registration, follow the link:\n"
				+ "http://localhost:8084/confirm-user?user=" + encodeString(user.getUsername()) + "&token=" + user.getToken();

		email.setMessage(text);

		emailSenderService.sendEmail(email);
		return true;
    }

}