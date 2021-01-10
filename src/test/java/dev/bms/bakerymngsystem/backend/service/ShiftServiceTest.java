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

@SpringBootTest
@AutoConfigureMockMvc
public class ShiftServiceTest {

    @Autowired
    private ShiftRepository shiftRepository;

    @Test
    public void test() {

    }
}
