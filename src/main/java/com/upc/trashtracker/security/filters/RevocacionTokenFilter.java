package com.upc.trashtracker.security.filters;
import com.upc.trashtracker.servicios.CuentaSeguridadService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/** Servlet filter before Spring Security; preserves the existing JWT implementation. */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class RevocacionTokenFilter extends OncePerRequestFilter {
    private final CuentaSeguridadService cuentas;
    public RevocacionTokenFilter(CuentaSeguridadService cuentas) { this.cuentas = cuentas; }
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                if (cuentas.estaRevocado(header.substring(7))) {
                    res.setStatus(401); res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"status\":401,\"error\":\"Sesion revocada\"}"); return;
                }
            } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
                // The existing JWT filter handles invalid tokens.
            }
        }
        chain.doFilter(req, res);
    }
}
