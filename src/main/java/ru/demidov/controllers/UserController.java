package ru.demidov.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.demidov.interfaces.UserConfirm;
import ru.demidov.interfaces.UserManager;
import ru.demidov.objects.Response;
import ru.demidov.objects.Users;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserConfirm userConfirm;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/create-user", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Response createUser(@RequestBody Users user, HttpServletRequest request) {
        return userManager.save(user, request);
    }

    @RequestMapping(value = "/confirm-user", method = RequestMethod.GET)
    public String confirmUser(@RequestParam(value = "user") String hashUsername,
                              @RequestParam(value = "token")String token) {
        return "redirect:"  + userConfirm.confirm(hashUsername, token);
    }

}
