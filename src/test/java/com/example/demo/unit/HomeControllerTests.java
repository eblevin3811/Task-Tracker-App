package com.example.demo.unit;
import com.example.demo.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import org.apache.catalina.realm.GenericPrincipal;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
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
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.security.Principal;
import java.sql.Date;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class HomeControllerTests {
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
    }

    @Test
    public void secureTest() throws Exception{
        //Arrange
        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        Set<Role> roles = new HashSet<Role>();
        roles.add(roleStub);
        when(roleRepository.findAllByUsername(defaultName)).thenReturn(roles);

        //Act
        String sent = homeController.secure(principal, model);

        //Assert
        assertThat(sent.equals("secure"));
    }

    @Test
    public void indexTest() throws Exception{
        //Act
        String sent = homeController.index();

        //Assert
        assertThat(sent.equals("index"));
    }

    //Ben's modification
    @Test
    public void loginTestNotLoggedIn() throws Exception{
        //Act
        String sent = homeController.login(null, model);

        //Assert
        assertThat(sent.equals("login"));
    }

    //Ben's modification
    @Test
    public void loginTestLoggedIn() throws Exception{

        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        Set<UserTaskPair> pairs = new HashSet<UserTaskPair>();
        pairs.add(userTaskPairStub);
        when(userStub.getId()).thenReturn(id);
        when(userTaskPairRepository.findAllByUserId(id)).thenReturn(pairs);

        when(userTaskPairStub.getTaskId()).thenReturn(id);
        when(taskRepository.findById(id)).thenReturn(taskStub);

        Set<Folder> folders = new HashSet<Folder>();
        folders.add(folderStub);
        when(folderRepository.findAllByCreator(defaultName)).thenReturn(folders);
        when(userStub.getGroupId()).thenReturn((int) id);

        //Act
        String sent = homeController.login(principal, model);

        //Assert
        assertThat(sent.equals("list-todos"));
    }

    @Test
    public void logoutTest() throws Exception{
        //Act
        String sent = homeController.logout();

        //Assert
        assertThat(sent.equals("redirect:/login?logout=true"));
    }

    @Test
    public void adminTest() throws Exception{
        //Act
        String sent = homeController.admin();

        //Assert
        assertThat(sent.equals("admin"));
    }

    @Test
    public void registerTestGet() throws Exception{
        //Act
        String sent = homeController.showRegisterationPage(model);

        //Assert
        assertThat(sent.equals("register"));
    }

    @Test
    public void registerTestPostErrors() throws Exception{
        //Arrange
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        //Act
        String sent = homeController.processRegisterationPage(userStub, result, model);

        //Assert
        assertThat(sent.equals("register"));
    }

    @Test
    public void registerTestPostNoError() throws Exception{
        //Arrange
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        //Act
        String sent = homeController.processRegisterationPage(userStub, result, model);

        //Assert
        assertThat(sent.equals("login"));
    }

    @Test
    public void updateTodoListTest() throws Exception{
        //Arrange
        when(taskRepository.findById(id)).thenReturn(taskStub);
        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);
        when(userStub.getId()).thenReturn(id);

        Set<UserTaskPair> pair = new HashSet<UserTaskPair>();
        pair.add(userTaskPairStub);

        when(userTaskPairRepository.findAllByUserId(id)).thenReturn(pair);

        when(userTaskPairStub.getTaskId()).thenReturn(id);
        Set<Folder> folders = new HashSet<Folder>();

        when(folderRepository.findAllByCreator(defaultName)).thenReturn(folders);

        //Act
        String sent = homeController.updateTodoList(id, new ModelMap(), principal);

        //Assert
        assertThat(sent.equals("list-todos"));
    }

    @Test
    public void deleteFromTodoListTest() throws Exception{
        //Arrange
        Set<FolderTaskPair> pairs = new HashSet<FolderTaskPair>();
        pairs.add(folderTaskPairStub);
        when(folderTaskPairRepository.findAllByTaskId(id)).thenReturn(pairs);

        Set<UserTaskPair> userPairs = new HashSet<UserTaskPair>();
        userPairs.add(userTaskPairStub);
        when(userTaskPairRepository.findAllByTaskId(id)).thenReturn(userPairs);

        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);
        when(userStub.getId()).thenReturn(id);
        when(userTaskPairRepository.findAllByUserId(id)).thenReturn(userPairs);

        when(userTaskPairStub.getTaskId()).thenReturn(id);
        when(taskRepository.findById(id)).thenReturn(taskStub);

        Set<Folder> folders = new HashSet<Folder>();
        folders.add(folderStub);
        when(folderRepository.findAllByCreator(defaultName)).thenReturn(folders);

        //Act
        String sent = homeController.deleteFromTodoList(id, new ModelMap(), principal);

        //Assert
        assertThat(sent.equals("list-todos"));
    }

    @Test(expected = NotFoundException.class)
    public void editTodoTestException() throws Exception{
        //Arrange
        when(taskRepository.findById(id)).thenReturn(null);

        //Act
        String sent = homeController.editTodo(id, principal, model);
    }

    @Test
    public void editTodoTest() throws Exception{
        //Arrange
        Task taskStub2 = new Task("testname", "testdesc", "testuser", new Date(2022, 12, 12), true);
        taskStub2.setId(id);
        when(taskRepository.findById(id)).thenReturn(taskStub);

        //Act
        String sent = homeController.editTodo(id, principal, model);

        //Assert
        assertThat(sent.equals("edit-todo"));
    }

    @Test
    public void postEditedTodoTestError() throws Exception{
        //Arrange
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        //Act
        String sent = homeController.postEditedTodo(taskStub, result, model, principal);

        //Assert
        assertThat(sent.equals("edit-todo"));
    }

    @Test
    public void postEditedTodoTestNoError() throws Exception{
        //Arrange
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        Set<UserTaskPair> pairs = new HashSet<UserTaskPair>();
        pairs.add(userTaskPairStub);
        when(userStub.getId()).thenReturn(id);
        when(userTaskPairRepository.findAllByUserId(id)).thenReturn(pairs);

        when(userTaskPairStub.getTaskId()).thenReturn(id);
        when(taskRepository.findById(id)).thenReturn(taskStub);

        Set<Folder> folders = new HashSet<Folder>();
        folders.add(folderStub);
        when(folderRepository.findAllByCreator(defaultName)).thenReturn(folders);

        //Act
        String sent = homeController.postEditedTodo(taskStub, result, model, principal);

        //Assert
        assertThat(sent.equals("redirect:list-todos"));
    }

    @Test
    public void listTodosTestNotLoggedIn() throws Exception{
        //Arrange
        Principal nullPrincipal = null;

        //Act
        String sent = homeController.listTodos(nullPrincipal, model);

        //Assert
        assertThat(sent.equals("login"));
    }

    @Test
    public void listTodosTestLoggedIn() throws Exception{
        //Arrange
        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        Set<UserTaskPair> pairs = new HashSet<UserTaskPair>();
        pairs.add(userTaskPairStub);
        when(userStub.getId()).thenReturn(id);
        when(userTaskPairRepository.findAllByUserId(id)).thenReturn(pairs);

        when(userTaskPairStub.getTaskId()).thenReturn(id);
        when(taskRepository.findById(id)).thenReturn(taskStub);

        Set<Folder> folders = new HashSet<Folder>();
        folders.add(folderStub);
        when(folderRepository.findAllByCreator(defaultName)).thenReturn(folders);
        when(userStub.getGroupId()).thenReturn((int) id);

        //Act
        String sent = homeController.listTodos(principal, model);

        //Assert
        assertThat(sent.equals("list-todos"));
    }

    @Test
    public void showAddTodoPageTestNotLoggedIn() throws Exception{

        //Act
        String sent = homeController.showAddTodoPage(model, null);

        //Assert
        assertThat(sent.equals("login"));
    }

    @Test
    public void showAddTodoPageTestLoggedIn() throws Exception{

        //Act
        String sent = homeController.showAddTodoPage(model, principal);

        //Assert
        assertThat(sent.equals("add-todo"));
    }

    @Test
    public void processAddTodoPageTestErrors() throws Exception{
        //Arrange
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        //Act
        String sent = homeController.processAddTodoPage(taskStub, result, model, principal);

        //Assert
        assertThat(sent.equals("add-todo"));
    }

    @Test
    public void processAddTodoPageTestNoErrors() throws Exception{
        //Arrange
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        when(userStub.getId()).thenReturn(id);
        Set<UserTaskPair> pair = new HashSet<UserTaskPair>();
        pair.add(userTaskPairStub);
        when(userTaskPairRepository.findAllByUserId(id)).thenReturn(pair);

        when(userTaskPairStub.getTaskId()).thenReturn(id);
        when(taskRepository.findById(id)).thenReturn(taskStub);

        Set<Folder> folders = new HashSet<Folder>();
        folders.add(folderStub);
        when(folderRepository.findAllByCreator(defaultName)).thenReturn(folders);

        //Act
        String sent = homeController.processAddTodoPage(taskStub, result, model, principal);

        //Assert
        assertThat(sent.equals("list-todos"));
    }

    @Test
    public void showAddFolderPageTest() throws Exception{
        //Act
        String sent = homeController.showAddFolderPage(model);

        //Assert
        assertThat(sent.equals("add-folder"));
    }

    @Test
    public void processAddFolderPageTest_HasErrors() throws Exception{
        //Arrange
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        //Act
        String sent = homeController.processAddFolderPage(folderStub, result, model, principal);

        //Assert
        assertThat(sent.equals("add-folder"));
    }

    @Test
    public void processAddFolderPageTest_NoErrors() throws Exception{
        //Arrange
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        when(principal.getName()).thenReturn(defaultName);

        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        Set<UserTaskPair> pairs = new HashSet<UserTaskPair>();
        pairs.add(userTaskPairStub);
        when(userStub.getId()).thenReturn(id);
        when(userTaskPairRepository.findAllByUserId(id)).thenReturn(pairs);

        when(userTaskPairStub.getTaskId()).thenReturn(id);
        when(taskRepository.findById(id)).thenReturn(taskStub);

        Set<Folder> folders = new HashSet<Folder>();
        folders.add(folderStub);
        when(folderRepository.findAllByCreator(defaultName)).thenReturn(folders);

        //Act
        String sent = homeController.processAddFolderPage(folderStub, result, model, principal);

        //Assert
        assertThat(sent.equals("list-todos"));
    }

    @Test(expected = NotFoundException.class)
    public void showSortOptionsTest_Exception() throws Exception{
        //Arrange
        when(taskRepository.findById(id)).thenReturn(null);

        //Act
        String sent = homeController.showSortOptions(id, principal, model);

        //Assert
    }

    @Test
    public void showSortOptionsTest_NoException() throws Exception{
        //Arrange
        when(taskRepository.findById(id)).thenReturn(taskStub);
        when(principal.getName()).thenReturn(defaultName);

        Set<Folder> folders = new HashSet<Folder>();
        folders.add(folderStub);
        when(folderRepository.findAllByCreator(defaultName)).thenReturn(folders);

        //Act
        String sent = homeController.showSortOptions(id, principal, model);

        //Assert
        assertThat(sent.equals("sort"));
    }

    @Test(expected = NotFoundException.class)
    public void sortTodoIntoListTest_ExceptionFolder() throws Exception{
        //Arrange
        long folderId = id;
        long taskId = id;
        when(folderRepository.findById(folderId)).thenReturn(null);

        //Act
        String sent = homeController.sortTodoIntoList(folderId, taskId, principal, model);
    }

    @Test(expected = NotFoundException.class)
    public void sortTodoIntoListTest_ExceptionTask() throws Exception{
        //Arrange
        long folderId = id;
        long taskId = id;
        when(folderRepository.findById(folderId)).thenReturn(folderStub);
        when(taskRepository.findById(taskId)).thenReturn(null);

        //Act
        String sent = homeController.sortTodoIntoList(folderId, taskId, principal, model);
    }

    @Test
    public void sortTodoIntoListTest_RunsNormally() throws Exception{
        //Arrange
        long folderId = id;
        long taskId = id;
        when(folderRepository.findById(folderId)).thenReturn(folderStub);
        when(taskRepository.findById(taskId)).thenReturn(taskStub);

        //From list-todos test
        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        Set<UserTaskPair> pairs = new HashSet<UserTaskPair>();
        pairs.add(userTaskPairStub);
        when(userStub.getId()).thenReturn(id);
        when(userTaskPairRepository.findAllByUserId(id)).thenReturn(pairs);

        when(userTaskPairStub.getTaskId()).thenReturn(id);
        when(taskRepository.findById(id)).thenReturn(taskStub);

        Set<Folder> folders = new HashSet<Folder>();
        folders.add(folderStub);
        when(folderRepository.findAllByCreator(defaultName)).thenReturn(folders);
        when(userStub.getGroupId()).thenReturn((int) id);

        //Act
        String sent = homeController.sortTodoIntoList(folderId, taskId, principal, model);

        //Assert
        assertThat(sent.equals("list-todos"));
    }

    @Test
    public void viewTasksInFolder_NoTasksInFolder() throws Exception{
        //Arrange
        when(folderRepository.findById(id)).thenReturn(folderStub);
        when(folderTaskPairRepository.findAllByFolderId(id)).thenReturn(null);

        //Act
        String sent = homeController.viewTasksInFolder(id, principal, model);

        //Assert
        assertThat(sent.equals("view-folder"));
    }

    @Test
    public void viewTasksInFolder_FolderHasTasks() throws Exception{
        long folderId = id;

        //Arrange
        when(folderRepository.findById(folderId)).thenReturn(folderStub);

        Set<FolderTaskPair> pairs = new HashSet<FolderTaskPair>();
        pairs.add(folderTaskPairStub);
        when(folderTaskPairRepository.findAllByFolderId(folderId)).thenReturn(pairs);

        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);

        when(folderTaskPairStub.getTaskId()).thenReturn(id);
        when(userStub.getId()).thenReturn(id);
        when(userTaskPairRepository.findByTaskIdAndUserId(id, id)).thenReturn(userTaskPairStub);

        when(userTaskPairStub.getTaskId()).thenReturn(id);
        when(taskRepository.findById(id)).thenReturn(taskStub);

        when(userStub.getGroupId()).thenReturn((int)id);

        //Act
        String sent = homeController.viewTasksInFolder(id, principal, model);

        //Assert
        assertThat(sent.equals("view-folder"));
    }

    @Test
    public void assignTaskToGroupMemberTest() throws Exception{
        //Arrange
        String memberName = "otherguy";
        long otherUserId = 2;
        int groupId = 101;

        when(userRepository.findByUsername(memberName)).thenReturn(groupMember);
        when(groupMember.getId()).thenReturn(otherUserId);
        when(principal.getName()).thenReturn(defaultName);

        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);
        when(userStub.getGroupId()).thenReturn(groupId);
        Set<User> groupMembers = new HashSet<User>();
        groupMembers.add(groupMember);
        groupMembers.add(userStub);
        when(userRepository.findAllByGroupId(groupId)).thenReturn(groupMembers);

        when(taskRepository.findById(id)).thenReturn(taskStub);
        Set<UserTaskPair> targetTaskPair = new HashSet<UserTaskPair>();
        targetTaskPair.add(userTaskPairStub);
        when(userTaskPairRepository.findAllByTaskId(id)).thenReturn(targetTaskPair);
        //Act
        String sent = homeController.assignTaskToGroupMember(id, memberName, principal, model);

        //Assert
        assertThat(sent.equals("assign"));
    }

    @Test
    public void assignTaskToGroupMemberTest_PairListSizeZero() throws Exception{
        //Arrange
        String memberName = "otherguy";
        long otherUserId = 2;
        int groupId = 101;

        when(userRepository.findByUsername(memberName)).thenReturn(groupMember);
        when(groupMember.getId()).thenReturn(otherUserId);
        when(principal.getName()).thenReturn(defaultName);

        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);
        when(userStub.getGroupId()).thenReturn(groupId);
        Set<User> groupMembers = new HashSet<User>();
        groupMembers.add(groupMember);
        groupMembers.add(userStub);
        when(userRepository.findAllByGroupId(groupId)).thenReturn(groupMembers);

        when(taskRepository.findById(id)).thenReturn(taskStub);
        Set<UserTaskPair> targetTaskPair = new HashSet<UserTaskPair>();
        when(userTaskPairRepository.findAllByTaskId(id)).thenReturn(targetTaskPair);
        //Act
        String sent = homeController.assignTaskToGroupMember(id, memberName, principal, model);

        //Assert
        assertThat(sent.equals("assign"));
    }

    @Test
    public void showAssignmentListTest() throws Exception{
        //Arrange
        int groupId = 50;

        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);
        when(userStub.getGroupId()).thenReturn(groupId);

        Set<User> groupMembers = new HashSet<User>();
        groupMembers.add(userStub);
        groupMembers.add(groupMember);
        when(userRepository.findAllByGroupId(groupId)).thenReturn(groupMembers);

        when(groupMember.getId()).thenReturn(id);
        when(userTaskPairRepository.findByTaskIdAndUserId(id, id)).thenReturn(userTaskPairStub);

        when(taskRepository.findById(id)).thenReturn(taskStub);

        //Act
        String sent = homeController.showAssignmentList(id, model, principal);

        //Assert
        assertThat(sent.equals("assign"));
    }

    @Test
    public void showAssignmentList_NullPairBranch() throws Exception{
        //Arrange
        int groupId = 50;

        when(principal.getName()).thenReturn(defaultName);
        when(userRepository.findByUsername(defaultName)).thenReturn(userStub);
        when(userStub.getGroupId()).thenReturn(groupId);

        Set<User> groupMembers = new HashSet<User>();
        groupMembers.add(userStub);
        groupMembers.add(groupMember);
        when(userRepository.findAllByGroupId(groupId)).thenReturn(groupMembers);

        when(groupMember.getId()).thenReturn(id);
        when(userTaskPairRepository.findByTaskIdAndUserId(id, id)).thenReturn(null);

        when(taskRepository.findById(id)).thenReturn(taskStub);

        //Act
        String sent = homeController.showAssignmentList(id, model, principal);

        //Assert
        assertThat(sent.equals("assign"));
    }
}
