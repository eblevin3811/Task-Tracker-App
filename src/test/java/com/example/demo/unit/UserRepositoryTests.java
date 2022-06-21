package com.example.demo.unit;

import com.example.demo.User;
import com.example.demo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    @Test
    public void testCreateUser(){
        User user = new User();
        user.setEmail("testuser@gmail.com");
        user.setPassword("pass");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setUsername("testUser");
        user.setEnabled(true);
        user.setGroupId(0);

        User savedUser = repository.save(user);

        User existUser = entityManager.find(User.class, savedUser.getId());

        assertThat(user.getEmail()).isEqualTo(existUser.getEmail());

        repository.delete(user);
    }

    @Test
    public void testFindByUsername(){
        User user = new User();
        user.setEmail("testuser2@gmail.com");
        user.setPassword("pass");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setUsername("testUser");
        user.setEnabled(true);
        user.setGroupId(0);

        repository.save(user);

        assertThat(repository.findByUsername("testUser") == user);

        repository.delete(user);
    }
}
