package com.OOP.RoomBooking.controller;

import com.OOP.RoomBooking.model.Room;
import com.OOP.RoomBooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping
    public ResponseEntity<Object> getRooms(@RequestParam(required = false) String date,
                                           @RequestParam(required = false) String time,
                                           @RequestParam(required = false) Integer capacity) {
        // Convert the date and time strings to LocalDateTime objects
        LocalDateTime dateTime = null;
        if (date != null && time != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            dateTime = LocalDateTime.parse(date + " " + time, formatter);
        }

        // Get all rooms
        List<Room> rooms = roomRepository.findAll();

        // Filter the rooms based on the provided parameters
        Stream<Room> roomStream = rooms.stream();
        if (dateTime != null) {
            roomStream = roomStream.filter(room -> {
                // Check if the room is available at the requested date and time
                // This requires additional logic and depends on how you store bookings
                // For now, we will assume all rooms are available
                return true;
            });
        }
        if (capacity != null) {
            roomStream = roomStream.filter(room -> room.getRoomCapacity() >= capacity);
        }

        // Convert the stream back to a list
        List<Room> filteredRooms = roomStream.collect(Collectors.toList());

        // Return the filtered rooms
        return ResponseEntity.ok(filteredRooms);
    }

//    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    @PostMapping
    public ResponseEntity<String> addRoom(@RequestBody Room newRoom) {
        if (newRoom.getRoomName() == null) {
            return ResponseEntity.status(400).body("Room name must not be null");
        }

        // Check if a room with the same name already exists
        Optional<Room> existingRoom = roomRepository.findByRoomName(newRoom.getRoomName());
        if (existingRoom.isPresent()) {
            return ResponseEntity.status(400).body("Room already exists");
        }

        // Validate the capacity
        if (newRoom.getRoomCapacity() < 1) {
            return ResponseEntity.status(400).body("Invalid capacity");
        }

        // Save the new room to the database
        roomRepository.save(newRoom);

        return ResponseEntity.ok("Room created successfully");
    }

    @PatchMapping
    public ResponseEntity<String> editRoom(@RequestBody Room updatedRoom) {
        Optional<Room> room = roomRepository.findById(updatedRoom.getId());

        if (room.isPresent()) {
            if (updatedRoom.getRoomCapacity() < 1) {
                return ResponseEntity.status(400).body("Invalid capacity");
            } else {
                room.get().setRoomName(updatedRoom.getRoomName());
                room.get().setRoomCapacity(updatedRoom.getRoomCapacity());
                roomRepository.save(room.get());
                return ResponseEntity.ok("Room edited successfully");
            }
        } else {
            return ResponseEntity.status(404).body("Room does not exist");
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteRoom(@RequestBody Room roomToDelete) {
        Optional<Room> room = roomRepository.findById(roomToDelete.getId());

        if (room.isPresent()) {
            roomRepository.delete(room.get());
            return ResponseEntity.ok("Room deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Room does not exist");
        }
    }
}