package dev.bms.bakerymngsystem.backend.service;

import dev.bms.bakerymngsystem.backend.entity.Job;
import dev.bms.bakerymngsystem.backend.repository.EmployeeRepository;
import dev.bms.bakerymngsystem.backend.repository.JobRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Service
public class JobService {
    private static final Logger LOGGER = Logger.getLogger(JobService.class.getName());
    private JobRepository jobRepository;
    private EmployeeRepository employeeRepository;

    public JobService(JobRepository jobRepository,
                        EmployeeRepository employeeRepository
    ){
        this.jobRepository = jobRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    public long count() {
        return jobRepository.count();
    }

    public void delete(Job job) {
        jobRepository.delete(job);
    }

    public void save(Job job) {

        if(job == null) {
            LOGGER.log(Level.SEVERE,
                    "Job is null. Are you sure you have connected your form to the application?");
        }
        jobRepository.save(job);
    }

    @PostConstruct
    public void populateTestData(){
        if(jobRepository.count() == 0) {
            jobRepository.saveAll(Arrays.asList(new Job("Burekgji", "Me hollu brumin")));
        }
    }
}
