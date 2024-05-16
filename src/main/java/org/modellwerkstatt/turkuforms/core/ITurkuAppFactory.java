package org.modellwerkstatt.turkuforms.core;

import com.vaadin.flow.server.VaadinRequest;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IMoLdapService;

public interface ITurkuAppFactory extends IToolkit_UiFactory {

    void setOnLogoutMainLandingPath(String homePath);
    String getOnLogoutMainLandingPath();

    boolean isCompactMode();
    boolean isCheckDeployedVersion();

    boolean isSingleAppInstanceMode();

    String getAuthenticatorClassFqName();

    IMoLdapService getLdapServiceIfPresent();

    String translateIconName(String name);
    String translateButtonLabel(String label, String hk);

    void setAutoParDeploymentForwardGracefully(boolean val);
    boolean isAutoParDeploymentForwardGracefully();

    String getRemoteAddr(VaadinRequest req);

}
