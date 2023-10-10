/*
 * Turkuforms modellwerkstatt.org
 * Stieger Dan, Winter 22/23
 *
 * Custom vaadin JS for moware turkuforms
 */

window.turku = {

    init : function(view) {
        window.onfocus = event => view.$server.turkuOnWindowFocusEvent();
        window.beforeunload = event => view.$server.turkuOnWindowUnload();
        console.log('TurkuEventListeners registered for ' + view);
    },

    copyToClipboard : async function(view, text) {
        try {
            await navigator.clipboard.writeText(text);

          } catch (err) {
            alert('Failed to copy content to clipboard!\n\n' + err);
          }
    },

    focusGrid : function(grid) {
        setTimeout(function() {
            let editDiv = grid.shadowRoot.querySelector("[aria-selected='true'] > div[part~='editable-cell']");
            if (editDiv) {
                editDiv.parentElement.focus();
                console.log('window.turku.focusGrid() editabled, focussed on ' + editDiv);

            } else {
                let firstTd = grid.shadowRoot.querySelector('[aria-selected="true"] > td');
                firstTd.focus();
                console.log('window.turku.focusGrid() NOT editabled, focussed on ' + firstTd);

            }

        }, 0);
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
