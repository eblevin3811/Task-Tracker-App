package com.example.demo;

import javax.persistence.*;

@Entity
@Table(name = "folders")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "creator", nullable = false, length = 45)
    private String creator;

    @Column(name = "folderName", nullable = false, length = 45)
    private String folderName;

    public Folder(){}

    public Folder(String folderName, String creator){
        this.folderName = folderName;
        this.creator = creator;
    }

    public long getId(){
        return this.id;
    }

    public String getFolderName(){
        return this.folderName;
    }

    public String getCreator(){
        return this.creator;
    }

    public void setId(long newID){
        this.id = newID;
    }
    public void setFolderName(String newName){
        this.folderName = newName;
    }

    public void setCreator(String newCreator){
        this.creator = newCreator;
    }
}
