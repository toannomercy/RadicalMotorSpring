package org.example.radicalmotor.Repositories;

import org.example.radicalmotor.Entities.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IVehicleRepository extends
        PagingAndSortingRepository<Vehicle, String>, JpaRepository<Vehicle, String> {

    @Query("""
                SELECT v FROM Vehicle v
                WHERE (v.chassisNumber LIKE %?1%
                OR v.vehicleName LIKE %?1%
                OR v.vehicleTypeId.vehicleTypeName LIKE %?1%)
                AND v.vehicleTypeId.isDeleted = false
                AND v.costId.isDeleted = false
                AND v.supplierId.isDeleted = false
                """)
    List<Vehicle> searchVehicle(String keyword);

    default List<Vehicle> findAllVehicles(Integer pageNo, Integer pageSize, String sortBy) {
        return findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy)))
                .getContent();
    }

    @Query("SELECT v FROM Vehicle v WHERE v.vehicleTypeId.vehicleTypeName = ?1" +
            " AND v.vehicleTypeId.isDeleted = false" +
            " AND v.costId.isDeleted = false" +
            " AND v.supplierId.isDeleted = false" +
            " AND v.isDeleted = false")
    Page<Vehicle> findByVehicleTypeName(String vehicleTypeName, Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.supplierId.isDeleted = false" +
            " AND v.isDeleted = false" +
            " AND v.vehicleTypeId.isDeleted = false" +
            " AND v.costId.isDeleted = false")
    List<Vehicle> findAllActiveVehicles();
}

