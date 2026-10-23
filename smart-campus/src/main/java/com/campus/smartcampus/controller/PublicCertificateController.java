package com.campus.smartcampus.controller;

import com.campus.smartcampus.entity.Certificate;
import com.campus.smartcampus.service.CertificateService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class PublicCertificateController {

    private final CertificateService certificateService;

    public PublicCertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/verify/certificate")
    public String verifyPortalHome(
            @RequestParam(required = false) String code,
            Model model) {
        if (code != null && !code.trim().isEmpty()) {
            return "redirect:/verify/certificate/" + code.trim();
        }
        model.addAttribute("searched", false);
        return "certificate-verify";
    }

    @GetMapping("/verify/certificate/{code}")
    public String verifyCertificateByCode(
            @PathVariable String code,
            Model model) {
        Optional<Certificate> certOpt = certificateService.verifyCertificate(code);
        model.addAttribute("searched", true);
        model.addAttribute("code", code);

        if (certOpt.isPresent()) {
            Certificate cert = certOpt.get();
            model.addAttribute("found", true);
            model.addAttribute("certificate", cert);
            model.addAttribute("isValid", cert.isValid());
        } else {
            model.addAttribute("found", false);
            model.addAttribute("certificate", null);
            model.addAttribute("isValid", false);
        }

        return "certificate-verify";
    }

    @GetMapping("/api/public/certificates/verify/{code}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verifyCertificateApi(@PathVariable String code) {
        Optional<Certificate> certOpt = certificateService.verifyCertificate(code);
        Map<String, Object> response = new HashMap<>();

        if (certOpt.isPresent()) {
            Certificate cert = certOpt.get();
            response.put("valid", cert.isValid());
            response.put("status", cert.getStatus());
            response.put("verificationCode", cert.getVerificationCode());
            response.put("certificateNumber", cert.getCertificateNumber());
            response.put("studentName", cert.getStudentName());
            response.put("eventName", cert.getEventName());
            response.put("department", cert.getDepartment());
            response.put("eventDate", cert.getEventDate() != null ? cert.getEventDate().toString() : null);
            response.put("issuedAt", cert.getIssuedAt() != null ? cert.getIssuedAt().toString() : null);
            return ResponseEntity.ok(response);
        } else {
            response.put("valid", false);
            response.put("status", "NOT_FOUND");
            response.put("message", "No certificate found matching the provided verification code.");
            return ResponseEntity.status(404).body(response);
        }
    }
}
