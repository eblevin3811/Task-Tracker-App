package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

public interface TaskRepository extends JpaRepository<Task, Long> {
    //TO-DO: Not sure what goes here yet
    //Task is one-to-many with users
    Set<Task> findAllByUsername(String username);

    Task findById(long id);
}
