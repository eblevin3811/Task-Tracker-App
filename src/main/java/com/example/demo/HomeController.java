package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Controller
public class HomeController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

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
}



