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
    }

}