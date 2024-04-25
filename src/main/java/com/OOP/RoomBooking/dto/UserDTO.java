package com.OOP.RoomBooking.dto;

public class UserDTO {
    private Long userID;
    private String email;
    private String name;

    // Constructor, getters and setters
    public UserDTO(Long userID, String email, String name) {
        this.userID = userID;
        this.email = email;
        this.name = name;
    }

    public Long getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}