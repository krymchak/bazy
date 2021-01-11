package com.stellarity.workingTime.controller.api;

import com.stellarity.workingTime.controller.DTo.SalaryData;
import com.stellarity.workingTime.controller.DTo.UserDTo;
import com.stellarity.workingTime.repository.entity.User;
import com.stellarity.workingTime.repository.UserRepository;
import com.stellarity.workingTime.exception.AccessDeniedException;
import com.stellarity.workingTime.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/user")
@RestController
public class UserRestController {

    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Boolean isManager(Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("MANAGER")))
            return true;
        return false;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<User> all(Authentication authentication) {
        if (!isManager(authentication))
            throw new AccessDeniedException();
        return userRepository.findAll();
    }

    @RequestMapping(value = "{id}/salary", method = RequestMethod.PUT)
    public User salary(@PathVariable Long id, @RequestBody SalaryData salary, Authentication authentication) {
        if (!isManager(authentication))
            throw new AccessDeniedException();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setSalary(Float.valueOf(salary.getSalary()));
        return userRepository.save(user);
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public User add(@Valid @RequestBody UserDTo userDTo) {
        userDTo.setPassword(passwordEncoder.encode(userDTo.getPassword()));
        User user = new User(userDTo);
        user.setSalary(0f);
        return userRepository.save(user);
    }

}
