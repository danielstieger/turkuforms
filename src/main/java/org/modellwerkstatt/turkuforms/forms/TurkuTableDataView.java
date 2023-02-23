package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import org.modellwerkstatt.dataux.runtime.utils.MoJSON;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class TurkuTableDataView<DTO> {
    private List<DTO> originalList;
    private List<DTO> filteredList;
    private List<DTO> removedList;

    private GridListDataView currentDataView;
    private String searchForWhat = "";
    private List<TurkuTableCol> colInfo;

    public TurkuTableDataView(List<TurkuTableCol> colInfo) {
        this.colInfo = colInfo;
    }


    // returns: need to set selections
    public boolean setNewList(Grid<DTO> grid, List<DTO> newList, IOFXSelection<DTO> sel) {
        // (0) SelCrtl clears selection if sel not in newList
        // (1) take over list
        // (2) apply filter if necessary, set filteredList
        // (3) Only in case filter is applied: if not all sel in filteredList, clear sel locally.

        originalList = newList;
        boolean allSelectionsFound = applyFilterAndRefreshGrid(sel.getObjects());
        // !! selections are cleared by instantiating a new dataView
        currentDataView = grid.setItems(filteredList);

        return allSelectionsFound;
    }

    // returns: need to set selections
    public boolean updateFilterList(Grid<DTO> grid, Set<DTO> currentTableSelection){
        boolean allSelectionsFound = applyFilterAndRefreshGrid(currentTableSelection);
        // !! selections are cleared by instantiating a new dataView
        currentDataView = grid.setItems(filteredList);

        return allSelectionsFound;
    }

    public boolean allSelectionsCurrentlyInFilter(Set<DTO> currentTableSelection){
        int selectionSize = currentTableSelection.size();
        int foundSel = 0;

        for (DTO item: filteredList){
            if (currentTableSelection.contains(item)) {
                foundSel ++;
            }

            if (foundSel >= selectionSize) { break; }
        }

        return foundSel == selectionSize;
    }

    private boolean applyFilterAndRefreshGrid(Collection<DTO> selection) {
        if (isNoSearch()) {
            filteredList = originalList;
            removedList = new ArrayList<>();
            return true;

        } else {
            filteredList = new ArrayList<>();
            removedList = new ArrayList<>();

            int selectionsFound = 0;

            for (DTO item: originalList) {
                if (textInDto(item)){
                    filteredList.add(item);
                    if (selection.contains(item)) { selectionsFound++; }
                } else {
                    removedList.add(item);
                }
            }
            return selection.size() == selectionsFound;
        }
    }

    private boolean textInDto(DTO item){
        for (TurkuTableCol col : colInfo) {
            String viewed = col.mowareConverter.convert(MoJSON.get(item, col.propertyName));
            if (viewed.toLowerCase().replace(".", "").contains(searchForWhat)) {
                return true;
            }
        }
        return false;
    }


    public String generateCsv() {
        StringBuilder csv = new StringBuilder();

        for (int i = 0; i < colInfo.size(); i++) {
            TurkuTableCol col = colInfo.get(i);
            csv.append(col.headerName);

            boolean last = i == (colInfo.size() - 1);
            csv.append(last ? "\n" : "\t");
        }


        for (DTO item : filteredList)
            for (int i = 0; i < colInfo.size(); i++) {
                TurkuTableCol col = colInfo.get(i);
                String viewed = col.mowareConverter.convert(MoJSON.get(item, col.propertyName));
                csv.append(viewed);

                boolean last = i == (colInfo.size() - 1);
                csv.append(last ? "\n" : "\t");
            }

        return csv.toString();
    }

    public int getFilteredTotalCount() {
        return filteredList.size();
    }

    public void setSearchText(String text) {
        searchForWhat = text.toLowerCase().replace(".", "");
    }

    private boolean isNoSearch(){
        return "".equals(searchForWhat);
    }
}
