package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.EmailDTO;
import com.claravalstore.backend.dto.NewPasswordDTO;
import com.claravalstore.backend.entities.PasswordRecover;
import com.claravalstore.backend.entities.User;
import com.claravalstore.backend.repositories.PasswordRecoverRepository;
import com.claravalstore.backend.repositories.UserRepository;
import com.claravalstore.backend.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${spring.mail.username}")
    private String defaultSender;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void createRecoverToken(EmailDTO body) {
        User user = userRepository.findByEmail(body.getEmail());

        if (user == null)
            throw new ResourceNotFoundException("Email não encontrado");

        String token = UUID.randomUUID().toString();

        var entity = new PasswordRecover();
        entity.setToken(token);
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));
        entity.setEmail(body.getEmail());
        passwordRecoverRepository.save(entity);

        var text = "Acesse o link para definir uma nova senha (válido por " + tokenMinutes + " minutos):\n\n"
                + recoverUri + token;

        emailService.sendEmail(body.getEmail(), "Recuperação de senha", text);
    }

    @Transactional
    public void saveNewPassword(NewPasswordDTO body) {
        List<PasswordRecover> list = passwordRecoverRepository.searchValidTokens(body.getToken(), Instant.now());

        if (list.isEmpty())
            throw new UsernameNotFoundException("Token inválido");

        var user = userRepository.findByEmail(list.get(0).getEmail());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        userRepository.save(user);

    }

    protected User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return userRepository.findByEmail(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Usuário inválido");
        }
    }

}
