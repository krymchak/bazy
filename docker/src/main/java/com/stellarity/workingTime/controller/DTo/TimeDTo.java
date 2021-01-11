package com.stellarity.workingTime.controller.DTo;

import com.stellarity.workingTime.repository.entity.Time;
import com.stellarity.workingTime.controller.DTo.validator.CorrectData;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@CorrectData(field = "timeStart", fieldMatch = "timeEnd", message = "Time of start must be before time of end!")
public class TimeDTo {

    private Long id = 1L;

    private Long idUser;

    @NotNull(message = "Time of start is mandatory")
    @PastOrPresent(message = "you can not add time to the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime timeStart;

    @NotNull(message = "Time of end is mandatory")
    @PastOrPresent(message = "you can not add time to the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime timeEnd;

    private String note;

    private String status;

    public TimeDTo(Long idUser, LocalDateTime timeStart, LocalDateTime timeEnd, String status, String note) {
        this.idUser = idUser;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.status = status;
        this.note = note;
    }


    public TimeDTo() {
    }

    public TimeDTo(Time time) {
        id = time.getId();
        //idUser = time.getIdUser();
        timeStart = time.getTimeStart();
        timeEnd = time.getTimeEnd();
        status = time.getStatus().value();
        note = time.getNote();
    }

    public Double workingHours() {
        Duration duration = Duration.between(timeStart, timeEnd);
        Double hours = Double.valueOf(duration.toHours());
        Double minutes = Double.valueOf(((duration.getSeconds() % (60 * 60)) / 60));
        Double result = hours + minutes / 60;
        result = new BigDecimal(result).setScale(2, RoundingMode.UP).doubleValue();

        return result;
    }

    public String getTimeStartString() {
        return timeStart.toString().substring(0, 16).replace("T", " ");
    }

    public String getTimeEndString() {
        return timeEnd.toString().substring(0, 16).replace("T", " ");
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }

    public LocalDateTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalDateTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalDateTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalDateTime timeEnd) {
        this.timeEnd = timeEnd;
    }
}
