package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDTOBooking {
    private Long id;
    private String name;
    private String description;
    @NotNull
    @JsonProperty(value = "available")
    private Boolean isAvailable;
    private Booking lastBooking;
    private Booking nextBooking;
}
