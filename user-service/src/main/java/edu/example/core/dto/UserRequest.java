package edu.example.core.dto;

import java.time.LocalDateTime;

public class UserRequest {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private LocalDateTime createdAt;

    public UserRequest() {}

    public UserRequest(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public Long getId() { return id; }
    /** Except C from CRUD*/
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    /** Only R from CRUD*/
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEmpty() {
        return(
            this.id == null
            && this.name == null
            && this.email == null
            && this.age == null
            && this.createdAt == null
        );
    }
}
