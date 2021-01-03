package dev.bms.bakerymngsystem.views.employees;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.bms.bakerymngsystem.backend.entity.Employee;
import dev.bms.bakerymngsystem.backend.entity.Job;
import dev.bms.bakerymngsystem.backend.service.EmployeeService;
import dev.bms.bakerymngsystem.backend.service.JobService;
import dev.bms.bakerymngsystem.views.main.MainView;

@Route(value = "employees", layout = MainView.class)
@PageTitle("Employees")
@CssImport("./styles/views/employees/employees-view.css")
public class EmployeesView extends VerticalLayout {


    private Grid<Employee> grid = new Grid<>(Employee.class);
    private EmployeeService employeeService;
    private JobService jobService;
    private TextField filterText = new TextField();
    private EmployeeForm employeeForm;


    public EmployeesView(EmployeeService employeeService,
                         JobService jobService){
        this.employeeService = employeeService;
        this.jobService = jobService;
        addClassName("employees-list");
        setSizeFull();
        configureGrid();

        employeeForm = new EmployeeForm(this.jobService.findAll());
        employeeForm.addListener(EmployeeForm.SaveEvent.class, this::saveEmployee);
        employeeForm.addListener(EmployeeForm.DeleteEvent.class, this::deleteEmployee);
        employeeForm.addListener(EmployeeForm.CloseEvent.class, e -> closeEditor());
        Div content = new Div(grid, employeeForm);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolbar(),content);
        updateList();

        closeEditor();
    }

    private void deleteEmployee(EmployeeForm.DeleteEvent event) {
        employeeService.delete(event.getEmployee());
        updateList();
        closeEditor();
    }

    private void saveEmployee(EmployeeForm.SaveEvent event) {
        employeeService.save(event.getEmployee());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        employeeForm.setEmployee(null);
        employeeForm.setVisible(false);
        removeClassName("editing");
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name..");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addEmployeeButton = new Button("Add employee");
        addEmployeeButton.addClickListener(click -> addEmployee());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addEmployeeButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

     void addEmployee() {
        grid.asSingleSelect().clear();
        editEmployee(new Employee());
    }

    private void updateList() {
        grid.setItems(employeeService.findAll(filterText.getValue()));
    }

    private void configureGrid() {
        grid.addClassName("employees-grid");
        grid.setSizeFull();
        grid.setColumns("name", "surname", "job", "phone", "address", "hoursPerWeek");
        grid.removeColumnByKey("job");
        grid.addColumn(employee -> {
            Job employeeJob = employee.getJob();
            return employeeJob.getTitle();
        }).setHeader("Job");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event ->
                editEmployee(event.getValue()));
    }

    private void editEmployee(Employee employee) {
        if(employee == null) {
            closeEditor();
        } else {
            employeeForm.setEmployee(employee);
            employeeForm.setVisible(true);
            addClassName("editing");
        }
    }

}
