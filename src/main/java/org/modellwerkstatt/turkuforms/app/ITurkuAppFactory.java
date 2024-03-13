package org.modellwerkstatt.turkuforms.app;

import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IMoLdapService;

import java.time.LocalDateTime;
import java.time.LocalTime;

public interface ITurkuAppFactory extends IToolkit_UiFactory {

    void setOnLogoutMainLandingPath(String homePath);
    String getOnLogoutMainLandingPath();

    boolean isCompactMode();
    boolean isCheckDeployedVersion();

    boolean isSingleAppInstanceMode();

    LocalTime getKillAppsAfter();
    void setKillAppsAfter(int hhmm);

    String getAuthenticatorClassFqName();

    IMoLdapService getLdapServiceIfPresent();

    String translateIconName(String name);
    String translateButtonLabel(String label, String hk);

}
