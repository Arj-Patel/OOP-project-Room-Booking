package com.OOP.RoomBooking.controller;

import com.OOP.RoomBooking.model.Booking;
import com.OOP.RoomBooking.model.User;
import com.OOP.RoomBooking.repository.BookingRepository;
import com.OOP.RoomBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginUser) {
        Optional<User> user = userRepository.findByEmail(loginUser.getEmail());

        if (user.isPresent()) {
            if (user.get().getPassword().equals(loginUser.getPassword())) {
                return ResponseEntity.ok("Login Successful");
            } else {
                return ResponseEntity.status(401).body("Username/Password Incorrect");
            }
        } else {
            return ResponseEntity.status(404).body("User does not exist");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signupUser(@RequestBody User newUser) {
        Optional<User> user = userRepository.findByEmail(newUser.getEmail());

        if (user.isPresent()) {
            return ResponseEntity.status(403).body("Forbidden, Account already exists");
        } else {
            userRepository.save(newUser);
            return ResponseEntity.ok("Account Creation Successful");
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getUserDetail(@RequestParam Long userID) {
        Optional<User> user = userRepository.findById(userID);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(404).body("User does not exist");
        }
    }

    @GetMapping("/history")
    public ResponseEntity<Object> getBookingHistory(@RequestParam Long userID) {
        Optional<User> user = userRepository.findById(userID);

        if (user.isPresent()) {
            List<Booking> bookings = bookingRepository.findByUser(user.get());
            return ResponseEntity.ok(bookings);
        } else {
            return ResponseEntity.status(404).body("User does not exist");
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Object> getUpcomingBookings(@RequestParam Long userID) {
        Optional<User> user = userRepository.findById(userID);

        if (user.isPresent()) {
            List<Booking> bookings = bookingRepository.findByUser(user.get())
                    .stream()
                    .filter(booking -> booking.getDateOfBooking().isAfter(LocalDate.now()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(bookings);
        } else {
            return ResponseEntity.status(404).body("User does not exist");
        }
    }

}