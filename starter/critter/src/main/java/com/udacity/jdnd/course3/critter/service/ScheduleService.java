package com.udacity.jdnd.course3.critter.service;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import com.udacity.jdnd.course3.critter.repository.UserRepository;
import com.udacity.jdnd.course3.critter.schedule.Schedule;
import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    UserRepository userRepository;

    public Schedule saveSchedule(Schedule schedule){
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllSchedules(){
        return scheduleRepository.findAll();
    }

    public List<Schedule> getSchedulesForPet(Long petId){
        return scheduleRepository.findByPets_Id(petId);
    }

    public List<Schedule> getSchedulesForEmployee(Long employeeId){
        return scheduleRepository.findByEmployees_Id(employeeId);
    }

    public List<Schedule> getSchedulesForCustomer(Long customerId){
        Optional<User> optionalUser = userRepository.findById(customerId);
        Customer customer = (Customer) optionalUser.orElse(null);
        List<Pet> pets = customer.getPets();

        ArrayList<Schedule> schedules = new ArrayList<>();
        for (Pet pet : pets) {
            schedules.addAll(scheduleRepository.findByPets_Id(pet.getId()));
        }
        return schedules;
    }
}
