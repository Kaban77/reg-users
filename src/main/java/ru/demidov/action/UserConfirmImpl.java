package ru.demidov.action;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.demidov.interfaces.UserConfirm;
import ru.demidov.objects.Users;

import javax.persistence.Query;

@Component
public class UserConfirmImpl implements UserConfirm {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(UserConfirmImpl.class);



    @Transactional(propagation = Propagation.REQUIRED)
    public String confirm(String hashUsername, String token) {
        try {
            Session session = sessionFactory.getCurrentSession();
            String hql = "from Users u where u.token = :token";
            Query query = session.createQuery(hql);
            Users user = (Users) query.setParameter("token", token).getSingleResult();

            if(!isEquals(user.getUsername(), hashUsername)) {
                logger.info("strings is not equals");
                return "error";
            }

            String update = "update Users set enabled = 1 where token = :token";
            query = session.createQuery(update);
            query.setParameter("token", token);
            query.executeUpdate();

            if(!sendFinalEmail(user.getEmail())) {
                return "error";
            }

            return "final";
        } catch (Exception e) {
            logger.info("confirm. Error " + e.getMessage());
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
            logger.info("sendFinalEmail. Error " + e.getMessage());
            return false;
        }
    }
}
