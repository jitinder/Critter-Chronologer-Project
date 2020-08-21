package com.udacity.jdnd.course3.critter.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select c from Customer c")
    List<Customer> findAllCustomers();

    Employee findEmployeeById(Long id);

    @Query("select e from Employee e where :day member of e.daysAvailable")
    List<Employee> findByDaysAvailableContaining(DayOfWeek day);
}
