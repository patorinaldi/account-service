package account_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint, AccessDeniedHandler accessDeniedHandler) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic(http -> http.authenticationEntryPoint(restAuthenticationEntryPoint))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(HttpMethod.GET, "/api/admin/user/**").hasRole("ADMINISTRATOR");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/admin/user/**").hasRole("ADMINISTRATOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/admin/user/role/**").hasRole("ADMINISTRATOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/admin/user/access").hasRole("ADMINISTRATOR");
                    auth.requestMatchers(HttpMethod.GET, "/api/security/events/").hasRole("AUDITOR");
                    auth.requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasRole("ACCOUNTANT");
                    auth.requestMatchers(HttpMethod.POST, "/api/acct/payments").hasRole("ACCOUNTANT");
                    auth.requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole("ACCOUNTANT", "USER");
                    auth.requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/error").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/error").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll();
                    auth.anyRequest().authenticated();
                }).sessionManagement(sessions -> sessions
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(13);
    }
}
