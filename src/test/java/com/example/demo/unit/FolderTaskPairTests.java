package com.example.demo.unit;
import com.example.demo.FolderTaskPair;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FolderTaskPairTests {

    @Test
    public void testEmptyConstructor(){
        FolderTaskPair pair = new FolderTaskPair();
        assertThat(pair.getTaskId() == 0 && pair.getFolderId() == 0);
    }

    @Test
    public void testConstructor(){
        FolderTaskPair pair = new FolderTaskPair(12, 34);
        assertThat(pair.getFolderId() == 12 && pair.getTaskId() == 34);
    }

    @Test
    public void testSetGetFolderId(){
        FolderTaskPair pair = new FolderTaskPair();
        pair.setFolderId(123);
        assertThat(pair.getFolderId() == 123);
    }

    @Test
    public void testSetGetId(){
        FolderTaskPair pair = new FolderTaskPair();
        pair.setId(123);
        assertThat(pair.getId() == 123);
    }

    @Test
    public void testGetSetTaskId(){
        FolderTaskPair pair = new FolderTaskPair();
        pair.setTaskId(123);
        assertThat(pair.getTaskId() == 123);
    }
}
