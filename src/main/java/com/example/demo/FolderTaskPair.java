package com.example.demo;

import javax.persistence.*;

@Entity
@Table(name = "folder_task_pair")
public class FolderTaskPair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "folderId", nullable = false, length = 45)
    private long folderId;

    @Column(name = "taskId", nullable = false, length = 45)
    private long taskId;

    public FolderTaskPair(){}

    public FolderTaskPair(long folderId, long taskId){
        this.folderId = folderId;
        this.taskId = taskId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }

    public long getFolderId() {
        return folderId;
    }

    public long getId() {
        return id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }
}
