package com.stellarity.workingTime.repository;

import com.stellarity.workingTime.repository.entity.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeRepository extends JpaRepository<Time, Long> {
    List<Time> findByUserIdAndTimeStartBetweenOrderByTimeStart(Long id, LocalDateTime start, LocalDateTime end);

    List<Time> findByUserIdOrderByTimeStart(Long id);

    List<Time> findByUserIdAndIdNotAndTimeStartBetweenOrderByTimeStart(Long idUser, Long id, LocalDateTime start, LocalDateTime end);

    List<Time> findByUserIdAndIdNotAndTimeEndBetweenOrderByTimeStart(Long idUser, Long id, LocalDateTime start, LocalDateTime end);

    List<Time> findByUserIdAndIdNotAndTimeStartLessThanAndTimeEndGreaterThan(Long idUser, Long id, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT user_id FROM time WHERE id=?1", nativeQuery = true)
    Long findUserIdById(Long id);


}