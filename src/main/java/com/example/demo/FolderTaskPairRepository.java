package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FolderTaskPairRepository extends JpaRepository<FolderTaskPair, Long> {
    //one-to-many relationship with folder and task

    Set<FolderTaskPair> findAllByTaskId(long taskId);

    Set<FolderTaskPair> findAllByFolderId(long folderId);
}

