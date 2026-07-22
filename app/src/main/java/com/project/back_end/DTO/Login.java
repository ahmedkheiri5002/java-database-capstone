package com.project.back_end.DTO;

public class Login {

    // 1. Represents the email address used for logging into the system.
    private String email;

    // 2. Represents the password associated with the email address.
    private String password;

    // 3. Default constructor
    public Login() {
    }

    // 4. Getters and Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
