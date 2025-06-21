package com.example.bookingapp.repository.booking;

import com.example.bookingapp.model.Booking;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.accommodation.id = :accommodationId "
            + "AND b.status != :status")
    boolean existsByAccommodationIdAndStatusNot(Long accommodationId,
                                                Booking.BookingStatus status);

    List<Booking> findByStatus(Booking.BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.accommodation.id = :accommodationId "
            + "AND b.status != :status "
            + "AND (b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate)")
    long countByAccommodationIdAndStatusNotAndDateOverlap(
            Long accommodationId, Booking.BookingStatus status,
            LocalDate checkInDate, LocalDate checkOutDate);
}
