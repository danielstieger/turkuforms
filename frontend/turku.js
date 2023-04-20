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
            let firstTd = grid.shadowRoot.querySelector('[aria-selected="true"] > td');
            firstTd.focus();
            console.log('window.turku.focusGrid() focussed on ' + firstTd);
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
    }




}
