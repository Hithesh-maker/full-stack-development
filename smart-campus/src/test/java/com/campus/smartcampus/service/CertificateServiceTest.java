package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Certificate;
import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.repository.CertificateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @InjectMocks
    private CertificateService certificateService;

    private Event mockEvent;
    private Registration mockRegistration;

    @BeforeEach
    void setUp() {
        mockEvent = new Event();
        mockEvent.setId(10L);
        mockEvent.setTitle("AI & Cloud Summit 2026");
        mockEvent.setDepartment("Computer Science and Engineering");
        mockEvent.setDate(LocalDate.of(2026, 10, 15));

        mockRegistration = new Registration();
        mockRegistration.setId(101L);
        mockRegistration.setStudentName("Jane Doe");
        mockRegistration.setEmail("jane.doe@campus.edu");
        mockRegistration.setDepartment("Computer Science and Engineering");
        mockRegistration.setEvent(mockEvent);
        mockRegistration.setToken("TOKEN-XYZ-123");
    }

    @Test
    @DisplayName("getOrCreateCertificate should create and save new certificate if none exists")
    void testGetOrCreateCertificate_CreatesNew() {
        when(certificateRepository.findByRegistrationId(101L)).thenReturn(Optional.empty());
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(invocation -> {
            Certificate c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        Certificate result = certificateService.getOrCreateCertificate(mockRegistration);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getStudentName());
        assertEquals("jane.doe@campus.edu", result.getStudentEmail());
        assertEquals("AI & Cloud Summit 2026", result.getEventName());
        assertEquals("Computer Science and Engineering", result.getDepartment());
        assertEquals(101L, result.getRegistrationId());
        assertTrue(result.getVerificationCode().startsWith("CERT-"));
        assertTrue(result.isValid());
        assertEquals("VALID", result.getStatus());

        verify(certificateRepository, times(1)).save(any(Certificate.class));
    }

    @Test
    @DisplayName("getOrCreateCertificate should return existing certificate without re-saving")
    void testGetOrCreateCertificate_ReturnsExisting() {
        Certificate existing = new Certificate(
                "CERT-ABC123DEF456", "EDU-10-101", "Jane Doe",
                "jane.doe@campus.edu", "AI & Cloud Summit 2026",
                "Computer Science and Engineering", LocalDate.of(2026, 10, 15),
                101L, 10L);
        existing.setId(5L);

        when(certificateRepository.findByRegistrationId(101L)).thenReturn(Optional.of(existing));

        Certificate result = certificateService.getOrCreateCertificate(mockRegistration);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("CERT-ABC123DEF456", result.getVerificationCode());
        verify(certificateRepository, never()).save(any(Certificate.class));
    }

    @Test
    @DisplayName("verifyCertificate should return certificate for valid code")
    void testVerifyCertificate_ValidCode() {
        Certificate cert = new Certificate(
                "CERT-VALID-CODE", "EDU-1-1", "John", "john@campus.edu",
                "Event", "CSE", LocalDate.now(), 1L, 1L);
        when(certificateRepository.findByVerificationCode("CERT-VALID-CODE")).thenReturn(Optional.of(cert));

        Optional<Certificate> opt = certificateService.verifyCertificate("CERT-VALID-CODE");

        assertTrue(opt.isPresent());
        assertEquals("John", opt.get().getStudentName());
    }

    @Test
    @DisplayName("verifyCertificate should return empty for invalid or missing code")
    void testVerifyCertificate_InvalidCode() {
        when(certificateRepository.findByVerificationCode("INVALID")).thenReturn(Optional.empty());

        Optional<Certificate> opt1 = certificateService.verifyCertificate("INVALID");
        Optional<Certificate> opt2 = certificateService.verifyCertificate("");
        Optional<Certificate> opt3 = certificateService.verifyCertificate(null);

        assertFalse(opt1.isPresent());
        assertFalse(opt2.isPresent());
        assertFalse(opt3.isPresent());
    }

    @Test
    @DisplayName("revokeCertificate should change status to REVOKED")
    void testRevokeCertificate() {
        Certificate cert = new Certificate(
                "CERT-TO-REVOKE", "EDU-1-1", "John", "john@campus.edu",
                "Event", "CSE", LocalDate.now(), 1L, 1L);
        when(certificateRepository.findByVerificationCode("CERT-TO-REVOKE")).thenReturn(Optional.of(cert));
        when(certificateRepository.save(any(Certificate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Certificate revoked = certificateService.revokeCertificate("CERT-TO-REVOKE");

        assertEquals("REVOKED", revoked.getStatus());
        assertFalse(revoked.isValid());
        verify(certificateRepository, times(1)).save(cert);
    }
}
