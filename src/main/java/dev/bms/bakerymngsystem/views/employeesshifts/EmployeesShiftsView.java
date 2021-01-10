package dev.bms.bakerymngsystem.views.employeesshifts;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.bms.bakerymngsystem.backend.entity.Employee;
import dev.bms.bakerymngsystem.backend.entity.Shift;
import dev.bms.bakerymngsystem.backend.service.EmployeeService;
import dev.bms.bakerymngsystem.backend.service.ShiftService;
import dev.bms.bakerymngsystem.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

@Route(value = "employees-shifts", layout = MainView.class)
@PageTitle("Employees Shifts")
@CssImport("./styles/views/employeesshifts/employees-shifts-view.css")
@RouteAlias(value = "", layout = MainView.class)
public class EmployeesShiftsView extends VerticalLayout
{
    private ShiftService shiftService;
    private EmployeeService employeeService;

    private Grid<Shift> grid = new Grid<>(Shift.class);

    private ComboBox<Employee> employeesCombo = new ComboBox<>();
    DatePicker fromDateDatePicker = new DatePicker();
    DatePicker toDateDatePicker = new DatePicker();

    public EmployeesShiftsView(ShiftService shiftService, EmployeeService employeeService) {
        this.shiftService = shiftService;
        this.employeeService = employeeService;
        addClassName("shift-view");
        setSizeFull();
        configureEmployeesComboBox();
        configureFromDateAndToDateDatePickers();
        configureGrid();

        HorizontalLayout filters = new HorizontalLayout();
        filters.add(employeesCombo, fromDateDatePicker, toDateDatePicker);

        add(filters, grid);
        updateList();
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
        grid.setColumns("employee", "shiftDate", "startTime", "endTime", "note",
                "breakStartTime", "breakEndTime");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void onFilterChange() {
        LocalDate toDateValue = toDateDatePicker.getValue();
        LocalDate fromDateValue = fromDateDatePicker.getValue();
        Employee employeeValue = employeesCombo.getValue();
        grid.setItems(shiftService.findAll(employeeValue, fromDateValue, toDateValue));
    }

}
