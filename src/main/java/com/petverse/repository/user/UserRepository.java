package com.petverse.repository.user;

import com.petverse.entity.concretes.user.User;
import com.petverse.entity.enums.RoleType;
import org.apache.catalina.mapper.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmailEquals(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))"+
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> findUsersByQuery(@Param("query") String query, Pageable pageable);

    List<User> findByLastLoginAtBeforeOrLastLoginAtIsNull(LocalDateTime oneYearAgo);

    Integer countByRoleType(RoleType roleType);

}
