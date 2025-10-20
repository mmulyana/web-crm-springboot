package com.crm.web.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.crm.web.dto.LoginDto;
import com.crm.web.dto.RegisterDto;
import com.crm.web.models.AppUser;
import com.crm.web.repositories.AppUserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

  @Autowired
  private AppUserRepository repo;

  private final BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();

  @GetMapping("/login")
  public String login(Model model) {
    model.addAttribute("loginDto", new LoginDto());
    return "login";
  }

  @PostMapping("/login")
  public String login(
      @Valid @ModelAttribute LoginDto loginDto,
      BindingResult result,
      Model model,
      RedirectAttributes redirectAttributes,
      HttpServletRequest request) {
    if (result.hasErrors()) {
      return "login";
    }

    AppUser user = repo.findByEmail(loginDto.getEmail());
    if (user == null) {
      result.addError(new FieldError("loginDto", "email", "Email tidak ditemukan"));
      return "login";
    }

    boolean passwordMatches = bCrypt.matches(loginDto.getPassword(), user.getPassword());
    if (!passwordMatches) {
      result.addError(new FieldError("loginDto", "password", "Password salah"));
      return "login";
    }

    // Jika berhasil login
    Authentication auth = new UsernamePasswordAuthenticationToken(
        user.getEmail(),
        null,
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())));
    SecurityContextHolder.getContext().setAuthentication(auth);

    HttpSession session = request.getSession(true);
    session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

    redirectAttributes.addFlashAttribute("successMessage", "Selamat datang, " + user.getUsername() + "!");
    return "redirect:/home";

  }

  @GetMapping("/register")
  public String register(Model model) {
    RegisterDto registerDto = new RegisterDto();
    model.addAttribute("registerDto", registerDto);
    model.addAttribute("success", false);
    return "register";
  }

  @PostMapping("/register")
  public String register(Model model, @Valid @ModelAttribute RegisterDto registerDto, BindingResult result) {
    AppUser appUserEmail = repo.findByEmail(registerDto.getEmail());
    if (appUserEmail != null) {
      result.addError(new FieldError("registerDto", "email", "email address is already used"));
    }
    AppUser appUserUsername = repo.findByUsername(registerDto.getUsername());
    if (appUserUsername != null) {
      result.addError(new FieldError("registerDto", "username", "username address is already used"));
    }

    if (result.hasErrors()) {
      return "register";
    }

    try {
      var bCryptEncoder = new BCryptPasswordEncoder();

      AppUser newUser = new AppUser();
      newUser.setUsername(registerDto.getUsername());
      newUser.setEmail(registerDto.getEmail());
      newUser.setPassword(bCryptEncoder.encode(registerDto.getPassword()));
      newUser.setRole("client");

      repo.save(newUser);
      model.addAttribute("success", true);
      return "register";
    } catch (Exception e) {
      e.printStackTrace();
      model.addAttribute("errorMessage", "An error occurred during registration. Please try again.");
      return "register";
    }
  }

  @GetMapping("/logout")
  public String logout(HttpServletRequest request, HttpServletResponse response,
      RedirectAttributes redirectAttributes) {
    // hapus authentication dari context
    SecurityContextHolder.clearContext();

    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }

    redirectAttributes.addFlashAttribute("successMessage", "Anda telah logout.");
    return "redirect:/login";
  }
}
