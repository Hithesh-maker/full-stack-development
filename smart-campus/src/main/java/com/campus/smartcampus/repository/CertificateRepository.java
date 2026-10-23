package com.campus.smartcampus.repository;

import com.campus.smartcampus.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByVerificationCode(String verificationCode);
    Optional<Certificate> findByRegistrationId(Long registrationId);
    List<Certificate> findByStudentEmail(String studentEmail);
}
