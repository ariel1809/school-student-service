package com.school.student.service;

import com.school.student.domain.Classroom;
import com.school.student.domain.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentEventPublisher {

    public static final String EXCHANGE = "school.students";

    private final RabbitTemplate rabbit;

    public void publishEnrolled(Student s) {
        send("student.enrolled", basePayload(s));
    }

    public void publishUpdated(Student s) {
        send("student.updated", basePayload(s));
    }

    public void publishStatusChanged(Student s) {
        Map<String, Object> p = basePayload(s);
        p.put("status", s.getStatus().name());
        send("student.status-changed", p);
    }

    public void publishClassroomAssigned(Student s, Classroom c) {
        Map<String, Object> p = basePayload(s);
        p.put("classroomId", c.getId().toString());
        p.put("classroomName", c.getName());
        send("student.classroom-assigned", p);
    }

    private Map<String, Object> basePayload(Student s) {
        Map<String, Object> p = new HashMap<>();
        p.put("type", "student.event");
        p.put("studentId", s.getId().toString());
        p.put("matricule", s.getMatricule());
        p.put("fullName", s.getFirstName() + " " + s.getLastName());
        p.put("classroomId", s.getCurrentClassroomId() == null ? null : s.getCurrentClassroomId().toString());
        p.put("timestamp", Instant.now().toString());
        return p;
    }

    private void send(String routingKey, Map<String, Object> event) {
        event.put("type", routingKey);
        try {
            rabbit.convertAndSend(EXCHANGE, routingKey, event);
        } catch (Exception ex) {
            log.warn("Failed to publish {}: {}", routingKey, ex.getMessage());
        }
    }

    @Configuration
    @RequiredArgsConstructor
    public static class StudentRabbitTopology {
        @Bean
        public TopicExchange studentsExchange() {
            return new TopicExchange(EXCHANGE, true, false);
        }
    }
}
