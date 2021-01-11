package com.stellarity.workingTime.repository.entity;

import com.stellarity.workingTime.repository.entity.enums.TypeOfUser;
import com.stellarity.workingTime.controller.DTo.UserDTo;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = 0L;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private TypeOfUser type;

    private Float salary = 0f;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Time> times;


    public User() {
    }

    public User(String firstName, String lastName, TypeOfUser type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
    }

    public User(UserDTo userDTo) {
        firstName = userDTo.getFirstName();
        lastName = userDTo.getLastName();
        type = TypeOfUser.fromString(userDTo.getType());
        username = userDTo.getUsername();
        password = userDTo.getPassword();
        salary = userDTo.getSalary();
    }


    public String getUsername() {
        return username;
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

   /* public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }*/


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

    public TypeOfUser getType() {
        return type;
    }

    public void setType(TypeOfUser type) {
        this.type = type;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }
}