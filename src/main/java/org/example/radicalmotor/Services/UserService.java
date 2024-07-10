package org.example.radicalmotor.Services;

import lombok.NoArgsConstructor;
import org.example.radicalmotor.Constants.RoleType;
import org.example.radicalmotor.Entities.Customer;
import org.example.radicalmotor.Entities.Role;
import org.example.radicalmotor.Entities.User;
import org.example.radicalmotor.Repositories.ICustomerRepository;
import org.example.radicalmotor.Repositories.IRoleRepository;
import org.example.radicalmotor.Repositories.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@NoArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private ICustomerRepository iCustomerRepository;

    @Autowired
    private IRoleRepository iRoleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = iUserRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        logger.info("Loaded user: {}", user.getUsername());
        logger.info("Authorities: {}", user.getAuthorities());

        if (user.getLockExpired() != null && user.getLockExpired().getTime() > System.currentTimeMillis() && !user.isAccountNonLocked()) {
            throw new LockedException("Your account is locked. Please wait 15 minutes before trying again.");
        }
        if (user.getProvider() != null && user.getProvider().equals("GOOGLE")) {
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    "",
                    user.getAuthorities()
            );
        } else {
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getAuthorities()
            );
        }
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

            Customer customer = new Customer();
            customer.setUserId(user);
            iCustomerRepository.save(customer);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<User> findByUsername(String username) {
        return iUserRepository.findByUsername(username);
    }

    public void resetLoginFail(User user) {
        user.setCountFail(0);
        user.setAccountNonLocked(true);
        iUserRepository.save(user);
    }

    public Optional<User> getUserByUsername(String username) {
        return iUserRepository.findByUsername(username);
    }

    public void updateFailCount(User user) {
        int count = iUserRepository.countFailByUsername(user.getUsername());
        if (user.getLockExpired() != null && user.getLockExpired().getTime() < System.currentTimeMillis()) {
            user.setAccountNonLocked(true);
            user.setLockExpired(null);
            user.setCountFail(0);
        } else {
            user.setCountFail(count + 1);
            if (user.getCountFail() >= 3) {
                user.setAccountNonLocked(false);
                user.setLockExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 15));
            }
        }
        iUserRepository.save(user);
    }

    public User authenticate(String username, String password) {
        Optional<User> userOptional = iUserRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isAccountNonLocked()) {
                return null;
            }
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public Optional<User> getUserByEmail(String email) {
        return iUserRepository.findByEmail(email);
    }

    public String GenToken(int Length){
        String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghiklmnopqrstuvwxyz0123456789";
        StringBuilder result= new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < Length; i++) {
            int index = random.nextInt(source.length());
            result.append(source.charAt(index));
        }
        return result.toString();
    }

    public String GenTokenResetPassword(User user){
        user.setTokenResetPassword(GenToken(45));
        user.setTokenResetPasswordExpired(new Date(System.currentTimeMillis()+1000*60*10));
        iUserRepository.save(user);
        return user.getTokenResetPassword();
    }

    public Optional<User> getUserByResetToken(String token) {
        return iUserRepository.findByTokenResetPassword(token);
    }

    public void ResetDateForgotPassword(User user){
        user.setTokenResetPasswordExpired(null);
        user.setTokenResetPassword(null);
        iUserRepository.save(user);
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTokenResetPassword(null);
        user.setTokenResetPasswordExpired(null);
        iUserRepository.save(user);
    }
}
