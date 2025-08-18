package com.petverse.payload.mappers.business;

import com.petverse.entity.concretes.business.Pet;
import com.petverse.entity.concretes.business.Photo;
import com.petverse.payload.request.business.PetRequest;
import com.petverse.payload.response.business.PetResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PetMapper {

    public Pet mapRequestToEntity(PetRequest petRequest) {
        Pet pet = Pet.builder()
                .name(petRequest.getName())
                .species(petRequest.getSpecies())
                .breed(petRequest.getBreed())
                .gender(petRequest.getGender())
                .birthDate(petRequest.getBirthDate())
                .weight(petRequest.getWeight())
                .height(petRequest.getHeight())
                .vaccinated(petRequest.isVaccinated())
                .sterilized(petRequest.isSterilized())
                .medicalHistory(petRequest.getMedicalHistory())
                .color(petRequest.getColor())
                .price(petRequest.getPrice())
                .microchipNumber(petRequest.getMicrochipNumber())
                .notes(petRequest.getNotes())
                .build();

        if (petRequest.getPhotoUrls() != null) {
            List<Photo> photos = petRequest.getPhotoUrls().stream()
                    .map(url -> Photo.builder()
                            .url(url)
                            .pet(pet)
                            .build())
                    .collect(Collectors.toList());
            pet.setPhotos(photos);
        }
        return pet;
    }

    public PetResponse mapEntityToResponse(Pet pet) {
        return PetResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .gender(pet.getGender())
                .birthDate(pet.getBirthDate())
                .weight(pet.getWeight())
                .height(pet.getHeight())
                .vaccinated(pet.isVaccinated())
                .sterilized(pet.isSterilized())
                .medicalHistory(pet.getMedicalHistory())
                .color(pet.getColor())
                .status(pet.getStatus())
                .price(pet.getPrice())
                .microchipNumber(pet.getMicrochipNumber())
                .notes(pet.getNotes())
                .photoUrls(pet.getPhotos() != null
                        ? pet.getPhotos().stream().map(photo -> photo.getUrl()).collect(Collectors.toList())
                        : null)
                .ownerId(pet.getOwner() != null ? pet.getOwner().getId() : null)
                .ownerName(pet.getOwner() != null ? pet.getOwner().getFirstName() + " " + pet.getOwner().getLastName() : null)
                .build();
    }
}
