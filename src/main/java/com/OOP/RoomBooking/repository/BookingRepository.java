package com.OOP.RoomBooking.repository;

import com.OOP.RoomBooking.model.Booking;
import com.OOP.RoomBooking.model.Room;
import com.OOP.RoomBooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND ((b.dateOfBooking >= :dateTimeFrom AND b.dateOfBooking <= :dateTimeTo) OR (b.timeFrom >= :dateTimeFrom AND b.timeTo <= :dateTimeTo))")
    List<Booking> findBookingsByRoomAndDate(@Param("roomId") Long roomId, @Param("dateTimeFrom") LocalDateTime dateTimeFrom, @Param("dateTimeTo") LocalDateTime dateTimeTo);

    List<Booking> findByUser(User user);
    @Transactional
    void deleteByRoomId(Long roomId);
}