package com.polentzi.controlview.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.polentzi.controlview.models.User;
import com.polentzi.controlview.services.UserService;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	private final UserService userService;
    public HomeController(UserService userService) {
        this.userService = userService;
    }
    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registrationPage.jsp";
    }
    
    //si el resultado tiene errores, retornar a la página de registro (no se preocupe por las validaciones por ahora)
    //si no, guarde el usuario en la base de datos, guarde el id del usuario en el objeto Session y redirija a /home
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
    	if(result.hasErrors()) {
    		return "redirect:/registration";
    	}else {
    		User newUser=userService.registerUser(user);
    		session.setAttribute("user_id",newUser.getId());
    		return "redirect:/home";
    	}
    	
    }
    @GetMapping("/login")
    public String loginPage() {
    	return "loginPage.jsp";
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session,RedirectAttributes redirectAttributes) {
    	if(userService.authenticateUser(email,password)) {
    		session.setAttribute("user_id",userService.findByEmail(email).getId());
    		return "redirect:/home";
    	}else {
    		
    		redirectAttributes.addFlashAttribute("error","Login information didn't match our records! Please, double-check it again.");
    		return "redirect:/login";
    	}
    }
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
    	if(session.getAttribute("user_id")==null) {
    		return "you must be logged in";
    	}else {
    		model.addAttribute("user",userService.findUserById((Long) session.getAttribute("user_id")) );
    		return "homePage.jsp";
    	}
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
    	session.invalidate();
    	return "redirect:/login";
    }

}