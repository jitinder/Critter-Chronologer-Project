package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Handles web requests related to Users.
 *
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PetRepository petRepository;

    private CustomerDTO getCustomerDTOFromCustomer(Customer customer){
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer, customerDTO, "pets");
        // Convert List of Pets to List of PetIds
        List<Long> petIds = new ArrayList<>();
        List<Pet> pets = customer.getPets();
        if(pets != null) {
            for (Pet p : pets) {
                petIds.add(p.getId());
            }
            customerDTO.setPetIds(petIds);
        }
        return customerDTO;
    }

    private Customer getCustomerFromCustomerDTO(CustomerDTO customerDTO){
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer, "petIds");

        // Convert List of PetIds to List of Pets
        List<Long> petIds = customerDTO.getPetIds();
        if(petIds != null) {
            List<Pet> pets = petRepository.findAllById(petIds);
            customer.setPets(pets);
        }

        return customer;
    }

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        // Convert DTO to Customer and save
        Customer customer = getCustomerFromCustomerDTO(customerDTO);
        Customer savedCustomer = userRepository.save(customer);

        // Convert Customer to DTO and return
        return getCustomerDTOFromCustomer(savedCustomer);
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        List<Customer> customers = userRepository.findAllCustomers();
        List<CustomerDTO> customerDTOs = new ArrayList<>();
        if(customers != null){
            for(Customer customer : customers){
                customerDTOs.add(getCustomerDTOFromCustomer(customer));
            }
        }
        return customerDTOs;
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId){
        Optional<Pet> optionalPet = petRepository.findById(petId);
        Pet pet = optionalPet.orElse(null);


        CustomerDTO customerDTO = new CustomerDTO();
        if (pet != null) {
            Customer customer = (Customer) pet.getOwner();
            BeanUtils.copyProperties(customer,customerDTO);
        }

        return customerDTO;
    }

    private Employee getEmployeeFromEmployeeDTO(EmployeeDTO employeeDTO){
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        return employee;
    }

    private EmployeeDTO getEmployeeDTOFromEmployee(Employee employee){
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employee, employeeDTO);
        return employeeDTO;
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        Employee employee = getEmployeeFromEmployeeDTO(employeeDTO);
        Employee savedEmployee = userRepository.save(employee);

        return getEmployeeDTOFromEmployee(savedEmployee);
    }

    @PostMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        Employee employee = userRepository.findEmployeeById(employeeId);
        return getEmployeeDTOFromEmployee(employee);
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        Employee employee = userRepository.findEmployeeById(employeeId);
        employee.setDaysAvailable(daysAvailable);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        DayOfWeek day = employeeDTO.getDate().getDayOfWeek();
        Set<EmployeeSkill> skills = employeeDTO.getSkills();

        List<Employee> employees = userRepository.findByDaysAvailableContaining(day);
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        if(employees != null){
            for(Employee e : employees){
                // Check if employee skills contains the required skills
                if(e.getSkills().containsAll(skills)) {
                    employeeDTOList.add(getEmployeeDTOFromEmployee(e));
                }
            }
        }
        return employeeDTOList;
    }

}
