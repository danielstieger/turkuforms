package org.modellwerkstatt.turkuforms.core;

import org.modellwerkstatt.dataux.runtime.auth.ExtAuthProvider;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IMoLdapService;
import org.modellwerkstatt.objectflow.runtime.IOFXCmdModule;

import java.util.List;

public interface ITurkuAppFactory extends IToolkit_UiFactory {

    void setOnLogoutMainLandingPath(String homePath);
    String getOnLogoutMainLandingPath();

    boolean isCompactMode();

    boolean isSingleAppInstanceMode();

    String getAuthenticatorClassFqName();

    IMoLdapService getLdapServiceIfPresent();

    String translateIconName(String name);
    String translateButtonLabel(String label, String hk);

    String getRemoteAddr();

    void initExtAuthProviders(List<ExtAuthProvider> providers);
    List<ExtAuthProvider> getAllExtAuthProviders();

    void initCmdUrlDefaults(boolean usngUrlHndlng);
    List<IOFXCmdModule.CmdUrlDefaults> getAllCmdUrlDefaults();

    boolean cmdAccessible(String fqName);

}
