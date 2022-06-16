package com.example.demo;
import javax.persistence.*;

@Entity
@Table(name = "user_task_pair")
public class UserTaskPair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "userId", nullable = false, length = 45)
    private long userId;

    @Column(name = "taskId", nullable = false, length = 45)
    private long taskId;

    public UserTaskPair(){}

    public UserTaskPair(long userId, long taskId){
        this.userId = userId;
        this.taskId = taskId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
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
