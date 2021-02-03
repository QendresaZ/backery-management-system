package dev.bms.bakerymngsystem.views.employeesshifts;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import dev.bms.bakerymngsystem.backend.entity.Employee;
import dev.bms.bakerymngsystem.backend.entity.Shift;
import dev.bms.bakerymngsystem.backend.service.EmployeeService;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EmployeesShiftForm extends FormLayout {

    ComboBox<Employee> employee = new ComboBox<>("Select Employee");
    DatePicker shiftDate = new DatePicker("Set shift date");
    TimePicker startTime = new TimePicker("Set start time");
    TimePicker endTime = new TimePicker("Set end time");
    TextArea note = new TextArea("Add notes");
    TimePicker breakStartTime = new TimePicker("Set break start time");
    TimePicker breakEndTime = new TimePicker("Set break end time");
    Checkbox autoBreakTimeCalculation = new Checkbox();

    Binder<Shift> binder = new BeanValidationBinder<>(Shift.class);

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private Shift shift;

    public EmployeesShiftForm(List<Employee> employeesList) {
        addClassName("shifts-form");
        employee.setItems(employeesList);
        employee.setItemLabelGenerator((e) -> e.getName() + " " + e.getSurname());

        binder.bindInstanceFields(this);
        configureShiftDate();
        configureTimePickers();
        configureAutoBeakTimeCalculationCheckBox();

        add(
                employee,
                shiftDate,
                startTime,
                endTime,
                note,
                autoBreakTimeCalculation,
                breakStartTime,
                breakEndTime,
                createButtonsLayout());
    }

    private void configureAutoBeakTimeCalculationCheckBox() {
        autoBreakTimeCalculation.setLabel("Calculate Break Time Automatically");
        autoBreakTimeCalculation.setValue(true);
        autoBreakTimeCalculation.addValueChangeListener(new HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<Checkbox, Boolean>>() {
            @Override
            public void valueChanged(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> checkboxBooleanComponentValueChangeEvent) {
                Boolean isChecked = checkboxBooleanComponentValueChangeEvent.getValue();
                if (isChecked) {
                    breakStartTime.setVisible(false);
                    breakStartTime.setValue(null);
                    breakEndTime.setVisible(false);
                    breakEndTime.setValue(null);
                } else {
                    breakStartTime.setVisible(true);
                    breakEndTime.setVisible(true);
                }
            }
        });
    }

    private void configureTimePickers() {
        startTime.setLocale(Locale.GERMANY);
        endTime.setLocale(Locale.GERMANY);
        breakStartTime.setLocale(Locale.GERMANY);
        breakStartTime.setLocale(Locale.GERMANY);

        // hide break start time and break end time on init
        breakStartTime.setVisible(false);
        breakEndTime.setVisible(false);
    }

    private void configureShiftDate() {
        shiftDate.setLocale(Locale.GERMANY);
        shiftDate.setClearButtonVisible(true);
        shiftDate.setWeekNumbersVisible(true);
        shiftDate.setI18n(new DatePicker.DatePickerI18n().setWeek("Week")
                .setCalendar("Calendar").setClear("Clear").setToday("Today")
                .setCancel("cancel").setFirstDayOfWeek(1)
                .setMonthNames(Arrays.asList("January", "February", "March",
                        "April", "May", "June", "July", "August", "September",
                        "October", "November", "December"))
                .setWeekdays(Arrays.asList("Sunday", "Monday", "Tuesday",
                        "Wednesday", "Thursday", "Friday", "Saturday"))
                .setWeekdaysShort(Arrays.asList("Sun", "Mon", "Tue", "Wed",
                        "Thu", "Fri", "Sat")));
    }

    public void setShift(Shift shift) {
        this.shift = shift;
        binder.readBean(shift);
    }

    public static abstract class ShiftFormEvent extends ComponentEvent<EmployeesShiftForm> {
        private Shift shift;

        protected ShiftFormEvent(EmployeesShiftForm source, Shift shift) {
            super(source, false);
            this.shift = shift;
        }

        public Shift getShift() {
            return shift;
        }
    }

    public static class SaveEvent extends ShiftFormEvent {
        SaveEvent(EmployeesShiftForm source, Shift shift) {
            super(source, shift);
        }
    }

    public static class DeleteEvent extends ShiftFormEvent {
        DeleteEvent(EmployeesShiftForm source, Shift shift) {
            super(source, shift);
        }
    }

    public static class CloseEvent extends ShiftFormEvent {
        CloseEvent(EmployeesShiftForm source, Shift shift) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, shift)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this, shift)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            if(checkIfBreakTimeIsEligible()) {
                setBreakTimeFields();
            }

            binder.writeBean(shift);
            fireEvent(new SaveEvent(this, shift));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    private void setBreakTimeFields() {
        LocalTime[] breakTime = calculateStartAndEndBreakTime();
        breakStartTime.setValue(breakTime[0]);
        breakEndTime.setValue(breakTime[1]);
    }

    private boolean checkIfBreakTimeIsEligible() {
        if (autoBreakTimeCalculation.isEnabled() && !startTime.isEmpty() && !endTime.isEmpty()) {
            LocalTime startTimeLocal = startTime.getValue();
            LocalTime endTimeLocal = endTime.getValue();
            // if employee has a shift more than 4 hours is eligible for break time
            return endTimeLocal.getHour() - startTimeLocal.getHour() > 4;
        }
        return false;
    }

    private LocalTime[] calculateStartAndEndBreakTime() {
        LocalTime startTimeLocal = startTime.getValue();

        LocalTime startBreakTime = startTimeLocal.plusHours(4);
        LocalTime endBreakTime = startBreakTime.plusMinutes(30);
        return new LocalTime[] {startBreakTime, endBreakTime};
    }
}
