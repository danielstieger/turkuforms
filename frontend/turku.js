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

    installBeacon: function(servletUrl, uiid) {
        // bei Minimierung?
        // statt visibilitychange on document

        window.addEventListener("pagehide", function sendRequest(event) {

          console.log('turku.installBeacon() Issuing request NOW, persisted ' + event.persisted);
          navigator.sendBeacon(  servletUrl + "/beacon", uiid);

        });

        console.log('turku.installBeacon() Beacon installed for ' + servletUrl + " and ui " + uiid);
    },

    setTurkuCookie: function(value,days) {
        var expires = '';
        if (days) {
            var date = new Date();
            date.setTime(date.getTime() + (days*24*60*60*1000));
            expires = '; expires=' + date.toUTCString();
        }
        document.cookie = 'TurkuIdent=' + (value || '')  + expires + '; path=/; SameSite=Strict;';
        console.log('window.turku.setTurkuCookie()  cookie set with value ' + value);
    },

    getTurkuCookie: function() {
        var nameEQ = 'TurkuIdent=';
        var ca = document.cookie.split(';');
        for(var i=0;i < ca.length;i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1,c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
        }
        return null;
    },

    eraseTurkuCookie: function() {
        document.cookie = 'TurkuIdent=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    }
}
