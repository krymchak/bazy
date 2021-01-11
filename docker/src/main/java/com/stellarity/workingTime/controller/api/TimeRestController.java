package com.stellarity.workingTime.controller.api;

import com.stellarity.workingTime.controller.ChekingClass;
import com.stellarity.workingTime.controller.DTo.MonthData;
import com.stellarity.workingTime.controller.DTo.TimeDTo;
import com.stellarity.workingTime.config.JWT.UserPrincipal;
import com.stellarity.workingTime.repository.entity.Time;
import com.stellarity.workingTime.repository.entity.enums.StatusOfTime;
import com.stellarity.workingTime.repository.TimeRepository;
import com.stellarity.workingTime.exception.AccessDeniedException;
import com.stellarity.workingTime.exception.BadTimeException;
import com.stellarity.workingTime.exception.TimeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/time")
@RestController
public class TimeRestController {

    private final TimeRepository timeRepository;

    ChekingClass forCheck;

    @Autowired
    public TimeRestController(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
        forCheck = new ChekingClass(timeRepository);
    }


    private Long getIdAuthorizedUser(Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        Long userId = user.getId();
        return userId;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Time> all(Authentication authentication) {
        return timeRepository.findByUserIdOrderByTimeStart(getIdAuthorizedUser(authentication));
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public List<Time> allById(Authentication authentication, @PathVariable Long id) {
        if (!forCheck.authorizedUserIsManager(authentication))
            throw new AccessDeniedException();
        return timeRepository.findByUserIdOrderByTimeStart(id);
    }

    @RequestMapping(value = "filter", method = RequestMethod.GET)
    public List<Time> allFilter(@RequestBody MonthData month, Authentication authentication) {
        return forCheck.getTimeInMonth(month.getMonth(), getIdAuthorizedUser(authentication));
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Time add(@Valid @RequestBody TimeDTo timeDTo, Authentication authentication) {
        if (!forCheck.dateNotOverlap(getIdAuthorizedUser(authentication), timeDTo))
            throw new BadTimeException();
        Time time = new Time(timeDTo);
        time.setStatus(StatusOfTime.PROGRESS);
        return timeRepository.save(time);
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.DELETE)
    public void delete(@PathVariable Long id, Authentication authentication) {
        if (!forCheck.authorizedUserHasAccess(authentication, id))
            throw new AccessDeniedException();
        timeRepository.deleteById(id);
    }



    @RequestMapping(value = "{id}/update", method = RequestMethod.PUT)
    public Time update(@Valid @RequestBody TimeDTo timeDTo, @PathVariable Long id, Authentication authentication) {
        if (!forCheck.dateNotOverlap(getIdAuthorizedUser(authentication), timeDTo, id))
            throw new BadTimeException();
        if (!forCheck.authorizedUserHasAccess(authentication, id))
            throw new AccessDeniedException();
        Time time = timeRepository.findById(id)
                .orElseThrow(() -> new TimeNotFoundException(id));
        time.update(timeDTo);

        return timeRepository.save(time);
    }

    @RequestMapping(value = "{id}/accept", method = RequestMethod.PUT)
    public Time accept(@PathVariable Long id, Authentication authentication) {
        if (!forCheck.authorizedUserIsManager(authentication))
            throw new AccessDeniedException();
        Time time = timeRepository.findById(id)
                .orElseThrow(() -> new TimeNotFoundException(id));
        time.setStatus(StatusOfTime.ACCEPTED);
        return timeRepository.save(time);
    }

}
