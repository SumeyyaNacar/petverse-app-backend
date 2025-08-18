package com.petverse.service.business;

import com.petverse.entity.concretes.business.Pet;
import com.petverse.entity.concretes.business.Photo;
import com.petverse.entity.concretes.user.User;
import com.petverse.entity.enums.PetSaleStatus;
import com.petverse.exception.ConflictException;
import com.petverse.exception.NoEntityFoundException;
import com.petverse.payload.ResponseMessage;
import com.petverse.payload.mappers.business.PetMapper;
import com.petverse.payload.messages.ErrorMessages;
import com.petverse.payload.messages.SuccessMessages;
import com.petverse.payload.request.business.PetRequest;
import com.petverse.payload.response.business.PetResponse;
import com.petverse.repository.business.PetRepository;
import com.petverse.service.helper.MethodHelper;
import com.petverse.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;
    private final PageableHelper pageableHelper;
    private final PetMapper petMapper;
    private final MethodHelper methodHelper;

    public Page<PetResponse> getAllPets(String query, int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);

        Page<Pet> pets;
        if (query == null || query.trim().isEmpty()) {
            pets = petRepository.findAll(pageable);
        } else {
            pets = petRepository.findPetsByQuery(query, pageable);
        }

        return pets.map(petMapper::mapEntityToResponse);
    }

    public ResponseMessage<PetResponse> getPetById(UUID id) {
        Pet foundPet = petRepository.findById(id).orElseThrow(() -> new NoEntityFoundException(String.format(
                ErrorMessages.PET_NOT_FOUND_MESSAGE, id
        )));
        return ResponseMessage.<PetResponse>builder()
                .message(SuccessMessages.PET_FOUND_MESSAGE)
                .object(petMapper.mapEntityToResponse(foundPet))
                .httpStatus(HttpStatus.OK)

                .build();
    }

    public ResponseMessage<PetResponse> createPet(PetRequest petRequest) {
        // Owner başlangıçta null
        User owner = null;

        // microcip kontrol
        if (petRequest.getMicrochipNumber() != null
                && petRepository.existsByMicrochipNumber(petRequest.getMicrochipNumber())) {
            throw new ConflictException(String.format(ErrorMessages.MICROCHIP_ALREADY_EXIST_MESSAGE,
                    petRequest.getMicrochipNumber()));
        }

        //dt0 to entity
        Pet pet = petMapper.mapRequestToEntity(petRequest);
        pet.setOwner(owner);
        pet.setStatus(PetSaleStatus.AVAILABLE);

        // db ye kayıt
        Pet savedPet = petRepository.save(pet);

        // Response
        PetResponse response = petMapper.mapEntityToResponse(savedPet);
        return ResponseMessage.<PetResponse>builder()
                .message(SuccessMessages.PET_SAVED_MESSAGE)
                .object(response)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    public ResponseMessage<PetResponse> buyPet(UUID petId, UUID userId) {
        //pet db de var mi?
        Pet pet = findPetById(petId);

        // satisa uygun mu
        if (pet.getStatus() != PetSaleStatus.AVAILABLE) {
            throw new ConflictException(ErrorMessages.PET_NOT_AVAILABLE_MESSAGE);
        }
        // almak isteyen owner db de var mi
        User owner = methodHelper.findUserById(userId);
        //owner ve pet durum guncellemesi
        pet.setOwner(owner);
        pet.setStatus(PetSaleStatus.SOLD);
        // db ye kayıt
        Pet savedPet = petRepository.save(pet);
        // response
        PetResponse response = petMapper.mapEntityToResponse(savedPet);
        return ResponseMessage.<PetResponse>builder()
                .message(SuccessMessages.PET_PURCHASED_MESSAGE)
                .object(response)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    private Pet findPetById(UUID id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new NoEntityFoundException(String.format(
                                ErrorMessages.PET_NOT_FOUND_MESSAGE, id)
                        )
                );
    }

    public ResponseMessage<PetResponse> addPhotoToPet(UUID petId, String photoUrl) {
        Pet pet = findPetById(petId);
        // max 10 photo
        if (pet.getPhotos().size() >= 10) {
            throw new ConflictException(ErrorMessages.MAX_PHOTO_LIMIT_MESSAGE);
        }

        //photo entity
        Photo photo = Photo.builder()
                .url(photoUrl)
                .pet(pet)
                .build();

        pet.getPhotos().add(photo);

        // pet entity e kayit
        Pet savedPet = petRepository.save(pet);

        PetResponse response = petMapper.mapEntityToResponse(savedPet);

        return ResponseMessage.<PetResponse>builder()
                .message(SuccessMessages.PHOTO_ADDED_MESSAGE)
                .object(response)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }


    //----------- SOFT DELETE ----------------
    public ResponseMessage<String> deletePet(UUID id) {
        Pet foundPet = findPetById(id);

        if (foundPet.getStatus() == PetSaleStatus.SOLD) {
            throw new ConflictException(ErrorMessages.PET_SOLD_NO_DELETE_MESSAGE);
        }

        foundPet.setStatus(PetSaleStatus.INACTIVE);
        petRepository.save(foundPet);

        return ResponseMessage.<String>builder()
                .message(SuccessMessages.PET_INACTIVATED_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();
    }
    //------------ HARD DELETE ----------------
    @Scheduled(cron = "0 0 2 * * ?") // her gece 02:00
    public void cleanOldPets() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        List<Pet> oldPets = petRepository.findAllByStatusInAndUpdatedAtBefore(
                List.of(PetSaleStatus.SOLD, PetSaleStatus.INACTIVE),
                oneYearAgo
        );
        if (!oldPets.isEmpty()) {
            petRepository.deleteAll(oldPets);
        }
    }


}
