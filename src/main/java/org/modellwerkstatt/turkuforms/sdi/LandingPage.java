package org.modellwerkstatt.turkuforms.sdi;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.dataux.runtime.genspecifications.AbstractAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.CmdAction;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import java.util.List;

public class LandingPage extends HorizontalLayout {


    private Div messageDiv;
    private VerticalLayout anchorLayout;

    public LandingPage(String appNameVersion) {
        setSizeFull();
        Peculiar.shrinkSpace(this);
        addClassName("LandingPage");

        anchorLayout = new VerticalLayout();
        Peculiar.shrinkSpace(anchorLayout);
        anchorLayout.setHeightFull();
        add(anchorLayout);


        VerticalLayout right = new VerticalLayout();
        Peculiar.shrinkSpace(right);
        right.setHeightFull();

        Span loginIdentityImage = new Span();
        loginIdentityImage.addClassName("DefaultLoginLogo");

        H1 appName = new H1();
        appName.setText(appNameVersion);

        right.add(loginIdentityImage, appName);
        right.setAlignSelf(Alignment.CENTER, loginIdentityImage);
        right.setAlignSelf(Alignment.CENTER, appName);

        messageDiv = new Div();
        messageDiv.addClassName("TurkuErrorDiv");
        messageDiv.addClassName("DefaultLoginContentWidth");
        right.addComponentAtIndex(0, messageDiv);
        right.setAlignSelf(Alignment.CENTER, messageDiv);

        add(right);
        // right.setJustifyContentMode(JustifyContentMode.CENTER);
    }

    public void setAvailableCommands(List<AbstractAction> menuItemList) {

        for (AbstractAction currentItem : menuItemList) {
            if (currentItem instanceof CmdAction) {
                CmdAction action = (CmdAction) currentItem;
                Anchor anchor = new Anchor(action.uriIdentifier, action.labelText);
                anchorLayout.add(anchor);

            } else if (currentItem.labelText == null) {
                // null is separator; not used yet ...

            } else {
                Label section = new Label(currentItem.labelText);
                anchorLayout.add(section);

                for (AbstractAction inFolder : ((Menu) currentItem).getAllItems()) {
                    if (inFolder instanceof CmdAction) {
                        CmdAction action = (CmdAction) inFolder;

                        Anchor anchor = new Anchor(action.uriIdentifier, action.labelText);
                        anchorLayout.add(anchor);
                    }
                }
            }
        }
    }

    public void setMessage(String msg) {
        messageDiv.setText(msg);
    }

}
