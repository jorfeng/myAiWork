package com.auth.letter.repository;

import com.auth.letter.entity.AuthLetter;
import com.auth.letter.enums.AuthLetterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthLetterRepository extends JpaRepository<AuthLetter, Long> {

    Optional<AuthLetter> findByCode(String code);

    boolean existsByCode(String code);

    Page<AuthLetter> findByStatus(AuthLetterStatus status, Pageable pageable);

    Page<AuthLetter> findByNameContaining(String name, Pageable pageable);

    @Query("SELECT a FROM AuthLetter a WHERE a.status = :status AND a.validTo < :now")
    List<AuthLetter> findExpiredAuthLetters(AuthLetterStatus status, LocalDateTime now);

    @Query("SELECT a FROM AuthLetter a WHERE a.status = 'PUBLISHED' AND " +
           "(a.validFrom IS NULL OR a.validFrom <= :now) AND " +
           "(a.validTo IS NULL OR a.validTo >= :now)")
    List<AuthLetter> findValidAuthLetters(LocalDateTime now);

    @Query("SELECT a FROM AuthLetter a WHERE a.code = :code AND a.status = 'PUBLISHED' AND " +
           "(a.validFrom IS NULL OR a.validFrom <= :now) AND " +
           "(a.validTo IS NULL OR a.validTo >= :now)")
    Optional<AuthLetter> findValidAuthLetterByCode(String code, LocalDateTime now);
}