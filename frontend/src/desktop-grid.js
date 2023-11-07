/*
 * modellwerkstatt desktop-grid vaadin gridpro extension
 *
 *
 * Daniel Stieger, Winter 23
 *
 *
 */


window.modellwerkstatt_desktopgrid = {

    focusGrid : function(grid, reschedule) {
        setTimeout(function() {
          let editDiv = grid.shadowRoot.querySelector("[aria-selected='true'] > div[part~='editable-cell']");
          if (editDiv) {
              editDiv.parentElement.focus();

          } else {
              let firstTd = grid.shadowRoot.querySelector('[aria-selected="true"] > td');
              if (firstTd) {
                  firstTd.focus();

              } else if (reschedule) {
                  setTimeout(() => { modellwerkstatt_desktopgrid.focusGrid(grid, false); }, 1500);
              }

          }
        }, 500);
    },
}
