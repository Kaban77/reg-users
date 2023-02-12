package ru.demidov.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ViewController {

	@GetMapping(value = "/final")
    public String completionOfRegistration() {
        return "final";
    }

	@GetMapping(value = "/")
    public String mainForm() {
        return "registration";
    }

	@GetMapping(value = "/error")
    public String registrationError() {
        return "error";
    }

	@GetMapping(value = "/successfully")
    public String successfullyRegistration() {
        return "success";
    }
}
