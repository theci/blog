package com.blog.demo.repository;

import com.blog.demo.entity.UserSuspension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserSuspensionRepository extends JpaRepository<UserSuspension, Long> {
    
    @Query("SELECT us FROM UserSuspension us WHERE us.user.id = :userId AND us.isActive = true " +
           "AND us.startDate <= :now AND (us.endDate IS NULL OR us.endDate > :now)")
    Optional<UserSuspension> findActiveSuspensionByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    Optional<UserSuspension> findByUserIdAndIsActiveTrue(Long userId);
}