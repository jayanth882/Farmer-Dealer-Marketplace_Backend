package agriculture2.demo.repository;

import agriculture2.demo.entities.users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<users, Long> {

    Optional<users> findByUsername(String username);

    Optional<users> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
