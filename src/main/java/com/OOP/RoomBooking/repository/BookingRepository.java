package com.OOP.RoomBooking.repository;

import com.OOP.RoomBooking.model.Booking;
import com.OOP.RoomBooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.dateOfBooking = :dateOfBooking")
    List<Booking> findBookingsByRoomAndDate(@Param("roomId") Long roomId, @Param("dateOfBooking") Date dateOfBooking);
    List<Booking> findByUser(User user);
}