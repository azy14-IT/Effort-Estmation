package com.tracking.config;

import com.tracking.model.*;
import com.tracking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) throws Exception {
        if (projectRepository.findByName("Create Website for Karakudi") != null) {
            return; // Data already exists
        }

        // 1. Ensure Roles (Fetching existing ones)
        Role managerRole = roleRepository.findAll().stream().filter(r -> r.getName().equals("Project Manager"))
                .findFirst().orElse(null);
        Role leadRole = roleRepository.findAll().stream().filter(r -> r.getName().equals("Team Lead")).findFirst()
                .orElse(null);
        Role empRole = roleRepository.findAll().stream().filter(r -> r.getName().equals("Employee")).findFirst()
                .orElse(null);

        // 2. Ensure Departments
        Department engineering = departmentRepository.findAll().stream().filter(d -> d.getName().equals("Engineering"))
                .findFirst().orElse(null);

        // 3. Create Users
        User manager = createUser("Siva", "Manager", "manager@karakudi.com", "password", managerRole, engineering);
        User lead = createUser("Ravi", "Lead", "lead@karakudi.com", "password", leadRole, engineering);
        User dev = createUser("Arun", "Developer", "dev@karakudi.com", "password", empRole, engineering);

        // 4. Create Project
        Project project = new Project();
        project.setName("Create Website for Karakudi");
        project.setDescription("Full stack development for Karakudi tourism and municipal services.");
        project.setBudget(new BigDecimal("15000.00"));
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusMonths(3));
        project.setManagerId(manager.getId()); // Assigned to Siva Manager
        project.setStatus(Project.ProjectStatus.ACTIVE);
        projectRepository.save(project);

        // 5. Create Tasks
        createTask("Design UI/UX Mockups", "Create Figma designs for Home, About, and Contact pages.",
                new BigDecimal("20.0"), project, lead, Task.Priority.HIGH); // Assigned to Team Lead

        createTask("Develop Backend API", "Setup Spring Boot and MySQL architecture.",
                new BigDecimal("40.0"), project, dev, Task.Priority.CRITICAL); // Assigned to Developer

        createTask("Frontend Integration", "Connect Thymeleaf templates with Backend APIs.",
                new BigDecimal("30.0"), project, null, Task.Priority.MEDIUM); // Unassigned (For anyone to pick)

        System.out.println("✅ 'Create Website for Karakudi' project seeded successfully!");
    }

    private User createUser(String first, String last, String email, String password, Role role, Department dept) {
        if (userRepository.findByEmail(email) != null)
            return userRepository.findByEmail(email);
        User user = new User();
        user.setFirstName(first);
        user.setLastName(last);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setDepartment(dept);
        user.setIsActive(true);
        return userRepository.save(user);
    }

    private void createTask(String title, String desc, BigDecimal hours, Project project, User assignee,
            Task.Priority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(desc);
        task.setEstimatedHours(hours);
        task.setProject(project);
        task.setAssignedTo(assignee);
        task.setPriority(priority);
        task.setStatus(Task.TaskStatus.TODO);
        task.setCreatedBy(1L); // Assuming Admin ID 1
        task.setDueDate(LocalDateTime.now().plusWeeks(2));
        taskRepository.save(task);
    }
}
