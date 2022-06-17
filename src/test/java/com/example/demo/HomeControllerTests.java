package com.example.demo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.realm.GenericPrincipal;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration(classes = {UserRepository.class, TaskRepository.class, FolderRepository.class,
UserTaskPairRepository.class, FolderTaskPairRepository.class, RoleRepository.class})
public class HomeControllerTests {

    private MockMvc mockMvc;

    @InjectMocks
    HomeController homeController;
    @InjectMocks
    User user;

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    UserRepository userRepository;

    @MockBean
    FolderRepository folderRepository;
    @MockBean
    UserTaskPairRepository userTaskPairRepository;
    @MockBean
    RoleRepository roleRepository;
    @MockBean
    FolderTaskPairRepository folderTaskPairRepository;
    @MockBean
    TaskRepository taskRepository;

    @Mock
    Principal principal;
    @Mock
    Model model;


    private @interface WithMockUser{
        String value() default "user";

        String username() default "";

        String[] roles() default {"USER"};

        String password() default "password";
    }

    private void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIndex(){
        assertThat(homeController.index().equals("index"));
    }

    @Test
    public void testLogin(){
        assertThat(homeController.login().equals("login"));
    }

    @Test
    public void testLogout(){
        assertThat(homeController.logout().equals("redirect:/login?logout=true"));
    }

    @Test
    public void testAdmin(){
        assertThat(homeController.admin().equals("admin"));
    }

    @Test
    public v
}
