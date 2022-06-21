package com.example.demo;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.Set;
import java.util.Iterator;

@Controller
public class HomeController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    FolderTaskPairRepository folderTaskPairRepository;

    @Autowired
    UserTaskPairRepository userTaskPairRepository;

    @RequestMapping("/")
    public String index() {
        return "index"; }

    //Ben's modification
    @RequestMapping("/login")
    public String login(Principal principal0, Model model0) {
        //Checking if user is logged in
        if(principal0 == null) {
            return "login";
        }
        //Return task list of current logged-in user
        else {
            //Calling listTodos method to generate user's task list
            return listTodos(principal0, model0);
        }
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login?logout=true"; }

    @RequestMapping("/admin")
    public String admin() {
        return "admin"; }

    @RequestMapping("/secure")
    //Principle retains all the information for the current user
    public String secure(Principal principal, Model model){
        String username = principal.getName();

        model.addAttribute("user", userRepository.findByUsername(username));
        //How to add the role into the model after matching it with user name
        model.addAttribute("roles", roleRepository.findAllByUsername(username));

       /*Optional from instead of having to return a set from the Rolerepository
        Set<Role> roles = new HashSet<Role>();
          roles = roleRepository.findAllByUsername(username);
          model.addAttribute("roles", roles);*/
        return "secure";

    }

    @GetMapping("/register")
    public String showRegisterationPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegisterationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        model.addAttribute("user", user);

        if (result.hasErrors()) {
            user.clearPassword();
            return "register";
        }
        else {
            model.addAttribute("message", "User Account Created!");
            user.setEnabled(true);

            Role role = new Role(user.getUsername(), "ROLE_USER");
            Set<Role> roles = new HashSet<Role>();
            roles.add(role);
            roleRepository.save(role);
            userRepository.save(user);
        }
        return "login";
    }

    @RequestMapping(value = "/complete-todo", method = RequestMethod.GET)
    public String updateTodoList(@RequestParam long id, ModelMap model, Principal principal){
        //Get the ID of the task
        Task task = taskRepository.findById(id);

        //Update completion status
        task.setCompletionStatus(true);
        taskRepository.save(task);

        //Get user identifier
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        //Get list of all tasks associated with user
        Set<UserTaskPair> allTasks = userTaskPairRepository.findAllByUserId(user.getId());
        Iterator<UserTaskPair> iterator = allTasks.iterator();

        Set<Task> taskList = new HashSet<>();
        while (iterator.hasNext()){
            Task nextTask = taskRepository.findById(iterator.next().getTaskId());
            taskList.add(nextTask);
        }

        //Get list of all folders associated with user
        Set<Folder> folderList = folderRepository.findAllByCreator(username);

        //Add tasks to list-todos list
        model.addAttribute(taskList);

        //Add folders to list-todos list
        model.addAttribute(folderList);

        return "list-todos";
    }

    @RequestMapping(value = "/delete-todo", method = RequestMethod.GET)
    public String deleteFromTodoList(@RequestParam long id, ModelMap model, Principal principal){

        //Delete task from repos
        taskRepository.deleteById(id);
        folderTaskPairRepository.deleteAll(folderTaskPairRepository.findAllByTaskId(id));
        userTaskPairRepository.deleteAll(userTaskPairRepository.findAllByTaskId(id));

        //Get user
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        //Get list of all tasks associated with user
        Set<UserTaskPair> allTasks = userTaskPairRepository.findAllByUserId(user.getId());
        Iterator<UserTaskPair> iterator = allTasks.iterator();

        Set<Task> taskList = new HashSet<>();
        while (iterator.hasNext()){
            Task nextTask = taskRepository.findById(iterator.next().getTaskId());
            taskList.add(nextTask);
        }

        //Get list of all folders associated with user
        Set<Folder> folderList = folderRepository.findAllByCreator(username);

        //Add tasks to list-todos list
        model.addAttribute(taskList);

        //Add folders to list-todos page
        model.addAttribute(folderList);

        return "list-todos";
    }

    @RequestMapping(value = "/edit-todo")
    public String editTodo(@RequestParam long id, Principal principal, Model model) throws NotFoundException{

        //Find todo
        Task task = taskRepository.findById(id);

        if (task == null){
            throw new NotFoundException("Task ID " + id + " not found.");
        }

        //Add todo to model
        model.addAttribute("task", task);

        return "edit-todo";
    }

    @PostMapping(value = "/edit-todo")
    public String postEditedTodo(@Valid @ModelAttribute("task") Task task, BindingResult result, Model model, Principal principal) throws NotFoundException {

        //Check for errors in user form
        if (result.hasErrors()) {
            model.addAttribute("task", task);
            return "edit-todo";
        }

        //Save changes to repository
        else {
            taskRepository.save(task);

            //Get user
            String username = principal.getName();
            User user = userRepository.findByUsername(username);

            //Get list of all tasks associated with user
            Set<UserTaskPair> allTasks = userTaskPairRepository.findAllByUserId(user.getId());
            Iterator<UserTaskPair> iterator = allTasks.iterator();

            Set<Task> taskList = new HashSet<>();
            while (iterator.hasNext()){
                Task nextTask = taskRepository.findById(iterator.next().getTaskId());
                taskList.add(nextTask);
            }

            //Get list of all folders associated with user
            Set<Folder> folderList = folderRepository.findAllByCreator(username);

            //Add tasks to list-todos list
            model.addAttribute(taskList);

            //Add folders to list-todos page
            model.addAttribute(folderList);

            return "redirect:list-todos";
        }
    }

    @RequestMapping("/list-todos")
    public String listTodos(Principal principal, Model model){

        String htmlPg = "";

        if (principal == null){
            return "login";
        }

        //Get user identifier
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        //Get list of all tasks associated with user
        Set<UserTaskPair> allTasks = userTaskPairRepository.findAllByUserId(user.getId());
        Iterator<UserTaskPair> iterator = allTasks.iterator();

        Set<Task> taskList = new HashSet<>();
        while (iterator.hasNext()){
            Task nextTask = taskRepository.findById(iterator.next().getTaskId());
            taskList.add(nextTask);
        }

        //Get list of all folders associated with user
        Set<Folder> folderList = folderRepository.findAllByCreator(username);

        //Add tasks to list-todos list
        model.addAttribute(taskList);

        //Add folders to list-todos page
        model.addAttribute(folderList);

        //Add group id to list-todos page
        model.addAttribute("groupId", user.getGroupId());

        return "list-todos";
    }

    @RequestMapping("/add-todo")
    public String showAddTodoPage(Model model, Principal principal) {

        if (principal == null){
            return "login";
        }

        model.addAttribute("task", new Task());
        return "add-todo";
    }

    @PostMapping("/add-todo")
    public String processAddTodoPage(@Valid @ModelAttribute("task") Task task, BindingResult result, Model model, Principal principal) {

        if (result.hasErrors()) {
            model.addAttribute("task", task);
            return "add-todo";
        }
        else {
            task.setUsername(principal.getName());
            taskRepository.save(task);

            //Get user identifier
            String username = principal.getName();
            User user = userRepository.findByUsername(username);

            //Create userTaskPair and add to database
            UserTaskPair newUserTaskPair = new UserTaskPair(user.getId(), task.getId());
            userTaskPairRepository.save(newUserTaskPair);

            //Get list of all tasks associated with user
            Set<UserTaskPair> allTasks = userTaskPairRepository.findAllByUserId(user.getId());
            Iterator<UserTaskPair> iterator = allTasks.iterator();

            Set<Task> taskList = new HashSet<>();
            while (iterator.hasNext()){
                Task nextTask = taskRepository.findById(iterator.next().getTaskId());
                taskList.add(nextTask);
            }

            //Get list of all folders associated with user
            Set<Folder> folderList = folderRepository.findAllByCreator(username);

            //Add tasks to list-todos list
            model.addAttribute(taskList);

            //Add folders to list-todos page
            model.addAttribute(folderList);

            return "list-todos";
        }

    }

    @RequestMapping("/add-folder")
    public String showAddFolderPage(Model model){
        model.addAttribute("folder", new Folder());
        return "add-folder";
    }

    @PostMapping("/add-folder")
    public String processAddFolderPage(@Valid @ModelAttribute("folder") Folder folder, BindingResult result, Model model, Principal principal){
        if (result.hasErrors()) {
            model.addAttribute("folder", folder);
            return "add-folder";
        }
        else {
            folder.setCreator(principal.getName());
            folderRepository.save(folder);

            //Get user identifier
            String username = principal.getName();
            User user = userRepository.findByUsername(username);

            //Get list of all tasks associated with user
            Set<UserTaskPair> allTasks = userTaskPairRepository.findAllByUserId(user.getId());
            Iterator<UserTaskPair> iterator = allTasks.iterator();

            Set<Task> taskList = new HashSet<>();
            while (iterator.hasNext()){
                Task nextTask = taskRepository.findById(iterator.next().getTaskId());
                taskList.add(nextTask);
            }

            //Get list of all folders associated with user
            Set<Folder> folderList = folderRepository.findAllByCreator(username);

            //Add tasks to list-todos list
            model.addAttribute(taskList);

            //Add folders to list-todos page
            model.addAttribute(folderList);

            return "list-todos";
        }
    }

    //When the user first enters the sort menu with only the task chosen
    @RequestMapping(value = "/sort")
    public String showSortOptions(@RequestParam("id") long taskId, Principal principal, Model model) throws NotFoundException{

        //Find task from task ID
        Task task = taskRepository.findById(taskId);

        //Check if task exists
        if (task == null){
            throw new NotFoundException("Task ID " + taskId + " not found.");
        }

        //Get folders from user
        Set<Folder> folders = folderRepository.findAllByCreator(principal.getName());

        //Add task to model
        model.addAttribute("task", task);

        //Add folders to model
        model.addAttribute("folderList", folders);

        return "sort";
    }

    //When the user selects a folder to add the task to,
    // it returns a task id and folder id to be linked in the DB
    @RequestMapping(value = "/sort/assigned")
    public String sortTodoIntoList(@RequestParam("folderId") long folderId, @RequestParam("taskId") long taskId, Principal principal, Model model) throws NotFoundException{

        //Check if folder exists
        Folder folder = folderRepository.findById(folderId);

        if (folder == null){
            throw new NotFoundException("Folder " + folderId + " not found!");
        }

        //Check if task exists
        Task task = taskRepository.findById(taskId);

        if (task == null){
            throw new NotFoundException("Task " + taskId + " not found!");
        }

        //Create new FolderTaskPair
        FolderTaskPair newPair = new FolderTaskPair(folderId, taskId);

        //Add pair to repository
        folderTaskPairRepository.save(newPair);

        return listTodos(principal, model);
    }

    @RequestMapping("/view-folder")
    public String viewTasksInFolder(@RequestParam("id") long folderId, Principal principal, Model model){

        //Get folder
        Folder currentFolder = folderRepository.findById(folderId);

        //Get list of all tasks associated with folder
        Set<FolderTaskPair> folderTaskPair = folderTaskPairRepository.findAllByFolderId(folderId);
        if (folderTaskPair == null){
            return "view-folder";
        }

        Set<Task> tasksInFolder = new HashSet<>();
        Iterator<FolderTaskPair> iterator = folderTaskPair.iterator();
        UserTaskPair currentpair;
        Task taskInList;
        long taskId;
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);

        while (iterator.hasNext()){
            //Get task id
            taskId = iterator.next().getTaskId();

            //Find associated user-task pair
            currentpair = userTaskPairRepository.findByTaskIdAndUserId(taskId, currentUser.getId());

            //Get task from user-task pair
            taskInList = taskRepository.findById(currentpair.getTaskId());

            //Add task to tasksInFolder
            tasksInFolder.add(taskInList);
        }

        //Add tasks to list-todos list
        model.addAttribute("taskList", tasksInFolder);

        //Add folders to list-todos page
        model.addAttribute("folder", currentFolder);

        User user = userRepository.findByUsername(principal.getName());
        model.addAttribute("groupId", user.getGroupId());

        return "view-folder";
    }

    @RequestMapping("/assign/complete")
    public String assignTaskToGroupMember(@RequestParam("id") long taskId, @RequestParam("username") String memberName, Principal principal, Model model){

        //Get group member
        User groupMember = userRepository.findByUsername(memberName);

        //Assign task to group member
        UserTaskPair newPair = new UserTaskPair(groupMember.getId(), taskId);
        userTaskPairRepository.save(newPair);

        //Add everything back to model
        User user = userRepository.findByUsername(principal.getName());
        Set<User> groupMembers = userRepository.findAllByGroupId(user.getGroupId());

        //Remove users who have been assigned the task
        groupMembers.remove(user);

        Iterator<User> iterator = groupMembers.iterator();
        Set<UserTaskPair> taskPairs;
        Set<UserTaskPair> targetTaskPair;
        while(iterator.hasNext()){

            //Check if group member has task in their list
            User currentUser = iterator.next();
            taskPairs = userTaskPairRepository.findAllByUserId(currentUser.getId());
            targetTaskPair = userTaskPairRepository.findAllByTaskId(taskId);

            //Remove group member from list if they have it
            if(targetTaskPair.size() != 0){
                groupMembers.remove(currentUser);
            }
        }

        //Get task from url
        Task currentTask = taskRepository.findById(taskId);

        //Add group list to model
        model.addAttribute("groupList", groupMembers);
        model.addAttribute("todo", currentTask);

        return "assign";
    }

    @RequestMapping("/assign")
    public String showAssignmentList(@RequestParam("id") long taskId, Model model, Principal principal){

        //Get group list from user
        User user = userRepository.findByUsername(principal.getName());
        Set<User> groupMembers = userRepository.findAllByGroupId(user.getGroupId());
        groupMembers.remove(user);

        Iterator<User> iterator = groupMembers.iterator();
        UserTaskPair targetTaskPair;
        while(iterator.hasNext()){

            //Check if group member has task in their list
            User currentUser = iterator.next();
            targetTaskPair = userTaskPairRepository.findByTaskIdAndUserId(taskId, currentUser.getId());

            //Remove group member from list if they have it
            if(targetTaskPair != null){
                groupMembers.remove(currentUser);
            }
        }

        //Get task from url
        Task currentTask = taskRepository.findById(taskId);

        //Add group list to model
        model.addAttribute("groupList", groupMembers);
        model.addAttribute("todo", currentTask);

        return "assign";
    }
}
