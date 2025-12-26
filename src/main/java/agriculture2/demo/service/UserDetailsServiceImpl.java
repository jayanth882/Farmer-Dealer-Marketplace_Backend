// File: src/main/java/agriculture2/demo/service/UserDetailsServiceImpl.java

package agriculture2.demo.service;

import agriculture2.demo.entities.users;
import agriculture2.demo.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetailsService; // Use the interface
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepo userRepository;

    public UserDetailsServiceImpl(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        users user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // IMPORTANT:
        // If the DB has "FARMER" → becomes "ROLE_FARMER"
        // If the DB already has "ROLE_FARMER" → stays "ROLE_FARMER"
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(r -> {
                String roleName = r.startsWith("ROLE_") ? r : "ROLE_" + r;
                return new SimpleGrantedAuthority(roleName);
            })
            .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities);
    }
}
