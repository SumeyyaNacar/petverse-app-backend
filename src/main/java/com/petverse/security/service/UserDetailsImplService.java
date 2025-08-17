package com.petverse.security.service;

import com.petverse.entity.concretes.user.User;
import com.petverse.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsImplService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
      User user =  userRepository.findByEmailEquals(email);
        if (user != null) {
            return new UserDetailsImpl(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getRoleType().name()
            );
        }
        throw new UsernameNotFoundException("User " + email+ "' not found.");
    }
}
