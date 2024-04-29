package com.OOP.RoomBooking.controller;

import com.OOP.RoomBooking.dto.BookingDTO;
import com.OOP.RoomBooking.dto.UserDTO;
import com.OOP.RoomBooking.model.Booking;
import com.OOP.RoomBooking.model.User;
import com.OOP.RoomBooking.repository.BookingRepository;
import com.OOP.RoomBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.OOP.RoomBooking.model.Room;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        String password = (String) payload.get("password");

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            if (user.get().getPassword().equals(password)) {
                return ResponseEntity.ok("Login Successful");
            } else {
                return ResponseEntity.status(403).body("Username/Password Incorrect");
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
            UserDTO userDTO = new UserDTO(user.get().getUserID(), user.get().getEmail(), user.get().getName());
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.status(404).body("User does not exist");
        }
    }

    @GetMapping("/history")
    public ResponseEntity<Object> getBookingHistory(@RequestParam Long userID) {
        Optional<User> user = userRepository.findById(userID);

        if (user.isPresent()) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            List<BookingDTO> bookingDTOs = bookingRepository.findByUser(user.get()).stream()
                    .filter(booking -> !booking.getDateOfBooking().toLocalDate().isAfter(LocalDate.now())) // Include bookings for today and previous dates
                    .map(booking -> {
                        Room room = booking.getRoom();
                        if (room == null) {
                            return null;
                        }
                        return new BookingDTO(
                                room.getRoomName(),
                                room.getId(),
                                booking.getBookingID(),
                                booking.getDateOfBooking().format(dateFormatter),
                                booking.getTimeFrom().format(timeFormatter),
                                booking.getTimeTo().format(timeFormatter),
                                booking.getPurpose());
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(bookingDTOs);
        } else {
            return ResponseEntity.status(404).body("User does not exist");
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Object> getUpcomingBookings(@RequestParam Long userID) {
        Optional<User> user = userRepository.findById(userID);

        if (user.isPresent()) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            List<BookingDTO> bookingDTOs = bookingRepository.findByUser(user.get()).stream()
                    .filter(booking -> booking.getDateOfBooking().toLocalDate().isAfter(LocalDate.now())) // Include bookings strictly after today
                    .map(booking -> {
                        Room room = booking.getRoom();
                        if (room == null) {
                            return null;
                        }
                        return new BookingDTO(
                                room.getRoomName(),
                                room.getId(),
                                booking.getBookingID(),
                                booking.getDateOfBooking().format(dateFormatter),
                                booking.getTimeFrom().format(timeFormatter),
                                booking.getTimeTo().format(timeFormatter),
                                booking.getPurpose());
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(bookingDTOs);
        } else {
            return ResponseEntity.status(404).body("User does not exist");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(user.getUserID(), user.getEmail(), user.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

}