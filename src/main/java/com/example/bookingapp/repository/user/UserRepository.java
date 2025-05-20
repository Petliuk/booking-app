package com.example.bookingapp.repository.user;

import com.example.bookingapp.model.User;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

@Registered
public interface UserRepository extends JpaRepository<User, Long> {
}
