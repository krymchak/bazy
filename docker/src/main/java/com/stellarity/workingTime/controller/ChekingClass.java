package com.stellarity.workingTime.controller;

import com.stellarity.workingTime.config.JWT.UserPrincipal;
import com.stellarity.workingTime.controller.DTo.TimeDTo;
import com.stellarity.workingTime.repository.TimeRepository;
import com.stellarity.workingTime.repository.entity.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ChekingClass {

    private final TimeRepository timeRepository;

    @Autowired
    public ChekingClass(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    public Boolean authorizedUserIsManager(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("MANAGER"));
    }

    public List<Time> getTimeInMonth(String month, Long idUser) {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM")
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .toFormatter();

            LocalDateTime startDate = LocalDateTime.parse(month, formatter);
            LocalDateTime endDate = startDate.plus(1, ChronoUnit.MONTHS);
            return timeRepository.findByUserIdAndTimeStartBetweenOrderByTimeStart(idUser, startDate, endDate);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Boolean dateNotOverlap(Long idUser, TimeDTo timeDTo, Long ... idTime) {

        Long id = idTime.length > 0 ? idTime[0] : 0;

        if (!timeRepository.findByUserIdAndIdNotAndTimeStartBetweenOrderByTimeStart(idUser, id, timeDTo.getTimeStart(), timeDTo.getTimeEnd()).isEmpty())
            return false;
        if (!timeRepository.findByUserIdAndIdNotAndTimeEndBetweenOrderByTimeStart(idUser, id, timeDTo.getTimeStart(), timeDTo.getTimeEnd()).isEmpty())
            return false;
        if (!timeRepository.findByUserIdAndIdNotAndTimeStartLessThanAndTimeEndGreaterThan(idUser, id, timeDTo.getTimeStart(), timeDTo.getTimeEnd()).isEmpty())
            return false;
        return true;
    }

    public Boolean authorizedUserHasAccess(Authentication authentication, Long id) {
        if (authorizedUserIsManager(authentication))
            return true;
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        Long id1 = user.getId();
        Long id2 = timeRepository.findUserIdById(id);
        return id1.equals(id2);
    }
}
