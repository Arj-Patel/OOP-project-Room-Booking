package com.OOP.RoomBooking.controller;

import com.OOP.RoomBooking.model.Booking;
import com.OOP.RoomBooking.model.Room;
import com.OOP.RoomBooking.model.User;
import com.OOP.RoomBooking.repository.BookingRepository;
import com.OOP.RoomBooking.repository.RoomRepository;
import com.OOP.RoomBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.ZoneId;

import java.util.Optional;

@RestController
@RequestMapping("/book")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @PostMapping
    public ResponseEntity<String> addBooking(@RequestBody Booking newBooking) {
        Optional<User> user = userRepository.findById(newBooking.getUser().getUserID());
        Optional<Room> room = roomRepository.findById(newBooking.getRoom().getId());

        if (user.isPresent() && room.isPresent()) {
            // Convert LocalDate to Date
            Date dateOfBooking = Date.from(newBooking.getDateOfBooking().atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<Booking> bookings = bookingRepository.findBookingsByRoomAndDate(room.get().getId(), dateOfBooking);
            if (bookings.isEmpty()) {
                bookingRepository.save(newBooking);
                return ResponseEntity.ok("Booking created successfully");
            } else {
                return ResponseEntity.status(400).body("Room unavailable");
            }
        } else if (!user.isPresent()) {
            return ResponseEntity.status(404).body("User does not exist");
        } else if (!room.isPresent()) {
            return ResponseEntity.status(404).body("Room does not exist");
        } else {
            return ResponseEntity.status(400).body("Invalid date/time");
        }
    }

    @PatchMapping
    public ResponseEntity<String> editBooking(@RequestBody Booking updatedBooking) {
        Optional<Booking> booking = bookingRepository.findById(updatedBooking.getId());

        if (booking.isPresent()) {
            // Convert LocalDate to Date
            Date dateOfBooking = Date.from(updatedBooking.getDateOfBooking().atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<Booking> bookings = bookingRepository.findBookingsByRoomAndDate(booking.get().getRoom().getId(), dateOfBooking);
            if (bookings.isEmpty() || bookings.get(0).getId().equals(updatedBooking.getId())) {
                booking.get().setDateOfBooking(updatedBooking.getDateOfBooking());
                booking.get().setTimeFrom(updatedBooking.getTimeFrom());
                booking.get().setTimeTo(updatedBooking.getTimeTo());
                booking.get().setPurpose(updatedBooking.getPurpose());
                bookingRepository.save(booking.get());
                return ResponseEntity.ok("Booking modified successfully");
            } else {
                return ResponseEntity.status(400).body("Room unavailable");
            }
        } else {
            return ResponseEntity.status(404).body("Booking does not exist");
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteBooking(@RequestBody Booking bookingToDelete) {
        Optional<Booking> booking = bookingRepository.findById(bookingToDelete.getUser().getUserID());
        if (booking.isPresent()) {
            bookingRepository.delete(booking.get());
            return ResponseEntity.ok("Booking deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Booking does not exist");
        }
    }
}