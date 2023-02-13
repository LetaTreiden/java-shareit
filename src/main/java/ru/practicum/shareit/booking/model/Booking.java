package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    @ToString.Exclude
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
}
