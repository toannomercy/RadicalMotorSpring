package org.example.radicalmotor.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vehicle_type")
public class VehicleType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicleTypeId")
    private Long vehicleTypeId;
    @Column(name = "vehicleTypeName", length = 50, nullable = false)
    private String vehicleTypeName;
    @Column(name = "isDeleted")
    private Boolean isDeleted;
    @OneToMany(mappedBy = "vehicleTypeId", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Vehicle> vehicles;
}
