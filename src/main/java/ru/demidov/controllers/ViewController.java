package ru.demidov.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ViewController {

    @RequestMapping(value = "/final", method = RequestMethod.GET)
    public String completionOfRegistration() {
        return "final";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String mainForm() {
        return "registration";
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String registrationError() {
        return "error";
    }

    @RequestMapping(value = "/successfully", method = RequestMethod.GET)
    public String successfullyRegistration() {
        return "success";
    }
}
