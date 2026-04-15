package com.digis01.GGarciaPruebaTecnica.Controller;

import com.digis01.GGarciaPruebaTecnica.DTO.LoginRequestDTO;
import com.digis01.GGarciaPruebaTecnica.DTO.LoginResponseDTO;
import com.digis01.GGarciaPruebaTecnica.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final UserService userService;
    
    public AuthController(UserService userService){
        this.userService = userService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO){
        return ResponseEntity.ok(userService.login(loginDTO.getRfc(), loginDTO.getPassword()));
    }
}
