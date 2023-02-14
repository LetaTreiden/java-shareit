package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDTO;

import java.util.List;

@Data
public class RequestDTOWithItems extends RequestDTO {

    private List<ItemDTO> items;

}
