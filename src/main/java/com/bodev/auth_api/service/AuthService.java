package com.bodev.auth_api.service;


import com.bodev.auth_api.config.JwtService;
import com.bodev.auth_api.dto.AuthRequest;
import com.bodev.auth_api.dto.AuthResponse;
import com.bodev.auth_api.dto.RegisterRequest;
import com.bodev.auth_api.entity.User;
import com.bodev.auth_api.exception.EmailAlreadyExistsException;
import com.bodev.auth_api.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    //CONSTRUCTOR
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("El email " + request.getEmail() + " ya está registrado");
        }


        User user = new User(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                User.Role.USER, // siempre USER en registro público
                true
        );


        userRepository.save(user);

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                token,
                refreshToken,
                user.getEmail(),
                user.getRole().name(),
                user.getFullName()
        );
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(
                token,
                refreshToken,
                user.getEmail(),
                user.getRole().name(),
                user.getFullName()
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Refresh token inválido");
        }

        String newToken = jwtService.generateToken(user);

        return new AuthResponse(
                newToken,
                refreshToken,
                user.getEmail(),
                user.getRole().name(),
                user.getFullName()
        );
    }
    public String extractEmailFromToken(String token) {
        return jwtService.extractEmail(token);
    }

    public long getTokenRemainingTime(String token) {
        long expiration = jwtService.getExpirationTime(token);
        long now = System.currentTimeMillis();
        return Math.max(0, (expiration - now) / 1000);
    }
}