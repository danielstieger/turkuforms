package org.modellwerkstatt.turkuforms.authmpreis;

import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import org.modellwerkstatt.turkuforms.auth.ParamInfo;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;


public class LdapAuthLandingPage extends IPAuthLandingPage implements BeforeEnterObserver, HasDynamicTitle {
    private String title;

    public LdapAuthLandingPage() {
        super();
    }

    @Override
    protected UserPrincipal tryIpOrParamLogin(ParamInfo params, ITurkuAppFactory factory) {
        return null;
    }
}
