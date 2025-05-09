/*
 * Turkuforms modellwerkstatt.org
 * Stieger Dan, Winter 22/23
 *
 * Custom vaadin JS for moware turkuforms
 */

window.turku = {

    adjustDateTimeTimePicker : function(element) {
        var picker = element.querySelector('vaadin-date-time-picker-time-picker');
        // picker.style.border = "1px solid red";
        var container = picker.shadowRoot.querySelector('.vaadin-time-picker-container');
        container.style.width="20%";
    },

    init : function(view) {
        // init not used right now, replaced by
        window.onfocus = event => view.$server.turkuOnWindowFocusEvent();
        window.beforeunload = event => view.$server.turkuOnWindowUnload();
        console.log('TurkuEventListeners registered for ' + view);
    },

    disableBrowserContextMenu : function() {
        window.addEventListener("contextmenu", function(e) { e.preventDefault(); });
    },

    mainWindowOnAttach : function() {
        console.log("mainWindowOnAttach()");
        document.querySelectorAll('.MainwindowTileButton').forEach((element) => element.shadowRoot.querySelector('.vaadin-button-container').style="padding: 1em; align-items: start; justify-content: left" );
    },

    copyToClipboard : async function(view, text) {

        // this is only working when serving over https
        try {
            await navigator.clipboard.writeText(text);
          } catch (err) {
            prompt('Can not paste csv to clipboard. Is this site served via https?', '' + err);
          }
    },

    selectAllOnChildInput: function(cmpt) {
        cmpt.querySelector("input").select();
    },

    replaceEuroSign: function(cmpt) {
        var theInput = cmpt.querySelector("input");

        theInput.addEventListener('keypress', function (e) {
            if (e.key == '€') {
                e.stopPropagation();
                e.preventDefault();

                var atPos = e.target.selectionStart;
                var content = e.target.value;
                e.target.value = content.slice(0, atPos) + 'EUR' + content.slice(atPos);

                atPos += 3;
                e.target.setSelectionRange(atPos, atPos);
            }
        });
    },

    delayedOpenReferenceEditor: function(refEdit) {
        setTimeout(() => {
          refEdit.open();
        }, 200);
    },

    installBeacon: function(servletUrl, uiid) {
        // bei Minimierung?
        // statt visibilitychange on document

        window.addEventListener("pagehide", function sendRequest(event) {

          console.log('turku.installBeacon() Issuing request NOW, persisted ' + event.persisted);
          navigator.sendBeacon(  servletUrl + "/beacon", uiid);

        });

        console.log('turku.installBeacon() Beacon installed for ' + servletUrl + " and ui " + uiid);
    },

    installFocusHandler: function(view) {

        window.addEventListener("focus", function sendFocus(event) {
            if (document.hasFocus()) {
                view.$server.browserTabFocusReceived();
            }
        });
        console.log('truku.installFocusHandler() installed the focus handler.');
    },

    closeConfirmHandler: function(ev) {
        // console.log('turku.closeConfirmHandler() called .. ' + window.Vaadin.connectionState.connectionState);

        if (window.Vaadin.connectionState.connectionState == 'connected') {
            ev.preventDefault();
            // Chrome requires returnValue to be set
            ev.returnValue = 'There are unsaved changes!';
        }
    },

    installCloseConfirm: function(installOrRemove) {

        console.log('turku.installCloseConfirm() installing (' + installOrRemove + ") or removing... ");

        if (installOrRemove) {
            window.addEventListener('beforeunload', window.turku.closeConfirmHandler);

        } else {
            window.removeEventListener('beforeunload', window.turku.closeConfirmHandler);

        }

    },

    setTurkuCommandColor(forComponent, colorString) {
        forComponent.style.setProperty('--turku-cmd-color', colorString);
        forComponent.style.setProperty('--turku-cmd-color-shade', colorString + "10");
    },


    childWindows: { },

    openerCanAccessWindow: function(crtlHash) {
        let noOpener = window.opener == null || window.opener == window;

        if (! noOpener) {
            let childWindows = window.opener.turku.childWindows;

            console.log('openerCanAccessWindow() childWindows list ' + childWindows);
            if (childWindows['turkuwin_' + crtlHash]) {
                return true;
            }

        }

        return false;
    },

    closeWindow: function(crtlHash) {
        console.log('Turku.closeWindow() closing win for ' + crtlHash);
        let theWin = turku.childWindows['turkuwin_' + crtlHash];
        console.log('                    it is ' + theWin);

        if (theWin) {
            theWin.close();

        }
    },

    openNewWindow: function(crtlHash, urlToOpen) {
        let newWindow = window.open(urlToOpen, '_blank');
        newWindow.focus();

        turku.childWindows['turkuwin_' + crtlHash] = newWindow;
        console.log("Turku.openNewWindow() stored the new window " + turku.childWindows['turkuwin_' + crtlHash] + " as " + crtlHash);
    },

    setNotLandingPage: function() {
        var link = document.querySelector("link[rel~='icon']");
        var current = link.href.substring(0, link.href.lastIndexOf('/'));
        var newUrl = current + '/faviconpassive.png';

        var links=document.getElementsByTagName('link');
        var head=document.getElementsByTagName('head')[0];
        for(var i=0; i<links.length; i++) {
            if(links[i].getAttribute('rel')==='icon') {
                head.removeChild(links[i])
            }
        }

        var link=document.createElement('link');
        link.type='image/x-icon';
        link.rel='icon';
        link.href=newUrl;
        document.getElementsByTagName('head')[0].appendChild(link);
    },

    setTurkuCookie: function(value,days) {
        let expires = '';
        if (days) {
            let date = new Date();
            date.setTime(date.getTime() + (days*24*60*60*1000));
            expires = '; expires=' + date.toUTCString();
        }
        document.cookie = 'TurkuIdent=' + (value || '')  + expires + '; path=/; SameSite=Strict;';
        console.log('window.turku.setTurkuCookie()  cookie set with value ' + value);
    },

    getTurkuCookie: function() {
        let nameEQ = 'TurkuIdent=';
        let ca = document.cookie.split(';');
        for(var i=0;i < ca.length;i++) {
            let c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1,c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
        }
        return null;
    },

    eraseTurkuCookie: function() {
        document.cookie = 'TurkuIdent=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    },
}
