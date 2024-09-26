package org.modellwerkstatt.turkuforms.core;

import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IMoLdapService;
import org.modellwerkstatt.objectflow.runtime.IOFXCmdModule;
import org.modellwerkstatt.turkuforms.auth.ExtAuthProvider;

import java.util.List;

public interface ITurkuAppFactory extends IToolkit_UiFactory {

    void setOnLogoutMainLandingPath(String homePath);
    String getOnLogoutMainLandingPath();

    boolean isCompactMode();

    boolean isCheckDeployedVersion();

    boolean isSingleAppInstanceMode();

    String getAuthenticatorClassFqName();

    String getTurkuAppImplClassFqName();



    IMoLdapService getLdapServiceIfPresent();

    String translateIconName(String name);
    String translateButtonLabel(String label, String hk);

    void setAutoParDeploymentForwardGracefully(boolean val);
    boolean isAutoParDeploymentForwardGracefully();

    String getRemoteAddr();

    void initExtAuthProviders(List<ExtAuthProvider> providers);
    List<ExtAuthProvider> getAllExtAuthProviders();

    void initCmdUrlDefaults(List<IOFXCmdModule.CmdUrlDefaults> cmds);
    List<IOFXCmdModule.CmdUrlDefaults> getAllCmdUrlDefaults();

    boolean cmdAccessible(String fqName);

}
