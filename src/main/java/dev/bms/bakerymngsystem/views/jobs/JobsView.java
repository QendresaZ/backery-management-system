package dev.bms.bakerymngsystem.views.jobs;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.bms.bakerymngsystem.backend.entity.Job;
import dev.bms.bakerymngsystem.backend.service.JobService;
import dev.bms.bakerymngsystem.views.main.MainView;

@Route(value = "jobs", layout = MainView.class)
@PageTitle("Jobs")
@CssImport("./styles/views/jobs/jobs-view.css")
public class JobsView extends VerticalLayout {

   private Grid<Job> grid = new Grid<>(Job.class);
   private JobForm form;
   private JobService jobService;

   public JobsView(JobService jobService) {
      this.jobService = jobService;
      addClassName("jobs-view");
      setSizeFull();
      configureGrid();

      updateList();

      form = new JobForm();
      form.addListener(JobForm.SaveEvent.class, this::saveJob);
      form.addListener(JobForm.DeleteEvent.class, this::deleteJob);
      form.addListener(JobForm.CloseEvent.class, e-> closeEditor());

      HorizontalLayout horizontalLayoutContent = new HorizontalLayout();
      horizontalLayoutContent.addClassName("jobs-content");
      horizontalLayoutContent.setSizeFull();
      horizontalLayoutContent.add(grid, form);

      add(getToolbar(), horizontalLayoutContent);

      closeEditor();
   }

   private HorizontalLayout getToolbar() {
      Button addJobButton = new Button("Add job", click -> addJob());

      HorizontalLayout toolbar = new HorizontalLayout(addJobButton);
      toolbar.addClassName("toolbar");
      return toolbar;
   }

   private void addJob() {
      grid.asSingleSelect().clear();
      editJob(new Job());
   }

   private void saveJob(JobForm.SaveEvent event) {
      jobService.save(event.getJob());
      updateList();
      closeEditor();
   }

   private void deleteJob(JobForm.DeleteEvent event){
      jobService.delete(event.getJob());
      updateList();
      closeEditor();
   }

   private void closeEditor() {
      form.setJob(null);
      form.setVisible(false);
      removeClassName("edit-job");
   }

   private void updateList() {
      grid.setItems(jobService.findAll());
   }

   private void configureGrid() {
      grid.addClassName("jobs-grid");
      grid.setSizeFull();
      grid.setColumns("title", "description");
      grid.getColumns().forEach(col -> col.setAutoWidth(true));

      grid.asSingleSelect().addValueChangeListener(event -> editJob(event.getValue()));
   }

   private void editJob(Job job) {
      if (job == null){
         closeEditor();
      } else {
         form.setJob(job);
         form.setVisible(true);
         addClassName("edit-job");
      }
   }

}
