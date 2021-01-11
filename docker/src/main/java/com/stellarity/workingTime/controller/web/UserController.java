package com.stellarity.workingTime.controller.web;

import com.stellarity.workingTime.repository.entity.User;
import com.stellarity.workingTime.controller.DTo.UserDTo;
import com.stellarity.workingTime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Controller
@RequestMapping("/web/users")
@PreAuthorize("hasAuthority('MANAGER')")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showUpdateForm(Model model) {
        List<UserDTo> listOfUserDTo = new ArrayList<>();
        for (User u : userRepository.findAllByOrderByLastNameAscFirstNameAsc()) {
            listOfUserDTo.add(new UserDTo(u));
        }
        model.addAttribute("users", listOfUserDTo);
        return "index";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String showTimeOfUser(@PathVariable("id") Long id, Model model, HttpServletResponse response) {
        Cookie idUser = new Cookie("idUser", id.toString());
        idUser.setPath("/web/time");
        response.addCookie(idUser);

        return "redirect:/web/time/list";
    }

    @RequestMapping(value = "{id}/salary")
    public String getSalary(@PathVariable("id") Long id, Model model, HttpServletResponse response) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid time Id:" + id));
        UserDTo userDTo = new UserDTo(user);
        model.addAttribute("user", userDTo);
        return "add-salary";
    }

    @RequestMapping(value = "{id}/addSalary", method = RequestMethod.POST)
    public String addSalary(@PathVariable("id") Long id, UserDTo userDTo) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid time Id:" + id));
        user.setSalary(userDTo.getSalary());
        userRepository.save(user);
        return "redirect:web/time/list";
    }

}