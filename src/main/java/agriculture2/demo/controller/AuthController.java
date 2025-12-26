package agriculture2.demo.controller;

import agriculture2.demo.dto.*;
import agriculture2.demo.entities.users;
import agriculture2.demo.repository.UserRepo;
import agriculture2.demo.config.JwtTokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthController(UserRepo userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    // ============ REGISTER ============
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("Username exists");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("Email exists");
        }

        users user = new users();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        Set<String> roles = new HashSet<>();
        if (req.getRoles() == null || req.getRoles().isEmpty()) {
            roles.add("BUYER");
        } else {
            roles.addAll(req.getRoles());
        }
        user.setRoles(roles);

        userRepository.save(user);
        return ResponseEntity.ok("User registered");
    }

    // ============ LOGIN ============
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return userRepository.findByUsername(req.getUsername())
            .map(user -> {
                // Check password
                if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                    return ResponseEntity.status(401).body("Invalid credentials");
                }

                // Generate JWT
                String token = jwtTokenUtil.generateToken(user.getUsername());

                // ✅ Include roles in response so frontend can know FARMER / BUYER
                JwtResponse jwtResponse = new JwtResponse(
                        token,
                        "Bearer",
                        user.getUsername(),
                        user.getRoles()
                );

                return ResponseEntity.ok(jwtResponse);
            })
            .orElseGet(() -> ResponseEntity.status(401).body("User not found"));
    }
}
