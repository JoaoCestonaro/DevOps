package br.com.fatecads.fatecads.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomAuthenticationSuccessHandler successHandler) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // =========================
                // ROTAS PÚBLICAS
                // =========================
                .requestMatchers(
                    "/login",
                    "/recuperar-senha",
                    "/recuperar-senha/**",
                    "/fatecads",
                    "/css/**",
                    "/images/**",
                    "/usuarios/criar",
                    "/usuarios/salvar"
                ).permitAll()

                // =========================
                // INDEX
                // ADMIN E USER PODEM ACESSAR
                // =========================
                .requestMatchers("/index")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")

                // =========================
                // HOME DO ADMIN
                // =========================
                .requestMatchers("/home")
                .hasAuthority("ROLE_ADMIN")

                // =========================
                // HOME DO CLIENTE
                // =========================
                .requestMatchers("/homecliente")
                .hasAuthority("ROLE_USER")

                // =========================
                // CRUDS - SOMENTE ADMIN
                // =========================
                .requestMatchers(
                    "/usuarios/**",
                    "/professores/**",
                    "/alunos/**",
                    "/produtos/**",
                    "/cursos/**",
                    "/disciplinas/**",
                    "/pedidos/**"
                ).hasAuthority("ROLE_ADMIN")

                // =========================
                // QUALQUER OUTRA ROTA
                // SOMENTE ADMIN
                // =========================
                .anyRequest()
                .hasAuthority("ROLE_ADMIN")
            )

            // =========================
            // LOGIN
            // =========================
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)
                .permitAll()
            )

            // =========================
            // LOGOUT
            // =========================
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    // =========================
    // CRIPTOGRAFIA DA SENHA
    // =========================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // =========================
    // AUTHENTICATION MANAGER
    // =========================
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }
}