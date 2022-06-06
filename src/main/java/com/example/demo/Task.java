package com.example.demo;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Entity
@Table(name="task_db")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column (name = "name", nullable = false, length = 45)
    @NotEmpty
    private String name;

    @Column (name = "description", nullable = false, length = 100)
    @NotEmpty
    private String description;

    @Column(name="username", nullable = false, length = 45)
    private String username;

    //Date format in java is YYYY-MM-DD and string can be converted to this
    @Column (name = "deadline", nullable = false, length = 45)
    private Date deadline;

    @Column (name = "completionStatus", nullable = false, length = 45)
    private boolean completionStatus;
    
    public Task(){
        //Ben added this
        //id = 0;
        name = "";
        username = "";
        description = "";
        //deadline = new Date(0000, 00, 00);
    }

    public Task(String name, String description, String username, Date deadline, boolean completionStatus){
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.completionStatus = completionStatus;
        this.username = username;
    }

    public void setId(long id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setDeadline(Date deadline){
        this.deadline = deadline;
    }

    public void setCompletionStatus(boolean completionStatus){
        this.completionStatus = completionStatus;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public Date getDeadline(){
       return deadline;
    }

    public boolean getCompletionStatus(){
        return completionStatus;
    }

    public String getUsername(){
        return username;
    }
}
