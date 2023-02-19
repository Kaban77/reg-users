package ru.demidov.users.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import ru.demidov.users.UserRegistrationResponse;
import ru.demidov.users.Users;


public interface UserManager {

    public UserRegistrationResponse save(Users user, HttpServletRequest request);
}
