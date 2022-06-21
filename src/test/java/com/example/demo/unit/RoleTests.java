package com.example.demo.unit;

import com.example.demo.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RoleTests {

    @Test
    public void testBlankConstructor(){
        Role role = new Role();
        long id = role.getId();
        assertThat(role.getId() == id);
    }

    @Test
    public void testGetSetId(){
        Role role = new Role ("user", "rolename");
        role.setId(123);
        assertThat(role.getId() == 123);
    }

    @Test
    public void testGetSetUsername(){
        Role role = new Role ();
        role.setUsername("test");
        assertThat(role.getUsername().equals("test"));
    }
    @Test
    public void testGetSetRole(){
        Role role = new Role();
        role.setRole("TestRole");
        assertThat(role.getRole().equals("TestRole"));
    }
}
