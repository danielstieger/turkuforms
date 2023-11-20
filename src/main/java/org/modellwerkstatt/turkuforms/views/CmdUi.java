package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.dataux.runtime.core.ConclusionEvent;
import org.modellwerkstatt.dataux.runtime.core.ICommandContainer;
import org.modellwerkstatt.dataux.runtime.core.KeyEvent;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUI;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.objectflow.runtime.OFXConclusionInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.forms.LeftRight;
import org.modellwerkstatt.turkuforms.util.*;

import java.util.ArrayList;
import java.util.List;

abstract public class CmdUi extends VerticalLayout implements IToolkit_CommandContainerUI, ShortcutEventListener {
    protected ICommandContainer cmdContainer;
    protected LeftRight conclusionLayout;
    protected ITurkuAppFactory factory;
    protected List<OFXConclusionInformation> conclusionInformations;
    protected List<Button> conclusionButtons;
    protected IToolkit_Form currentFormToFocus;
    protected String color;


    public CmdUi(ITurkuAppFactory fact) {
        super();
        factory = fact;
        conclusionButtons = new ArrayList<>();
        conclusionLayout = new LeftRight("BottomConclusionButtons");
    }

    @Override
    public void onShortcut(ShortcutEvent event) {
        String keyName = HkTranslate.trans(event.getKey());
        Turku.l("CmdUi.onShortcut() received " + keyName + " hash " + event.hashCode());
        // Turku.l("CmdUi.onShortcut() " + event.getSource().getElement() + "/" + event.getSource());

        if (Workarounds.sameHkInThisRequest(keyName)) {
            return;
        }

        // check conclusion first
        for (OFXConclusionInformation info: conclusionInformations) {
            if (keyName.equals(info.hotkey)) {
                if (info.enabled) {
                    cmdContainer.receiveAndProcess(new ConclusionEvent(info.conclusionHashCode, info.buttonTitle));
                } else {
                    Turku.l("CmdUi.onShortcut() conclusion '" + info.buttonTitle + "' received, but not enabled.");
                }
                return;
            }
        }

        // early returns above :(
        cmdContainer.receiveAndProcess(new KeyEvent(Defs.hkNeedsCrtl(keyName), keyName));
    }

    public void initialShow(IToolkit_Form formAsComponent) {
        currentFormToFocus = formAsComponent;
        currentFormToFocus.setHLevel(0,0);

        this.add((Component) currentFormToFocus, conclusionLayout);
    }

    @Override
    public void setContent(IToolkit_Form formAsComponent) {
        // changing pane content
        currentFormToFocus = formAsComponent;
        currentFormToFocus.setHLevel(0,0);

        Component existing = this.getComponentAt(0);
        this.replace(existing, (Component) currentFormToFocus);
    }


    @Override
    public void setColor(String col) {
        // in form of #AABBCC or transparent
        color = col;
        /* getElement().getStyle().set("border-top", "4px solid " + color); */
    }

    @Override
    public void setConclusions(List<OFXConclusionInformation> conclusionInfo, List<String> globalHks) {
        // already optimized, only called for "new" pages, not on reloads of same page
        conclusionInformations = conclusionInfo;
        conclusionLayout.clear();
        conclusionButtons.clear();

        for (String hk: globalHks) {
            Peculiar.useGlobalShortcutHk(this, hk, this );
        }

        for (OFXConclusionInformation oci : conclusionInfo) {

            if (Defs.needsHkRegistration(oci.hotkey)) {
                oci.buttonTitle = factory.translateButtonLabel(oci.buttonTitle, oci.hotkey);
            }

            Button button;
            if (Defs.hasIcon(oci.iconName)) {
                button = new Button(oci.buttonTitle, Workarounds.createIconWithCollection(factory.translateIconName(oci.iconName)), event -> {
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
                Peculiar.useGlobalShortcutHk(this, oci.hotkey,this);
            }

            // button.setDisableOnClick(true);
            conclusionLayout.add(button);
            conclusionButtons.add(button);

            if (conclusionButtons.size() == 1) {
                conclusionLayout.spacer();
            }
            if (conclusionButtons.size() > 1) {
                button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            }
        }
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
            conclusionButtons.get(i).setEnabled(concInfos.get(i).enabled);
        }
    }

    @Override
    public void setNotification(String s) {

        Div div = new Div(new Text(s));
        div.addClassName("TabLockingMessage");

        Button closeButton = new Button(Workarounds.createIconWithCollection("close"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");

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

    public String getColorOrNull() { return color; }
}
