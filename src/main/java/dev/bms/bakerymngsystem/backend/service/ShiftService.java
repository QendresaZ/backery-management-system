package dev.bms.bakerymngsystem.backend.service;

import dev.bms.bakerymngsystem.backend.entity.Shift;
import dev.bms.bakerymngsystem.backend.repository.EmployeeRepository;
import dev.bms.bakerymngsystem.backend.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ShiftService {
    private static final Logger LOGGER = Logger.getLogger(ShiftService.class.getName());
    private ShiftRepository shiftRepository;
    private EmployeeRepository employeeRepository;

    public ShiftService(ShiftRepository shiftRepository,
                        EmployeeRepository employeeRepository) {

        this.shiftRepository = shiftRepository;
        this.employeeRepository = employeeRepository;
    }

    public void delete(Shift shift) {
        shiftRepository.delete(shift);
    }

    public void save(Shift shift) {
        if(shift == null) {
            LOGGER.log(Level.SEVERE,
                    "Shift is null. Are you sure you have connected your form to the application?");
            return;
        }
        shiftRepository.save(shift);
    }
}
