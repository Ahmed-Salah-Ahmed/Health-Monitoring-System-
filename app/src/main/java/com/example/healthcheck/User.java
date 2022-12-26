package com.example.healthcheck;

public class User {
    private String name;
    private String email;
    private String id;
    private String age;
    private String phone;

    public User() { }

    public User(String name, String email, String id, String age, String phone) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.age = age;
        this.phone = phone;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getAge() { return age; }

    public void setAge(String age) { this.age = age; }

    public String getEmergencyphone() { return phone; }

    public void setEmergencyphone(String emergencyphone) { this.phone = emergencyphone; }

}
