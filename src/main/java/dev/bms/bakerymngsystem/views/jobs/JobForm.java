package dev.bms.bakerymngsystem.views.jobs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import dev.bms.bakerymngsystem.backend.entity.Job;

import java.util.List;

public class JobForm extends FormLayout {

    TextField title = new TextField("Job title");
    TextArea description = new TextArea("Job desciption");

    Binder<Job> binder = new BeanValidationBinder<>(Job.class);
    private Job job;

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Close");

    public JobForm() {
        addClassName("job-form");
        binder.bindInstanceFields(this);
        add(title,
                description,
                createButtonsLayout());

    }

    public void setJob(Job job) {
        this.job = job;
        binder.readBean(job);
    }

    //Events
    public static abstract class JobFormEvent extends ComponentEvent<JobForm> {

        private Job job;

        public JobFormEvent(JobForm source, Job job) {
            super(source, false);
            this.job = job;
        }

        public Job getJob() {
            return job;
        }
    }

    public static class SaveEvent extends JobFormEvent {
        SaveEvent(JobForm source, Job job) {
            super(source, job);
        }
    }

    public static class DeleteEvent extends JobFormEvent {
        DeleteEvent(JobForm source, Job job) {
            super(source, job);
        }
    }


    public static class CloseEvent extends JobFormEvent {
        CloseEvent(JobForm source) {
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

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, job)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(job);
            fireEvent(new SaveEvent(this, job));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}




