package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Certificate;
import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.repository.CertificateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CertificateService {

    private final CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    public Certificate getOrCreateCertificate(Registration registration) {
        if (registration == null || registration.getId() == null) {
            throw new IllegalArgumentException("Registration cannot be null");
        }

        Optional<Certificate> existing = certificateRepository.findByRegistrationId(registration.getId());
        if (existing.isPresent()) {
            return existing.get();
        }

        Event event = registration.getEvent();
        String eventTitle = (event != null && event.getTitle() != null) ? event.getTitle() : "Campus Event";
        String dept = (registration.getDepartment() != null && !registration.getDepartment().isBlank())
                ? registration.getDepartment()
                : (event != null && event.getDepartment() != null ? event.getDepartment() : "General Studies");
        LocalDate date = (event != null && event.getDate() != null) ? event.getDate() : LocalDate.now();

        // Unguessable verification code e.g. CERT-A9F43B21E8D0
        String randToken = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String verificationCode = "CERT-" + randToken;
        String certNumber = "EDU-" + (event != null ? event.getId() : "0") + "-" + registration.getId();

        Certificate certificate = new Certificate(
                verificationCode,
                certNumber,
                registration.getStudentName(),
                registration.getEmail(),
                eventTitle,
                dept,
                date,
                registration.getId(),
                event != null ? event.getId() : null
        );

        return certificateRepository.save(certificate);
    }

    @Transactional(readOnly = true)
    public Optional<Certificate> verifyCertificate(String verificationCode) {
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            return Optional.empty();
        }
        return certificateRepository.findByVerificationCode(verificationCode.trim().toUpperCase());
    }

    @Transactional(readOnly = true)
    public Optional<Certificate> getCertificateByRegistrationId(Long registrationId) {
        if (registrationId == null) {
            return Optional.empty();
        }
        return certificateRepository.findByRegistrationId(registrationId);
    }

    public Certificate revokeCertificate(String verificationCode) {
        Certificate cert = certificateRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found for code: " + verificationCode));
        cert.setStatus("REVOKED");
        return certificateRepository.save(cert);
    }
}
