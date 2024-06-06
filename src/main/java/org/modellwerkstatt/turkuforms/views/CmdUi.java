package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ShortcutEvent;
import com.vaadin.flow.component.ShortcutEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.dataux.runtime.core.ConclusionEvent;
import org.modellwerkstatt.dataux.runtime.core.ICommandContainer;
import org.modellwerkstatt.dataux.runtime.core.KeyEvent;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUi;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.objectflow.runtime.OFXConclusionInformation;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.forms.LeftRight;
import org.modellwerkstatt.turkuforms.util.*;

import java.util.ArrayList;
import java.util.List;

abstract public class CmdUi extends VerticalLayout implements IToolkit_CommandContainerUi, ShortcutEventListener {
    protected ICommandContainer cmdContainer;
    protected LeftRight conclusionLayout;
    protected ITurkuAppFactory factory;
    protected List<OFXConclusionInformation> conclusionInformations;
    protected List<Button> conclusionButtons;
    protected IToolkit_Form currentFormToFocus;
    protected String color;
    protected HotKeyPreventer recorder;

    public CmdUi(ITurkuAppFactory fact) {
        super();
        factory = fact;
        conclusionButtons = new ArrayList<>();
        conclusionLayout = new LeftRight("BottomConclusionButtons");
        recorder = new HotKeyPreventer();
    }

    @Override
    public void onShortcut(ShortcutEvent event) {

        String keyName = HkTranslate.trans(event.getKey());
        Turku.l(getClass().getSimpleName() + ".onShortcut() received " + keyName);

        // e.g. ESC problem, closing multiple GO s from Search
        if (Workarounds.sameHkInThisRequest(keyName)) { return; }

        // no longer necessary, Dan Feb, 2024
        OFXConclusionInformation conclusion = conclusionInformations.stream().filter(e -> keyName.equals(e.hotkey)).findFirst().orElse(null);

        if (conclusion != null && conclusion.enabled) {
            cmdContainer.receiveAndProcess(new ConclusionEvent(conclusion.conclusionHashCode, conclusion.buttonTitle));

        } else if (conclusion != null){
            // this is okay, conclusion disabled, but hk still active for the cmdUI

        } else {
            cmdContainer.receiveAndProcess(new KeyEvent(Defs.hkNeedsCrtl(keyName), keyName));

        }

    }

    public void initialShow(IToolkit_Form formAsComponent) {
        currentFormToFocus = formAsComponent;
        this.add((Component) currentFormToFocus, conclusionLayout);
    }

    @Override
    public void setContent(IToolkit_Form formAsComponent) {
        // changing pane content
        currentFormToFocus = formAsComponent;
        Component existing = this.getComponentAt(0);
        this.replace(existing, (Component) currentFormToFocus);
    }


    @Override
    public void setColor(String col) {
        // in form of #AABBCC or transparent
        color = col;
        getElement().executeJs("turku.setTurkuCommandColor($0, $1)", this.getElement(), color);
    }

    @Override
    public void setConclusions(List<OFXConclusionInformation> conclusionInfo, List<String> globalHks) {
        // already optimized, only called for "new" pages, not on reloads of same page
        conclusionInformations = conclusionInfo;
        conclusionLayout.clear();
        conclusionButtons.clear();

        recorder.invalidateAllRegistrations();

        for (String hk: globalHks) {
            recorder.remove(Peculiar.useGlobalShortcutHk(this, hk, this ));
        }


        for (int i = 0; i < conclusionInfo.size(); i++) {
            OFXConclusionInformation oci = conclusionInfo.get(i);

            if (Defs.needsHkRegistration(oci.hotkey)) {
                oci.buttonTitle = factory.translateButtonLabel(oci.buttonTitle, oci.hotkey);
            }

            Button button;
            if (Defs.hasIcon(oci.iconName)) {
                button = new Button(oci.buttonTitle, Workarounds.createIconWithCollection(factory.translateIconName(oci.iconName), false), event -> {
                    cmdContainer.receiveAndProcess(new ConclusionEvent(oci.conclusionHashCode, oci.buttonTitle));

                });
            } else {
                button = new Button(oci.buttonTitle, event -> {
                    cmdContainer.receiveAndProcess(new ConclusionEvent(oci.conclusionHashCode, oci.buttonTitle));

                });
            }

            if ("UPD".equals(oci.hotkey)) {
                button.setVisible(false);
            }

            if (Defs.needsHkRegistration(oci.hotkey)) {
                recorder.remove(Peculiar.useGlobalShortcutHk(this, oci.hotkey,this));
            }

            // button.setDisableOnClick(true);
            conclusionLayout.add(button);
            conclusionButtons.add(button);

            if (i == 0) {
                // first button is ESC
                button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                button.addClassName("TurkuLumoTertiary");
                conclusionLayout.spacer();

            } else if (i == (conclusionInfo.size() -1)) {
                // last button to the right is the primary
                button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                button.addClassName("TurkuLumoPrimary");

            } else {
                button.addClassName("TurkuLumoSecondary");
            }

        }

        recorder.registerRemaining(this);
    }

    @Override
    public void delayedRequestFocus() {
        currentFormToFocus.myRequestFocus();
    }

    @Override
    public void delayedAfterFullUiInitialized() {  currentFormToFocus.afterFullUiInitialized(); }

    @Override
    public void reevalConclusions(List<OFXConclusionInformation> concInfos) {
        conclusionInformations = concInfos;
        for (int i = 0; i < concInfos.size(); i++) {
            if (i == 0 && !concInfos.get(i).enabled) {
                // this is the ESC
                conclusionButtons.get(i).setVisible(false);

            } else {
                conclusionButtons.get(i).setEnabled(concInfos.get(i).enabled);

            }
        }
    }

    @Override
    public void setNotification(String s) {
        Div div = new Div(new Text(s));
        div.addClassName("TabLockingMessage");

        Button closeButton = new Button(Workarounds.createIconWithCollection("close", false));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClassName("TabLockingButton");

        HorizontalLayout lyt = new HorizontalLayout();
        Peculiar.shrinkSpace(lyt);
        lyt.add(div, closeButton);

        closeButton.addClickListener(event -> {
            this.remove(lyt);
        });
        this.addComponentAtIndex(0, lyt);
    }

    @Override
    public boolean isTabWindow() {
        return this instanceof CmdUiTab;
    }

    @Override
    public void close() {
        // needs no implementation for tabs
        factory = null;
        cmdContainer = null;
        currentFormToFocus = null;
        conclusionButtons.clear();
    }

    @Override
    public boolean isModalTabWindow() {
        return false;
    }

    @Override
    public void setCommandContainer(ICommandContainer iCommandContainer) {
        cmdContainer = iCommandContainer;
    }
}
