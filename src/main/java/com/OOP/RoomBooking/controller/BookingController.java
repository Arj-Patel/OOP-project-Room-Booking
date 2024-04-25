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
import java.time.LocalDate;
import java.time.LocalTime;

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

    //TODO cannot add booking for all times of the day.
    @PostMapping
    public ResponseEntity<String> addBooking(@RequestBody Map<String, Object> payload) {
        Long userID = Long.valueOf((Integer) payload.get("userID"));
        Long roomID = Long.valueOf((Integer) payload.get("roomID"));
        String dateOfBookingStr = (String) payload.get("dateOfBooking");
        String timeFrom = (String) payload.get("timeFrom");
        String timeTo = (String) payload.get("timeTo");
        String purpose = (String) payload.get("purpose");

        Optional<User> user = userRepository.findById(userID);
        Optional<Room> room = roomRepository.findById(roomID);

        if (user.isPresent() && room.isPresent()) {
            // Convert String to LocalDate
            LocalDate dateOfBooking = LocalDate.parse(dateOfBookingStr);
            // Create new Booking
            Booking newBooking = new Booking();
            newBooking.setUser(user.get());
            newBooking.setRoom(room.get());
            newBooking.setDateOfBooking(dateOfBooking);
            newBooking.setTimeFrom(LocalTime.parse(timeFrom));
            newBooking.setTimeTo(LocalTime.parse(timeTo));
            newBooking.setPurpose(purpose);

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
        Optional<Booking> booking = bookingRepository.findById(updatedBooking.getBookingID());

        if (booking.isPresent()) {
            List<Booking> bookings = bookingRepository.findBookingsByRoomAndDate(booking.get().getRoom().getId(), updatedBooking.getDateOfBooking());
            if (bookings.isEmpty() || bookings.get(0).getBookingID().equals(updatedBooking.getBookingID())) {
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