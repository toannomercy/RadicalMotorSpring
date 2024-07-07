package org.example.radicalmotor.Repositories;

import org.example.radicalmotor.Entities.CostTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICostTableRepository extends JpaRepository<CostTable, Long> {

}
