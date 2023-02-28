package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.dataux.runtime.core.ConclusionEvent;
import org.modellwerkstatt.dataux.runtime.core.ICommandContainer;
import org.modellwerkstatt.dataux.runtime.core.KeyEvent;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUI;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.objectflow.runtime.OFXConclusionInformation;
import org.modellwerkstatt.objectflow.runtime.OFXConsoleHelper;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.forms.LeftRight;
import org.modellwerkstatt.turkuforms.util.HkTranslate;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.ArrayList;
import java.util.List;

abstract public class CmdUi extends VerticalLayout implements IToolkit_CommandContainerUI, ShortcutEventListener {
    protected ICommandContainer cmdContainer;
    protected List<Button> conclusionButtons;
    protected LeftRight conclusionLayout;
    protected ITurkuFactory factory;
    protected List<String> globalHotkeysWhennAttached;

    public CmdUi(ITurkuFactory fact) {
        super();
        factory = fact;
        conclusionButtons = new ArrayList<>();
        conclusionLayout = new LeftRight("ConclusionBtns");
    }

    @Override
    public void onShortcut(ShortcutEvent event) {
        try {
            String keyName = HkTranslate.trans(event.getKey());
            Turku.l("CmdUi.onShortcut() received " + keyName + " / from " + event.getLifecycleOwner());
            cmdContainer.receiveAndProcess(new KeyEvent(Defs.hkNeedsCrtl(keyName), keyName));
        } catch (Throwable t) {
            Turku.l("CmdUi.onShortcut() " + OFXConsoleHelper.stackTrace2String(t));
        }
    }

    public void initialShow(IToolkit_Form content) {
        this.add((Component) content, conclusionLayout);
    }

    @Override
    public void setContent(IToolkit_Form formAsComponent) {
        // changing pane content
        Component existing = this.getComponentAt(0);
        this.replace(existing, (Component) formAsComponent);
    }


    @Override
    public void setColor(String s) {
        // TODO: color missing
    }

    @Override
    public void setConclusions(List<OFXConclusionInformation> conclusionInfo, List<String> globalHks) {
        Turku.l("CmdUI.setConclusions()");
        // already optimized, only called for "new" pages, not on reloads of same page
        conclusionLayout.clear();
        conclusionButtons.clear();


        this.globalHotkeysWhennAttached = globalHks;
        for (String hk: globalHotkeysWhennAttached) {
            Workarounds.useGlobalShortcutHk(this, hk, event -> { Turku.l("Hello Shortcut: " + event); });
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
                Workarounds.useButtonShortcutHk(button, oci.hotkey);
            }

            // button.setDisableOnClick(true);
            conclusionLayout.add(button);
            conclusionButtons.add(button);

            if (conclusionButtons.size() == 1) {
                conclusionLayout.spacer();
            }
        }
    }

    @Override
    public void delayedRequestFocus() {
        ((IToolkit_Form) this.getComponentAt(0)).myRequestFocus();
    }

    @Override
    public void delayedAfterFullUiInitialized() {
        ((IToolkit_Form) this.getComponentAt(0)).afterFullUiInitialized();
    }

    @Override
    public void reevalConclusions(List<OFXConclusionInformation> conclusionInformations) {
        for (int i = 0; i < conclusionInformations.size(); i++) {
            conclusionButtons.get(i).setEnabled(conclusionInformations.get(i).enabled);
        }
    }

    @Override
    public void setNotification(String s) {
        Notification.show(s, 3000, Notification.Position.TOP_CENTER);
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
