package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import com.udacity.jdnd.course3.critter.service.UserService;
import com.udacity.jdnd.course3.critter.user.Employee;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    PetService petService;

    @Autowired
    UserService userService;

    private Schedule getScheduleFromScheduleDTO(ScheduleDTO scheduleDTO){
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleDTO, schedule, "employeeIds", "petIds");

        List<Employee> employees = new ArrayList<>();
        if(scheduleDTO.getEmployeeIds() != null){
            for(Long l : scheduleDTO.getEmployeeIds()){
                employees.add(userService.getEmployeeById(l));
            }
        }
        List<Pet> pets = new ArrayList<>();
        if(scheduleDTO.getPetIds() != null){
            pets = petService.getAllPetsByIds(scheduleDTO.getPetIds());
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
        Schedule savedSchedule = scheduleService.saveSchedule(schedule);
        return getScheduleDTOFromSchedule(savedSchedule);
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();

        List<ScheduleDTO> scheduleDTOs = new ArrayList<>();
        for(Schedule schedule : schedules){
            scheduleDTOs.add(getScheduleDTOFromSchedule(schedule));
        }
        return scheduleDTOs;
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        List<Schedule> schedules = scheduleService.getSchedulesForPet(petId);

        List<ScheduleDTO> scheduleDTOs = new ArrayList<>();
        for(Schedule schedule : schedules){
            scheduleDTOs.add(getScheduleDTOFromSchedule(schedule));
        }
        return scheduleDTOs;
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        List<Schedule> schedules = scheduleService.getSchedulesForEmployee(employeeId);

        List<ScheduleDTO> scheduleDTOs = new ArrayList<>();
        for(Schedule schedule : schedules){
            scheduleDTOs.add(getScheduleDTOFromSchedule(schedule));
        }
        return scheduleDTOs;
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        List<Schedule> schedules = scheduleService.getSchedulesForCustomer(customerId);

        List<ScheduleDTO> scheduleDTOs = new ArrayList<>();
        for (Schedule schedule : schedules) {
            scheduleDTOs.add(getScheduleDTOFromSchedule(schedule));
        }
        return scheduleDTOs;
    }
}
