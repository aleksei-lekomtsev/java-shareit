package ru.practicum.shareit.user;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;


@DataJpaTest
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmailAndIdNot() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@mail.ru");

        userRepository.save(user);

        List<User> actual = userRepository.findByEmailAndIdNot("email@mail.ru", 999L);
        assertFalse(actual.isEmpty());

        userRepository.deleteAll();
    }
}