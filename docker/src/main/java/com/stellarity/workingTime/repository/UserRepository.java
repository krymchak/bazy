package com.stellarity.workingTime.repository;

import com.stellarity.workingTime.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    List<User> findAllByOrderByLastNameAscFirstNameAsc();

   // @Query(value = "SELECT id FROM users WHERE username=?1", nativeQuery = true)
   // Long findId(String username);

}