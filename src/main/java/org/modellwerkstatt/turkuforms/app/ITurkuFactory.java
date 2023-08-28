package org.modellwerkstatt.turkuforms.app;

import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;

public interface ITurkuFactory extends IToolkit_UiFactory {

    void setRedirectAfterLogoutPath(String homePath);

    boolean isCompactMode();

    String getAuthenticatorClassFqName();

    String translateIconName(String name);
    String translateButtonLabel(String label, String hk);

}
