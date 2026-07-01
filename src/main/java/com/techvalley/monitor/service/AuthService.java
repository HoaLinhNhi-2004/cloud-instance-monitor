package com.techvalley.monitor.service;

import com.techvalley.monitor.domain.Member;
import com.techvalley.monitor.dto.LoginRequest;
import com.techvalley.monitor.dto.LoginResponse;
import com.techvalley.monitor.exception.ResourceNotFoundException;
import com.techvalley.monitor.repository.MemberRepository;
import com.techvalley.monitor.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        String token = jwtUtil.generateToken(member.getEmail());
        return new LoginResponse(token, member.getEmail(), member.getName(), member.getRole());
    }
}
