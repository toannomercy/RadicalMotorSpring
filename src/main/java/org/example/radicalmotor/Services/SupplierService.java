package org.example.radicalmotor.Services;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.Supplier;
import org.example.radicalmotor.Repositories.ISupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class SupplierService {
    private final ISupplierRepository supplierRepository;
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findByIsDeletedFalse();
    }
    public void addSupplier(Supplier supplier) {
        supplierRepository.save(supplier);
    }
    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }
    public void updateSupplier(@NotNull Supplier supplier) {
        Supplier existingSupplier = supplierRepository.findById(supplier.getSupplierId())
                .orElse(null);
        assert existingSupplier != null;
        existingSupplier.setSupplierName(supplier.getSupplierName());
        existingSupplier.setSupplierAddress(supplier.getSupplierAddress());
        existingSupplier.setSupplierPhone(supplier.getSupplierPhone());
        supplierRepository.save(existingSupplier);
    }
}
