package com.example.demo;

import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class SystemTests {

    @Autowired
    private MockMvc mockMvc;

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
    public void init() {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }

    //User registers without a group and logs in
    @Test
    public void userRegistersWithoutGroup_LogsIn() throws Exception{
        User newUser = new User();
        BindingResult result = Mockito.mock(BindingResult.class);

        //Go to registration page
        String success = homeController.showRegisterationPage(model);
        assertThat(success.equals("register"));

        //Fill in registration information
        newUser.setUsername("");
        newUser.setPassword("Password");
        newUser.setEmail("test@gmail.com");
        newUser.setFirstName("First");
        newUser.setLastName("Last");
        success = homeController.processRegisterationPage(newUser, result, model);

        //Error registering due to no username
        when(result.hasErrors()).thenReturn(true);
        assertThat(success.equals("register"));

        //Register again
        newUser.setUsername("Username");
        newUser.setPassword("Password");
        newUser.setEmail("test@gmail.com");
        newUser.setFirstName("First");
        newUser.setLastName("Last");
        success = homeController.processRegisterationPage(newUser, result, model);

        //Successful register
        assertThat(success.equals("login"));
        assertThat(newUser.isEnabled());
        assertThat(roleRepository.findById(newUser.getId()));
        assertThat(userRepository.findByUsername("Username"));

        //Login
        success = homeController.login(null, model);
        assertThat(success.equals("login"));

        assertThat(newUser.getUsername().equals("Username"));
        assertThat(newUser.getPassword().equals("Password"));
        assertThat(newUser.getFirstName().equals("First"));
        assertThat(newUser.getLastName().equals("Last"));
        assertThat(newUser.getEmail().equals("test@gmail.com"));

    }

    //User registers with a group and logs in
    @Test
    public void userRegistersWithGroup_LogsIn() throws Exception{
        User newUser = new User();
        BindingResult result = Mockito.mock(BindingResult.class);

        //Go to registration page
        String success = homeController.showRegisterationPage(model);
        assertThat(success.equals("register"));

        //Fill in registration information
        newUser.setUsername("");
        newUser.setGroupId(123);
        newUser.setPassword("Password");
        newUser.setEmail("test@gmail.com");
        newUser.setFirstName("First");
        newUser.setLastName("Last");
        success = homeController.processRegisterationPage(newUser, result, model);

        //Error registering due to no username
        when(result.hasErrors()).thenReturn(true);
        assertThat(success.equals("register"));

        //Register again
        newUser.setUsername("Username");
        newUser.setGroupId(123);
        newUser.setPassword("Password");
        newUser.setEmail("test@gmail.com");
        newUser.setFirstName("First");
        newUser.setLastName("Last");
        success = homeController.processRegisterationPage(newUser, result, model);

        //Successful register
        assertThat(success.equals("login"));
        assertThat(newUser.isEnabled());
        assertThat(roleRepository.findById(newUser.getId()));
        assertThat(userRepository.findByUsername("Username"));

        //Login
        success = homeController.login(null, model);
        assertThat(success.equals("login"));

        assertThat(newUser.getUsername().equals("Username"));
        assertThat(newUser.getPassword().equals("Password"));
        assertThat(newUser.getFirstName().equals("First"));
        assertThat(newUser.getLastName().equals("Last"));
        assertThat(newUser.getEmail().equals("test@gmail.com"));
    }

    //User who is logged in logs out
    @Test
    public void userLogsIn_LogsOut() throws Exception{
        //Existing user
        userStub = new User("testUser", "test@email.com", "testPassword", "testFirst", "testLast", false, 0);
        userRepository.save(userStub);

        //Login
        String success = homeController.login(null, model);
        assertThat(success.equals("login"));

        //Logout
        success = homeController.logout();
        assertThat(success.equals("redirect:/login?logout=true"));

        //User should still be in repository
        assertThat(userRepository.findByUsername("testUser"));
    }

    //User creates a new task
    @Test
    public void userCreatesNewTask() throws Exception{
        BindingResult result = Mockito.mock(BindingResult.class);

        //New task to be created
        Task newTask = new Task();

        //Existing user logged in
        userStub = new User("testUser", "test@email.com", "testPassword", "testFirst", "testLast", true, 0);
        userRepository.save(userStub);
        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(userStub);

        //Navigate to index
        String success = homeController.index();
        assertThat(success.equals("index"));

        //Navigate Create page
        success = homeController.showAddTodoPage(model, principal);
        assertThat(success.equals("add-todo"));

        //Create task
        success = homeController.processAddTodoPage(newTask, result, model, principal);
        when(result.hasErrors()).thenReturn(false);

        //User should be in task list
        assertThat(success.equals("list-todos"));

        //Task should be in task repository
        assertThat(taskRepository.findById(taskStub.getId()));

    }

    //User views their task list and marks a task as complete
    @Test
    public void userViewsTaskList_MarkComplete() throws Exception{
        BindingResult result = Mockito.mock(BindingResult.class);

        //Existing user logged in
        userStub = new User("testUser", "test@email.com", "testPassword", "testFirst", "testLast", true, 0);
        userRepository.save(userStub);
        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(userStub);

        //Existing task in list
        Date deadline = new Date(2022, 2, 2);
        taskStub = new Task("taskName", "taskDesc", "testUser", deadline, false);
        taskRepository.save(taskStub);
        when(taskRepository.findById(taskStub.getId())).thenReturn(taskStub);

        //Navigate to index
        String success = homeController.index();
        assertThat(success.equals("index"));

        //Navigate to task list
        success = homeController.listTodos(principal, model);
        assertThat(success.equals("list-todos"));

        //Task should be in list
        assertThat(model.containsAttribute(String.valueOf(taskStub)));

        //Click complete button
        success = homeController.updateTodoList(taskStub.getId(), new ModelMap(), principal);

        //User should be returned to todo list
        assertThat(success.equals("list-todos"));

        //Task completion status should be true
        assertThat(taskStub.getCompletionStatus());
    }

    //User views their task list and creates a folder
    @Test
    public void userViewsTaskList_CreatesFolder() throws Exception{
        BindingResult result = Mockito.mock(BindingResult.class);

        //Existing user logged in
        userStub = new User("testUser", "test@email.com", "testPassword", "testFirst", "testLast", true, 0);
        userRepository.save(userStub);
        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(userStub);

        //Navigate to index
        String success = homeController.index();
        assertThat(success.equals("index"));

        //Navigate to task list
        success = homeController.listTodos(principal, model);
        assertThat(success.equals("list-todos"));

        //Click create folder button
        success = homeController.showAddFolderPage(model);

        //New folder created
        Folder newfolder = new Folder();
        newfolder.setFolderName("testFolder");
        newfolder.setCreator("testUser");

        success = homeController.processAddFolderPage(newfolder, result, model, principal);

        //User should be returned to task list page
        assertThat(success.equals("list-todos"));

        //Folder should be in repository
        assertThat(folderRepository.findById(newfolder.getId()));

        //Folder should be on list page
        assertThat(model.containsAttribute(String.valueOf(newfolder)));
    }

    //User views their task list and sorts a task into a folder
    @Test
    public void userViewsTaskList_SortsTask() throws Exception{
        BindingResult result = Mockito.mock(BindingResult.class);

        //Existing user logged in
        userStub = new User("testUser", "test@email.com", "testPassword", "testFirst", "testLast", true, 0);
        userRepository.save(userStub);
        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(userStub);

        //Existing task in list
        Date deadline = new Date(2022, 2, 2);
        taskStub = new Task("taskName", "taskDesc", "testUser", deadline, false);
        taskRepository.save(taskStub);
        when(taskRepository.findById(taskStub.getId())).thenReturn(taskStub);

        //Existing task in folder
        folderStub = new Folder("testFolder", "testUser");
        folderRepository.save(folderStub);
        when(folderRepository.findById(folderStub.getId())).thenReturn(folderStub);
        folderTaskPairStub = new FolderTaskPair(folderStub.getId(), userStub.getId());
        folderTaskPairRepository.save(folderTaskPairStub);

        //Navigate to index
        String success = homeController.index();
        assertThat(success.equals("index"));

        //Navigate to task list
        success = homeController.listTodos(principal, model);
        assertThat(success.equals("list-todos"));

        //Click sort button
        success = homeController.showSortOptions(taskStub.getId(), principal, model);
        assertThat(success.equals("sort"));

        //Select folder to sort task into
        success = homeController.sortTodoIntoList(folderStub.getId(), taskStub.getId(), principal, model);
        assertThat(success.equals("list-todos"));

        //Folder-task pair should have been created
        assertThat(folderTaskPairRepository.findAllByFolderId(folderStub.getId()));

        //View the folder
        success = homeController.viewTasksInFolder(folderStub.getId(), principal, model);
        assertThat(success.equals("view-folder"));
        assertThat(model.containsAttribute(String.valueOf(taskStub)));
    }

    //User views their task list and edits a task's description, deadline and name
    @Test
    public void userViewsTaskList_EditsInfo() throws Exception{
        BindingResult result = Mockito.mock(BindingResult.class);

        //Existing user logged in
        userStub = new User("testUser", "test@email.com", "testPassword", "testFirst", "testLast", true, 0);
        userRepository.save(userStub);
        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(userStub);

        //Existing task in list
        Date deadline = new Date(2022, 2, 2);
        taskStub = new Task("taskName", "taskDesc", "testUser", deadline, false);
        taskRepository.save(taskStub);
        when(taskRepository.findById(taskStub.getId())).thenReturn(taskStub);

        //Navigate to index
        String success = homeController.index();
        assertThat(success.equals("index"));

        //Navigate to task list
        success = homeController.listTodos(principal, model);
        assertThat(success.equals("list-todos"));

        //Click edit button on task
        success = homeController.editTodo(taskStub.getId(), principal, model);
        assertThat(success.equals("edit-todo"));

        //Edit the task
        taskStub.setName("editedTaskName");
        taskStub.setDescription("editedTaskDesc");
        taskStub.setDeadline(new Date(2022, 3, 3));
        success = homeController.postEditedTodo(taskStub, result, model, principal);

        //User should be returned to the list of todos
        assertThat(success.equals("redirect:list-todos"));

        //Task should have edited info
        assertThat(model.containsAttribute(String.valueOf(taskStub)));
    }

    //User views their task list and assigns a task to a group member
    @Test
    public void userViewsTaskList_AssignsTask() throws Exception{
        BindingResult result = Mockito.mock(BindingResult.class);

        //Existing user logged in
        userStub = new User("testUser", "test@email.com", "testPassword", "testFirst", "testLast", true, 123);
        userRepository.save(userStub);
        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(userStub);

        //Existing user in same group
        groupMember = new User("testMember", "testMember@email.com", "testPassword", "testFirst", "testLast", true, 123);
        userRepository.save(groupMember);
        when(principal.getName()).thenReturn("testMember");
        when(userRepository.findByUsername("testMember")).thenReturn(groupMember);

        //Existing task in list
        Date deadline = new Date(2022, 2, 2);
        taskStub = new Task("taskName", "taskDesc", "testUser", deadline, false);
        taskRepository.save(taskStub);
        when(taskRepository.findById(taskStub.getId())).thenReturn(taskStub);

        //Navigate to index
        String success = homeController.index();
        assertThat(success.equals("index"));

        //Navigate to task list
        success = homeController.listTodos(principal, model);
        assertThat(success.equals("list-todos"));

        //Click assign button on task list
        success = homeController.showAssignmentList(taskStub.getId(), model, principal);
        assertThat(success.equals("assign"));

        success = homeController.assignTaskToGroupMember(taskStub.getId(), groupMember.getUsername(), principal, model);
        assertThat(success.equals("assign"));

        //Group member should have task too
        assertThat(userTaskPairRepository.findByTaskIdAndUserId(taskStub.getId(), groupMember.getId()));
    }

    //User views their task list and deletes a task
    @Test
    public void userViewsTaskList_DeletesTask() throws Exception{
        BindingResult result = Mockito.mock(BindingResult.class);

        //Existing user logged in
        userStub = new User("testUser", "test@email.com", "testPassword", "testFirst", "testLast", true, 123);
        userRepository.save(userStub);
        when(principal.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(userStub);

        //Existing task in list
        Date deadline = new Date(2022, 2, 2);
        taskStub = new Task("taskName", "taskDesc", "testUser", deadline, false);
        taskRepository.save(taskStub);
        when(taskRepository.findById(taskStub.getId())).thenReturn(taskStub);

        //Navigate to index
        String success = homeController.index();
        assertThat(success.equals("index"));

        //Navigate to task list
        success = homeController.listTodos(principal, model);
        assertThat(success.equals("list-todos"));

        //Click delete button on task
        ModelMap model2 = new ModelMap();
        success = homeController.deleteFromTodoList(taskStub.getId(), model2, principal);
        assertThat(success.equals("list-todos"));

        //Model should not contain task
        assertThat(!model2.containsAttribute(String.valueOf(taskStub)));

        //Task should not be in repo
        assertThat(taskRepository.findById(taskStub.getId()) == null);
    }
}