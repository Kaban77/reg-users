package ru.demidov.users.action;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.demidov.email.Email;
import ru.demidov.email.EmailSenderService;
import ru.demidov.users.db.UsersRepository;
import ru.demidov.users.interfaces.UserConfirm;

@Component
public class UserConfirmImpl implements UserConfirm {

	private final EmailSenderService emailSenderService;
	private final UsersRepository usersRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserConfirmImpl.class);

	public UserConfirmImpl(EmailSenderService emailSenderService, UsersRepository usersRepository) {
		this.emailSenderService = emailSenderService;
		this.usersRepository = usersRepository;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	@Override
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
		var email = new Email();
		email.setSendTo(address);
		email.setTitle("Registration completed");
		email.setMessage("Registration completed successfully");

		emailSenderService.sendEmail(email);
		return true;
    }
}
