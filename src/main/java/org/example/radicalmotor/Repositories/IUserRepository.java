package org.example.radicalmotor.Repositories;

import org.example.radicalmotor.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @Query("select u.countFail from User u where u.username=?1")
    int countFailByUsername(String username);
    Optional<User> findByTokenResetPassword(String token);
}
