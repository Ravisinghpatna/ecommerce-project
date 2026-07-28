package com.example.ecommerce.model;

import jakarta.persistence.*;

/**
 * AdminUser — login credentials store karta hai. Sirf admin users hote hain
 * is project me (koi customer signup/login nahi — shopping bina login ke
 * open hai, sirf product add/edit/delete ke liye login chahiye).
 *
 * Password kabhi bhi plain text me store nahi hota — BCryptPasswordEncoder
 * se hash karke store hota hai (SecurityConfig me bean defined hai).
 */
@Entity
@Table(name = "admin_users")
public class AdminUser {

    @Override
	public String toString() {
		return "AdminUser [id=" + id + ", username=" + username + ", password=" + password + "]";
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // BCrypt hashed

    public AdminUser() {
    }

    public AdminUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
