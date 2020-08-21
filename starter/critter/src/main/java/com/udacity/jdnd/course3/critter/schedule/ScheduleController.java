package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import com.udacity.jdnd.course3.critter.user.Customer;
import com.udacity.jdnd.course3.critter.user.Employee;
import com.udacity.jdnd.course3.critter.user.User;
import com.udacity.jdnd.course3.critter.user.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    UserRepository userRepository;

    private Schedule getScheduleFromScheduleDTO(ScheduleDTO scheduleDTO){
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDTO, schedule, "employeeIds", "petIds");

        List<Employee> employees = new ArrayList<>();
        if(scheduleDTO.getEmployeeIds() != null){
            for(Long l : scheduleDTO.getEmployeeIds()){
                employees.add(userRepository.findEmployeeById(l));
            }
        }
        List<Pet> pets = new ArrayList<>();
        if(scheduleDTO.getPetIds() != null){
            pets = (petRepository.findAllById(scheduleDTO.getPetIds()));
        }
        schedule.setEmployees(employees);
        schedule.setPets(pets);

        return schedule;
    }

    private ScheduleDTO getScheduleDTOFromSchedule(Schedule schedule){
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, scheduleDTO, "employees", "pets");

        List<Long> employeeIds = new ArrayList<>();
        for(Employee e : schedule.getEmployees()){
            employeeIds.add(e.getId());
        }
        List<Long> petIds = new ArrayList<>();
        for(Pet p : schedule.getPets()){
            petIds.add(p.getId());
        }
        scheduleDTO.setEmployeeIds(employeeIds);
        scheduleDTO.setPetIds(petIds);

        return scheduleDTO;
    }

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        Schedule schedule = getScheduleFromScheduleDTO(scheduleDTO);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return getScheduleDTOFromSchedule(savedSchedule);
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();

        List<ScheduleDTO> scheduleDTOs = new ArrayList<>();
        for(Schedule schedule : schedules){
            scheduleDTOs.add(getScheduleDTOFromSchedule(schedule));
        }
        return scheduleDTOs;
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        List<Schedule> schedules = scheduleRepository.findByPets_Id(petId);
        List<ScheduleDTO> scheduleDTOs = new ArrayList<>();
        for(Schedule schedule : schedules){
            scheduleDTOs.add(getScheduleDTOFromSchedule(schedule));
        }
        return scheduleDTOs;
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        List<Schedule> schedules = scheduleRepository.findByEmployees_Id(employeeId);
        List<ScheduleDTO> scheduleDTOs = new ArrayList<>();
        for(Schedule schedule : schedules){
            scheduleDTOs.add(getScheduleDTOFromSchedule(schedule));
        }
        return scheduleDTOs;
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        Optional<User> optionalUser = userRepository.findById(customerId);
        Customer customer = (Customer) optionalUser.orElse(null);
        List<Pet> pets = customer.getPets();

        List<Schedule> schedules = new ArrayList<>();
        List<ScheduleDTO> scheduleDTOs = new ArrayList<>();
        for (Pet pet : pets) {
            schedules.addAll(scheduleRepository.findByPets_Id(pet.getId()));
        }


        for (Schedule schedule : schedules) {
            scheduleDTOs.add(getScheduleDTOFromSchedule(schedule));
        }
        return scheduleDTOs;
    }
}
