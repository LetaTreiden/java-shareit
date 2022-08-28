package ru.practicum.booking;

import org.springframework.stereotype.Service;
import ru.practicum.item.Item;
import ru.practicum.user.User;

@Service
public class BookingMapper {

    public BookingDTO toBookingDto(Booking booking) {
        return BookingDTO.builder().id(booking.getId()).start(booking.getStart()).end(booking.getEnd()).item(toBookingItem(booking.getItem())).booker(toUserBooking(booking.getBooker())).bookingStatus(booking.getBookingStatus()).build();
    }

    public Booking toBooking(BookingDTO bookingDto) {
        return Booking.builder().id(bookingDto.getId()).start(bookingDto.getStart()).end(bookingDto.getEnd()).item(toItem(bookingDto.getItem())).booker(toUser(bookingDto.getBooker())).bookingStatus(bookingDto.getBookingStatus()).build();
    }

    private BookingDTO.Item toBookingItem(Item item) {
        return BookingDTO.Item.builder().id(item.getId()).name(item.getName()).description(item.getDescription()).available(item.getAvailable()).request(item.getRequest()).build();
    }

    private Item toItem(BookingDTO.Item bookingItem) {
        return Item.builder().id(bookingItem.getId()).name(bookingItem.getName()).description(bookingItem.getDescription()).available(bookingItem.isAvailable()).request(bookingItem.getRequest()).build();
    }

    private BookingDTO.User toUserBooking(User user) {
        return BookingDTO.User.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();
    }

    private User toUser(BookingDTO.User bookingUser) {
        return User.builder().id(bookingUser.getId()).name(bookingUser.getName()).email(bookingUser.getEmail()).build();
    }
}