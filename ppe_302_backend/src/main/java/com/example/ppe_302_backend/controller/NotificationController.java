package com.example.ppe_302_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:8080")
public class NotificationController {

    // Simulation d'un stockage en mémoire (en production, utilisez une base de données)
    private static Map<Long, List<Map<String, Object>>> notifications = new HashMap<>();

    @PostMapping("/client/{clientId}")
    public ResponseEntity<String> envoyerNotification(
            @PathVariable Long clientId,
            @RequestParam String type,
            @RequestParam String message) {

        notifications.computeIfAbsent(clientId, k -> new ArrayList<>());

        Map<String, Object> notification = new HashMap<>();
        notification.put("id", System.currentTimeMillis());
        notification.put("type", type);
        notification.put("message", message);
        notification.put("lu", false);
        notification.put("timestamp", System.currentTimeMillis());

        notifications.get(clientId).add(0, notification);

        return ResponseEntity.ok("Notification envoyée");
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Map<String, Object>>> getNotifications(@PathVariable Long clientId) {
        return ResponseEntity.ok(notifications.getOrDefault(clientId, new ArrayList<>()));
    }

    @PutMapping("/client/{clientId}/marquer-lu/{notificationId}")
    public ResponseEntity<String> marquerCommeLu(
            @PathVariable Long clientId,
            @PathVariable Long notificationId) {

        List<Map<String, Object>> clientNotifications = notifications.get(clientId);
        if (clientNotifications != null) {
            clientNotifications.stream()
                    .filter(n -> n.get("id").equals(notificationId))
                    .findFirst()
                    .ifPresent(n -> n.put("lu", true));
        }

        return ResponseEntity.ok("Notification marquée comme lue");
    }

    @GetMapping("/client/{clientId}/non-lues")
    public ResponseEntity<Long> getNombreNonLues(@PathVariable Long clientId) {
        List<Map<String, Object>> clientNotifications = notifications.getOrDefault(clientId, new ArrayList<>());
        long nonLues = clientNotifications.stream()
                .filter(n -> !(Boolean) n.get("lu"))
                .count();

        return ResponseEntity.ok(nonLues);
    }
}