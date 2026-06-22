package com.bodev.auth_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Endpoints exclusivos para administradores")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard de administrador",
            description = "Acceso solo para usuarios con rol ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "¡Bienvenido al panel de administración! Solo accesible para ADMIN.";
    }

    @GetMapping("/users")
    @Operation(summary = "Listar usuarios",
            description = "Lista de todos los usuarios (solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers() {
        return "Lista de todos los usuarios (solo ADMIN)";
    }
}