//
// Turkuforms modellwerkstatt.org
// Stieger Dan, Winter 22/23
//

window.turku = {

    init : function(view) {
        window.onfocus = event => view.$server.turkuOnWindowFocusEvent();
        window.beforeunload = event => view.$server.turkuOnWindowUnload();
        console.log('TurkuEventListeners registered for ' + view);
    }


}