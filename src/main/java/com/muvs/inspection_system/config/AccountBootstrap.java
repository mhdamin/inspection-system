package com.muvs.inspection_system.config;

import com.muvs.inspection_system.entity.Role;
import com.muvs.inspection_system.entity.User;
import com.muvs.inspection_system.repository.RoleRepository;
import com.muvs.inspection_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

/** Explicit first-admin provisioning without public registration or demo credentials. */
@Component
@Order(-100)
@RequiredArgsConstructor
public class AccountBootstrap implements CommandLineRunner {
    private final RoleRepository roles;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    @Value("${app.bootstrap.username:}") private String username;
    @Value("${app.bootstrap.password:}") private String password;

    @Override
    @Transactional
    public void run(String... args) {
        String[] names={"ROLE_CUSTOMER","ROLE_USER","ROLE_INSPECTOR","ROLE_MANAGER","ROLE_ADMIN","ROLE_SUPERADMIN"};
        for (int i=0;i<names.length;i++) if (roles.findByName(names[i]).isEmpty())
            roles.save(Role.builder().name(names[i]).level(i).description(names[i].substring(5)).build());
        if (username.isBlank()) return;
        if (users.count() != 0) return;
        if (password.length() < 12) throw new IllegalStateException("Initial administrator password must contain at least 12 characters");
        users.save(User.builder().username(username).password(encoder.encode(password))
                .roles(Set.of(roles.findByName("ROLE_SUPERADMIN").orElseThrow())).build());
    }
}
