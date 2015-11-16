package org.lostfan.ktv.view.model;

import org.lostfan.ktv.domain.Entity;
import org.lostfan.ktv.model.entity.EntityModel;
import org.lostfan.ktv.model.EntityFieldTypes;
import org.lostfan.ktv.utils.ResourceBundles;

import javax.swing.table.AbstractTableModel;

public class EntityTableModel<T extends Entity> extends AbstractTableModel {

    private EntityModel<T> model;
    public EntityTableModel(EntityModel<T> model) {
        this.model = model;
        this.model.addObserver(args -> fireTableDataChanged());
    }

    @Override
    public int getRowCount() {
        return this.model.getList().size();
    }

    @Override
    public int getColumnCount() {
        return this.model.getFields().size();
    }

    @Override
    public String getColumnName(int columnIndex) {
    return ResourceBundles.getEntityBundle().getString(
            this.model.getFields().get(columnIndex).getTitleKey());
}

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return this.model.getFields().get(columnIndex).getType().getClazz();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return this.model.getFields().get(columnIndex).getType().isEntityClass();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = this.model.getFields().get(columnIndex).get(this.model.getList().get(rowIndex));
        EntityFieldTypes thisType = this.model.getFields().get(columnIndex).getType();
        if(thisType.getClazz() == Integer.class && value ==null) {
            return 0;
        }
        if( thisType.isEntityClass()) {
            value =  thisType.getDAO().get((Integer) value);
        }

        return value;
    }

    public EntityModel getEntityModel() {
        return this.model;
    }
}
