package ru.demidov.users;

public class UserRegistrationResponse {

    private boolean isUsernameCorrect;
    private boolean isEmailCorrect;
    private boolean isEmailSent;

    public UserRegistrationResponse() {
        this.isUsernameCorrect = false;
        this.isEmailCorrect = false;
        this.isEmailSent = false;
    }

    public boolean isUsernameCorrect() {
        return isUsernameCorrect;
    }

    public void setUsernameCorrect(boolean usernameCorrect) {
        isUsernameCorrect = usernameCorrect;
    }

    public boolean isEmailCorrect() {
        return isEmailCorrect;
    }

    public void setEmailCorrect(boolean emailCorrect) {
        isEmailCorrect = emailCorrect;
    }

    public boolean isEmailSent() {
        return isEmailSent;
    }

    public void setEmailSent(boolean emailSent) {
        isEmailSent = emailSent;
    }
}
