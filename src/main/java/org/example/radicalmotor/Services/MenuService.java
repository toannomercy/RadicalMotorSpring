package org.example.radicalmotor.Services;

import org.example.radicalmotor.Entities.Menu;
import org.example.radicalmotor.Repositories.IMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuService {

    @Autowired
    private IMenuRepository IMenuRepository;

    public List<Menu> getAllMenus() {
        return IMenuRepository.findAll();
    }

    public Optional<Menu> getMenuById(Long id) {
        return IMenuRepository.findById(id);
    }

    public Menu saveMenu(Menu menu) {
        return IMenuRepository.save(menu);
    }

    public Menu updateMenu(Long id, Menu menuDetails) {
        Optional<Menu> optionalMenu = IMenuRepository.findById(id);
        if (optionalMenu.isPresent()) {
            Menu menu = optionalMenu.get();
            menu.setName(menuDetails.getName());
            menu.setUrl(menuDetails.getUrl());
            menu.setParent(menuDetails.getParent());
            menu.setChildren(menuDetails.getChildren());
            return IMenuRepository.save(menu);
        } else {
            throw new RuntimeException("Menu not found with id " + id);
        }
    }

    public void deleteMenu(Long id) {
        IMenuRepository.deleteById(id);
    }
}
