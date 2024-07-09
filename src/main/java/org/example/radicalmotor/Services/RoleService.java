package org.example.radicalmotor.Services;

import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Constants.RoleType;
import org.example.radicalmotor.Entities.Role;
import org.example.radicalmotor.Repositories.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    @Autowired
    private IRoleRepository iRoleRepository;
    public Role findByName(RoleType name) {
        return iRoleRepository.findByName(name).orElse(null);
    }
    public void save(Role role) {
        iRoleRepository.save(role);
    }

}
