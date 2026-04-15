package com.digis01.GGarciaPruebaTecnica.Controller;

import com.digis01.GGarciaPruebaTecnica.DTO.UserResponse;
import com.digis01.GGarciaPruebaTecnica.Model.Usuario;
import com.digis01.GGarciaPruebaTecnica.Service.UserService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

}
