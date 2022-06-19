package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserTaskPairRepository extends JpaRepository<UserTaskPair, Long> {

    Set<UserTaskPair> findAllByTaskId(long taskId);

    Set<UserTaskPair> findAllByUserId(long userId);

    UserTaskPair findByTaskIdAndUserId(long taskId, long userId);
}

