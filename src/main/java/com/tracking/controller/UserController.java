package com.tracking.controller;

import com.tracking.model.User;
import com.tracking.model.Role;
import com.tracking.model.Department;
import com.tracking.repository.UserRepository;
import com.tracking.repository.RoleRepository;
import com.tracking.repository.DepartmentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/users")
    public String listUsers(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        // RBAC: Only Super Admin can see users
        if (!"Super Admin".equals(user.getRole().getName())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        return "users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@org.springframework.web.bind.annotation.PathVariable Long id, Model model,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        if (!"Super Admin".equals(user.getRole().getName())) {
            return "redirect:/dashboard";
        }

        User targetUser = userRepository.findById(id).orElseThrow();
        model.addAttribute("targetUser", targetUser);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        return "user-edit";
    }

    @PostMapping("/users/update")
    public String updateUser(@RequestParam Long id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) java.math.BigDecimal hourlyRate,
            @RequestParam Integer roleId,
            @RequestParam Integer departmentId,
            @RequestParam Boolean isActive,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        if (!"Super Admin".equals(user.getRole().getName())) {
            return "redirect:/dashboard";
        }

        User existingUser = userRepository.findById(id).orElseThrow();
        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser.setEmail(email);
        existingUser.setHourlyRate(hourlyRate);
        existingUser.setRole(roleRepository.findById(roleId).orElseThrow());
        existingUser.setDepartment(departmentRepository.findById(departmentId).orElseThrow());
        existingUser.setIsActive(isActive);

        userRepository.save(existingUser);
        return "redirect:/users";
    }

    @PostMapping("/users/create")
    public String createUser(@RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) java.math.BigDecimal hourlyRate,
            @RequestParam String password,
            @RequestParam Integer roleId,
            @RequestParam Integer departmentId,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        // RBAC: Only Super Admin can add users
        if (!"Super Admin".equals(user.getRole().getName())) {
            return "redirect:/dashboard";
        }

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setHourlyRate(hourlyRate);
        newUser.setPassword(password);

        Role role = roleRepository.findById(roleId).orElseThrow();
        newUser.setRole(role);

        Department dept = departmentRepository.findById(departmentId).orElseThrow();
        newUser.setDepartment(dept);

        userRepository.save(newUser);
        return "redirect:/users";
    }

    @PostMapping("/users/reset-password")
    public String resetPassword(@RequestParam Long id, @RequestParam String newPassword, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"Super Admin".equals(user.getRole().getName())) {
            return "redirect:/login";
        }
        User targetUser = userRepository.findById(id).orElseThrow();
        targetUser.setPassword(newPassword);
        userRepository.save(targetUser);
        return "redirect:/users";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";
        model.addAttribute("user", user);
        return "change-password";
    }

    @PostMapping("/change-password")
    public String processChangePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
            HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        User dbUser = userRepository.findById(user.getId()).orElseThrow();
        if (!dbUser.getPassword().equals(oldPassword)) {
            model.addAttribute("error", "Incorrect current password!");
            model.addAttribute("user", dbUser);
            return "change-password";
        }
        dbUser.setPassword(newPassword);
        userRepository.save(dbUser);
        model.addAttribute("success", "Password updated successfully!");
        model.addAttribute("user", dbUser);
        return "change-password";
    }
}
