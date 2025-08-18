package com.petverse.repository.business;

import com.petverse.entity.concretes.business.Pet;
import com.petverse.entity.concretes.user.User;
import com.petverse.entity.enums.PetSaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PetRepository extends JpaRepository<Pet, UUID> {
    @Query("SELECT p FROM Pet p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(p.color) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(p.species) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Pet> findPetsByQuery(@Param("query") String query, Pageable pageable);

    boolean existsByMicrochipNumber(String microchipNumber);
    boolean existsByNameAndOwner(String name, User owner);

    List<Pet> findAllByStatusInAndUpdatedAtBefore(List<PetSaleStatus> statuses, LocalDateTime dateTime);
}
