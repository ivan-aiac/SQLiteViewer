package viewer;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class DataBaseTableModel extends AbstractTableModel {

    private String[] cols = new String[0];
    private List<Object[]> data = new ArrayList<>(0);

    @Override
    public String getColumnName(int column) {
        return cols[column];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data.get(row)[col];
    }

    public void setTableData(String[] cols, List<Object[]> data) {
        this.cols = cols;
        this.data = data;
        fireTableStructureChanged();
        fireTableDataChanged();
    }
}
