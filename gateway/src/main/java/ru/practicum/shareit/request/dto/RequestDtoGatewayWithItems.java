package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoGateway;

import java.util.List;

@Data
public class RequestDtoGatewayWithItems extends RequestDtoGateway {
    private List<ItemDtoGateway> items;
}
