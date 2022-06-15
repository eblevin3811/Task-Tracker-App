package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    //one-to-many relationship with user and folder
    Set<Folder> findAllByCreator(String creator);

    Folder findById(long id);
}

