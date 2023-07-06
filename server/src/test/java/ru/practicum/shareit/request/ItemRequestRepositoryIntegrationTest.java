package ru.practicum.shareit.request;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;


import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;


@DataJpaTest
class ItemRequestRepositoryIntegrationTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");
        itemRequest = new ItemRequest();
        itemRequest.setDescription("expected");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
    }

    @Test
    void findAllByRequestorIdNot() {
        PageRequest   page   = new ItemRequestPageRequest(0, 10);

        userRepository.save(user);
        itemRequestRepository.save(itemRequest);

        Page<ItemRequest> actual = itemRequestRepository.findAllByRequestorIdNot(999L, page);
        assertFalse(actual.getContent().isEmpty());
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
        userRepository.save(user);
        itemRequestRepository.save(itemRequest);

        List<ItemRequest> actual = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(user.getId());
        assertFalse(actual.isEmpty());
    }

    @AfterEach
    void afterEach() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }
}