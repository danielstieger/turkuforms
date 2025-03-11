package org.modellwerkstatt.turkuforms.core;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.dataux.runtime.core.BaseUiFactory;
import org.modellwerkstatt.dataux.runtime.sdicore.CheckerForCmdUrlDefaults;
import org.modellwerkstatt.dataux.runtime.toolkit.*;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IMoLdapService;
import org.modellwerkstatt.objectflow.runtime.IOFXCmdModule;
import org.modellwerkstatt.turkuforms.auth.ExtAuthProvider;
import org.modellwerkstatt.turkuforms.editors.*;
import org.modellwerkstatt.turkuforms.forms.*;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.views.CmdUiPrompt;
import org.modellwerkstatt.turkuforms.views.CmdUiTab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurkuAppFactory extends BaseUiFactory implements ITurkuAppFactory {
    public final static String DEFAULT_AUTHENTICATOR = "org.modellwerkstatt.turkuforms.authmpreis.IPAuthLandingPage";
    public final static String DEFAULT_TURKUAPPIMPL = "org.modellwerkstatt.turkuforms.core.TurkuApp";

    private VaadinIconTranslator iconTranslator;

    /* onTheFly settings can be accessed statically in the app (factory instance not available?) */
    public static boolean onTheFly_allowEuroSignInDelegates = false;

    private boolean compactMode = false;
    private boolean singleAppInstanceMode = false;

    private boolean deployedVersionCheck = true;
    private String onLogoutMainLandingPath;
    private String authentiactorClassFqName;
    private String turkuAppImplClassFqName;


    private Map<String,IOFXCmdModule.CmdUrlDefaults> defaultUrlForFqCmd;
    private boolean usingUrlHandling;

    private List<ExtAuthProvider> allAuthProviders;


    // Empty, app has to handle stuff, i.e. empty = return full path
    // for uploadLocations

    public TurkuAppFactory() {
        super(MoWareTranslations.TranslationSelection.MAIN_TRANSLATIONS);

        iconTranslator = new MaterialIconsTranslator();
        authentiactorClassFqName = DEFAULT_AUTHENTICATOR;
        turkuAppImplClassFqName = DEFAULT_TURKUAPPIMPL;
        usingUrlHandling = false;

        // should be initialized in servlet
        onLogoutMainLandingPath = null;
    }


    public String getRemoteAddr() {
        // can not use the getRemoteAddr() from IRemoteIp, since we are on websockets.
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
    public boolean isSingleAppInstanceMode() { return singleAppInstanceMode; }
    public void setSingleAppInstanceMode(boolean val) { singleAppInstanceMode = val; }

    @Override
    public String getAuthenticatorClassFqName() {
        return authentiactorClassFqName;
    }

    public void setAuthenticatorClassFqName(String fqName) {
        authentiactorClassFqName = fqName;
    }

    @Override
    public String getTurkuAppImplClassFqName() {
        return turkuAppImplClassFqName;
    }

    public void setTurkuAppImplClassFqName(String fqName) {
        turkuAppImplClassFqName = fqName;
        usingUrlHandling = true;
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
    public IToolkit_FormContainer<?> createToolkitFormContainer(boolean flex) {
        if (flex) {
            return new SliderLayout<>(this);
        } else {
            return new TurkuGridLayout<>(this);

        }
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
            return new DatePickerEditor(createGermanI18n());
        } else {
            return new TextEditor(TextEditor.ConfigOption.FOR_LOCALDATE);
        }

    }

    @Override
    public IToolkit_DateOrTimeEditor createDateAndTimeEditor(boolean withPicker) {
        if (withPicker) {
            return new DateTimePickerEditor(createGermanI18n());
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

    @Override
    public void initCmdUrlDefaults() {
        super.initCmdUrlDefaults();

        defaultUrlForFqCmd = new HashMap<>();
        for (IOFXCmdModule.CmdUrlDefaults it: allUrlDefaults) {
            defaultUrlForFqCmd.put(it.cmdFqName, it);
        }
    }

    @Override
    public boolean cmdAccessible(String fqName) {
        if (! usingUrlHandling) { return true; };

        return defaultUrlForFqCmd.containsKey(fqName);
    }

    @Override
    public void initExtAuthProviders(List<ExtAuthProvider> providers) {
        allAuthProviders = providers;
    }

    @Override
    public List<ExtAuthProvider> getAllExtAuthProviders() {
        return allAuthProviders;
    }

    public static DatePicker.DatePickerI18n createGermanI18n() {
        DatePicker.DatePickerI18n dpI18n = new DatePicker.DatePickerI18n();
        dpI18n.setDateFormat("dd.MM.yyyy");
        dpI18n.setFirstDayOfWeek(1);
        dpI18n.setToday("Heute");
        dpI18n.setCancel("Abbrechen");
        dpI18n.setMonthNames(List.of("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli",
                "August", "September", "Oktober", "November", "Dezember"));
        dpI18n.setWeekdays(List.of("Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"));
        dpI18n.setWeekdaysShort(List.of("So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"));
        return dpI18n;
    }
}
