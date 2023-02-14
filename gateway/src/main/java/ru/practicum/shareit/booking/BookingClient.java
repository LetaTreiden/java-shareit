package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDtoGateway;
import ru.practicum.shareit.booking.dto.StateGateway;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, StateGateway stateGateway, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", stateGateway.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getByOwner(long userId, StateGateway stateGateway, Integer from,
                                             Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", stateGateway.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }


    public ResponseEntity<Object> bookingItem(long userId, BookingDtoGateway bookingDto) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> bookingStatus(long userId, Long bookingId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved + "", userId);
    }

    public ResponseEntity<Object> get(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }
}
