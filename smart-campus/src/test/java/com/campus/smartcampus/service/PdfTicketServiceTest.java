package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class PdfTicketServiceTest {

    private PdfTicketService pdfTicketService;
    private Registration registration;
    private Event event;

    @BeforeEach
    void setUp() {
        QrCodeService qrCodeService = new QrCodeService();
        pdfTicketService = new PdfTicketService(qrCodeService);

        event = new Event();
        event.setId(1L);
        event.setTitle("AI & Robotics Symposium 2026");
        event.setDescription("Annual academic gathering of AI and robotics enthusiasts.");
        event.setDepartment("Computer Science and Engineering");
        event.setDate(LocalDate.now().plusDays(10));
        event.setTime(LocalTime.of(10, 0));
        event.setVenue("Sir MV Hall, Main Academic Block");
        event.setType("Symposium");
        event.setCapacity(100);
        event.setPrice(0.0);
        event.setIsPaid(false);

        registration = new Registration();
        registration.setId(101L);
        registration.setEventId(1L);
        registration.setEvent(event);
        registration.setStudentName("Aditi Rao");
        registration.setEmail("aditi.rao@campus.edu");
        registration.setDepartment("Computer Science and Engineering");
        registration.setTickets(1);
        registration.setToken("SC-E1-ADITI123");
        registration.setStatus("CONFIRMED");
    }

    @Test
    void testGenerateTicketPdf() {
        byte[] pdfBytes = pdfTicketService.generateTicketPdf(registration);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500);

        // Verify PDF header magic bytes: %PDF-
        String header = new String(pdfBytes, 0, 5);
        assertEquals("%PDF-", header);
    }

    @Test
    void testGenerateCertificatePdf() {
        byte[] certBytes = pdfTicketService.generateCertificatePdf(registration);
        assertNotNull(certBytes);
        assertTrue(certBytes.length > 500);

        // Verify PDF header magic bytes: %PDF-
        String header = new String(certBytes, 0, 5);
        assertEquals("%PDF-", header);
    }

    @Test
    void testGenerateConferenceBadge() {
        byte[] badgeBytes = pdfTicketService.generateConferenceBadge(registration);
        assertNotNull(badgeBytes);
        assertTrue(badgeBytes.length > 500);

        // Verify PDF header magic bytes: %PDF-
        String header = new String(badgeBytes, 0, 5);
        assertEquals("%PDF-", header);
    }
}
