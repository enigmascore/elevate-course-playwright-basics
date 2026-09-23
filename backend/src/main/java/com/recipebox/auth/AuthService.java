package com.recipebox.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recipebox.auth.dto.LoginRequest;
import com.recipebox.auth.dto.LoginResponse;
import com.recipebox.common.AuthException;
import com.recipebox.user.User;
import com.recipebox.user.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService( UserRepository userRepository, PasswordEncoder passwordEncoder,
            JwtService jwtService ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional( readOnly = true )
    public LoginResponse login( LoginRequest request ) {
        String email = request.email() == null ? "" : request.email().trim();
        String password = request.password() == null ? "" : request.password();

        User user = userRepository.findByEmailIgnoreCase( email )
                .filter( u -> passwordEncoder.matches( password, u.getPasswordHash() ) )
                .orElseThrow( () -> new AuthException( AuthException.WRONG_CREDENTIALS,
                        "Wrong email or password" ) );

        if ( !user.isActivated() ) {
            throw new AuthException( AuthException.NOT_ACTIVATED, "Please activate your account first" );
        }

        return new LoginResponse( jwtService.issue( user.getEmail() ), user.getName(), user.getEmail() );
    }
}
