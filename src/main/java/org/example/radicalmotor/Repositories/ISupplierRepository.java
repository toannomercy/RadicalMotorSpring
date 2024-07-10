package org.example.radicalmotor.Repositories;

import org.example.radicalmotor.Entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByIsDeletedFalse();
}
