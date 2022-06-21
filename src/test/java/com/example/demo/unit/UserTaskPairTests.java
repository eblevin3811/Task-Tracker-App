package com.example.demo.unit;
import com.example.demo.UserTaskPair;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTaskPairTests {

    @Test
    public void testEmptyConstructor(){
        UserTaskPair pair = new UserTaskPair();
        assertThat(pair.getUserId() == 0 && pair.getTaskId() == 0);
    }

    @Test
    public void testConstructor(){
        UserTaskPair pair = new UserTaskPair(123, 456);
        assertThat(pair.getUserId() == 123 && pair.getTaskId() == 456);
    }

    @Test
    public void testSetGetUserId(){
        UserTaskPair pair = new UserTaskPair();
        pair.setUserId(123);
        assertThat(pair.getUserId() == 123);
    }

    @Test
    public void testSetGetId(){
        UserTaskPair pair = new UserTaskPair();
        pair.setId(2);
        assertThat(pair.getId() == 2);
    }

    @Test
    public void testSetGetTaskId(){
        UserTaskPair pair = new UserTaskPair();
        pair.setTaskId(345);
        assertThat(pair.getTaskId() == 345);
    }
}
