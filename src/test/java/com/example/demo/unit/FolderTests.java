package com.example.demo.unit;

import com.example.demo.Folder;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class FolderTests {

    @Test
    public void testEmptyConstructor(){
        Folder folder = new Folder();
        assertThat(folder.getFolderName() == null && folder.getCreator() == null);
    }

    @Test
    public void testConstructor(){
        Folder folder = new Folder("testfolder", "testcreator");
        assertThat(folder.getFolderName().equals("testfolder") && folder.getCreator().equals("testcreator"));
    }

    @Test
    public void testGetSetId(){
        Folder folder = new Folder();
        folder.setId(123);
        assertThat(folder.getId() == 123);
    }

    @Test
    public void testGetSetFolderName(){
        Folder folder = new Folder();
        folder.setFolderName("testfolder");
        assertThat(folder.getFolderName().equals("testfolder"));
    }

    @Test
    public void testGetSetCreator(){
        Folder folder = new Folder();
        folder.setCreator("testcreator");
        assertThat(folder.getCreator().equals("testcreator"));
    }
}
