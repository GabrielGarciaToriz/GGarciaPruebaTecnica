package com.digis01.GGarciaPruebaTecnica.Repository;

import com.digis01.GGarciaPruebaTecnica.Model.Direccion;
import com.digis01.GGarciaPruebaTecnica.Model.Usuario;
import com.digis01.GGarciaPruebaTecnica.Utill.AesEncryptionService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final List<Usuario> usuarios = new ArrayList<>();
    private final AesEncryptionService aes;

    public UserRepository(AesEncryptionService aes) {
        this.aes = aes;
        initData();
    }

    private void initData() {
        String rawPassword = "7c4a8d09ca3762af61e59520943dc26494f8941b";
        usuarios.add(Usuario.builder()
                .id(UUID.randomUUID())
                .email("gabrielgarciatoriz@gmail.com")
                .name("GabTrax")
                .phone("+52 56 68 59 24 02")
                .rfc("GATG011227KP6")
                .password(aes.encrypt(rawPassword))
                .created_at(Date.from(Instant.now()))
                .direcciones(List.of(
                        Direccion.builder()
                                .id(1)
                                .name("Hogar")
                                .street("Rio Blanco")
                                .country_code("MX")
                                .build(),
                        Direccion.builder()
                                .id(2)
                                .name("Trabajo")
                                .street("Juan Salvador")
                                .country_code("MX")
                                .build()
                ))
                .build());
        usuarios.add(Usuario.builder()
                .id(UUID.randomUUID())
                .email("roosario@gmail.com")
                .name("Roosario")
                .phone("+52 56 68 59 24 04")
                .rfc("SAT090594KP6")
                .password(aes.encrypt(rawPassword))
                .created_at(Date.from(Instant.now()))
                .direcciones(List.of(
                        Direccion.builder()
                                .id(1)
                                .name("Hogar")
                                .street("Rio Blanco")
                                .country_code("MX")
                                .build(),
                        Direccion.builder()
                                .id(2)
                                .name("Trabajo")
                                .street("Av. Paseo de la reforma")
                                .country_code("MX")
                                .build()
                ))
                .build());
        usuarios.add(Usuario.builder()
                .id(UUID.randomUUID())
                .email("jocelyn@gmail.com")
                .name("Doctora Toriz")
                .phone("+52 56 68 59 24 01")
                .rfc("TOJJ901015ABC")
                .password(aes.encrypt(rawPassword))
                .created_at(Date.from(Instant.now()))
                .direcciones(List.of(
                        Direccion.builder()
                                .id(1)
                                .name("Hogar")
                                .street("Rio Blanco")
                                .country_code("MX")
                                .build(),
                        Direccion.builder()
                                .id(1)
                                .name("Trabajo")
                                .street("Tenancingo")
                                .country_code("MX")
                                .build()
                ))
                .build());

    }

    public List<Usuario> findAll() {
        return new ArrayList<>(usuarios);
    }

    public Optional<Usuario> findById(UUID id) {
        return usuarios.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public Optional<Usuario> findByRFC(String rfc) {
        return usuarios.stream().filter(u -> u.getRfc().equals(rfc)).findFirst();
    }

    public boolean existeRFC(String rfc) {
        return usuarios.stream().anyMatch(u -> u.getRfc().equalsIgnoreCase(rfc));
    }

    public Usuario Guardar(Usuario usuario) {
        usuarios.add(usuario);
        return usuario;
    }

    public void Update(Usuario usuario) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(usuario.getId())) {
                usuarios.set(i, usuario);
                return;
            }
        }
    }

    public boolean eliminarById(UUID id) {
        return usuarios.removeIf(u -> u.getId().equals(id));
    }

}
