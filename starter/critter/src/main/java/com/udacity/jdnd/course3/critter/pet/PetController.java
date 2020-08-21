package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.UserService;
import com.udacity.jdnd.course3.critter.user.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    PetService petService;

    @Autowired
    UserService userService;

    private Pet getPetFromPetDTO(PetDTO petDTO){
        Pet pet = new Pet();
        BeanUtils.copyProperties(petDTO, pet, "ownerId");

        User user = userService.getCustomerById(petDTO.getOwnerId());
        pet.setOwner(user);

        return pet;
    }

    private PetDTO getPetDTOFromPet(Pet pet){
        PetDTO petDTO = new PetDTO();
        BeanUtils.copyProperties(pet, petDTO, "owner");

        petDTO.setOwnerId(pet.getOwner().getId());

        return petDTO;
    }

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {
        Pet pet = getPetFromPetDTO(petDTO);

        Pet savedPet = petService.savePet(pet);
        return getPetDTOFromPet(savedPet);
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {
        Pet pet = petService.getPetById(petId);

        PetDTO petDTO = new PetDTO();
        if(pet != null) {
            petDTO = getPetDTOFromPet(pet);
        }
        return petDTO;
    }

    @GetMapping
    public List<PetDTO> getPets(){
        List<Pet> pets = petService.getAllPets();
        List<PetDTO> petDTOs = new ArrayList<>();

        for(Pet pet : pets){
            petDTOs.add(getPetDTOFromPet(pet));
        }

        return petDTOs;
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        List<Pet> pets = petService.getPetsByOwnerId(ownerId);

        List<PetDTO> petDTOs = new ArrayList<>();
        for(Pet pet : pets){
            petDTOs.add(getPetDTOFromPet(pet));
        }
        return petDTOs;
    }
}
