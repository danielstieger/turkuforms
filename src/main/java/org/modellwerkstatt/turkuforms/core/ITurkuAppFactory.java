package org.modellwerkstatt.turkuforms.core;

import org.modellwerkstatt.dataux.runtime.auth.IExtAuthProvider;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IMoLdapService;
import org.modellwerkstatt.objectflow.runtime.IOFXCmdModule;

import java.math.BigDecimal;
import java.util.List;

public interface ITurkuAppFactory extends IToolkit_UiFactory {

    String TURKU_PORTJ = "org.modellwerkstatt.turkuforms";

    void setOnLogoutMainLandingPath(String homePath);
    String getOnLogoutMainLandingPath();

    boolean isCompactMode();

    boolean isSingleAppInstanceMode();

    String getAuthenticatorClassFqName();

    IMoLdapService getLdapServiceIfPresent();

    String translateIconName(String name);
    String translateButtonLabel(String label, String hk);

    String getRemoteAddr();

    void initExtAuthProviders();
    List<IExtAuthProvider> getAllExtAuthProviders();

    void initCmdUrlDefaults(boolean usngUrlHndlng);
    List<IOFXCmdModule.CmdUrlDefaults> getAllCmdUrlDefaults();

    boolean cmdAccessible(String fqName);

    boolean isUseMinimalDelegateFormLabelWidth();
    BigDecimal getDefaultScaling();
    Integer getHideTableSearchWhenBelow();
    boolean isTableColumnGrowTo100();
}
