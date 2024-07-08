package org.example.radicalmotor.Services;

import lombok.NoArgsConstructor;
import org.example.radicalmotor.Constants.RoleType;
import org.example.radicalmotor.Entities.Role;
import org.example.radicalmotor.Entities.User;
import org.example.radicalmotor.Repositories.IRoleRepository;
import org.example.radicalmotor.Repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@NoArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    private IUserRepository iUserRepository;
    @Autowired
    private IRoleRepository iRoleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = iUserRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(String.valueOf(RoleType.USER)));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
    @Transactional
    public boolean save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Set<Role> roles = new HashSet<>();
            Role userRole = iRoleRepository.findByName(RoleType.USER).orElseGet(() -> {
                Role newRole = new Role(RoleType.USER);
                iRoleRepository.save(newRole);
                return newRole;
            });
            roles.add(userRole);
            user.setRoles(roles);
            iUserRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Role getOrCreateUserRole() {
        return iRoleRepository.findByName(RoleType.USER).orElseGet(() -> {
            Role newUserRole = new Role(RoleType.USER);
            iRoleRepository.save(newUserRole);
            return newUserRole;
        });
    }

    public User authenticate(String usernameOrEmail, String password) {
        Optional<User> userOptional = iUserRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}
