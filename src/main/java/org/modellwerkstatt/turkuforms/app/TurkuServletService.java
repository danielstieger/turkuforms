package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import org.modellwerkstatt.turkuforms.util.Turku;

public class TurkuServletService extends VaadinServletService {

    public TurkuServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration) {
        super(servlet, deploymentConfiguration);
    }

    @Override
    public void requestStart(VaadinRequest request, VaadinResponse response) {
        Turku.l("TurkuServletService.requestStart() START  - - - - - - - - - - - - - - - - - - - - ");

        super.requestStart(request, response);
    }

    @Override
    public void requestEnd(VaadinRequest request, VaadinResponse response, VaadinSession session) {
        Turku.l("TurkuServletService.requestEnd() END - - - - - - - - - - - - - - - - - - - - ");

        super.requestEnd(request, response, session);
    }
}
