package org.example.radicalmotor.Services;

import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Entities.CostTable;
import org.example.radicalmotor.Repositories.ICostTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
public class CostTableService {
    private final ICostTableRepository costTableRepository;
    public List<CostTable> getAllCostTables() {
        return costTableRepository.findAll();
    }
}
