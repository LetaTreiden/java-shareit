package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestorOrderByCreatedDesc(User requestor);

    @Query(nativeQuery = true, value = "SELECT * FROM REQUESTS" +
            " WHERE REQUESTOR_ID != ?1")
    Page<ItemRequest> findAllBy(Long userId, Pageable pageable);

}
