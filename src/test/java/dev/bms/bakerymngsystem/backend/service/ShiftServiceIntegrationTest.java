package dev.bms.bakerymngsystem.backend.service;

import dev.bms.bakerymngsystem.Application;
import dev.bms.bakerymngsystem.backend.entity.Employee;
import dev.bms.bakerymngsystem.backend.entity.Job;
import dev.bms.bakerymngsystem.backend.entity.Shift;
import dev.bms.bakerymngsystem.backend.repository.EmployeeRepository;
import dev.bms.bakerymngsystem.backend.repository.JobRepository;
import org.assertj.core.api.Fail;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class ShiftServiceIntegrationTest {

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DataSource dataSource;

    private Employee employee;
    private Employee employee2;

    @Before
    public void setUp() {
        Job job = new Job();
        job.setTitle("Burekgji");
        job.setDescription("Me hollu brumin");
        Job job1 = jobRepository.save(job);

        Employee employee = new Employee();
        employee.setName("Filan");
        employee.setSurname("Fistek");
        employee.setHoursPerWeek(35d);
        employee.setJob(job);
        this.employee = employeeRepository.save(employee);

        employee2 = new Employee();
        employee2.setName("James");
        employee2.setSurname("Rodrigez");
        employee2.setHoursPerWeek(20d);
        employee2.setJob(job);
        this.employee2 = employeeRepository.save(employee2);

        Shift shift = new Shift();
        shift.setEmployee(employee);
        shift.setShiftDate(LocalDate.of(2021, 1, 9));
        shift.setStartTime(LocalTime.of(9, 0));
        shift.setEndTime(LocalTime.of(17, 0));
        shiftService.save(shift);

        Shift shift2 = new Shift();
        shift2.setEmployee(employee2);
        shift2.setShiftDate(LocalDate.of(2021, 1, 1));
        shift2.setStartTime(LocalTime.of(9, 0));
        shift2.setEndTime(LocalTime.of(17, 0));

        Shift shift3 = new Shift();
        shift3.setEmployee(employee2);
        shift3.setShiftDate(LocalDate.of(2020, 1, 1));
        shift3.setStartTime(LocalTime.of(5, 0));
        shift3.setEndTime(LocalTime.of(7, 0));
        shiftService.save(shift3);

        Shift shift4 = new Shift();
        shift4.setEmployee(employee2);
        shift4.setShiftDate(LocalDate.of(2020, 1, 7));
        shift4.setStartTime(LocalTime.of(5, 0));
        shift4.setEndTime(LocalTime.of(7, 0));
        shiftService.save(shift4);
    }

//    @After
//    public void tearDown() {
//        try {
//            clearDatabase();
//        } catch (Exception e) {
//            Fail.fail(e.getMessage());
//        }
//    }

    public void clearDatabase() throws SQLException {
        Connection c = dataSource.getConnection();
        Statement s = c.createStatement();

        // Disable FK
        s.execute("SET REFERENTIAL_INTEGRITY FALSE");

        // Find all tables and truncate them
        Set<String> tables = new HashSet<String>();
        ResultSet rs = s.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
        while (rs.next()) {
            tables.add(rs.getString(1));
        }
        rs.close();
        for (String table : tables) {
            s.executeUpdate("TRUNCATE TABLE " + table);
        }

        // Idem for sequences
        Set<String> sequences = new HashSet<String>();
        rs = s.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA='PUBLIC'");
        while (rs.next()) {
            sequences.add(rs.getString(1));
        }
        rs.close();
        for (String seq : sequences) {
            s.executeUpdate("ALTER SEQUENCE " + seq + " RESTART WITH 1");
        }

        // Enable FK
        s.execute("SET REFERENTIAL_INTEGRITY TRUE");
        s.close();
        c.close();
    }

    @Test
    public void testEmployeeNotNull() {
        Assert.assertNotNull(employee);
    }

    @Test
    public void testGivenMoreThanTwoEmployeesReturnMoreThanTwoEmployees() {
        Assert.assertTrue(shiftService.findAll().size() > 1);
    }

    @Test
    public void testGivenEmployeeReturnsAllShiftsOfThatEmployee() {
        List<Shift> shifts = shiftService.findAll(employee, null, null);
        Assert.assertEquals(1, shifts.size());
    }

    @Test
    public void testGivenFromDateReturnsAllShiftsFromThatDate() {
        List<Shift> shifts = shiftService.findAll(null, LocalDate.of(2021, 1, 9), null);
        System.out.println(shifts);
        Assert.assertEquals(1, shifts.size());
    }

    @Test
    public void testGivenToDateReturnsAllShiftsToThatDate() {
        List<Shift> shifts = shiftService.findAll(null, null, LocalDate.of(2021, 1, 1));
        System.out.println(shifts);
        Assert.assertEquals(2, shifts.size());
    }

    @Test
    public void testGivenEmployeeAndFromDateReturnsAllShiftsOfThatEmployeeFromThatDate() {
        List<Shift> shifts = shiftService.findAll(employee2, LocalDate.of(2020, 12, 31), null);
        for (Shift sh: shifts) {
            Assert.assertEquals(employee2, sh.getEmployee());
            Assert.assertFalse(LocalDate.of(2021, 1, 1).isAfter(sh.getShiftDate()));
        }
    }
}
