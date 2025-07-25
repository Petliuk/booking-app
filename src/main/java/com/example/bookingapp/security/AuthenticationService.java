package com.example.bookingapp.security;

import com.example.bookingapp.dto.user.UserLoginRequestDto;
import com.example.bookingapp.dto.user.UserLoginResponseDto;
import com.example.bookingapp.model.User;
import com.example.bookingapp.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public UserLoginResponseDto authenticate(UserLoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with email: " + request.getEmail() + " not found"));

        String token = jwtUtil.generateToken(user.getId());
        return new UserLoginResponseDto(token);
    }
}
