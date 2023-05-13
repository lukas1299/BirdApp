package com.birdapp.BirdApp.config;

import com.birdapp.BirdApp.entity.RoleType;
import com.birdapp.BirdApp.entity.Token;
import com.birdapp.BirdApp.entity.User;
import com.birdapp.BirdApp.repository.TokenRepository;
import com.birdapp.BirdApp.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private static final int FIFTEEN_MIN_AD_MILLIS = 15 * 60 * 1000;
    private static final int SIXTY_MIN_AD_MILLIS = 60 * 60 * 1000;

    @Test
    public void shouldAuthenticateSuccessfully() throws Exception {
        // given
        JwtRequest jwtRequest = new JwtRequest("testUser", "testPassword");

        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";

        User user = userBuilder("testUser@example.com", "testUser", "pass", RoleType.USER);

        Token createdToken = Token.builder().id(UUID.randomUUID()).token(accessToken).expired(false).revoked(false).build();

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.login(), jwtRequest.password()))).thenReturn(authentication);

        when(jwtTokenUtil.generateToken(authentication.getName(), FIFTEEN_MIN_AD_MILLIS)).thenReturn(accessToken);
        when(jwtTokenUtil.generateToken(authentication.getName(), SIXTY_MIN_AD_MILLIS)).thenReturn(refreshToken);
        when(userRepository.findByUsernameOrEmail(eq("testUser"), eq(null))).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(Token.class))).thenReturn(createdToken);

        // when
        JwtResponse jwtResponse = authenticationService.authenticate(jwtRequest);

        // then
        assertEquals(accessToken, jwtResponse.access_token());
        assertEquals(refreshToken, jwtResponse.refresh_token());
        verify(userRepository, times(1)).save(user);
    }

    private User userBuilder(String email, String username, String password, RoleType roleType) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .username(username)
                .password(password)
                .role(roleType)
                .build();
    }
}