package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.dataux.runtime.core.ConclusionEvent;
import org.modellwerkstatt.dataux.runtime.core.ICommandContainer;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_CommandContainerUI;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.objectflow.runtime.OFXConclusionInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.util.LeftRight;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.ArrayList;
import java.util.List;

abstract public class CmdUi extends VerticalLayout implements IToolkit_CommandContainerUI {
    protected ICommandContainer cmdContainer;
    protected List<Button> conclusionButtons;
    protected LeftRight conclusionLayout;
    protected ITurkuFactory factory;

    public CmdUi(ITurkuFactory fact) {
        factory = fact;
        conclusionButtons = new ArrayList<>();
        conclusionLayout = new LeftRight("ConclusionBtns");
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
        // already optimized, only called for "new" pages, not on reloads of same page
        conclusionLayout.clear();
        conclusionButtons.clear();

        List<String> conclusionHks = new ArrayList<String>();

        for (OFXConclusionInformation oci : conclusionInfo) {
            if (Workarounds.hasHk(oci.hotkey)) {
                oci.buttonTitle = factory.translateButtonLabel(oci.buttonTitle, oci.hotkey);
                conclusionHks.add(oci.hotkey);
            }

            Button button;
            if (Workarounds.hasIcon(oci.iconName)) {
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

            button.setDisableOnClick(true);
            conclusionLayout.add(button);
            conclusionButtons.add(button);


            if (conclusionButtons.size() == 1) { conclusionLayout.spacer(); }
        }

        // TODO: register globalHks and conclusionHks
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
