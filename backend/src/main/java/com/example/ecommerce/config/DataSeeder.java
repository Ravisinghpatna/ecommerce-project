package com.example.ecommerce.config;

import com.example.ecommerce.model.AdminUser;
import com.example.ecommerce.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner -> is method ka run() application fully start hone ke
 * turant baad, automatically ek baar chalta hai.
 *
 * Kaam: Agar database me koi admin user nahi hai (fresh setup), to
 * application.properties me diye gaye default username/password se
 * ek admin user bana do — taaki pehli baar app chalate hi login kiya ja sake.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.username}")
    private String defaultUsername;

    @Value("${admin.default.password}")
    private String defaultPassword;

    public DataSeeder(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminUserRepository.findByUsername(defaultUsername).isEmpty()) {
            AdminUser admin = new AdminUser(defaultUsername, passwordEncoder.encode(defaultPassword));
            adminUserRepository.save(admin);
            System.out.println(">>> Default admin created — username: " + defaultUsername
                    + " | password: " + defaultPassword + " (application.properties me change kar sakte hain) <<<");
        }
    }
}
