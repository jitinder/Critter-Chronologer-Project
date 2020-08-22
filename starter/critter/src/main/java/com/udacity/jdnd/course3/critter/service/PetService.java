package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.UserRepository;
import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PetService {

    @Autowired
    PetRepository petRepository;

    @Autowired
    UserRepository userRepository;

    public Pet savePet(Pet pet){
        Pet savedPet =  petRepository.save(pet);
        Customer customer = (Customer) savedPet.getOwner();
        List<Pet> customerPets = customer.getPets();
        if(customerPets == null){
            customerPets = new ArrayList<>();
        }
        customerPets.add(savedPet);
        customer.setPets(customerPets);
        userRepository.save(customer);

        return savedPet;
    }

    public Pet getPetById(Long petId){
        Optional<Pet> optionalPet = petRepository.findById(petId);
        return optionalPet.orElse(null);
    }

    public List<Pet> getAllPetsByIds(List<Long> ids){
        return petRepository.findAllById(ids);
    }

    public List<Pet> getAllPets(){
        return petRepository.findAll();
    }

    public List<Pet> getPetsByOwnerId(Long ownerId){
        Optional<User> optionalUser = userRepository.findById(ownerId);
        Customer customer = (Customer) optionalUser.orElse(null);

        if (customer != null) {
            return customer.getPets();
        } else {
            return new ArrayList<Pet>();
        }
    }
}
