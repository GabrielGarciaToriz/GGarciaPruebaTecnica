/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digis01.GGarciaPruebaTecnica.Security;

import com.digis01.GGarciaPruebaTecnica.Exception.UsuarioNoEncontrado;
import com.digis01.GGarciaPruebaTecnica.Model.Usuario;
import com.digis01.GGarciaPruebaTecnica.Repository.UserRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImplementation implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String rfc) throws UsernameNotFoundException {
        Usuario usuario = userRepository.findByRFC(rfc).orElseThrow(
                () -> new UsuarioNoEncontrado("El usuario no fue encontraod"));
        
        return new User(
                usuario.getRfc(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
                
    }

}
