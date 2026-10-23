package com.campus.smartcampus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Time is required")
    private LocalTime time;

    @NotBlank(message = "Venue is required")
    private String venue;

    @NotBlank(message = "Event type is required")
    private String type;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @Min(value = 0, message = "Price cannot be negative")
    private Double price = 0.0;

    private Boolean isPaid = false;

    @Column(name = "registration_type", length = 32)
    private String registrationType = "INDIVIDUAL";

    @Column(name = "min_team_size")
    private Integer minTeamSize = 2;

    @Column(name = "max_team_size")
    private Integer maxTeamSize = 4;

    private String speakerName;

    private String speakerTitle;

    private String speakerCompany;

    @Column(length = 1000)
    private String speakerBio;

    private String speakerAvatarUrl;

    @jakarta.persistence.Transient
    private Integer availableSeats;

    @jakarta.persistence.Transient
    private Boolean soldOut;

    @jakarta.persistence.Transient
    private Double averageRating = 0.0;

    @jakarta.persistence.Transient
    private Long reviewCount = 0L;

    public Event() {
    }

    public boolean isPast() {
        return date != null && date.isBefore(LocalDate.now());
    }

    public boolean isToday() {
        return date != null && date.isEqual(LocalDate.now());
    }

    public boolean isUpcoming() {
        return date != null && !date.isBefore(LocalDate.now());
    }

    public Integer getAvailableSeats() {
        return availableSeats != null ? availableSeats : capacity;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
        this.soldOut = availableSeats != null && availableSeats <= 0;
    }

    public Boolean isSoldOut() {
        return soldOut != null ? soldOut : (availableSeats != null && availableSeats <= 0);
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Double getAverageRating() {
        return averageRating != null ? averageRating : 0.0;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getReviewCount() {
        return reviewCount != null ? reviewCount : 0L;
    }

    public void setReviewCount(Long reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Double getPrice() {
        return price != null ? price : 0.0;
    }

    public void setPrice(Double price) {
        this.price = price;
        this.isPaid = price != null && price > 0.0;
    }

    public Boolean getIsPaid() {
        return isPaid != null ? isPaid : false;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public boolean isPaid() {
        return Boolean.TRUE.equals(this.isPaid) && this.price != null && this.price > 0.0;
    }

    @jakarta.persistence.Transient
    public String getFormattedPrice() {
        if (!isPaid()) {
            return "Free";
        }
        return String.format("₹%.2f", price);
    }

    @jakarta.persistence.Transient
    public String getBuildingBlock() {
        if (venue == null || venue.isBlank()) {
            return "Main University Campus";
        }
        String lower = venue.toLowerCase();
        if (lower.contains("auditorium")) {
            return "Sir M. Visvesvaraya Hall (Main Academic Block, Floor 1)";
        } else if (lower.contains("lab") || lower.contains("tech")) {
            return "Ramanujan Computing Complex (Lab Wing, Floor 2)";
        } else if (lower.contains("seminar") || lower.contains("hall")) {
            return "Dr. APJ Abdul Kalam Seminar Hall (Block B, Floor 1)";
        } else if (lower.contains("ground") || lower.contains("open") || lower.contains("stadium")) {
            return "University Central Grounds & Sports Pavilion";
        } else if (lower.contains("library")) {
            return "Rabindranath Tagore Central Library (Reading Hall A)";
        }
        return venue + " (University Campus Zone)";
    }

    @jakarta.persistence.Transient
    public String getStarsDisplay() {
        if (averageRating == null || averageRating < 0.5) return "☆☆☆☆☆";
        int fullStars = (int) Math.round(averageRating);
        fullStars = Math.min(5, Math.max(1, fullStars));
        return "★".repeat(fullStars) + "☆".repeat(5 - fullStars);
    }

    public boolean hasSpeaker() {
        return speakerName != null && !speakerName.isBlank();
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public String getSpeakerTitle() {
        return speakerTitle;
    }

    public void setSpeakerTitle(String speakerTitle) {
        this.speakerTitle = speakerTitle;
    }

    public String getSpeakerCompany() {
        return speakerCompany;
    }

    public void setSpeakerCompany(String speakerCompany) {
        this.speakerCompany = speakerCompany;
    }

    public String getSpeakerBio() {
        return speakerBio;
    }

    public void setSpeakerBio(String speakerBio) {
        this.speakerBio = speakerBio;
    }

    public String getSpeakerAvatarUrl() {
        return speakerAvatarUrl;
    }

    public void setSpeakerAvatarUrl(String speakerAvatarUrl) {
        this.speakerAvatarUrl = speakerAvatarUrl;
    }

    public String getRegistrationType() {
        return registrationType != null ? registrationType : "INDIVIDUAL";
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = registrationType;
    }

    public Integer getMinTeamSize() {
        return minTeamSize != null ? minTeamSize : 2;
    }

    public void setMinTeamSize(Integer minTeamSize) {
        this.minTeamSize = minTeamSize;
    }

    public Integer getMaxTeamSize() {
        return maxTeamSize != null ? maxTeamSize : 4;
    }

    public void setMaxTeamSize(Integer maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    public boolean isTeamEvent() {
        return "TEAM".equalsIgnoreCase(this.registrationType) || "Hackathon".equalsIgnoreCase(this.type);
    }
}