package ru.practicum.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.user.User;

import java.time.LocalDateTime;

    @Data
    @Builder
    public class Request {
        private Long id;
        private String description;
        private User requester;
        private LocalDateTime created;
    }

