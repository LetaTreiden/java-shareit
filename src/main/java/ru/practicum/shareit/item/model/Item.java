package ru.practicum.shareit.item.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String description;

    @NotNull
    @Column
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private User owner;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Item item = (Item) o;
        return id != 0 && Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
