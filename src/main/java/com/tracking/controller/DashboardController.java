package com.tracking.controller;

import com.tracking.model.EffortLog;
import com.tracking.model.User;
import com.tracking.repository.EffortLogRepository;
import com.tracking.repository.ProjectRepository;
import com.tracking.repository.TaskRepository;
import com.tracking.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private EffortLogRepository effortLogRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        model.addAttribute("user", user);

        // Common Data
        List<EffortLog> myLogs = effortLogRepository.findByUserId(user.getId());
        BigDecimal dailyHours = myLogs.stream()
                .filter(l -> l.getLogDate().equals(LocalDate.now()))
                .map(EffortLog::getHoursLogged)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("myLogs", myLogs);
        model.addAttribute("todayHours", dailyHours);
        model.addAttribute("projects", projectRepository.findAll());
        model.addAttribute("tasks", taskRepository.findAll());

        // Add user total hours and total cost
        BigDecimal myTotalHours = myLogs.stream()
                .map(EffortLog::getHoursLogged)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal hourlyRate = user.getHourlyRate() != null ? user.getHourlyRate() : BigDecimal.ZERO;
        BigDecimal myTotalEarnings = myTotalHours.multiply(hourlyRate);

        model.addAttribute("myTotalHours", myTotalHours);
        model.addAttribute("myTotalEarnings", myTotalEarnings);

        // SUPER ADMIN SPECIFIC DATA
        if (user.getRole() != null && "Super Admin".equals(user.getRole().getName())) {
            List<EffortLog> allLogs = effortLogRepository.findAll();

            // 1. Total Hours Logged (All Time)
            BigDecimal totalHours = allLogs.stream()
                    .map(EffortLog::getHoursLogged)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 2. Hours per Project
            Map<String, Double> projectHours = allLogs.stream()
                    .collect(Collectors.groupingBy(
                            log -> log.getTask().getProject().getName(),
                            Collectors.summingDouble(log -> log.getHoursLogged().doubleValue())));

            // 3. Hours per Employee
            // 3. Hours per Employee (Top 5 Performers)
            Map<Long, User> userMap = userRepository.findAll().stream()
                    .collect(Collectors.toMap(User::getId, u -> u));

            Map<String, Double> fullEmployeeHours = new java.util.HashMap<>();
            // Initialize all users with 0.0
            userMap.values().forEach(u -> fullEmployeeHours.put(u.getFirstName() + " " + u.getLastName(), 0.0));

            // Sum up hours from logs
            allLogs.forEach(log -> {
                User u = userMap.get(log.getUserId());
                if (u != null) {
                    String key = u.getFirstName() + " " + u.getLastName();
                    fullEmployeeHours.merge(key, log.getHoursLogged().doubleValue(), Double::sum);
                }
            });

            // Sort by hours (descending) and limit to Top 5
            Map<String, Double> employeeHours = fullEmployeeHours.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(5)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            java.util.LinkedHashMap::new));

            model.addAttribute("allLogs", allLogs);
            model.addAttribute("totalHours", totalHours);
            model.addAttribute("projectHours", projectHours);
            model.addAttribute("employeeHours", employeeHours);
            model.addAttribute("totalEmployees", userRepository.count());
            model.addAttribute("activeProjects", projectRepository.findAll().size());
        }

        return "dashboard";
    }

    @GetMapping("/employee-performance")
    public String employeePerformance(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"Super Admin".equals(user.getRole().getName())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("user", user);

        List<User> allUsers = userRepository.findAll();
        List<EffortLog> allLogs = effortLogRepository.findAll();

        // Create performance data structure
        // List of maps containing: user, totalHours, logs
        List<Map<String, Object>> performanceList = new java.util.ArrayList<>();

        for (User u : allUsers) {
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("user", u);

            List<EffortLog> userLogs = allLogs.stream()
                    .filter(l -> l.getUserId().equals(u.getId()))
                    .collect(Collectors.toList());

            BigDecimal totalHours = userLogs.stream()
                    .map(EffortLog::getHoursLogged)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            data.put("logs", userLogs);
            data.put("totalHours", totalHours);

            // Calculate Total Cost
            BigDecimal rate = u.getHourlyRate() != null ? u.getHourlyRate() : BigDecimal.ZERO;
            BigDecimal totalSpent = totalHours.multiply(rate);
            data.put("totalSpent", totalSpent);
            data.put("hourlyRate", rate);

            performanceList.add(data);
        }

        model.addAttribute("performanceList", performanceList);
        // Also add project/task maps if needed for display names in logs,
        // but logs have @ManyToOne Task which has Project, so we are good.

        return "employee-performance";
    }

    @PostMapping("/log-effort")
    public String logEffort(@RequestParam Long taskId,
            @RequestParam String hours,
            @RequestParam String date,
            @RequestParam String description,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        EffortLog log = new EffortLog();
        log.setUserId(user.getId());
        log.setTask(taskRepository.findById(taskId).orElseThrow());
        log.setHoursLogged(new BigDecimal(hours));
        log.setDescription(description);
        log.setLogDate(LocalDate.parse(date));

        effortLogRepository.save(log);

        return "redirect:/dashboard";
    }
}
