package com.bside.mzoffice.user.repository;

import com.bside.mzoffice.user.domain.SnsType;
import com.bside.mzoffice.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySnsTypeAndSnsId(SnsType snsType, String snsId);
}
