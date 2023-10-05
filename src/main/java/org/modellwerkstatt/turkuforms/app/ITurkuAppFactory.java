package org.modellwerkstatt.turkuforms.app;

import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IMoLdapService;

public interface ITurkuAppFactory extends IToolkit_UiFactory {

    void setRedirectAfterLogoutPath(String homePath);
    String getRedirectAfterLogoutPath();

    boolean isCompactMode();
    boolean isCheckDeployedVersion();

    String getAuthenticatorClassFqName();

    IMoLdapService getLdapServiceIfPresent();

    String translateIconName(String name);
    String translateButtonLabel(String label, String hk);

}