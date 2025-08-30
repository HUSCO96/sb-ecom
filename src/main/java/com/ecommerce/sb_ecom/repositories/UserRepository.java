package com.ecommerce.sb_ecom.repositories;

import com.ecommerce.sb_ecom.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String username);

    boolean ExistsByUserName(@NotBlank @Size(min = 3, max = 20) String username);

    boolean ExistsByEmail(@NotBlank @Size(max = 50) @Email String email);
}
