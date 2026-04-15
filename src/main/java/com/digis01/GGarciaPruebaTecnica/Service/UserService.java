package com.digis01.GGarciaPruebaTecnica.Service;

import com.digis01.GGarciaPruebaTecnica.DTO.LoginResponseDTO;
import com.digis01.GGarciaPruebaTecnica.DTO.UserRequestDTO;
import com.digis01.GGarciaPruebaTecnica.DTO.UserResponse;
import com.digis01.GGarciaPruebaTecnica.Exception.UsuarioNoEncontrado;
import com.digis01.GGarciaPruebaTecnica.Model.Usuario;
import com.digis01.GGarciaPruebaTecnica.Repository.UserRepository;
import com.digis01.GGarciaPruebaTecnica.Utill.AesEncryptionService;
import jakarta.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
    private final AesEncryptionService aes;
    private final UserRepository userRepository;

    @Value("${app.timezone}")
    private String timezoneStr;
    private ZoneId madagascar;

    @PostConstruct
    public void init() {
        this.madagascar = ZoneId.of(timezoneStr);
    }

    public UserService(UserRepository userRepository1, AesEncryptionService aes) {
        this.userRepository = userRepository1;
        this.aes = aes;
    }

    public List<UserResponse> getUsuarios(String sortedBy, String filter) {
        List<Usuario> list = userRepository.findAll();
        if (filter != null && !filter.isBlank()) {
            list = applyFilter(list, filter);
        }
        if (sortedBy != null && !filter.isBlank()) {
            list = applySorting(list, sortedBy);
        }
        return list.stream().map(this::toResponseDto).collect(Collectors.toList());

    }
//    
//    public UserResponse crearUsuario(UserRequestDTO userDTO){
//        if(userRepository.existeRFC(userDTO.getRfc())){
//            
//        }
//    }

    public void deteleUsuario(UUID id) {
        if (!userRepository.eliminarById(id)) {
            throw new UsuarioNoEncontrado("Usuario no encontrado " + id);
        }
    }

    /*Login*/
    public LoginResponseDTO login(String rfc, String password) {
        Usuario usuario = userRepository.findByRFC(rfc).orElseThrow(() -> new UsuarioNoEncontrado("Usuario no encontrado"));
        String passwordDescifrado = aes.decrypt(password);
        if (!passwordDescifrado.equals(password)) {
            throw new UsuarioNoEncontrado("Credenciales invalidas");
        }
        return new LoginResponseDTO("Acceso exitoso", rfc);
    }

    private List<Usuario> applySorting(List<Usuario> list, String field) {
        Comparator<Usuario> comparator = switch (field.toLowerCase()) {
            case "email" ->
                Comparator.comparing(Usuario::getEmail);
            case "id" ->
                Comparator.comparing(u -> u.getId().toString());
            case "name" ->
                Comparator.comparing(Usuario::getName);
            case "phone" ->
                Comparator.comparing(Usuario::getPhone);
            case "rfc" ->
                Comparator.comparing(Usuario::getRfc);
            case "created_at" ->
                Comparator.comparing(Usuario::getCreated_at);
            default ->
                throw new IllegalArgumentException(
                        "Campo de ordenamiento no válido: " + field);
        };
        return list.stream().sorted(comparator).collect(Collectors.toList());
    }

    private List<Usuario> applyFilter(List<Usuario> list, String filterParam) {
        // El parámetro llega como "campo+op+valor" o "campo op valor"
        // Postman puede decodificar + como espacio, manejamos ambos
        String[] parts = filterParam.split("[+\\s]", 3);

        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Formato de filter inválido. Usa: campo+op+valor");
        }

        String field = parts[0].toLowerCase();
        String operator = parts[1].toLowerCase();
        String value = parts[2];

        return list.stream()
                .filter(u -> matchesFilter(u, field, operator, value))
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(Usuario u, String field, String op, String value) {
        String fieldValue = getFieldValue(u, field);
        if (fieldValue == null) {
            return false;
        }

        return switch (op) {
            case "eq" ->
                fieldValue.equalsIgnoreCase(value);
            case "co" ->
                fieldValue.toLowerCase().contains(value.toLowerCase());
            case "sw" ->
                fieldValue.toLowerCase().startsWith(value.toLowerCase());
            case "ew" ->
                fieldValue.toLowerCase().endsWith(value.toLowerCase());
            default ->
                throw new IllegalArgumentException("Operador no válido: " + op);
        };
    }

    private String getFieldValue(Usuario u, String field) {
        return switch (field) {
            case "email" ->
                u.getEmail();
            case "id" ->
                u.getId().toString();
            case "name" ->
                u.getName();
            case "phone" ->
                u.getPhone();
            case "tax_id" ->
                u.getRfc();
            case "created_at" ->
                u.getCreated_at().toString();
            default ->
                throw new IllegalArgumentException(
                        "Campo de filtro no válido: " + field);
        };
    }

    private UserResponse toResponseDto(Usuario u) {
        return UserResponse.builder()
                .Id(u.getId())
                .email(u.getEmail())
                .name(u.getName())
                .phone(u.getPhone())
                .rfc(u.getRfc())
                .created_at(u.getCreated_at())
                .direcciones(u.getDirecciones())
                .build();
    }

}
