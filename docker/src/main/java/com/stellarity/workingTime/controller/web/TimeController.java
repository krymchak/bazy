package com.stellarity.workingTime.controller.web;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.stellarity.workingTime.config.JWT.UserPrincipal;
import com.stellarity.workingTime.controller.ChekingClass;
import com.stellarity.workingTime.controller.GeneratePdfReport;
import com.stellarity.workingTime.repository.entity.Time;
import com.stellarity.workingTime.repository.entity.User;
import com.stellarity.workingTime.repository.entity.enums.StatusOfTime;
import com.stellarity.workingTime.controller.DTo.TimeDTo;
import com.stellarity.workingTime.repository.TimeRepository;
import com.stellarity.workingTime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/time")
public class TimeController {

    private final TimeRepository timeRepository;
    private final UserRepository userRepository;

    ChekingClass forCheck;

    @Autowired
    public TimeController(TimeRepository timeRepository, UserRepository userRepository) {
        this.timeRepository = timeRepository;
        this.userRepository = userRepository;
        forCheck = new ChekingClass(timeRepository);
    }

    private List<String> errors(BindingResult result) {
        List<String> errorsString = new ArrayList<>();
        List<FieldError> errors = result.getFieldErrors();
        for (FieldError error : errors) {
            if (!errorsString.contains(error.getDefaultMessage()))
                errorsString.add(error.getDefaultMessage());
        }
        List<ObjectError> globalErrors = result.getGlobalErrors();
        for (ObjectError error : globalErrors) {
            if (!errorsString.contains(error.getDefaultMessage()))
                errorsString.add(error.getDefaultMessage());
        }
        return errorsString;
    }


    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String showUpdateForm(Model model, Authentication authentication, @CookieValue(value = "idUser", defaultValue = "0") String idUser, @RequestParam(required = false) String month) {
        Long userId;
        if (forCheck.authorizedUserIsManager(authentication))
            userId = Long.parseLong(idUser);
        else {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            userId = userPrincipal.getId();
        }
        List<TimeDTo> listOfTimeDTo;
        if (month!=null)
        {

            listOfTimeDTo = forCheck.getTimeInMonth(month, userId)
                    .stream()
                    .map(TimeDTo::new)
                    .collect(Collectors.toList());
        }
        else {
            listOfTimeDTo = timeRepository.findByUserIdOrderByTimeStart(userId)
                    .stream()
                    .map(TimeDTo::new)
                    .collect(Collectors.toList());
        }
        if (!listOfTimeDTo.isEmpty())
            model.addAttribute("times", listOfTimeDTo);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + idUser));
        model.addAttribute("nameUser", user.getFirstName());
        model.addAttribute("lastNameUser", user.getLastName());
        model.addAttribute("salary", user.getSalary());
        model.addAttribute("month", new String());
        model.addAttribute("userId", Integer.valueOf(idUser));
        model.addAttribute("isManager", forCheck.authorizedUserIsManager(authentication));
        return "list-time";
    }


    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String addTime(@Valid TimeDTo timeDTo, BindingResult result, RedirectAttributes redirAttrs, @CookieValue(value = "idUser", defaultValue = "0") String idUser, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        if (result.hasErrors()) {
            redirAttrs.addFlashAttribute("errors", errors(result));
            return "redirect:/web/time/list";
        }
        if (!forCheck.dateNotOverlap(userId, timeDTo)) {
            List<String> errors = new ArrayList<>();
            errors.add("Nakładanie się czasu pracy");
            redirAttrs.addFlashAttribute("errors", errors);
            return "redirect:/web/time/list";
        }
        System.out.println(timeDTo.getTimeEnd());
        Time time = new Time(timeDTo);
        time.setUser(userPrincipal.getUser());
        time.setStatus(StatusOfTime.PROGRESS);
        timeRepository.save(time);

        return "redirect:/web/time/list";
    }

    @RequestMapping(value = "new")
    public String newTime(Model model) {
        model.addAttribute("time", new TimeDTo());
        return "add-time";
    }

    @RequestMapping(value = "{id}/note")
    public String note(@PathVariable("id") Long id, Model model, Authentication authentication) {
        if (!forCheck.authorizedUserHasAccess(authentication, id))
            return "access-denied";

        Time time = timeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid time Id:" + id));
        ;
        model.addAttribute("noteData", time.getNote());
        return "note-time";
    }


    @RequestMapping(value = "{id}/change")
    public String view(@PathVariable("id") Long id, Model model, Authentication authentication) {
        if (!forCheck.authorizedUserHasAccess(authentication, id))
            return "access-denied";
        Time time = timeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid time Id:" + id));
        TimeDTo timeDTo = new TimeDTo(time);
        model.addAttribute("time", timeDTo);
        return "update-time";
    }


    @RequestMapping(value = "{id}/update", method = RequestMethod.POST)
    public String updateTime(@PathVariable("id") Long id, Authentication authentication, @Valid @ModelAttribute("time") TimeDTo timeDTo, BindingResult result, RedirectAttributes redirAttrs, @CookieValue(value = "idUser", defaultValue = "0") String idUser, Model model) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        if (!forCheck.authorizedUserHasAccess(authentication, id))
            return "access-denied";
        if (result.hasErrors()) {
            redirAttrs.addFlashAttribute("errors", errors(result));
            return "redirect:/web/time/list";
        }
        if (!forCheck.dateNotOverlap(userId, timeDTo, id)) {
            List<String> errors = new ArrayList<>();
            errors.add("Nakładanie się czasu pracy");
            redirAttrs.addFlashAttribute("errors", errors);
            return "redirect:/web/time/list";
        }
        Time time = timeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid time Id:" + id));
        time.update(timeDTo);
        timeRepository.save(time);
        return "redirect:/web/time/list";
    }

    @RequestMapping(value = "{id}/answerAccept")
    public String answerAccept(@PathVariable("id") Long id, Model model, Authentication authentication) {
        if (!forCheck.authorizedUserHasAccess(authentication, id))
            return "access-denied";
        model.addAttribute("id", id);
        return "accept-time";
    }

    @RequestMapping(value = "{id}/accept", method = RequestMethod.GET)
    public String acceptTime(@PathVariable("id") Long id, Authentication authentication) {
        if (!forCheck.authorizedUserIsManager(authentication))
            return "access-denied";
        Time time = timeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid time Id:" + id));
        time.setStatus(StatusOfTime.ACCEPTED);
        timeRepository.save(time);
        return "redirect:/web/time/list";
    }

    @RequestMapping(value = "{id}/answerDelete")
    public String answerDelete(@PathVariable("id") Long id, Model model, Authentication authentication) {
        if (!forCheck.authorizedUserHasAccess(authentication, id))
            return "access-denied";
        model.addAttribute("id", id);
        return "delete-time";
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.GET)
    public String deleteTime(@PathVariable("id") Long id, Model model, Authentication authentication) {
        if (!forCheck.authorizedUserHasAccess(authentication, id))
            return "access-denied";
        Time time = timeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid time Id:" + id));
        timeRepository.delete(time);
        return "redirect:/web/time/list";
    }

    @RequestMapping(value = "/month",params = "filter")
    public String selectFilter(RedirectAttributes redirAttrs, @RequestParam(required = false) String month) {
        redirAttrs.addAttribute("month", month);
        return "redirect:/web/time/list";
    }

    @RequestMapping(value = "/month", params = "raport")
    public String selectRaport(RedirectAttributes redirAttrs, @RequestParam(defaultValue = "all") String month) {
        redirAttrs.addAttribute("month", month);
        return "redirect:/web/time/raport";
    }

    @RequestMapping(value = "/raport", method = RequestMethod.GET , produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> findCities(Authentication authentication, @CookieValue(value = "idUser", defaultValue = "0") String idUser,  @RequestParam(required = false) String month) {
        Long userId;
        if (forCheck.authorizedUserIsManager(authentication))
            userId = Long.parseLong(idUser);
        else {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            userId = userPrincipal.getId();
        }
        List<Time> times;
        System.out.println("AAAAAAAAAAAAAAAA");
        System.out.println(month);
        if (month.equals("all"))
        {
            times = timeRepository.findByUserIdOrderByTimeStart(userId);

        }
        else
        {
            times = forCheck.getTimeInMonth(month, userId);
        }
        ByteArrayInputStream bis = GeneratePdfReport.citiesReport(times);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}