package dev.bms.bakerymngsystem.backend.service;

import dev.bms.bakerymngsystem.backend.entity.Employee;
import dev.bms.bakerymngsystem.backend.entity.Shift;
import dev.bms.bakerymngsystem.backend.repository.EmployeeRepository;
import dev.bms.bakerymngsystem.backend.repository.ShiftRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ShiftService {
    private static final Logger LOGGER = Logger.getLogger(ShiftService.class.getName());
    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;

    public ShiftService(ShiftRepository shiftRepository,
                        EmployeeRepository employeeRepository) {
        this.shiftRepository = shiftRepository;
        this.employeeRepository = employeeRepository;
    }

    public void delete(Shift shift) {
        shiftRepository.delete(shift);
    }

    public void save(Shift shift) {
        if (shift == null) {
            LOGGER.log(Level.SEVERE,
                    "Shift is null. Are you sure you have connected your form to the application?");
            return;
        }
        shiftRepository.save(shift);
    }

    public List<Shift> findAll() {
        return shiftRepository.findAll();
    }

    public List<Shift> findAll(Employee employee, LocalDate fromDate, LocalDate toDate) {
        return shiftRepository.findAll((Specification<Shift>) (root, criteriaQuery, criteriaBuilder) -> {
            Predicate p = criteriaBuilder.conjunction();
            if (fromDate != null && toDate != null) {
                p = criteriaBuilder.and(p, criteriaBuilder.between(root.get("shiftDate"), fromDate, toDate));
            } else if (fromDate != null) {
                p = criteriaBuilder.and(p, criteriaBuilder.greaterThanOrEqualTo(root.get("shiftDate"), fromDate));
//                p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("shiftDate"), fromDate));
            } else if (toDate != null) {
                p = criteriaBuilder.and(p, criteriaBuilder.lessThanOrEqualTo(root.get("shiftDate"), toDate));
            }
            if (employee != null) {
                p = criteriaBuilder.and(p, criteriaBuilder.equal(root.get("employee"), employee));
            }
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("shiftDate")));
            return p;
        });
    }
}