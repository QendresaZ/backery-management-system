package dev.bms.bakerymngsystem.backend.service;

import dev.bms.bakerymngsystem.backend.repository.ShiftRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.DayOfWeek;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
public class ShiftServiceTest {

    @Autowired
    private ShiftService shiftService;

    @Test
    public void test() {
        LocalDate shiftDate = LocalDate.now();
        System.out.println(shiftDate.with(DayOfWeek.MONDAY));
        System.out.println(shiftDate.with(DayOfWeek.SUNDAY));
    }
}
