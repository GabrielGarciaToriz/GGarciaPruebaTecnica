package com.digis01.GGarciaPruebaTecnica.Controller;

import com.digis01.GGarciaPruebaTecnica.DTO.UserRequestDTO;
import com.digis01.GGarciaPruebaTecnica.DTO.UserResponse;
import com.digis01.GGarciaPruebaTecnica.Model.Usuario;
import com.digis01.GGarciaPruebaTecnica.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/usuarios")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsuarios(
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String filterBy
    ) {
        return ResponseEntity.ok(userService.getUsuarios(sortedBy, filterBy));
    }
     @PostMapping
    @Operation(summary = "Crear nuevo usuario")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.crearUsuario(dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente un usuario")
    public ResponseEntity<UserResponse> patchUser(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> fields) {
        return ResponseEntity.ok(userService.patchUser(id, fields));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario por id")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deteleUsuario(id);
        return ResponseEntity.noContent().build();
    }

}
