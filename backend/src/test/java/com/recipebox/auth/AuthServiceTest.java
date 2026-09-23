package com.recipebox.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.recipebox.auth.dto.LoginRequest;
import com.recipebox.common.AuthException;
import com.recipebox.user.User;
import com.recipebox.user.UserRepository;

class AuthServiceTest {

    private UserRepository users;
    private PasswordEncoder encoder;
    private AuthService service;

    @BeforeEach
    void setUp() {
        users = mock( UserRepository.class );
        encoder = mock( PasswordEncoder.class );
        service = new AuthService( users, encoder, new JwtService(
                "unit-test-secret-that-is-long-enough-for-hmac-sha256-signing", 60_000 ) );
    }

    private User activatedAlice() {
        User alice = new User( "Alice", "alice@example.com", "hash" );
        alice.setActivated( true );
        return alice;
    }

    @Test
    void wrongPasswordIsWrongCredentials() {
        when( users.findByEmailIgnoreCase( "alice@example.com" ) ).thenReturn( Optional.of( activatedAlice() ) );
        when( encoder.matches( "nope", "hash" ) ).thenReturn( false );

        AuthException e = assertThrows( AuthException.class,
                () -> service.login( new LoginRequest( "alice@example.com", "nope" ) ) );

        assertEquals( AuthException.WRONG_CREDENTIALS, e.getCode() );
    }

    @Test
    void unknownEmailIsAlsoWrongCredentials() {
        when( users.findByEmailIgnoreCase( anyString() ) ).thenReturn( Optional.empty() );

        AuthException e = assertThrows( AuthException.class,
                () -> service.login( new LoginRequest( "nobody@example.com", "bookworm" ) ) );

        assertEquals( AuthException.WRONG_CREDENTIALS, e.getCode() );
    }

    @Test
    void anUnactivatedAccountCannotLogIn() {
        User carol = new User( "Carol", "carol@example.com", "hash" );
        when( users.findByEmailIgnoreCase( "carol@example.com" ) ).thenReturn( Optional.of( carol ) );
        when( encoder.matches( "longenough", "hash" ) ).thenReturn( true );

        AuthException e = assertThrows( AuthException.class,
                () -> service.login( new LoginRequest( "carol@example.com", "longenough" ) ) );

        assertEquals( AuthException.NOT_ACTIVATED, e.getCode() );
    }

    @Test
    void aGoodLoginReturnsATokenForTheUser() {
        when( users.findByEmailIgnoreCase( "alice@example.com" ) ).thenReturn( Optional.of( activatedAlice() ) );
        when( encoder.matches( "bookworm", "hash" ) ).thenReturn( true );

        var response = service.login( new LoginRequest( "alice@example.com", "bookworm" ) );

        assertEquals( "Alice", response.name() );
    }
}
