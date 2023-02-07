package org.modellwerkstatt.turkuforms.app;

import org.modellwerkstatt.dataux.runtime.core.BaseUiFactory;
import org.modellwerkstatt.dataux.runtime.toolkit.*;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;

public class TurkuFactory extends BaseUiFactory implements ITurkuFactory {
    public static String DEFAULT_REDIRECT_TO_AFTER_LOGOUT = ".";
    private String redirectAfterLogoutPath;
    public static boolean allowEuroSignInDelegates = false;


    public TurkuFactory() {
        super(MoWareTranslations.TranslationSelection.V_TRANSLATIONS);
        redirectAfterLogoutPath = DEFAULT_REDIRECT_TO_AFTER_LOGOUT;
    }
    public String getRedirectAfterLogoutPath() {
        return redirectAfterLogoutPath;
    }
    public void setRedirectAfterLogoutPath(String path) {
        redirectAfterLogoutPath = path;
    }

    public void setAllowEuroSign(boolean val) {
        allowEuroSignInDelegates = val;
    }
    public boolean getAllowEuroSign() {
        return allowEuroSignInDelegates;
    }

    @Override
    public boolean useBackgroundThread() {
        return false;
    }

    @Override
    public boolean flagValidationAdditionally() {
        return false;
    }

    @Override
    public IToolkit_FormContainer<?> createToolkitFormContainer() {
        return null;
    }

    @Override
    public IToolkit_TabForm<?> createToolkitTabForm() {
        return null;
    }

    @Override
    public IToolkit_TableForm<?> createToolkitTableForm() {
        return null;
    }

    @Override
    public IToolkit_DelegateForm<?> createToolkitDelegateForm() {
        return null;
    }

    @Override
    public IToolkit_CommandContainerUI createTabContainerUi(IToolkit_Application iToolkit_application, boolean b) {
        return null;
    }

    @Override
    public IToolkit_CommandContainerUI createPromptContainerUi(IToolkit_Application iToolkit_application, boolean b) {
        return null;
    }

    @Override
    public IToolkit_TextEditor createTextEditor() {
        return null;
    }

    @Override
    public IToolkit_TextEditor createDummyEditor() {
        return null;
    }

    @Override
    public IToolkit_ReferenceEditor createReferenceEditor() {
        return null;
    }

    @Override
    public IToolkit_StatusEditor createStatusEditor() {
        return null;
    }

    @Override
    public IToolkit_DateOrTimeEditor createDateEditor(boolean b) {
        return null;
    }

    @Override
    public IToolkit_DateOrTimeEditor createDateAndTimeEditor() {
        return null;
    }

    @Override
    public IToolkit_ImageEditor createImageEditor() {
        return null;
    }

    @Override
    public IToolkit_TextEditor createTextAreaEditor(int i) {
        return null;
    }
}
