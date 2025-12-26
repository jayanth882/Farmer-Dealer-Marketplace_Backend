package agriculture2.demo.service;

import agriculture2.demo.repository.UserRepo;
import agriculture2.demo.entities.users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public Optional<users> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public users saveUser(users u) {
        return userRepo.save(u);
    }
}