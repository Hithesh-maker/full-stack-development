package com.campus.smartcampus.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "event_sessions")
public class EventSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "session_title", nullable = false)
    private String sessionTitle;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "speaker")
    private String speaker;

    @Column(name = "room_venue")
    private String roomVenue;

    @Column(name = "session_type", length = 32)
    private String sessionType = "SESSION"; // KEYNOTE, WORKSHOP, PANEL, BREAK, NETWORKING

    @Column(name = "session_order")
    private Integer sessionOrder = 1;

    public EventSession() {
    }

    public EventSession(Event event, String sessionTitle, LocalTime startTime, LocalTime endTime,
                        String speaker, String roomVenue, String sessionType, Integer sessionOrder) {
        this.event = event;
        this.sessionTitle = sessionTitle;
        this.startTime = startTime;
        this.endTime = endTime;
        this.speaker = speaker;
        this.roomVenue = roomVenue;
        this.sessionType = sessionType;
        this.sessionOrder = sessionOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getRoomVenue() {
        return roomVenue;
    }

    public void setRoomVenue(String roomVenue) {
        this.roomVenue = roomVenue;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public Integer getSessionOrder() {
        return sessionOrder;
    }

    public void setSessionOrder(Integer sessionOrder) {
        this.sessionOrder = sessionOrder;
    }
}
