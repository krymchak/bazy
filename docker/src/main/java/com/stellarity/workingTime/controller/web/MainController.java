package com.stellarity.workingTime.controller.web;

import com.stellarity.workingTime.repository.entity.User;
import com.stellarity.workingTime.controller.DTo.UserDTo;
import com.stellarity.workingTime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/web")
public class MainController {

    private final UserRepository userRepository;

    @Autowired
    public MainController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(Authentication authentication, Principal principal, HttpServletResponse response) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("MANAGER"))) {
            return "redirect:/web/users";
		}
        else {
            /*User user = userRepository.findByUsername(authentication.getName());
            Cookie idUser = new Cookie("idUser", Long.toString(user.getId()));
            idUser.setPath("/time");
            response.addCookie(idUser);*/

            return "redirect:/web/time/list";
        }

    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("user", new UserDTo());
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String addUser(@ModelAttribute("user") @Valid UserDTo userDTo, BindingResult result, Model model) {
        if (result.hasErrors())
            return "registration";
        User userdb = userRepository.findByUsername(userDTo.getUsername());
        if (userdb != null) {
            model.addAttribute("error", "Username zajęte");
            return "registration";
        }

        userDTo.setPassword(passwordEncoder.encode(userDTo.getPassword()));
        User user = new User(userDTo);
        user.setSalary(0f);
        userRepository.save(user);
        return "redirect:/web/login";
    }

}
