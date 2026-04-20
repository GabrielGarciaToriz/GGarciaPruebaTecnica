package com.digis01.GGarciaPruebaTecnica.Service;

import com.digis01.GGarciaPruebaTecnica.DTO.LoginResponseDTO;
import com.digis01.GGarciaPruebaTecnica.DTO.UserRequestDTO;
import com.digis01.GGarciaPruebaTecnica.DTO.UserResponse;
import com.digis01.GGarciaPruebaTecnica.Exception.UsuarioDuplicado;
import com.digis01.GGarciaPruebaTecnica.Exception.UsuarioNoEncontrado;
import com.digis01.GGarciaPruebaTecnica.Model.Usuario;
import com.digis01.GGarciaPruebaTecnica.Repository.UserRepository;
import com.digis01.GGarciaPruebaTecnica.Utill.AesEncryptionService;
import com.digis01.GGarciaPruebaTecnica.Utill.JwtUtil;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
    private final UserRepository userRepository;
    private final AesEncryptionService aes;
    private ZoneId madagascar;
    private final JwtUtil jwtUtil;

    @Value("${app.timezone}")
    private String timezoneStr;

    @PostConstruct
    public void init() {
        this.madagascar = ZoneId.of(timezoneStr);
    }

    public UserService(UserRepository userRepository1, AesEncryptionService aes, JwtUtil jwtUtil1) {
        this.userRepository = userRepository1;
        this.aes = aes;
        this.jwtUtil=jwtUtil1;
    }

    public List<UserResponse> getUsuarios(String sortedBy, String filter) {
        List<Usuario> list = userRepository.findAll();
        if (filter != null && !filter.isBlank()) {
            list = applyFilter(list, filter);
        }
        if (sortedBy != null && !sortedBy.isBlank()) {
            list = applySorting(list, sortedBy);
        }
        return list.stream().map(this::toResponseDto).collect(Collectors.toList());
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

    private boolean matchesFilter(Usuario usuario, String field, String op, String value) {
        String fieldValue = getFieldValue(usuario, field);
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
            case "rfc" ->
                u.getRfc();
            case "created_at" ->
                u.getCreated_at().toString();
            default ->
                throw new IllegalArgumentException(
                        "Campo de filtro no válido: " + field);
        };
    }

    public UserResponse crearUsuario(UserRequestDTO userRequestDTO) {
        if (userRepository.existeRFC(userRequestDTO.getRfc())) {
            throw new UsuarioDuplicado("Ya existe el usuario con " + userRequestDTO.getRfc());
        }

        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID())
                .email(userRequestDTO.getEmail())
                .name(userRequestDTO.getName())
                .phone(userRequestDTO.getPhone())
                .password(aes.encrypt(userRequestDTO.getPassword()))
                .rfc(userRequestDTO.getRfc())
                .created_at(Date.from(Instant.now()))
                .direcciones(userRequestDTO.getDirecciones() != null ? userRequestDTO.getDirecciones() : List.of())
                .build();

        return toResponseDto(userRepository.Guardar(usuario));
    }

    public UserResponse patchUser(UUID id, Map<String, Object> fields) {
        Usuario user = userRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontrado("Usuario no encontrado: " + id));

        fields.forEach((key, rawValue) -> {
            String value = rawValue != null ? rawValue.toString() : null;
            switch (key.toLowerCase()) {
                case "email" ->
                    user.setEmail(value);
                case "name" ->
                    user.setName(value);
                case "phone" ->
                    user.setPhone(value);
                case "password" ->
                    user.setPassword(aes.encrypt(value));
                case "rfc" -> {
                    if (userRepository.existeRFC(value) && !value.equalsIgnoreCase(user.getRfc())) {
                        throw new UsuarioDuplicado("RFC  ya en uso: " + value);
                    }
                    user.setRfc(value);
                }
                default -> {
                }
            }
        });

        userRepository.Update(user);
        return toResponseDto(user);
    }

    public void deteleUsuario(UUID id) {
        if (!userRepository.eliminarById(id)) {
            throw new UsuarioNoEncontrado("Usuario no encontrado " + id);
        }
    }

    /*Login*/
    public LoginResponseDTO login(String rfc, String password) {
        Usuario usuario = userRepository.findByRFC(rfc)
                .orElseThrow(() -> new UsuarioNoEncontrado("Credenciales invalidas"));

        String passwordGuardado = aes.decrypt(usuario.getPassword()); // ← descifra el de la BD
        if (!passwordGuardado.equals(password)) {                     // ← compara contra lo que llega
            throw new UsuarioNoEncontrado("Credenciales invalidas");
        }
        String token = jwtUtil.generarToeken(rfc);
        return new LoginResponseDTO("Acceso exitoso", rfc,token);
    }

    private UserResponse toResponseDto(Usuario u) {
        return UserResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .name(u.getName())
                .phone(u.getPhone())
                .rfc(u.getRfc())
                .created_at(u.getCreated_at())
                .direcciones(u.getDirecciones())
                .build();
    }

}
