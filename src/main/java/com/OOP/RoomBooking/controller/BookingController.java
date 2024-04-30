package com.OOP.RoomBooking.controller;

import com.OOP.RoomBooking.exception.CustomException;
import com.OOP.RoomBooking.model.Booking;
import com.OOP.RoomBooking.model.Room;
import com.OOP.RoomBooking.model.User;
import com.OOP.RoomBooking.repository.BookingRepository;
import com.OOP.RoomBooking.repository.RoomRepository;
import com.OOP.RoomBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeParseException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    public ResponseEntity<String> addBooking(@RequestBody Map<String, Object> payload) {
        Long userID = Long.valueOf((Integer) payload.get("userID"));
        Long roomID = Long.valueOf((Integer) payload.get("roomID"));
        String dateOfBookingStr = (String) payload.get("dateOfBooking");
        String timeFromStr = (String) payload.get("timeFrom");
        String timeToStr = (String) payload.get("timeTo");
        String purpose = (String) payload.get("purpose");

        Optional<Room> room = roomRepository.findById(roomID);
        if (!room.isPresent()) {
            throw new CustomException("Room does not exist");
        }

        Optional<User> user = userRepository.findById(userID);
        if (!user.isPresent()) {
            throw new CustomException("User does not exist");
        }

        try {
            // Convert String to LocalDateTime
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateOfBooking = LocalDate.parse(dateOfBookingStr, dateFormatter);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime timeFrom = LocalTime.parse(timeFromStr, timeFormatter);
            LocalTime timeTo = LocalTime.parse(timeToStr, timeFormatter);
            LocalDateTime dateTimeFrom = LocalDateTime.of(dateOfBooking, timeFrom);
            LocalDateTime dateTimeTo = LocalDateTime.of(dateOfBooking, timeTo);

            if (dateTimeTo.isBefore(dateTimeFrom)) {
                throw new CustomException("Invalid date/time");
            }

            if (dateTimeFrom.isBefore(LocalDateTime.now())) {
                throw new CustomException("Invalid date/time");
            }

            // Create new Booking
            Booking newBooking = new Booking();
            newBooking.setUser(user.get());
            newBooking.setRoom(room.get());
            newBooking.setDateOfBooking(dateTimeFrom);
            newBooking.setTimeFrom(dateTimeFrom);
            newBooking.setTimeTo(dateTimeTo);
            newBooking.setPurpose(purpose);

            List<Booking> bookings = bookingRepository.findBookingsByRoomAndDate(room.get().getId(), dateTimeFrom, dateTimeTo);
            if (bookings.isEmpty()) {
                bookingRepository.save(newBooking);
                return ResponseEntity.ok("Booking created successfully");
            } else {
                throw new CustomException("Room unavailable");
            }
        } catch (DateTimeParseException e) {
            throw new CustomException("Invalid date/time");
        }
    }

    @PatchMapping
    public ResponseEntity<String> editBooking(@RequestBody Map<String, Object> payload) {
        Long userID = Long.valueOf((Integer) payload.get("userID"));
        Long roomID = Long.valueOf((Integer) payload.get("roomID"));
        Long bookingID = Long.valueOf((Integer) payload.get("bookingID"));
        String dateOfBookingStr = (String) payload.get("dateOfBooking");
        String timeFromStr = (String) payload.get("timeFrom");
        String timeToStr = (String) payload.get("timeTo");
        String purpose = (String) payload.get("purpose");

        Optional<Room> room = roomRepository.findById(roomID);
        if (!room.isPresent()) {
            throw new CustomException("Room does not exist");
        }

        Optional<Booking> booking = bookingRepository.findById(bookingID);
        if (!booking.isPresent()) {
            throw new CustomException("Booking does not exist");
        }

        Optional<User> user = userRepository.findById(userID);
        if (!user.isPresent()) {
            throw new CustomException("User does not exist");
        }

        try {
            // Convert String to LocalDateTime
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateOfBooking = LocalDate.parse(dateOfBookingStr, dateFormatter);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime timeFrom = LocalTime.parse(timeFromStr, timeFormatter);
            LocalTime timeTo = LocalTime.parse(timeToStr, timeFormatter);
            LocalDateTime dateTimeFrom = LocalDateTime.of(dateOfBooking, timeFrom);
            LocalDateTime dateTimeTo = LocalDateTime.of(dateOfBooking, timeTo);

            if (dateTimeTo.isBefore(dateTimeFrom)) {
                throw new CustomException("Invalid date/time");
            }

            if (dateTimeFrom.isBefore(LocalDateTime.now())) {
                throw new CustomException("Invalid date/time");
            }

            // Update Booking
            Booking updatedBooking = booking.get();
            updatedBooking.setUser(user.get());
            updatedBooking.setRoom(room.get());
            updatedBooking.setDateOfBooking(dateTimeFrom);
            updatedBooking.setTimeFrom(dateTimeFrom);
            updatedBooking.setTimeTo(dateTimeTo);
            updatedBooking.setPurpose(purpose);

            List<Booking> bookings = bookingRepository.findBookingsByRoomAndDate(room.get().getId(), dateTimeFrom, dateTimeTo);
            if (bookings.isEmpty() || bookings.get(0).getBookingID().equals(bookingID)) {
                bookingRepository.save(updatedBooking);
                return ResponseEntity.ok("Booking modified successfully");
            } else {
                throw new CustomException("Room unavailable");
            }
        } catch (DateTimeParseException e) {
            throw new CustomException("Invalid date/time");
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteBooking(@RequestParam Long bookingID) {
        Optional<Booking> booking = bookingRepository.findById(bookingID);
        if (booking.isPresent()) {
            bookingRepository.delete(booking.get());
            return ResponseEntity.ok("Booking deleted successfully");
        } else {
            throw new CustomException("Booking does not exist");
//            return ResponseEntity.status(404).body("Booking does not exist");
        }
    }
}