package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.UserRepository;
import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.Employee;
import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import com.udacity.jdnd.course3.critter.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PetRepository petRepository;

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public List<Customer> getAllCustomers(){
        return userRepository.findAllCustomers();
    }

    public Customer getOwnerByPetId(Long petId){
        Optional<Pet> optionalPet = petRepository.findById(petId);
        Pet pet = optionalPet.orElse(new Pet());
        Customer customer = (Customer) pet.getOwner();
        if(customer == null){
            return new Customer();
        }
        return customer;
    }

    public Customer getCustomerById(Long customerId){
        return userRepository.findCustomerById(customerId);
    }

    public Employee getEmployeeById(Long employeeId){
        return userRepository.findEmployeeById(employeeId);
    }

    public List<Employee> getAvailableEmployees(DayOfWeek day, Set<EmployeeSkill> skills){
        List<Employee> employees = userRepository.findAllByDaysAvailableContaining(day);
        List<Employee> availableEmployees = new ArrayList<>();
        for(Employee e : employees){
            // Check if employee skills contains the required skills
            if(e.getSkills().containsAll(skills)) {
                availableEmployees.add(e);
            }
        }
        return availableEmployees;
    }
}
