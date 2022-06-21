package com.example.demo.unit;

import com.example.demo.Role;
import com.example.demo.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.test.context.annotation.SecurityTestExecutionListeners;
import org.springframework.test.annotation.Rollback;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository repository;

    @Test
    public void testCreateRole() {
        Role role = new Role();
        role.setRole("Test Role");
        role.setUsername("testUser");

        Role savedRole = repository.save(role);

        Role existRole = entityManager.find(Role.class, savedRole.getId());

        assertThat(role.getRole()).isEqualTo(existRole.getRole());

        repository.delete(role);
    }

    @Test
    public void testFindAllByUsername(){
        Role role1 = new Role();
        role1.setRole("Test Role 1");
        role1.setUsername("testUser");
        repository.save(role1);

        Role role2 = new Role();
        role2.setRole("Test Role 2");
        role2.setUsername("testUser");
        repository.save(role2);

        Set<Role> roleSet = repository.findAllByUsername("testUser");

        assertThat(roleSet.size() == 2);

        repository.delete(role1);
        repository.delete(role2);
    }

    @Test
    public void findInstance(){
        Role role = new Role();
        role.setRole("Test Role");
        role.setUsername("Testuser");

        repository.save(role);

        assertThat(repository.findById(role.getId()).equals(role));

        repository.delete(role);
    }
}
