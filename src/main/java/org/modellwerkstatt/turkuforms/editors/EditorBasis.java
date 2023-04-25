package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import org.modellwerkstatt.dataux.runtime.extensions.IDataUxDelegate;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;
import org.modellwerkstatt.objectflow.runtime.OFXConclusionInformation;
import org.modellwerkstatt.objectflow.runtime.OFXConsoleHelper;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.forms.TurkuDelegatesForm;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

abstract public class EditorBasis<T extends Component & HasValidation & HasEnabled & Focusable<?>> extends FormChild<T> {

    protected boolean issueUpdateEnabled = false;
    protected Button updateConclusionButton;
    protected String lastIssuedUpdateText = null;

    protected boolean cachedEnabledState = true;
    protected String cachedValue = null;


    public EditorBasis(T theField) {
        super(theField);
        // label.setFor(inputField)
    }

    public void setValidationErrorText(String text) {
        if (Defs.hasValidationErrorText(text)) {
            inputField.setErrorMessage(text);
            inputField.setInvalid(true);
        } else{
            inputField.setErrorMessage("");
            inputField.setInvalid(false);
        }
    }

    public void setEnabled(boolean b) {
        // since setEnabled is called oftentimes, unclear if that does impair vaadin performance
        if (cachedEnabledState != b) {
            cachedEnabledState = b;
            if (updateConclusionButton!= null) { updateConclusionButton.setEnabled(b); }
            inputField.setEnabled(b);
        }
    }

    public void setEditorPrompt(String s) { inputField.getElement().setProperty("placeholder", s);}

    public void newObjectBound() {
        lastIssuedUpdateText = null;
    }

    public boolean execUpdateConclusion(String newValue) {
        // default implementation
        Turku.l("EditorBasis.execUpdateConclusion() [" + issueUpdateEnabled + "] " + lastIssuedUpdateText + " -> " + newValue + " for " + this);
        /* if (newValue == null) {
            Turku.l(OFXConsoleHelper._____organizeCurrentStacktrace_____());
        } */

        if (cachedEnabledState && issueUpdateEnabled) {
            if (!SaveObjectComperator.equals(lastIssuedUpdateText, newValue)) {
                lastIssuedUpdateText = newValue;
                issueUpdateEnabled = false;
                delegate.issueUpdateConclusionAfterContentChange();
                issueUpdateEnabled = true;
                return true;
            }
        }

        return false;
    }

    public void updateConclusionButtonClicked() {
        // it might be necessary to overwrite the button click
        execUpdateConclusion(this.getText());
    }

    public void setIssuesUpdateConclusion() {

        HorizontalLayout hl = new HorizontalLayout();
        Peculiar.shrinkSpace(hl);

        updateConclusionButton = new Button(
                Workarounds.createIconWithCollection("refresh"),
                event -> { updateConclusionButtonClicked(); });
        updateConclusionButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        updateConclusionButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        hl.add(inputField, updateConclusionButton);
        hl.setFlexGrow(1.0, inputField);
        hl.setFlexGrow(0.0, updateConclusionButton);

        rightPart = hl;
        issueUpdateEnabled = true;
    }


    public void setOption(IToolkit_TextEditor.Option... options) {
    }

    @Override
    public void turkuFocus() { inputField.focus(); }
}
