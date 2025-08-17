package com.petverse.entity.enums;

public enum Species {
    DOG("Dog"),
    CAT("Cat"),
    BIRD("Bird"),
    FISH("Fish"),
    RABBIT("Rabbit"),
    OTHER("Other");


    private final String species;

    Species(String species) {
        this.species = species;
    }
}
