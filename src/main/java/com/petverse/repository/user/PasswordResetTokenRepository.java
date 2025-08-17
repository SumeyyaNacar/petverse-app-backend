package com.petverse.repository.user;


import com.petverse.entity.concretes.user.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {
    PasswordResetTokenEntity findByToken(String token);
    void deleteByUserId(UUID userId);


}