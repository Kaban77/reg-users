package ru.demidov.users.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.demidov.interfaces.UserConfirm;
import ru.demidov.interfaces.UserManager;
import ru.demidov.objects.Response;
import ru.demidov.users.Users;

@RestController
public class UserController {

	public UserController(UserManager userManager, UserConfirm userConfirm) {
		this.userManager = userManager;
		this.userConfirm = userConfirm;
	}

	private final UserManager userManager;
	private final UserConfirm userConfirm;

	@PostMapping(value = "/create-user", consumes = "application/json", produces = "application/json")
    public Response createUser(@RequestBody Users user, HttpServletRequest request) {
        return userManager.save(user, request);
    }

	@GetMapping(value = "/confirm-user")
    public String confirmUser(@RequestParam(value = "user") String hashUsername,
                              @RequestParam(value = "token")String token) {
        return "redirect:"  + userConfirm.confirm(hashUsername, token);
    }

}