package org.modellwerkstatt.turkuforms.core;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
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
    public final static String DEFAULT_AUTHENTICATOR = "org.modellwerkstatt.turkuforms.authmpreis.IPAuthLandingPage";
    public final static String DEFAULT_AUTHENTICATOR_SDI = "org.modellwerkstatt.turkuforms.authmpreis.LdapAuthLandingPageSDI";


    private VaadinIconTranslator iconTranslator;

    /* onTheFly settings can be accessed statically in the app (factory instance not available?) */
    public static boolean onTheFly_allowEuroSignInDelegates = false;

    private boolean compactMode = false;
    private boolean singleAppInstanceMode = false;

    private boolean deployedVersionCheck = true;
    private boolean autoParDeploymentForwardGracefully = false;
    private String onLogoutMainLandingPath;
    private String authentiactorClassFqName;


    // Empty, app has to handle stuff, i.e. empty = return full path
    // for uploadLocations

    public TurkuAppFactory() {
        super(MoWareTranslations.TranslationSelection.MAIN_TRANSLATIONS);

        iconTranslator = new MaterialIconsTranslator();
        authentiactorClassFqName = DEFAULT_AUTHENTICATOR;

        // should be initialized in servlet
        onLogoutMainLandingPath = null;
    }

    @Override
    public String getRemoteAddr() {
        WrappedSession session = VaadinSession.getCurrent().getSession();
        String address = (String) session.getAttribute("x-forwarded-for");

        if (address == null) {
            address = VaadinRequest.getCurrent().getRemoteAddr();
        }

        return address;
    }

    public String getOnLogoutMainLandingPath() {
        return onLogoutMainLandingPath;
    }
    public void setOnLogoutMainLandingPath(String path) {
        onLogoutMainLandingPath = path;
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
    public void setSDIMode(boolean val) {
        super.setSDIMode(val);
        // bullshit, not set to false anyway.
        authentiactorClassFqName = DEFAULT_AUTHENTICATOR_SDI;
    }


    @Override
    public boolean isSingleAppInstanceMode() { return singleAppInstanceMode; }
    public void setSingleAppInstanceMode(boolean val) { singleAppInstanceMode = val; }

    @Override
    public boolean isCheckDeployedVersion() {
        return deployedVersionCheck;
    }
    public void setCheckDeployedVersion(boolean val) { deployedVersionCheck = val; }

    @Override
    public void setAutoParDeploymentForwardGracefully(boolean val) {
        autoParDeploymentForwardGracefully = val;
    }

    @Override
    public boolean isAutoParDeploymentForwardGracefully() {
        return autoParDeploymentForwardGracefully;
    }

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
    public IToolkit_CommandContainerUi createTabContainerUi(IToolkit_MainWindow iToolkit_application, boolean modal) {
        return new CmdUiTab(this, modal);
    }

    @Override
    public IToolkit_CommandContainerUi createPromptContainerUi(IToolkit_MainWindow iToolkit_application, boolean fullSize) {
        return new CmdUiPrompt(this, fullSize);
    }

    @Override
    public IToolkit_TextEditor createTextEditor() {
        return new TextEditor(TextEditor.ConfigOption.NONE);
    }

    @Override
    public IToolkit_TextEditor createDummyEditor() {
        return new DummyEditor();
    }

    @Override
    public IToolkit_ReferenceEditor createReferenceEditor(boolean alter) {
        return new ReferenceEditor();
    }

    @Override
    public IToolkit_StatusEditor createStatusEditor(boolean alter) {
        if (alter) {
            return new RadioStatusEditor();

        } else {
            return new StatusEditor();
        }
    }

    @Override
    public IToolkit_DateOrTimeEditor createDateEditor(boolean withPicker) {
        if (withPicker) {
            return new DatePickerEditor();
        } else {
            return new TextEditor(TextEditor.ConfigOption.FOR_LOCALDATE);
        }

    }

    @Override
    public IToolkit_DateOrTimeEditor createDateAndTimeEditor(boolean withPicker) {
        if (withPicker) {
            return new DateTimePickerEditor();
        } else {
            return new TextEditor(TextEditor.ConfigOption.FOR_DATETIME);
        }

    }

    @Override
    public IToolkit_ImageEditor createImageEditor() {
        return new ImageViewer(uploadLocationRetrieve);
    }

    @Override
    public IToolkit_TextEditor createTextAreaEditor(int i) {
        return new TextAreaEditor(i);
    }

    @Override
    public IToolkit_UploadEditor createUploadEditor() {
        return new UploadEditor(uploadFsLocationStore, this);
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
