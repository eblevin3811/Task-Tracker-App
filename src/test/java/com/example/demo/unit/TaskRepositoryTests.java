package com.example.demo.unit;

import com.example.demo.Task;
import com.example.demo.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.sql.Date;
import java.util.Set;

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
        task.setDeadline((java.sql.Date) new Date(2023, 01, 04));
        task.setCompletionStatus(false);

        Task savedTask = repository.save(task);

        Task existTask = entityManager.find(Task.class, savedTask.getId());

        assertThat(task.getUsername()).isEqualTo(existTask.getUsername());

        repository.delete(task);
    }

    @Test
    public void testFindAllByUsername(){

        Task task1 = new Task();
        task1.setName("Test Task 1");
        task1.setDescription("Task 1 designed for testing!");
        task1.setUsername("testUser");
        task1.setDeadline((java.sql.Date) new Date(2023, 01, 04));
        task1.setCompletionStatus(false);
        repository.save(task1);

        Task task2 = new Task();
        task2.setName("Test Task 2");
        task2.setDescription("Task 2 designed for testing!");
        task2.setUsername("testUser");
        task2.setDeadline((java.sql.Date) new Date(2023, 01, 04));
        task2.setCompletionStatus(false);
        repository.save(task2);

        Set<Task> taskSet = repository.findAllByUsername("testUser");

        assertThat(taskSet.size() == 2);

        repository.delete(task1);
        repository.delete(task2);
    }

    @Test
    public void testFindByID(){
        Task task1 = new Task();
        task1.setName("Test Task 1");
        task1.setDescription("Task 1 designed for testing!");
        task1.setUsername("testUser");
        task1.setDeadline((java.sql.Date) new Date(2023, 01, 04));
        task1.setCompletionStatus(false);
        repository.save(task1);
        long id = task1.getId();

        Task task2 = new Task();
        task2.setName("Test Task 2");
        task2.setDescription("Task 2 designed for testing!");
        task2.setUsername("testUser");
        task2.setDeadline((java.sql.Date) new Date(2023, 01, 04));
        task2.setCompletionStatus(false);
        repository.save(task2);

        assertThat(repository.findById(id) == task1);

        repository.delete(task1);
        repository.delete(task2);
    }
}
