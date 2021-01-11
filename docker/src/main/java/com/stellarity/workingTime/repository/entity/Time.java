package com.stellarity.workingTime.repository.entity;

import com.stellarity.workingTime.repository.entity.enums.StatusOfTime;
import com.stellarity.workingTime.controller.DTo.TimeDTo;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
public class Time implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = 0L;

    @Column(name = "time_start")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime timeStart;

    @Column(name = "time_end")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime timeEnd;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusOfTime status;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public Time(LocalDateTime timeStart, LocalDateTime timeEnd, StatusOfTime status, String note) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.status = status;
        this.note = note;
    }

    public Time() {
    }

    public Time(TimeDTo timeDTo) {
        timeStart = timeDTo.getTimeStart();
        timeEnd = timeDTo.getTimeEnd();
        note = timeDTo.getNote();

    }

    public void update(TimeDTo timeDTo) {
        timeStart = timeDTo.getTimeStart();
        timeEnd = timeDTo.getTimeEnd();
        note = timeDTo.getNote();
    }



    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public StatusOfTime getStatus() {
        return status;
    }

    public void setStatus(StatusOfTime status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*public long getIdUser() {
        return idUser;
    }

    public void setIdUser(long idUser) {
        this.idUser = idUser;
    }*/

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


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
