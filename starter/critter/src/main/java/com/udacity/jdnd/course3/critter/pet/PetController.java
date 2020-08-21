package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.User;
import com.udacity.jdnd.course3.critter.user.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    PetRepository petRepository;

    @Autowired
    UserRepository userRepository;

    private Pet getPetFromPetDTO(PetDTO petDTO){
        Pet pet = new Pet();
        BeanUtils.copyProperties(petDTO, pet, "ownerId");

        Optional<User> optionalUser = userRepository.findById(petDTO.getOwnerId());
        User user = optionalUser.orElse(null);
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
        Pet savedPet = petRepository.save(pet);

        // Add pet to Customer
        Customer customer = (Customer) savedPet.getOwner();
        List<Pet> customerPets = customer.getPets();
        if(customerPets == null){
            customerPets = new ArrayList<>();
        }
        customerPets.add(savedPet);
        customer.setPets(customerPets);
        userRepository.save(customer);

        return getPetDTOFromPet(savedPet);
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {
        Optional<Pet> optionalPet = petRepository.findById(petId);
        Pet pet = optionalPet.orElse(null);

        PetDTO petDTO = new PetDTO();
        if(pet != null) {
            petDTO = getPetDTOFromPet(pet);
        }
        return petDTO;
    }

    @GetMapping
    public List<PetDTO> getPets(){
        List<Pet> pets = petRepository.findAll();
        List<PetDTO> petDTOs = new ArrayList<>();

        for(Pet pet : pets){
            petDTOs.add(getPetDTOFromPet(pet));
        }

        return petDTOs;
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        Optional<User> optionalUser = userRepository.findById(ownerId);
        Customer customer = (Customer) optionalUser.orElse(null);

        List<PetDTO> petDTOs = new ArrayList<>();
        if(customer != null) {
            List<Pet> pets = customer.getPets();
            for(Pet pet : pets){
                petDTOs.add(getPetDTOFromPet(pet));
            }
        }
        return petDTOs;
    }
}
