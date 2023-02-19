package ru.demidov.users.interfaces;

public interface UserConfirm {

    public String confirm(String hashUsername, String token);
}
