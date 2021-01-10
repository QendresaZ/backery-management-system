package dev.bms.bakerymngsystem.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Employee extends AbstractEntity{

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jobId", nullable = false)
    private Job job;

    @OneToMany(mappedBy = "employee", fetch = FetchType.EAGER)
    private List<Shift> shifts = new LinkedList<>();

    private String phone;

    private String address;

    @NotNull
    private Double hoursPerWeek;

    public Employee() {
    }

    public Employee(Long id, @NotNull String name, @NotNull String surname) {
        super(id);
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getHoursPerWeek() {
        return hoursPerWeek;
    }

    public void setHoursPerWeek(Double hoursPerWeek) {
        this.hoursPerWeek = hoursPerWeek;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
