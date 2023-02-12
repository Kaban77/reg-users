package ru.demidov.action;



import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.demidov.interfaces.UserConfirm;
import ru.demidov.users.db.UsersRepository;

@Component
public class UserConfirmImpl implements UserConfirm {

	private final SessionFactory sessionFactory;
	private final JavaMailSender mailSender;
	private final UsersRepository usersRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserConfirmImpl.class);

	public UserConfirmImpl(SessionFactory sessionFactory, JavaMailSender mailSender, UsersRepository usersRepository) {
		this.sessionFactory = sessionFactory;
		this.mailSender = mailSender;
		this.usersRepository = usersRepository;
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public String confirm(String hashUsername, String token) {
        try {
			var user = usersRepository.getUserByToken(token);

            if(!isEquals(user.getUsername(), hashUsername)) {
                LOGGER.info("strings is not equals");
                return "error";
            }
			user.setEnabled(1);
			usersRepository.update(user);

            if(!sendFinalEmail(user.getEmail())) {
                return "error";
            }

            return "final";
        } catch (Exception e) {
			LOGGER.error("confirm. Error " + e.getMessage(), e);
            return "error";
        }
    }

    private boolean isEquals(String string, String hashString) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        return encoder.matches(string, hashString);
    }

    private boolean sendFinalEmail(String address) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(address);
            email.setSubject("Registration completed");


            String text = "Registration completed successfully";

            email.setText(text);
            email.setFrom("Service");

            mailSender.send(email);
            return true;
        } catch (Exception e) {
            LOGGER.info("sendFinalEmail. Error " + e.getMessage());
            return false;
        }
    }
}
