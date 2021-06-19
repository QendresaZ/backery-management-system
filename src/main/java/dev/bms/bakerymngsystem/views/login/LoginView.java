package dev.bms.bakerymngsystem.views.login;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login | BMS")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");

        add(new H1("Bakery Management System"), login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // inform user about an auth error
        if (queryParametersHaveError(event)) {
            login.setError(true);
        }
    }

    private boolean queryParametersHaveError(BeforeEnterEvent event) {
        return event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error");
    }
}
