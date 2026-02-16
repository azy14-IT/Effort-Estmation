package com.tracking.controller;

import com.tracking.model.Task;
import com.tracking.model.User;
import com.tracking.repository.ProjectRepository;
import com.tracking.repository.TaskRepository;
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
import java.time.LocalDateTime;

@Controller
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/tasks")
    public String showTasks(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/login";

        model.addAttribute("tasks", taskRepository.findAll());
        model.addAttribute("projects", projectRepository.findAll());
        model.addAttribute("users", userRepository.findAll());

        return "tasks";
    }

    @PostMapping("/tasks/create")
    public String createTask(@RequestParam String title,
            @RequestParam String description,
            @RequestParam BigDecimal estimatedHours,
            @RequestParam Long projectId,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam String priority,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        // RBAC: Only Admin, Project Manager, or Team Lead can create tasks
        String role = user.getRole().getName();
        if (!"Super Admin".equals(role) && !"Project Manager".equals(role) && !"Team Lead".equals(role)) {
            return "redirect:/tasks?error=unauthorized";
        }

        return saveTask(new Task(), title, description, estimatedHours, projectId, assignedToId, priority, "TODO",
                session);
    }

    @GetMapping("/tasks/edit/{id}")
    public String editTask(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        // RBAC Check
        String role = user.getRole().getName();
        if (!"Super Admin".equals(role) && !"Project Manager".equals(role) && !"Team Lead".equals(role)) {
            return "redirect:/tasks?error=unauthorized";
        }

        model.addAttribute("task", taskRepository.findById(id).orElseThrow());
        model.addAttribute("projects", projectRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "task-edit";
    }

    @PostMapping("/tasks/update")
    public String updateTask(@RequestParam Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam BigDecimal estimatedHours,
            @RequestParam Long projectId,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam String priority,
            @RequestParam String status,
            HttpSession session) {
        Task task = taskRepository.findById(id).orElseThrow();
        return saveTask(task, title, description, estimatedHours, projectId, assignedToId, priority, status, session);
    }

    private String saveTask(Task task, String title, String description, BigDecimal estimatedHours,
            Long projectId, Long assignedToId, String priority, String status, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        task.setTitle(title);
        task.setDescription(description);
        task.setEstimatedHours(estimatedHours);
        task.setProject(projectRepository.findById(projectId).orElseThrow());

        if (assignedToId != null) {
            task.setAssignedTo(userRepository.findById(assignedToId).orElse(null));
        } else {
            task.setAssignedTo(null);
        }

        task.setPriority(Task.Priority.valueOf(priority));
        if (task.getCreatedBy() == null)
            task.setCreatedBy(user.getId());
        task.setStatus(Task.TaskStatus.valueOf(status));

        if (task.getDueDate() == null)
            task.setDueDate(LocalDateTime.now().plusDays(7));

        taskRepository.save(task);
        return "redirect:/tasks";
    }
}
