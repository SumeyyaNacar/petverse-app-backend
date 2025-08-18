package com.petverse.controller.business;

import com.petverse.payload.ResponseMessage;
import com.petverse.payload.request.business.PetRequest;
import com.petverse.payload.response.business.PetResponse;
import com.petverse.service.business.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("pets")
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;

    //List all pets
    @GetMapping
    public Page<PetResponse> getAllPets(
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        return petService.getAllPets(query, page, size, sort,type);
    }

    //Get a pet
    @GetMapping("/{id}")
    public ResponseMessage<PetResponse> getPetById(@PathVariable UUID id) {
        return petService.getPetById(id);
    }
    //Create new pet
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<PetResponse> createPet(@RequestBody PetRequest petRequest) {
        return petService.createPet(petRequest);
    }

    // Satın alma işlemi
    @PostMapping("/{petId}/buy")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseMessage<PetResponse> buyPet(
            @PathVariable UUID petId,
            @RequestParam UUID userId) {

        return petService.buyPet(petId, userId);
    }
    //add new photo to pet
    @PostMapping("/{petId}/photos")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<PetResponse> addPhotoToPet(
            @PathVariable UUID petId,
            @RequestParam String photoUrl) {

        return petService.addPhotoToPet(petId, photoUrl);
    }


    //delete pet
    @DeleteMapping("/{id}")
    public  ResponseMessage<String > deletePet(@PathVariable UUID id) {
        return petService.deletePet(id);


    }

}
