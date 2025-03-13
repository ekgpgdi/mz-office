package com.bside.mzoffice.user.repository;

import com.bside.mzoffice.user.enums.SnsType;
import com.bside.mzoffice.user.enums.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySnsTypeAndSnsId(SnsType snsType, String snsId);
}
