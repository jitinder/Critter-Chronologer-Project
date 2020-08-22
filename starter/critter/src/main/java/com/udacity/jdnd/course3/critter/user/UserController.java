package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
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
    UserService userService;

    @Autowired
    PetService petService;

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
            List<Pet> pets = petService.getAllPetsByIds(petIds);
            customer.setPets(pets);
        }

        return customer;
    }

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        // Convert DTO to Customer and save
        Customer customer = getCustomerFromCustomerDTO(customerDTO);
        Customer savedCustomer = (Customer) userService.saveUser(customer);

        // Convert Customer to DTO and return
        return getCustomerDTOFromCustomer(savedCustomer);
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        List<Customer> customers = userService.getAllCustomers();
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
        Pet pet = petService.getPetById(petId);

        CustomerDTO customerDTO = new CustomerDTO();
        Customer customer = (Customer) pet.getOwner();
        customerDTO = getCustomerDTOFromCustomer(customer);

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
        Employee savedEmployee = (Employee) userService.saveUser(employee);

        return getEmployeeDTOFromEmployee(savedEmployee);
    }

    @PostMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        Employee employee = userService.getEmployeeById(employeeId);
        return getEmployeeDTOFromEmployee(employee);
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        Employee employee = userService.getEmployeeById(employeeId);
        employee.setDaysAvailable(daysAvailable);
        userService.saveUser(employee);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        DayOfWeek day = employeeDTO.getDate().getDayOfWeek();
        Set<EmployeeSkill> skills = employeeDTO.getSkills();

        List<Employee> employees = userService.getAvailableEmployees(day, skills);//userRepository.findByDaysAvailableContaining(day);
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        for(Employee e : employees){
                employeeDTOList.add(getEmployeeDTOFromEmployee(e));
        }
        return employeeDTOList;
    }

}
