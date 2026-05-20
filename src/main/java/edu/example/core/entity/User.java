package edu.example.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private Integer age;

    @Column(nullable = false)
    private String password;

    @Convert(converter = RoleListConverter.class)
    @Column(columnDefinition = "jsonb")
    private List<UserRole> roles = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public User(
    String name, String email, Integer age,
    String password, List<UserRole> roles) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.password = password;
        this.roles = roles != null ? roles : new ArrayList<>();
    }

    public User(String name, String email, Integer age, String password) {
        this(name, email, age, password, null);
    }

    public User(String name, String email, String password) {
        this(name, email, null, password, null);
    }

    public boolean hasRole(UserRole role) {
        return roles != null && roles.contains(role);
    }
}
