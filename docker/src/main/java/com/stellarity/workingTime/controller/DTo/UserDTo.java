package com.stellarity.workingTime.controller.DTo;


import com.stellarity.workingTime.repository.entity.User;
import com.stellarity.workingTime.controller.DTo.validator.FieldsValueMatch;

import javax.validation.constraints.NotBlank;

@FieldsValueMatch(field = "password", fieldMatch = "verifyPassword", message = "Passwords do not match!")
public class UserDTo {

    private Long id;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    private String type;

    @NotBlank(message = "Username is mandatory")
    private String username;
    @NotBlank(message = "Password is mandatory")
    private String password;
    @NotBlank(message = "Verify password is mandatory")
    private String verifyPassword;

    private Float salary = 0f;

    public String getUsername() {
        return username;
    }

    public UserDTo() {
    }

    public UserDTo(String firstName, String lastName, String type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
    }

    public UserDTo(User user) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        type = user.getType().value();
        salary = user.getSalary();
    }

    public Boolean isManager() {
        if (type == "manager")
            return true;
        return false;
    }

    public String getVerifyPassword() {
        return verifyPassword;
    }

    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = verifyPassword;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }
}