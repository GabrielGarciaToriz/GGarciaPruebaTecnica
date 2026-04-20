package com.digis01.GGarciaPruebaTecnica.Security;

import com.digis01.GGarciaPruebaTecnica.Utill.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailServiceImplementation userDetailServiceImplementation;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailServiceImplementation userDetailServiceImplementation1) {
        this.jwtUtil = jwtUtil;
        this.userDetailServiceImplementation = userDetailServiceImplementation1;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        if (jwtUtil.esValido(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String rfc = jwtUtil.extraerRFC(token);

            UserDetails userDetails = userDetailServiceImplementation.loadUserByUsername(rfc);

            // Construimos el objeto de autenticación y lo ponemos en el contexto
            UsernamePasswordAuthenticationToken authToken
                    = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }

}
