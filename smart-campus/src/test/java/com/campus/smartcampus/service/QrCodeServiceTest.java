package com.campus.smartcampus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QrCodeServiceTest {

    private QrCodeService qrCodeService;

    @BeforeEach
    void setUp() {
        qrCodeService = new QrCodeService();
    }

    @Test
    void testGenerateQrCodePng() {
        byte[] bytes = qrCodeService.generateQrCodePng("SC-TEST-TOKEN-12345", 200, 200);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
        // Verify PNG magic bytes: 0x89 0x50 0x4E 0x47
        assertEquals((byte) 0x89, bytes[0]);
        assertEquals((byte) 'P', bytes[1]);
        assertEquals((byte) 'N', bytes[2]);
        assertEquals((byte) 'G', bytes[3]);
    }

    @Test
    void testGenerateQrCodeBase64() {
        String base64 = qrCodeService.generateQrCodeBase64("SC-TEST-TOKEN-12345", 200, 200);
        assertNotNull(base64);
        assertTrue(base64.startsWith("data:image/png;base64,"));
        assertTrue(base64.length() > 50);
    }
}
