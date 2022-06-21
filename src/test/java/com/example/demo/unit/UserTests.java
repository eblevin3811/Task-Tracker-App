package com.example.demo.unit;

import com.example.demo.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

public class UserTests {

    @Test
    public void testGetId(){
        User user = new User();
        long id = user.getId();
        assertThat(user.getId() == id);
    }

    @Test
    public void testGetUser(){
        User user = new User("testuser", "test@test.com", "password", "first", "last", true, 234);
        assertThat(user.getUsername().equals("testuser"));
    }

    @Test
    public void testGetEmail(){
        User user = new User("testuser", "test@test.com", "password", "first", "last", true, 234);
        assertThat(user.getEmail().equals("test@test.com"));
    }
    @Test
    public void testGetPassword(){
        User user = new User("testuser", "test@test.com", "password", "first", "last", true, 234);
        assertThat(user.getPassword().equals("password"));
    }

    @Test
    public void testGetFirstName(){
        User user = new User("testuser", "test@test.com", "password", "first", "last", true, 234);
        assertThat(user.getFirstName().equals("first"));
    }

    @Test
    public void testGetLastName(){
        User user = new User("testuser", "test@test.com", "password", "first", "last", true, 234);
        assertThat(user.getLastName().equals("last"));
    }

    @Test
    public void testGetGroupId(){
        User user = new User("testuser", "test@test.com", "password", "first", "last", true, 234);
        assertThat(user.getGroupId() == 234);
    }

    @Test
    public void testGetEnabled(){
        User user = new User("testuser", "test@test.com", "password", "first", "last", true, 234);
        assertThat(user.isEnabled() == true);
    }

    @Test
    public void testSetId(){
        User user = new User();
        user.setId(353);
        assertThat(user.getId() == 353);
    }

    @Test
    public void testSetUsername(){
        User user = new User();
        user.setUsername("test");
        assertThat(user.getUsername().equals("test"));
    }

    @Test
    public void testSetEmail(){
        User user = new User();
        user.setEmail("test@gmail.com");
        assertThat(user.getEmail().equals("test@gmail.com"));
    }

    @Test
    public void testSetPassword(){
        User user = new User();
        user.setPassword("pass");
        assertThat(user.getPassword().equals("pass"));
    }

    @Test
    public void testSetFirstname(){
        User user = new User();
        user.setFirstName("First");
        assertThat(user.getFirstName().equals("First"));
    }

    @Test
    public void testSetLastname(){
        User user = new User();
        user.setLastName("Last");
        assertThat(user.getLastName().equals("Last"));
    }

    @Test
    public void testSetEnabled(){
        User user = new User();
        user.setEnabled(true);
        assertThat(user.isEnabled() == true);
    }

    @Test
    public void testSetGroupId(){
        User user = new User();
        user.setGroupId(234);
        assertThat(user.getGroupId() == 234);
    }

    @Test
    public void testClearPassword(){
        User user = new User();
        user.setPassword("test");
        user.clearPassword();
        assertThat(user.getPassword().equals(""));
    }

}
