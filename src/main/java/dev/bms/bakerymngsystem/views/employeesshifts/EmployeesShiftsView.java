package dev.bms.bakerymngsystem.views.employeesshifts;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.bms.bakerymngsystem.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "employees-shifts", layout = MainView.class)
@PageTitle("Employees Shifts")
@CssImport("./styles/views/employeesshifts/employees-shifts-view.css")
@RouteAlias(value = "", layout = MainView.class)
public class EmployeesShiftsView extends HorizontalLayout {



}
