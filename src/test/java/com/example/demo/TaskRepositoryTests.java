package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class TaskRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository repository;

    @Test
    public void testCreateTask(){
        Task task = new Task();
        task.setName("Test Task");
        task.setDescription("Task designed for testing!");
        task.setUsername("testUser");
        task.setDeadline("2023-01-04");
        task.setCompletionStatus(false);

        Task savedTask = repository.save(task);

        Task existTask = entityManager.find(Task.class, savedTask.getId());

        assertThat(task.getUsername()).isEqualTo(existTask.getUsername());
    }
}
