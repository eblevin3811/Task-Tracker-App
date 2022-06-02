package com.example.demo;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //User findByUsername(String username);
    //What's happening is:select * from user where username =? ( which is username parameter)
}
