package ru.demidov.action;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import ru.demidov.interfaces.UserManager;
import ru.demidov.objects.Authorities;
import ru.demidov.objects.Response;
import ru.demidov.users.Users;

@Component
public class UserManagerImpl implements UserManager {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagerImpl.class);

    @Transactional(propagation = Propagation.REQUIRED)
    public Response save(Users user, HttpServletRequest request) {
        try {
            Session session = sessionFactory.getCurrentSession();
            Response response = new Response();
            response.setUsernameCorrect(checkUsername(user.getUsername(), session));
            response.setEmailCorrect(checkEmail(user.getEmail(), session));

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
            LOGGER.info(" save. Error " + e.getMessage());
            return new Response();
        }
    }

    private boolean checkEmail (String email, Session session) {
        try {
            String hql = "FROM Users u WHERE u.email = :email";
            Query query = session.createQuery(hql);
            Users user = (Users) query.setParameter("email", email).getSingleResult();

            return false;
        } catch (NoResultException e) {
            return true;
        } catch (Exception e) {
            LOGGER.info("checkEmail " + e.getMessage());
            return false;
        }
    }

    private boolean checkUsername (String username, Session session) {
        try {
            Users user = (Users) session.get(Users.class, username);
            LOGGER.info("username is " + user.getUsername());
            return false;
        } catch (NullPointerException npe) {
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(user.getEmail());
            email.setSubject("Confirmation of registration");

            String text = "Dear " + user.getUsername() + "!\nTo confirm the registration, follow the link:\n" +
                          "http://localhost:8084/confirm-user?user=" + encodeString(user.getUsername()) +
                          "&token=" + user.getToken();

            email.setText(text);
            email.setFrom("Service");

            mailSender.send(email);
            return true;
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            return false;
        }
    }

}