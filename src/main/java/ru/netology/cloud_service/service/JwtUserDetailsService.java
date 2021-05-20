package ru.netology.cloud_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloud_service.model.MyUserPrincipal;
import ru.netology.cloud_service.model.UserData;
import ru.netology.cloud_service.repository.UserDataRepository;


@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDataRepository userDataRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserData userData = userDataRepository.findByUsername(username).orElseThrow(IllegalArgumentException::new);
        if (userData == null) {
            throw new UsernameNotFoundException(username);
        }
        return new MyUserPrincipal(userData);
    }
}


