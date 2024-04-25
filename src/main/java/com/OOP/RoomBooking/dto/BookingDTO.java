package com.OOP.RoomBooking.dto;

import java.time.LocalDate;

public class BookingDTO {
    private String room;
    private Long roomID;
    private Long bookingID;
    private LocalDate dateOfBooking;
    private String timeFrom;
    private String timeTo;
    private String purpose;

    // Constructor, getters and setters
    public BookingDTO(String room, Long roomID, Long bookingID, LocalDate dateOfBooking, String timeFrom, String timeTo, String purpose) {
        this.room = room;
        this.roomID = roomID;
        this.bookingID = bookingID;
        this.dateOfBooking = dateOfBooking;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.purpose = purpose;
    }

    public String getRoom() {
        return room;
    }

    public Long getRoomID() {
        return roomID;
    }

    public Long getBookingID() {
        return bookingID;
    }

    public LocalDate getDateOfBooking() {
        return dateOfBooking;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public String getPurpose() {
        return purpose;
    }
}