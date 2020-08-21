package com.udacity.jdnd.course3.critter.user;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.DayOfWeek;
import java.util.Set;

@Entity
public class Employee extends User{
    @ElementCollection
    private Set<EmployeeSkill> skills;
    @ElementCollection
    private Set<DayOfWeek> daysAvailable;

    public Employee() {
    }

    public Employee(Long id, String name, Set<EmployeeSkill> skills, Set<DayOfWeek> daysAvailable) {
        super(id, name);
        this.skills = skills;
        this.daysAvailable = daysAvailable;
    }

    public Set<EmployeeSkill> getSkills() {
        return skills;
    }

    public void setSkills(Set<EmployeeSkill> skills) {
        this.skills = skills;
    }

    public Set<DayOfWeek> getDaysAvailable() {
        return daysAvailable;
    }

    public void setDaysAvailable(Set<DayOfWeek> daysAvailable) {
        this.daysAvailable = daysAvailable;
    }
}
