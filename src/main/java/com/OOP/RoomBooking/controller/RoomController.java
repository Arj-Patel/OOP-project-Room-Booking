package com.OOP.RoomBooking.controller;

import com.OOP.RoomBooking.exception.CustomException;
import com.OOP.RoomBooking.model.Room;
import com.OOP.RoomBooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.OOP.RoomBooking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.*;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping
    public ResponseEntity<Object> getRooms(@RequestParam Integer capacity) {
        // Check if capacity is less than zero
        if (capacity != null && capacity <= 0) {
            throw new CustomException("Invalid parameters");
//            return ResponseEntity.status(400).body("Invalid parameters");
        }

        // Get all rooms
        List<Room> rooms = roomRepository.findAll();

        // Filter the rooms based on the provided capacity
        Stream<Room> roomStream = rooms.stream();
        if (capacity != null) {
            roomStream = roomStream.filter(room -> room.getRoomCapacity() >= capacity);
        }

        // Convert the stream back to a list
        List<Room> filteredRooms = roomStream.collect(Collectors.toList());

        // Define the date and time formatters
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Map the rooms to the response format
        List<Map<String, Object>> response = filteredRooms.stream().map(room -> {
            Map<String, Object> roomMap = new HashMap<>();
            roomMap.put("roomID", room.getId());
            roomMap.put("capacity", room.getRoomCapacity());

            List<Map<String, Object>> bookings = room.getBookings().stream().map(booking -> {
                Map<String, Object> bookingMap = new HashMap<>();
                bookingMap.put("bookingID", booking.getBookingID());
                bookingMap.put("dateOfBooking", booking.getDateOfBooking().format(dateFormatter));
                bookingMap.put("timeFrom", booking.getTimeFrom().format(timeFormatter));
                bookingMap.put("timeTo", booking.getTimeTo().format(timeFormatter));
                bookingMap.put("purpose", booking.getPurpose());
                bookingMap.put("userID", booking.getUser().getUserID());
                return bookingMap;
            }).collect(Collectors.toList());

            roomMap.put("booked", bookings);
            return roomMap;
        }).collect(Collectors.toList());

        // Return the response
        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<String> addRoom(@RequestBody Room newRoom) {

        Optional<Room> existingRoom = roomRepository.findByRoomName(newRoom.getRoomName());
        if (existingRoom.isPresent()) {
            throw new CustomException("Room already exists");
//            return ResponseEntity.status(400).body("Room already exists");
        }

        if (newRoom.getRoomCapacity() <= 0) {
            throw new CustomException("Invalid capacity");
//            return ResponseEntity.status(400).body("Invalid capacity");
        }

        roomRepository.save(newRoom);

        return ResponseEntity.ok("Room created successfully");
    }

    @PatchMapping
    public ResponseEntity<String> editRoom(@RequestBody Map<String, Object> payload) {
        Long roomID = Long.valueOf((Integer) payload.get("roomID"));
        String roomName = (String) payload.get("roomName");
        Integer roomCapacity = (Integer) payload.get("roomCapacity");

        Optional<Room> room = roomRepository.findById(roomID);
        if (!room.isPresent()) {
            throw new CustomException("Room does not exist");
        }

        Optional<Room> existingRoom = roomRepository.findByRoomName(roomName);
        if (existingRoom.isPresent() && !existingRoom.get().getId().equals(roomID)) {
            throw new CustomException("Room with given name already exists");
        }

        if (roomCapacity != null && roomCapacity <= 0) {
            throw new CustomException("Invalid capacity");
        }

        room.get().setRoomName(roomName);
        room.get().setRoomCapacity(roomCapacity);
        roomRepository.save(room.get());
        return ResponseEntity.ok("Room edited successfully");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteRoom(@RequestParam Long roomID) {
        Optional<Room> room = roomRepository.findById(roomID);

        if (room.isPresent()) {
            roomRepository.delete(room.get());
            return ResponseEntity.ok("Room deleted successfully");
        } else {
            throw new CustomException("Room does not exist");
        }
    }
}