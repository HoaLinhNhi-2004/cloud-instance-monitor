package com.techvalley.monitor.config;

import com.techvalley.monitor.domain.Member;
import com.techvalley.monitor.domain.enums.MemberRole;
import com.techvalley.monitor.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (memberRepository.existsByEmail("admin@techvalley.com")) return;

        Member admin = new Member();
        admin.setEmail("admin@techvalley.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setName("Admin");
        admin.setRole(MemberRole.ADMIN);
        memberRepository.save(admin);
    }
}
