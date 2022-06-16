package com.example.demo;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //Select * from userDB where username = ?
    User findByUsername(String username);

    //Select * from UserDB where groupId = ?
    Set<User> findAllByGroupId(int groupId);
}
