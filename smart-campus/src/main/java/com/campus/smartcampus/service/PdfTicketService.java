package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfTicketService {

    private final QrCodeService qrCodeService;

    // Educare University brand palette
    private static final Color NAVY_DARK = new Color(15, 23, 42);      // #0f172a
    private static final Color NAVY_PRIMARY = new Color(30, 41, 59);   // #1e293b
    private static final Color CRIMSON = new Color(153, 27, 27);       // #991b1b
    private static final Color GOLD = new Color(180, 83, 9);          // #b45309
    private static final Color GOLD_LIGHT = new Color(254, 243, 199);  // #fef3c7
    private static final Color SLATE_GRAY = new Color(100, 116, 139);  // #64748b
    private static final Color BG_LIGHT = new Color(248, 250, 252);    // #f8fafc
    private static final Color BORDER_GRAY = new Color(226, 232, 240); // #e2e8f0

    public PdfTicketService(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    public byte[] generateTicketPdf(Registration registration) {
        Event event = registration.getEvent();
        String token = registration.getToken() != null ? registration.getToken() : ("SC-" + registration.getId());

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // 1. Header Banner
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);

            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(NAVY_DARK);
            headerCell.setPadding(18);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            Font univFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.WHITE);
            Paragraph univPara = new Paragraph("EDUCARE UNIVERSITY", univFont);
            univPara.setAlignment(Element.ALIGN_CENTER);

            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(203, 213, 225));
            Paragraph subPara = new Paragraph("OFFICIAL CAMPUS EVENT ADMISSION PASS", subFont);
            subPara.setAlignment(Element.ALIGN_CENTER);

            headerCell.addElement(univPara);
            headerCell.addElement(subPara);
            headerTable.addCell(headerCell);
            document.add(headerTable);

            // Spacer
            document.add(new Paragraph("\n"));

            // 2. Main Ticket Body (Left: Event & Attendee Info, Right: QR Code & Verification)
            PdfPTable mainTable = new PdfPTable(2);
            mainTable.setWidthPercentage(100);
            mainTable.setWidths(new float[]{65f, 35f});

            // Left Column: Event & Attendee Info
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorderColor(BORDER_GRAY);
            leftCell.setBackgroundColor(BG_LIGHT);
            leftCell.setPadding(16);

            // Event Title
            String eventTitle = event != null ? event.getTitle() : "Campus Event";
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, CRIMSON);
            leftCell.addElement(new Paragraph(eventTitle, titleFont));

            // Department & Type Badges
            String deptAndType = (event != null ? event.getDepartment() : registration.getDepartment()) +
                    (event != null ? " • " + event.getType() : "");
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, SLATE_GRAY);
            Paragraph metaPara = new Paragraph(deptAndType, metaFont);
            metaPara.setSpacingAfter(10f);
            leftCell.addElement(metaPara);

            // Event Details Grid inside left cell
            PdfPTable infoGrid = new PdfPTable(2);
            infoGrid.setWidthPercentage(100);
            infoGrid.setWidths(new float[]{35f, 65f});

            addInfoRow(infoGrid, "Date & Time",
                    (event != null ? event.getDate().toString() : "TBD") + " at " +
                    (event != null && event.getTime() != null ? event.getTime().toString() : "09:00"));

            addInfoRow(infoGrid, "Venue", event != null ? event.getVenue() : "Main Campus");
            if (event != null) {
                addInfoRow(infoGrid, "Building Location", event.getBuildingBlock());
            }
            addInfoRow(infoGrid, "Attendee", registration.getStudentName());
            addInfoRow(infoGrid, "Student Email", registration.getEmail());
            addInfoRow(infoGrid, "Department", registration.getDepartment());
            addInfoRow(infoGrid, "Tickets Booked", String.valueOf(registration.getTickets()));
            addInfoRow(infoGrid, "Admission Type", (event != null && event.isPaid())
                    ? "PAID (" + event.getFormattedPrice() + ")" : "COMPLIMENTARY / FREE");
            addInfoRow(infoGrid, "Pass Status", registration.getStatus());

            leftCell.addElement(infoGrid);
            mainTable.addCell(leftCell);

            // Right Column: QR Code & Security Pass ID
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorderColor(BORDER_GRAY);
            rightCell.setBackgroundColor(Color.WHITE);
            rightCell.setPadding(16);
            rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            Font qrHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, NAVY_DARK);
            Paragraph qrHead = new Paragraph("SCAN FOR ENTRY", qrHeaderFont);
            qrHead.setAlignment(Element.ALIGN_CENTER);
            rightCell.addElement(qrHead);

            // Generate ZXing QR Image
            byte[] qrBytes = qrCodeService.generateQrCodePng(token, 160, 160);
            Image qrImage = Image.getInstance(qrBytes);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            qrImage.scalePercent(80);
            rightCell.addElement(qrImage);

            // Pass Token
            Font tokenLabelFont = FontFactory.getFont(FontFactory.HELVETICA, 8, SLATE_GRAY);
            Paragraph tokenLabel = new Paragraph("PASS CODE / TOKEN", tokenLabelFont);
            tokenLabel.setAlignment(Element.ALIGN_CENTER);
            rightCell.addElement(tokenLabel);

            Font tokenFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 11, NAVY_DARK);
            Paragraph tokenPara = new Paragraph(token, tokenFont);
            tokenPara.setAlignment(Element.ALIGN_CENTER);
            rightCell.addElement(tokenPara);

            // Fast Track Check-in badge
            Font scanHintFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, SLATE_GRAY);
            Paragraph scanHint = new Paragraph("Fast-track live door check-in verified", scanHintFont);
            scanHint.setAlignment(Element.ALIGN_CENTER);
            rightCell.addElement(scanHint);

            mainTable.addCell(rightCell);
            document.add(mainTable);

            // 3. Entry Rules & Security Instructions
            document.add(new Paragraph("\n"));
            PdfPTable termsTable = new PdfPTable(1);
            termsTable.setWidthPercentage(100);

            PdfPCell termsCell = new PdfPCell();
            termsCell.setBackgroundColor(new Color(241, 245, 249));
            termsCell.setPadding(12);
            termsCell.setBorderColor(BORDER_GRAY);

            Font termsHeadFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, NAVY_DARK);
            termsCell.addElement(new Paragraph("CAMPUS ADMISSION INSTRUCTIONS & TERMS", termsHeadFont));

            Font termsBodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(71, 85, 105));
            termsCell.addElement(new Paragraph("1. Present this digital PDF pass on your mobile phone or as a printout at the door.", termsBodyFont));
            termsCell.addElement(new Paragraph("2. University security / coordinators will scan your QR code for immediate automated check-in.", termsBodyFont));
            termsCell.addElement(new Paragraph("3. Carry your University Student ID card along with this pass.", termsBodyFont));
            termsCell.addElement(new Paragraph("4. This ticket is non-transferable and valid only for the named student.", termsBodyFont));

            termsTable.addCell(termsCell);
            document.add(termsTable);

            // 4. Footer Note
            document.add(new Paragraph("\n"));
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, SLATE_GRAY);
            Paragraph footer = new Paragraph("Educare University • Office of Student Activities & Campus Life • Generated automatically via Smart Campus Portal", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF ticket: " + e.getMessage(), e);
        }
    }

    public byte[] generateCertificatePdf(Registration registration) {
        return generateCertificatePdf(registration, null);
    }

    public byte[] generateCertificatePdf(Registration registration, com.campus.smartcampus.entity.Certificate certificate) {
        Event event = registration.getEvent();
        String studentName = registration.getStudentName() != null ? registration.getStudentName() : "Student";
        String department = registration.getDepartment() != null ? registration.getDepartment() : "General Studies";
        String eventTitle = event != null ? event.getTitle() : "Campus Event";
        String eventDate = event != null && event.getDate() != null ?
                event.getDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")) :
                LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        String venue = event != null && event.getVenue() != null ? event.getVenue() : "Main Campus";
        String verificationCode = (certificate != null && certificate.getVerificationCode() != null)
                ? certificate.getVerificationCode()
                : ("CERT-" + (registration.getToken() != null ? registration.getToken() : ("R" + registration.getId())));
        String certId = "EDU-CERT-" + verificationCode;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // A4 Landscape: 842 x 595 points
            Document document = new Document(PageSize.A4.rotate(), 40, 40, 40, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            // Outer Frame Table
            PdfPTable frameTable = new PdfPTable(1);
            frameTable.setWidthPercentage(100);

            PdfPCell frameCell = new PdfPCell();
            frameCell.setBorderWidth(4f);
            frameCell.setBorderColor(GOLD);
            frameCell.setBackgroundColor(Color.WHITE);
            frameCell.setPadding(24);

            // Inner Decorative Table
            PdfPTable innerTable = new PdfPTable(1);
            innerTable.setWidthPercentage(100);

            // University Header
            Font crestFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, GOLD);
            Paragraph crest = new Paragraph("★ EDUCARE UNIVERSITY ★", crestFont);
            crest.setAlignment(Element.ALIGN_CENTER);
            innerTable.addCell(createCleanCell(crest));

            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_GRAY);
            Paragraph subHeader = new Paragraph("OFFICE OF ACADEMIC AFFAIRS & STUDENT DEVELOPMENT", subHeaderFont);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            innerTable.addCell(createCleanCell(subHeader));

            // Spacing
            innerTable.addCell(createCleanCell(new Paragraph(" ")));

            // Certificate Title
            Font certTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 26, NAVY_DARK);
            Paragraph certTitle = new Paragraph("CERTIFICATE OF PARTICIPATION", certTitleFont);
            certTitle.setAlignment(Element.ALIGN_CENTER);
            innerTable.addCell(createCleanCell(certTitle));

            // Gold divider line
            Font lineFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, GOLD);
            Paragraph linePara = new Paragraph("═════════════════════════════════════════════", lineFont);
            linePara.setAlignment(Element.ALIGN_CENTER);
            innerTable.addCell(createCleanCell(linePara));

            // Intro text
            Font introFont = FontFactory.getFont(FontFactory.HELVETICA, 11, NAVY_PRIMARY);
            Paragraph intro = new Paragraph("This is to proudly certify that", introFont);
            intro.setAlignment(Element.ALIGN_CENTER);
            innerTable.addCell(createCleanCell(intro));

            // Recipient Name
            Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, CRIMSON);
            Paragraph namePara = new Paragraph(studentName.toUpperCase(), nameFont);
            namePara.setAlignment(Element.ALIGN_CENTER);
            innerTable.addCell(createCleanCell(namePara));

            // Department & Details
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11, NAVY_PRIMARY);
            Paragraph body1 = new Paragraph(
                    "from the Department of " + department +
                    " has actively participated and successfully completed the campus session", bodyFont);
            body1.setAlignment(Element.ALIGN_CENTER);
            innerTable.addCell(createCleanCell(body1));

            // Event Title
            Font evTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, NAVY_DARK);
            Paragraph evTitlePara = new Paragraph("“ " + eventTitle + " ”", evTitleFont);
            evTitlePara.setAlignment(Element.ALIGN_CENTER);
            innerTable.addCell(createCleanCell(evTitlePara));

            // Date & Venue
            Font detailFont = FontFactory.getFont(FontFactory.HELVETICA, 10, SLATE_GRAY);
            Paragraph detailPara = new Paragraph("Conducted on " + eventDate + " at " + venue + ".", detailFont);
            detailPara.setAlignment(Element.ALIGN_CENTER);
            innerTable.addCell(createCleanCell(detailPara));

            // Spacing
            innerTable.addCell(createCleanCell(new Paragraph(" ")));

            // Signatures & Verification Block
            PdfPTable footerTable = new PdfPTable(3);
            footerTable.setWidthPercentage(100);
            footerTable.setWidths(new float[]{35f, 30f, 35f});

            // Dean Signature
            PdfPCell sign1 = new PdfPCell();
            sign1.setBorder(Rectangle.NO_BORDER);
            sign1.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph signLine1 = new Paragraph("_________________________\nDr. Katherine Vance\nDean of Student Affairs",
                    FontFactory.getFont(FontFactory.HELVETICA, 9, NAVY_DARK));
            signLine1.setAlignment(Element.ALIGN_CENTER);
            sign1.addElement(signLine1);
            footerTable.addCell(sign1);

            // University Seal / Stamp & Verification QR
            PdfPCell sealCell = new PdfPCell();
            sealCell.setBorder(Rectangle.NO_BORDER);
            sealCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            // Generate Verification QR code (points directly to public verification portal)
            try {
                String verifyUrl = "http://localhost:8080/verify/certificate/" + verificationCode;
                byte[] qrBytes = qrCodeService.generateQrCodePng(verifyUrl, 85, 85);
                if (qrBytes != null && qrBytes.length > 0) {
                    Image qrImage = Image.getInstance(qrBytes);
                    qrImage.setAlignment(Element.ALIGN_CENTER);
                    qrImage.scalePercent(75);
                    sealCell.addElement(qrImage);
                }
            } catch (Exception ignored) {
            }

            Font sealFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, GOLD);
            Paragraph sealPara = new Paragraph("SCAN TO VERIFY\n" + certId, sealFont);
            sealPara.setAlignment(Element.ALIGN_CENTER);
            sealCell.addElement(sealPara);
            footerTable.addCell(sealCell);

            // Coordinator Signature
            PdfPCell sign2 = new PdfPCell();
            sign2.setBorder(Rectangle.NO_BORDER);
            sign2.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph signLine2 = new Paragraph("_________________________\nProf. Ronald Sterling\nEvent Program Director",
                    FontFactory.getFont(FontFactory.HELVETICA, 9, NAVY_DARK));
            signLine2.setAlignment(Element.ALIGN_CENTER);
            sign2.addElement(signLine2);
            footerTable.addCell(sign2);

            innerTable.addCell(createCleanCell(footerTable));

            frameCell.addElement(innerTable);
            frameTable.addCell(frameCell);
            document.add(frameTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating certificate PDF: " + e.getMessage(), e);
        }
    }

    private void addInfoRow(PdfPTable table, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, SLATE_GRAY);
        Font valFont = FontFactory.getFont(FontFactory.HELVETICA, 9, NAVY_DARK);

        PdfPCell c1 = new PdfPCell(new Paragraph(label, labelFont));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(4f);

        PdfPCell c2 = new PdfPCell(new Paragraph(value, valFont));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setPadding(4f);

        table.addCell(c1);
        table.addCell(c2);
    }

    private PdfPCell createCleanCell(Element element) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.addElement(element);
        return cell;
    }

    // Official Conference Lanyard Badge Generator (3.8" x 5.5" Badge format)
    public byte[] generateConferenceBadge(Registration registration) {
        Event event = registration.getEvent();
        String token = registration.getToken() != null ? registration.getToken() : ("SC-" + registration.getId());

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Standard conference name tag badge dimensions (approx 270pt x 390pt)
            Rectangle badgeSize = new Rectangle(270f, 390f);
            Document document = new Document(badgeSize, 14f, 14f, 14f, 14f);
            PdfWriter.getInstance(document, out);
            document.open();

            // Outer Badge Border Container
            PdfPTable containerTable = new PdfPTable(1);
            containerTable.setWidthPercentage(100);

            PdfPCell mainCell = new PdfPCell();
            mainCell.setBorderColor(NAVY_DARK);
            mainCell.setBorderWidth(2.5f);
            mainCell.setPadding(0);
            mainCell.setBackgroundColor(Color.WHITE);

            // 1. Lanyard Slot Punch Guide
            Paragraph slotGuide = new Paragraph("✂  --- PUNCH LANYARD SLOT HERE ---  ✂",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, SLATE_GRAY));
            slotGuide.setAlignment(Element.ALIGN_CENTER);
            slotGuide.setSpacingBefore(4f);
            slotGuide.setSpacingAfter(4f);
            mainCell.addElement(slotGuide);

            // 2. Navy Header Bar
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(NAVY_DARK);
            headerCell.setPadding(8f);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            Paragraph univPara = new Paragraph("EDUCARE UNIVERSITY",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE));
            univPara.setAlignment(Element.ALIGN_CENTER);
            headerCell.addElement(univPara);

            Paragraph passPara = new Paragraph("OFFICIAL EVENT DELEGATE PASS",
                    FontFactory.getFont(FontFactory.HELVETICA, 7, new Color(203, 213, 225)));
            passPara.setAlignment(Element.ALIGN_CENTER);
            headerCell.addElement(passPara);
            headerTable.addCell(headerCell);
            mainCell.addElement(headerTable);

            // 3. Attendee Name & Department Block
            Paragraph roleBadge = new Paragraph("★ STUDENT ATTENDEE ★",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, CRIMSON));
            roleBadge.setAlignment(Element.ALIGN_CENTER);
            roleBadge.setSpacingBefore(12f);
            mainCell.addElement(roleBadge);

            Paragraph namePara = new Paragraph(registration.getStudentName().toUpperCase(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 17, NAVY_DARK));
            namePara.setAlignment(Element.ALIGN_CENTER);
            namePara.setSpacingBefore(4f);
            mainCell.addElement(namePara);

            Paragraph deptPara = new Paragraph(registration.getDepartment(),
                    FontFactory.getFont(FontFactory.HELVETICA, 9, SLATE_GRAY));
            deptPara.setAlignment(Element.ALIGN_CENTER);
            deptPara.setSpacingBefore(2f);
            mainCell.addElement(deptPara);

            // 4. Event Information
            String eventTitle = event != null ? event.getTitle() : "Campus Event";
            Paragraph eventPara = new Paragraph(eventTitle,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, NAVY_PRIMARY));
            eventPara.setAlignment(Element.ALIGN_CENTER);
            eventPara.setSpacingBefore(10f);
            mainCell.addElement(eventPara);

            String eventDetails = (event != null ? event.getDate().toString() : "2026")
                    + (event != null ? " • " + event.getVenue() : "");
            Paragraph detailsPara = new Paragraph(eventDetails,
                    FontFactory.getFont(FontFactory.HELVETICA, 8, SLATE_GRAY));
            detailsPara.setAlignment(Element.ALIGN_CENTER);
            detailsPara.setSpacingBefore(2f);
            mainCell.addElement(detailsPara);

            // 5. Embedded ZXing QR Code
            byte[] qrBytes = qrCodeService.generateQrCodePng(token, 110, 110);
            if (qrBytes != null && qrBytes.length > 0) {
                Image qrImage = Image.getInstance(qrBytes);
                qrImage.setAlignment(Element.ALIGN_CENTER);
                qrImage.scaleToFit(95f, 95f);
                qrImage.setSpacingBefore(8f);
                mainCell.addElement(qrImage);
            }

            Paragraph tokenPara = new Paragraph(token,
                    FontFactory.getFont(FontFactory.COURIER_BOLD, 9, NAVY_DARK));
            tokenPara.setAlignment(Element.ALIGN_CENTER);
            tokenPara.setSpacingBefore(3f);
            tokenPara.setSpacingAfter(8f);
            mainCell.addElement(tokenPara);

            containerTable.addCell(mainCell);
            document.add(containerTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate conference lanyard badge: " + e.getMessage(), e);
        }
    }
}
