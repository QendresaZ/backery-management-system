package dev.bms.bakerymngsystem.backend.service;

import dev.bms.bakerymngsystem.backend.entity.Employee;
import dev.bms.bakerymngsystem.backend.repository.EmployeeRepository;
import dev.bms.bakerymngsystem.backend.repository.JobRepository;
import dev.bms.bakerymngsystem.backend.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EmployeeService {

    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
    private JobRepository jobRepository;
    private ShiftRepository shiftRepository;
    private EmployeeRepository employeeRepository;

    public EmployeeService(JobRepository jobRepository,
                           ShiftRepository shiftRepository, EmployeeRepository employeeRepository) {

        this.jobRepository = jobRepository;
        this.shiftRepository = shiftRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public long count() {
        return employeeRepository.count();
    }

    public void delete(Employee employee) {
        employeeRepository.delete(employee);
    }

    public void save(Employee employee) {
        if(employee == null) {
            LOGGER.log(Level.SEVERE,
                    "Employee is null. Are you sure you have connected your form to the application?");
            return;
        }
        employeeRepository.save(employee);
    }

    public List<Employee> findAll(String stringFilter){
        if (stringFilter == null || stringFilter.isEmpty()) {
            return employeeRepository.findAll();
        } else {
            return employeeRepository.search(stringFilter);
        }
    }
}
