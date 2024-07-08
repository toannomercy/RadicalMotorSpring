package org.example.radicalmotor.Services;

import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.Role;
import org.example.radicalmotor.Repositories.IRoleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final IRoleRepository iRoleRepository;

    public void save(Role role) {
        iRoleRepository.save(role);
    }

}
