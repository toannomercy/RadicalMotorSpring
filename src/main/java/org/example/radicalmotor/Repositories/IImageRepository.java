package org.example.radicalmotor.Repositories;

import org.example.radicalmotor.Entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IImageRepository extends
        PagingAndSortingRepository<Image, Long>, JpaRepository<Image, Long>{
    @Query("""
                SELECT i FROM Image i
                WHERE i.chassisNumber.chassisNumber LIKE %?1%
                """)
    Image findByChassisNumber(String keyword);
}
