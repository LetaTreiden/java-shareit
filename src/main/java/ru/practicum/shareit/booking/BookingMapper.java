package ru.practicum.shareit.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;

@Component
public class BookingMapper {
    static Logger logger = LoggerFactory.getLogger("log");

    public static BookingDTO toBookingDto(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem().getId())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDTO bookingDto, ItemRepository iRepo) {
        logger.info("Booking DTO to Booking process");
        Booking booking = new Booking();
        logger.info("" + bookingDto);
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(iRepo.getReferenceById(bookingDto.getItem()));
        booking.setBooker(UserMapper.toUser(bookingDto.getBooker()));

        if (bookingDto.getStatus() == null) {
            booking.setStatus(BookingStatus.WAITING);
        } else {
            booking.setStatus(bookingDto.getStatus());
        }
        logger.info("the process is finished");
        return booking;
    }
}