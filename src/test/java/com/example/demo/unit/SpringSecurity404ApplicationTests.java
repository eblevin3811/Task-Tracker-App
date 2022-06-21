package com.example.demo.unit;

import com.example.demo.SpringSecurity404Application;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringSecurity404ApplicationTests {
    @Spy
    @InjectMocks
    private SpringSecurity404Application springSecurity404Application;

    @Test
    void contextLoads() {}

    //Application runs?
    @Test
    public void mainTest() throws Exception{
        springSecurity404Application.main(new String[] {});
    }

}