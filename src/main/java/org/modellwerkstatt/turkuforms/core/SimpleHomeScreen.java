package org.modellwerkstatt.turkuforms.core;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinSession;
import org.joda.time.LocalTime;
import org.modellwerkstatt.objectflow.runtime.DeprecatedServerDateProvider;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.auth.ParamInfo;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.List;

import static org.modellwerkstatt.turkuforms.core.MPreisAppConfig.HOME_REDIRECT_PREFIX_LABEL;
import static org.modellwerkstatt.turkuforms.core.MPreisAppConfig.OK_HOKTEY;

public class SimpleHomeScreen extends VerticalLayout implements HasDynamicTitle, BeforeEnterObserver {

    private final Button button;
    protected String appName;
    protected ParamInfo paramInfo;

    public SimpleHomeScreen() {

        appName = Workarounds.getCurrentTurkuServlet().getAppNameVersion();

        Span loginIdentityImage = new Span();
        loginIdentityImage.addClassName("DefaultLoginLogo");
        add(loginIdentityImage);
        setAlignSelf(Alignment.CENTER, loginIdentityImage);

        H1 appNameH1 = new H1();
        appNameH1.setText(appName);
        add(appNameH1);
        setAlignSelf(Alignment.CENTER, appNameH1);


        button = new Button (HOME_REDIRECT_PREFIX_LABEL + " (" + OK_HOKTEY + ")", event -> {
            VaadinSession.getCurrent().getSession().invalidate();

            NavigationUtil.absolutNavi(TurkuServlet.LOGIN_ROUTE + paramInfo.getParamsToForwardIfAny());
        });
        Peculiar.useButtonShortcutHk(button, OK_HOKTEY);
        button.addClassName("DefaultLoginContentWidth");
        add(button);
        setAlignSelf(Alignment.CENTER, button);

        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        paramInfo = new ParamInfo(event.getLocation().getQueryParameters());

        if (paramInfo.hasUsername()) {
            button.setText(HOME_REDIRECT_PREFIX_LABEL + " " + paramInfo.getUsername() + " (" + OK_HOKTEY + ")");
        }

        LocalTime curDbTime = DeprecatedServerDateProvider.getSqlServerDateTime().toLocalTime();
        boolean inReebootInterval = curDbTime.isAfter(MPreisAppConfig.REBOOTINTERVAL_STARTTIME) ||
                                    curDbTime.isBefore(MPreisAppConfig.REBOOTINTERVAL_STOPTIME);

        List<String> segments = event.getLocation().getSegments();
        boolean hasCmdPath = segments.size() > 0 && !"".equals(segments.get(0));

        if (inReebootInterval && hasCmdPath) {
            // get rid of any /<path>
            String userParam = paramInfo.hasUsername() ? paramInfo.getOnlyUsernameParam() : "";
            event.forwardTo("/" + userParam);

        } else if (hasCmdPath && paramInfo.hasReroute()) {
            NavigationUtil.absolutNavi(TurkuServlet.LOGIN_ROUTE + paramInfo.getParamsToForwardIfAny());


        } else if (hasCmdPath) {
            String otherParams = paramInfo.getParamsToForwardIfAny();
            otherParams += otherParams.length() == 0 ? "?" : "&";
            otherParams += NavigationUtil.REROUTE_TO + "=" + event.getLocation().getPath();


            NavigationUtil.absolutNavi(TurkuServlet.LOGIN_ROUTE + otherParams);
        }
    }

    @Override
    public String getPageTitle() {
        return appName;
    }
}
