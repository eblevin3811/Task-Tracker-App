package com.example.demo;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Controller
public class HomeController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    TaskRepository taskRepository;

    @RequestMapping("/")
    public String index() {
        return "index"; }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login?logout=true"; }

    @RequestMapping("/admin")
    public String admin() {
        return "admin"; }

        @RequestMapping("/secure")
        //Pricnple retains all the information for the current user
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
        model.addAttribute("user", user);//was inside the if with teachers

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

        //Get list of all tasks associated with user
        Set<Task> taskList = taskRepository.findAllByUsername(username);

        //Add tasks to list-todos list
        model.addAttribute(taskList);

        return "list-todos";
    }

    @RequestMapping(value = "/delete-todo", method = RequestMethod.GET)
    public String deleteFromTodoList(@Valid @ModelAttribute("task") Task task, ModelMap model, Principal principal){

        //Delete task from repo
        //taskRepository.deleteById(id);

        //Get list of all tasks with task id
        String username = principal.getName();

        //Get list of all tasks associated with user
        Set<Task> taskList = taskRepository.findAllByUsername(username);

        //Add tasks to list-todos list
        model.addAttribute(taskList);

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

        //Task task = taskRepository.findById(id);

        //Check for errors in user form
        if (result.hasErrors()) {
            model.addAttribute("task", task);
            return "edit-todo";
        }

        //Save changes to repository
        else {
            taskRepository.save(task);

            //Get user identifier
            String username = principal.getName();

            //Get list of all tasks associated with user
            Set<Task> taskList = taskRepository.findAllByUsername(username);

            //Add tasks to list-todos list
            model.addAttribute(taskList);

            return "redirect:list-todos";
        }
    }

    @RequestMapping("/list-todos")
    public String listTodos(Principal principal, Model model){

        //Get user identifier
        String username = principal.getName();

        //Get list of all tasks associated with user
        Set<Task> taskList = taskRepository.findAllByUsername(username);

        //Add tasks to list-todos list
        model.addAttribute(taskList);

        return "list-todos";
    }

    @RequestMapping("/add-todo")
    public String showAddTodoPage(Model model) {
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

            //Get list of all tasks associated with user
            Set<Task> taskList = taskRepository.findAllByUsername(username);

            //Add tasks to list-todos list
            model.addAttribute(taskList);

            return "list-todos";
        }

    }
}



