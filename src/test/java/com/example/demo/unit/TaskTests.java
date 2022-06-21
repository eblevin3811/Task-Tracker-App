package com.example.demo.unit;
import com.example.demo.Task;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

public class TaskTests {

    @Test
    public void testBlankConstructor(){
        Task task = new Task();
        assertThat(task.getName().equals(""));
    }

    @Test
    public void testConstructor(){
        Task task = new Task("taskname", "taskdesc", "testuser", new Date(2022, 03, 03), true);
        assertThat(task.getName().equals("taskname") && task.getDescription().equals("taskdesc") &&
                task.getUsername().equals("testUser") && task.getDeadline().equals(new Date(2022, 03, 03)) && task.getCompletionStatus() == true);
    }

    @Test
    public void testSetGetId(){
        Task task = new Task();
        task.setId(123);
        assertThat(task.getId() == 123);
    }

    @Test
    public void testSetGetName(){
        Task task = new Task();
        task.setName("testname");
        assertThat(task.getName().equals("testname"));
    }

    @Test
    public void testSetGetDesc(){
        Task task = new Task();
        task.setDescription("testdesc");
        assertThat(task.getDescription().equals("testdesc"));
    }

    @Test
    public void testSetGetDeadline(){
        Task task = new Task();
        Date date = new Date(2022, 2, 2);
        task.setDeadline(date);
        assertThat(task.getDeadline().equals(date));
    }

    @Test
    public void testSetGetCompletion(){
        Task task = new Task();
        task.setCompletionStatus(true);
        assertThat(task.getCompletionStatus() == true);
    }

    @Test
    public void testSetGetUsername(){
        Task task = new Task();
        task.setUsername("testuser");
        assertThat(task.getUsername().equals("testuser"));
    }
}
