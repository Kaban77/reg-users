package ru.demidov.interfaces;

import ru.demidov.objects.Response;
import ru.demidov.users.Users;

import javax.servlet.http.HttpServletRequest;

public interface UserManager {

    public Response save(Users user, HttpServletRequest request);
}
