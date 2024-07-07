package org.example.radicalmotor.Services;

import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.Supplier;
import org.example.radicalmotor.Repositories.ISupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class SupplierService {
    private final ISupplierRepository supplierRepository;
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }
}
