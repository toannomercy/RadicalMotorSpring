package org.example.radicalmotor.Repositories;

import org.example.radicalmotor.Constants.RoleType;
import org.example.radicalmotor.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(RoleType name);
}
