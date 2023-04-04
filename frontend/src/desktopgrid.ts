import { LitElement, customElement, state, html, css} from 'lit-element';
import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-selection-column.js';
import type { GridActiveItemChangedEvent } from '@vaadin/grid';


@customElement('desktop-grid')
export class DesktopGrid extends LitElement {


    static styles = css`
        :host {
            width: 100%;
            height: 100%;
        }
    `;


    @state()
    private items: DS[] = [];

    @state()
    private selectedItems: DS[] = [];

    private view: any;
    private grid: any | null;

    public override connectedCallback() {
        super.connectedCallback();

        this.grid = this.shadowRoot!.querySelector("vaadin-grid");
        console.log('The grid is ' + this.grid);
        this.addEventListener('keyup', this._onKeyUp);


    }

    protected _onKeyUp(e : any) {
        console.log(e);
        if (e.key == "ArrowUp" || e.key == "ArrowDown") {
          const element = e.composedPath()[0];
          const isRow = this.grid.__isRow(element);

          if (isRow || !element._content || !element._content.firstElementChild) {
            this.grid.dispatchEvent(
              new CustomEvent(isRow ? 'row-activate' : 'cell-activate', {
                detail: {
                  model: this.grid.__getRowModel(isRow ? element : element.parentElement),
                },
              }),
            );
          }
        }
    }

    public override disconnectedCallback() {
        this.removeEventListener('keyup', this._onKeyUp);
        super.disconnectedCallback();
    }

    protected override createRenderRoot() {
        const root = super.createRenderRoot();
        console.log('DesktopGrid.createRenderRoot() called');
        return root;
    }

    protected override firstUpdated() {
        // this.items.push({'c1': 'dan', 'c2':'Man', 'c3':'@', 'c4':'CTO'});
        console.log('DesktopGrid.firstUpdated() called ' + this.items);
    }






    protected initData(val: string, ssView: any){
        this.view = ssView;
        this.items = JSON.parse('[' + val + ']');
        console.log('DesktopGrid.initData() called with');
        console.log(this.items);
    }

    protected selectedItemChanged(e: GridActiveItemChangedEvent<DS>) {
        const item = e.detail.value;

        console.log('GridActiveItemChangedEvent is');
        console.log(e);
        this.selectedItems = item ? [item] : [];
        // async server update goes here

        this.view.$server.select(item ? item.index : -1);
    }

    render() {
        return html`<vaadin-grid .items="${this.items}" .selectedItems="${this.selectedItems}"
                                                                @active-item-changed="${this.selectedItemChanged}">
                       <vaadin-grid-column header="First Name" path="c1"></vaadin-grid-column>
                       <vaadin-grid-column header="Last Name" path="c2"></vaadin-grid-column>
                       <vaadin-grid-column path="c3"></vaadin-grid-column>
                       <vaadin-grid-column path="c4"></vaadin-grid-column>
                     </vaadin-grid>`;
    }
}



class DS {
    protected _index : number = -1;


    get index() : number {
        return this._index;
    }

    set index(val : number) {
        this._index = val;
    }

}