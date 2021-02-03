package dev.bms.bakerymngsystem.views.employeesshifts;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.bms.bakerymngsystem.backend.entity.Employee;
import dev.bms.bakerymngsystem.backend.entity.Shift;
import dev.bms.bakerymngsystem.backend.service.EmployeeService;
import dev.bms.bakerymngsystem.backend.service.ShiftService;
import dev.bms.bakerymngsystem.backend.util.DateUtils;
import dev.bms.bakerymngsystem.views.jobs.JobForm;
import dev.bms.bakerymngsystem.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Route(value = "employees-shifts", layout = MainView.class)
@PageTitle("Employees Shifts")
@CssImport("./styles/views/employeesshifts/employeesshifts-view.css")
@RouteAlias(value = "", layout = MainView.class)
public class EmployeesShiftsView extends VerticalLayout {
    private ShiftService shiftService;
    private EmployeeService employeeService;
    private EmployeesShiftForm employeesShiftForm;

    private Grid<Shift> grid = new Grid<>(Shift.class);

    private ComboBox<Employee> employeesCombo = new ComboBox<>();
    DatePicker fromDateDatePicker = new DatePicker();
    DatePicker toDateDatePicker = new DatePicker();

    public EmployeesShiftsView(ShiftService shiftService, EmployeeService employeeService) {
        this.shiftService = shiftService;
        this.employeeService = employeeService;
        employeesShiftForm = new EmployeesShiftForm(employeeService.findAll());
        employeesShiftForm.addListener(EmployeesShiftForm.SaveEvent.class, this::saveEmployeeShift);
        employeesShiftForm.addListener(EmployeesShiftForm.DeleteEvent.class, this::deleteEmployeeShift);
        employeesShiftForm.addListener(EmployeesShiftForm.CloseEvent.class, e -> closeEditor());

        addClassName("shift-list");
        setSizeFull();
        configureEmployeesComboBox();
        configureFromDateAndToDateDatePickers();
        configureGrid();
        Div content = new Div(grid, employeesShiftForm);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolbar(), content);
        updateList();
        closeEditor();
    }

    private HorizontalLayout getToolbar() {
        Button addShiftButton = new Button("Add Shift");
        final Button reportGeneratorButton = new Button("Generate Report");
        reportGeneratorButton.addClassName("reportGeneratorButton");
        addShiftButton.addClickListener(click -> addShift());

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.add(employeesCombo, fromDateDatePicker, toDateDatePicker, addShiftButton,reportGeneratorButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addShift() {
        grid.asSingleSelect().clear();
        editShift(new Shift());
    }

    private void configureFromDateAndToDateDatePickers() {
        configureDatePickerForGridFilter(fromDateDatePicker, "From Date");
        configureDatePickerForGridFilter(toDateDatePicker, "To Date");
    }

    private void configureDatePickerForGridFilter(DatePicker datePicker, String placeholder) {
        datePicker.setPlaceholder(placeholder);
        datePicker.setWeekNumbersVisible(true);
        datePicker.setLocale(Locale.GERMANY);
        datePicker.setI18n(getI18nForDatePickerWeeks());
        datePicker.setClearButtonVisible(true);
        datePicker.addValueChangeListener((HasValue.ValueChangeEvent<LocalDate> event) -> {
            onFilterChange();
        });
    }

    private DatePicker.DatePickerI18n getI18nForDatePickerWeeks() {
        return new DatePicker.DatePickerI18n().setWeek("Week")
                .setCalendar("Calendar").setClear("Clear").setToday("Today")
                .setCancel("cancel").setFirstDayOfWeek(1)
                .setMonthNames(Arrays.asList("January", "February", "March",
                        "April", "May", "June", "July", "August", "September",
                        "October", "November", "December"))
                .setWeekdays(Arrays.asList("Sunday", "Monday", "Tuesday",
                        "Wednesday", "Thursday", "Friday", "Saturday"))
                .setWeekdaysShort(Arrays.asList("Sun", "Mon", "Tue", "Wed",
                        "Thu", "Fri", "Sat"));
    }

    private void configureEmployeesComboBox() {
        employeesCombo.setPlaceholder("Select employee");
        employeesCombo.setItems(employeeService.findAll());
        employeesCombo.setItemLabelGenerator((e) -> e.getName() + " " + e.getSurname());
        employeesCombo.setClearButtonVisible(true);
        employeesCombo.addValueChangeListener((HasValue.ValueChangeEvent<Employee> event) -> {
            onFilterChange();
        });
    }


    private void updateList() {
        grid.setItems(shiftService.findAll());
    }

    private void configureGrid() {
        grid.addClassName("shift-grid");
        grid.setSizeFull();


//        grid.removeColumnByKey("employee");
//        grid.setColumns("employee", "shiftDate", "startTime", "endTime", "note",
//                "breakStartTime", "breakEndTime");
        grid.removeAllColumns();

        grid.addColumn(sh -> {
            Employee employee = sh.getEmployee();
            return employee.getName() + " " + employee.getSurname();
        }).setHeader("Employee").setKey("employee");
        grid.addColumn(sh -> {
            return DateUtils.formatDate(sh.getShiftDate());
        }).setHeader("Shift date");
        grid.addColumn(sh -> {
            return sh.getStartTime();
        }).setHeader("Start time");
        grid.addColumn(sh -> {
            return sh.getEndTime();
        }).setHeader("End time");
        grid.addColumn(sh -> {
            return sh.getNote();
        }).setHeader("Note");
        grid.addColumn(sh -> {
            return sh.getBreakStartTime();
        }).setHeader("Break start time");
        grid.addColumn(sh -> {
            return sh.getBreakEndTime();
        }).setHeader("Break end time");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event ->
                editShift(event.getValue()));
    }

    private void editShift(Shift shift) {
        if (shift == null) {
            closeEditor();
        } else {
            employeesShiftForm.setShift(shift);
            employeesShiftForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void saveEmployeeShift(EmployeesShiftForm.SaveEvent event) {
        shiftService.save(event.getShift());
        updateList();
        closeEditor();
    }

    private void deleteEmployeeShift(EmployeesShiftForm.DeleteEvent event) {
        shiftService.delete(event.getShift());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        employeesShiftForm.setShift(null);
        employeesShiftForm.setVisible(false);
        removeClassName("editing");
    }

    private void onFilterChange() {
        LocalDate toDateValue = toDateDatePicker.getValue();
        LocalDate fromDateValue = fromDateDatePicker.getValue();
        Employee employeeValue = employeesCombo.getValue();
        grid.setItems(shiftService.findAll(employeeValue, fromDateValue, toDateValue));
    }
}
