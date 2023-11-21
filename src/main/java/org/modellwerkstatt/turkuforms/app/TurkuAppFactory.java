package org.modellwerkstatt.turkuforms.app;

import org.modellwerkstatt.dataux.runtime.core.BaseUiFactory;
import org.modellwerkstatt.dataux.runtime.toolkit.*;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IMoLdapService;
import org.modellwerkstatt.turkuforms.editors.*;
import org.modellwerkstatt.turkuforms.forms.TurkuDelegatesForm;
import org.modellwerkstatt.turkuforms.forms.TurkuGridLayout;
import org.modellwerkstatt.turkuforms.forms.TurkuTabForm;
import org.modellwerkstatt.turkuforms.forms.TurkuTable;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.views.CmdUiPrompt;
import org.modellwerkstatt.turkuforms.views.CmdUiTab;

public class TurkuAppFactory extends BaseUiFactory implements ITurkuAppFactory {
    public final static String DEFAULT_AUTHENTICATOR = "org.modellwerkstatt.turkuforms.auth.SimpleIPAuthenticator";

    private VaadinIconTranslator iconTranslator;

    /* onTheFly settings can be accessed statically in the app (factory instance not available?) */
    public static boolean onTheFly_allowEuroSignInDelegates = false;

    private boolean compactMode = true;
    private boolean deployedVersionCheck = true;
    private String redirectAfterLogoutPath;
    private String authentiactorClassFqName;

    public TurkuAppFactory() {
        super(MoWareTranslations.TranslationSelection.MAIN_TRANSLATIONS);

        iconTranslator = new MaterialIconsTranslator();
        redirectAfterLogoutPath = "(ups not initialized!)";
        authentiactorClassFqName = DEFAULT_AUTHENTICATOR;
    }
    public String getRedirectAfterLogoutPath() {
        return redirectAfterLogoutPath;
    }
    public void setRedirectAfterLogoutPath(String path) {
        redirectAfterLogoutPath = path;
    }

    public void setAllowEuroSign(boolean val) {
        onTheFly_allowEuroSignInDelegates = val;
    }
    public boolean getAllowEuroSign() {
        return onTheFly_allowEuroSignInDelegates;
    }

    @Override
    public boolean isCompactMode() { return compactMode; }
    public void setCompactMode(boolean val) { compactMode = val; }

    @Override
    public boolean isCheckDeployedVersion() {
        return deployedVersionCheck;
    }
    public void setCheckDeployedVersion(boolean val) { deployedVersionCheck = val; }

    @Override
    public String getAuthenticatorClassFqName() {
        return authentiactorClassFqName;
    }

    public void setAuthenticatorName(String fqName) {
        authentiactorClassFqName = fqName;
    }

    @Override
    public boolean useBackgroundThread() {
        return false;
    }

    @Override
    public boolean flagValidationAdditionally() {
        return true;
    }

    @Override
    public IToolkit_FormContainer<?> createToolkitFormContainer() {
        return new TurkuGridLayout(this);
    }

    @Override
    public IToolkit_TabForm<?> createToolkitTabForm() {
        return new TurkuTabForm();
    }

    @Override
    public IToolkit_TableForm<?> createToolkitTableForm() {
        return new TurkuTable(this);
    }

    @Override
    public IToolkit_DelegateForm<?> createToolkitDelegateForm() {
        return new TurkuDelegatesForm<>(this);
    }

    @Override
    public IToolkit_CommandContainerUI createTabContainerUi(IToolkit_Application iToolkit_application, boolean modal) {
        return new CmdUiTab(this, modal);
    }

    @Override
    public IToolkit_CommandContainerUI createPromptContainerUi(IToolkit_Application iToolkit_application, boolean fullSize) {
        return new CmdUiPrompt(this, fullSize);
    }

    @Override
    public IToolkit_TextEditor createTextEditor() {
        return new TextEditor(false);
    }

    @Override
    public IToolkit_TextEditor createDummyEditor() {
        return new DummyEditor();
    }

    @Override
    public IToolkit_ReferenceEditor createReferenceEditor() {
        return new ReferenceEditor();
    }

    @Override
    public IToolkit_StatusEditor createStatusEditor() {
        return new StatusEditor();
    }

    @Override
    public IToolkit_DateOrTimeEditor createDateEditor(boolean withPicker) {
        if (withPicker) {
            return new DatePickerEditor();
        } else {
            return new TextEditor(true);
        }

    }

    @Override
    public IToolkit_DateOrTimeEditor createDateAndTimeEditor() {
        return new DateTimeEditor();
    }

    @Override
    public IToolkit_ImageEditor createImageEditor() {
        return new ImageViewer();
    }

    @Override
    public IToolkit_TextEditor createTextAreaEditor(int i) {
        return new TextAreaEditor();
    }

    @Override
    public IToolkit_UploadEditor createUploadEditor() {
        throw new RuntimeException("Not implemented yet.");
    }

    public IMoLdapService getLdapServiceIfPresent() {
        IMoLdapService instance = this.context.getAutowireCapableBeanFactory().getBean(IMoLdapService.class);
        return instance;
    }

    @Override
    public String translateIconName(String name) {
        return iconTranslator.translate(name);
    }

    @Override
    public String translateButtonLabel(String label, String hk) {
        String fullLabel = label;
        if (Defs.needsHkRegistration(hk)) {
            fullLabel += Defs.hkNeedsCrtl(hk) ? " (CRTL-" + hk + ")" : " (" + hk + ")";
        }

        return fullLabel;
    }
}
