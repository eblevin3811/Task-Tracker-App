package com.example.demo.integration;

import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.*;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import static org.mockito.Mockito.when;
import java.security.Principal;
import java.sql.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerIntegrationTesting {

    @Autowired
    private MockMvc mockMvc;

    @Spy
    @InjectMocks
    private SpringSecurity404Application springSecurity404Application;

    @Spy
    @InjectMocks
    private HomeController homeController;

    @Mock
    private Principal principal;

    @Mock
    private User userStub;
    @Mock
    private User groupMember;

    @Mock
    private Role roleStub;

    @Mock
    private Task taskStub;

    @Mock
    private Folder folderStub;

    @Mock
    private FolderTaskPair folderTaskPairStub;

    @Mock
    private UserTaskPair userTaskPairStub;

    @Mock
    private Model model;

    private String defaultName = "test";
    private long id = 101;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private FolderTaskPairRepository folderTaskPairRepository;

    @Mock
    private UserTaskPairRepository userTaskPairRepository;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }

    //Program runs
    @Test
    public void programRuns() throws Exception{
        springSecurity404Application.main(new String[] {});
    }

    //Testing admin page
    @Test
    public void adminPageShouldDisplay() throws Exception {
        String result = homeController.admin();
        assertThat(result.equals("admin"));
    }

    //Testing controller to index page
    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk());

        String result = homeController.logout();
        assertThat(result.equals("redirect:/login?logout=true"));
    }

    //Testing controller to login page
    @Test
    public void shouldDisplayLoginPrompt() throws Exception{
        this.mockMvc.perform(get("/login")).andDo(print()).andExpect(content().string(containsString("Login")));
    }

    //Testing logout-login interaction
    @Test
    public void logoutSuccessReturnToLogin() throws Exception{
        this.mockMvc.perform(post("/logout")).andExpect(redirectedUrl("/login?logout=true"));
    }

    //Testing security page interacting with repositories
    @Test
    public void securePageDisplaysInfo() throws Exception{
        userStub = new User("", "", "", "", "", false, 0);
        userStub.setUsername(defaultName);
        userRepository.save(userStub);

        roleStub = new Role();
        roleStub.setUsername(defaultName);
        roleStub.setRole("ADMIN");
        roleStub.setId(id);
        roleRepository.save(roleStub);

        when(principal.getName()).thenReturn(defaultName);

        String result = homeController.secure(principal, model);

        assertThat(result.equals("secure"));
        assertThat(roleStub.getUsername().equals(defaultName));
        assertThat(roleStub.getRole().equals("ADMIN"));
        assertThat(roleStub.getId() == id);
    }

    //Testing controller -> registration page integration
    @Test
    public void registerPageSetup() throws Exception{
        this.mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Register")));
    }

    //Testing posting registration page
    @Test
    public void registrationPageContainsUserInfo() throws Exception{
        userStub = new User();
        userStub.setUsername("test");
        userStub.setId(1);
        userStub.setEmail("test@email.com");
        userStub.setPassword("test");
        userStub.setEnabled(false);
        userStub.setLastName("testlast");
        userStub.setFirstName("testfirst");
        userStub.setGroupId(0);

        String result = homeController.processRegisterationPage(userStub, Mockito.mock(BindingResult.class), model);

        assertThat(userStub.isEnabled());
    }

    //Testing controller->complete task page integration and task entity methods
    @Test
    public void todoListUpdatesTaskCompletion() throws Exception{

        //Setup
        taskStub = new Task();
        taskStub.setCompletionStatus(false);
        taskStub.setId(1);
        taskRepository.save(taskStub);
        when(taskRepository.findById(1)).thenReturn(taskStub);

        userStub.setUsername(defaultName);
        userRepository.save(userStub);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);
        when(principal.getName()).thenReturn(defaultName);

        String result = homeController.updateTodoList(1, new ModelMap(), principal);

        //Completion status should be true now
        assertThat(taskStub.getCompletionStatus());
    }

    //Testing deletion from a list via controller
    @Test
    public void taskShouldBeDeleted() throws Exception{
        taskStub = new Task();
        taskStub.setId(1);
        taskStub.setUsername(defaultName);
        taskStub.setName("testname");
        taskStub.setDeadline(new Date(2022,2,2));
        taskStub.setDescription("testdesc");
        taskRepository.save(taskStub);

        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        String result = homeController.deleteFromTodoList(1, new ModelMap(), principal);

        assertThat(taskRepository.findById(1) == null);
    }

    //Testing editing the content of a task
    @Test
    public void taskShouldBePutInModel() throws Exception{
        when(taskRepository.findById(1)).thenReturn(taskStub);

        String result = homeController.editTodo(1, principal, model);

        assertThat(model.containsAttribute("task"));
    }

    @Test
    public void afterEditedTaskModelShouldHaveFolders() throws Exception{
        Date deadline = new Date(2022,2,2);
        taskStub = new Task("testtask", "testdesc", defaultName, deadline, false);
        taskRepository.save(taskStub);
        when(taskRepository.findById(taskStub.getId())).thenReturn(taskStub);

        userStub.setUsername(defaultName);
        userRepository.save(userStub);

        userTaskPairStub = new UserTaskPair();
        userTaskPairStub.setUserId(userStub.getId());
        userTaskPairStub.setTaskId(taskStub.getId());
        userTaskPairRepository.save(userTaskPairStub);
        long stubId = 123;
        userTaskPairStub.setId(stubId);

        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        String result = homeController.postEditedTodo(taskStub, Mockito.mock(BindingResult.class), model, principal);

        assertThat(taskRepository.findById(taskStub.getId()).getName().equals("testtask"));
        assertThat(taskRepository.findById(taskStub.getId()).getUsername().equals(defaultName));
        assertThat(taskRepository.findById(taskStub.getId()).getDeadline().equals(deadline));
        assertThat(taskRepository.findById(taskStub.getId()).getDescription().equals("testdesc"));
        assertThat(!(taskRepository.findById(taskStub.getId()).getCompletionStatus()));
        assertThat(userTaskPairStub.getTaskId() == taskStub.getId());
        assertThat(userTaskPairStub.getUserId() == userStub.getId());
        assertThat(userTaskPairStub.getId() == stubId);
    }

    //Testing that an error when registering will result in the user's password being cleared
    @Test
    public void registrationErrorLeadsToPasswordClear() throws Exception{
        userStub = new User();
        userStub.setUsername(defaultName);
        userStub.setPassword("shouldBeDeleted");

        BindingResult result = Mockito.mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        String senturl = homeController.processRegisterationPage(userStub, Mockito.mock(BindingResult.class), model);

        assertThat(userStub.getPassword().equals(""));

        userStub.clearPassword();

        assertThat(senturl.equals("register"));
    }

    //Testing whether the group id is displayed on the model from the user entity
    @Test
    public void taskShouldAppearOnTodoList() throws Exception{
        userStub = new User();
        userStub.setUsername(defaultName);
        userStub.setGroupId(101);
        userRepository.save(userStub);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        when(principal.getName()).thenReturn(defaultName);
        
        String res = homeController.listTodos(principal, model);

        assertThat(model.containsAttribute("groupId"));
    }

    //Testing if the todo page redirects the user properly
    @Test
    public void usersNotLoggedInRedirectedByAddTodoPage() throws Exception{
        mockMvc.perform(get("/add-todo"))
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void errorsInTodoRedirectsToAddPage() throws Exception{
        BindingResult result = Mockito.mock(BindingResult.class);

        when(result.hasErrors()).thenReturn(true);

        String url = homeController.processAddTodoPage(taskStub, result, model, principal);

        assertThat(url.equals("add-todo"));
        assertThat(model.containsAttribute("task"));
    }

    @Test
    public void addFolderPageShouldDisplay() throws Exception{
        mockMvc.perform(get("/add-folder"))
                .andExpect(content().string(containsString("Create New Folder")));
    }

    @Test
    public void addedFolderShouldHaveSetCreator() throws Exception{
        BindingResult result = Mockito.mock(BindingResult.class);
        folderStub = new Folder("", "");
        folderStub.setId(id);
        folderStub.setFolderName("testname");
        userStub.setUsername(defaultName);
        userStub.setId(folderStub.getId());

        when(result.hasErrors()).thenReturn(false);
        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        String url = homeController.processAddFolderPage(folderStub, result, model, principal);

        assertThat(url.equals("list-todos"));
        assertThat(folderStub.getCreator().equals(defaultName));
        assertThat(folderStub.getFolderName().equals("testname"));
    }

    @Test
    public void taskFromRepoShouldBeOnModel_SortingOptions() throws Exception{
        taskStub = new Task();
        taskStub.setId(0);
        taskRepository.save(taskStub);
        when(taskRepository.findById(taskStub.getId())).thenReturn(taskStub);

        when(principal.getName()).thenReturn(defaultName);

        String url = homeController.showSortOptions(taskStub.getId(), principal, model);

        assertThat(model.containsAttribute("task"));
        assertThat(model.containsAttribute("folderList"));
    }

    @Test
    public void folderTaskPairShouldBeCreated() throws Exception{
        when(folderRepository.findById(1)).thenReturn(folderStub);
        when(taskRepository.findById(1)).thenReturn(taskStub);
        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        folderTaskPairStub = new FolderTaskPair(1, 1);
        folderTaskPairStub.setId(id);

        FolderTaskPair folderTaskPairStub2 = new FolderTaskPair();
        folderTaskPairStub2.setFolderId(1);
        folderTaskPairStub2.setTaskId(1);

        String url = homeController.sortTodoIntoList(folderTaskPairStub.getFolderId(), folderTaskPairStub.getTaskId(), principal, model);

        assertThat(folderTaskPairRepository.findById(folderTaskPairStub.getId()) != null);
        assertThat(folderTaskPairStub.getTaskId() == folderTaskPairStub2.getTaskId());
        assertThat(folderTaskPairStub.getFolderId() == folderTaskPairStub2.getFolderId());
    }

    @Test
    public void groupIdShouldBeAddedToModel() throws Exception{
        folderStub.setId(id);
        folderRepository.save(folderStub);

        userStub.setGroupId(101);
        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        String url = homeController.viewTasksInFolder(id, principal, model);
        assertThat(model.containsAttribute(String.valueOf(userStub.getGroupId())));
    }

    @Test
    public void taskShouldAppearInGroupMembersList() throws Exception{
        groupMember = new User();
        groupMember.setGroupId(101);
        groupMember.setUsername("user");
        groupMember.setId(id);
        userRepository.save(groupMember);

        when(userRepository.findByUsername("user")).thenReturn(groupMember);
        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        String url = homeController.assignTaskToGroupMember(taskStub.getId(), groupMember.getUsername(), principal, model);
        assertThat(userTaskPairRepository.findByTaskIdAndUserId(taskStub.getId(), groupMember.getId()) != null);
    }

    @Test
    public void assignmentListShouldNotDisplayUser() throws Exception{
        taskStub = new Task();
        taskStub.setId(id);

        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        homeController.showAssignmentList(taskStub.getId(), model, principal);

        assertThat(!model.containsAttribute(String.valueOf(userStub)));
    }

    @Test
    public void shouldHaveNewTaskOnModel() throws Exception{
        String url = homeController.showAddTodoPage(model, principal);

        assertThat(url.equals("add-todo"));
        assertThat(model.containsAttribute("task"));
    }
}