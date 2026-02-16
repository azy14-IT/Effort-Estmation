package com.tracking.controller;

import com.tracking.model.Project;
import com.tracking.model.User;
import com.tracking.repository.ProjectRepository;
import com.tracking.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/projects")
    public String showProjects(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";

        model.addAttribute("projects", projectRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "projects";
    }

    @PostMapping("/projects/create")
    public String createProject(@RequestParam String name,
            @RequestParam String description,
            @RequestParam BigDecimal budget,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Long managerId,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        // RBAC: Only Super Admin or Project Manager can create projects
        String role = user.getRole().getName();
        if (!"Super Admin".equals(role) && !"Project Manager".equals(role)) {
            return "redirect:/projects?error=unauthorized";
        }

        return saveProject(new Project(), name, description, budget, startDate, endDate, managerId, "PLANNED", session);
    }

    @GetMapping("/projects/edit/{id}")
    public String editProject(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        // RBAC Check
        String role = user.getRole().getName();
        if (!"Super Admin".equals(role) && !"Project Manager".equals(role)) {
            return "redirect:/projects?error=unauthorized";
        }

        model.addAttribute("project", projectRepository.findById(id).orElseThrow());
        model.addAttribute("users", userRepository.findAll());
        return "project-edit";
    }

    @PostMapping("/projects/update")
    public String updateProject(@RequestParam Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam BigDecimal budget,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Long managerId,
            @RequestParam String status,
            HttpSession session) {
        Project project = projectRepository.findById(id).orElseThrow();
        return saveProject(project, name, description, budget, startDate, endDate, managerId, status, session);
    }

    private String saveProject(Project project, String name, String description, BigDecimal budget, String startDate,
            String endDate, Long managerId, String status, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        project.setName(name);
        project.setDescription(description);
        project.setBudget(budget);
        project.setStartDate(LocalDate.parse(startDate));
        project.setEndDate(LocalDate.parse(endDate));
        project.setManagerId(managerId != null ? managerId : user.getId());
        project.setStatus(Project.ProjectStatus.valueOf(status));

        projectRepository.save(project);
        return "redirect:/projects";
    }
}
