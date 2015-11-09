package org.lostfan.ktv.view;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import org.lostfan.ktv.domain.Entity;
import org.lostfan.ktv.model.BaseEntityModel;
import org.lostfan.ktv.model.EntityField;
import org.lostfan.ktv.utils.Observer;
import org.lostfan.ktv.utils.ResourceBundles;
import org.lostfan.ktv.utils.ViewActionListener;
import org.lostfan.ktv.view.model.EntityTableModel;

public class EntityTableView {

    private class ModelObserver implements Observer {
        @Override
        public void update(Object args) {
            EntityTableView.this.revalidate();
        }
    }

    private JPanel contentPanel;
    private JTable table;
    private JScrollPane tableScrollPane;
    private JButton findButton;
    private JButton addButton;
    private JButton changeButton;
    private JButton deleteButton;

    private ModelObserver modelObserver;

    private BaseEntityModel model;

    private ViewActionListener findActionListener;
    private ViewActionListener addActionListener;
    private ViewActionListener changeActionListener;
    private ViewActionListener deleteActionListener;

    public EntityTableView(BaseEntityModel<? extends Entity> model) {
        this.model = model;

        this.table = new JTable(new EntityTableModel<>(model));
        this.table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        this.table.setAutoCreateRowSorter(true);
        this.table.setFillsViewportHeight(true);
        this.table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                EntityTableView view = EntityTableView.this;
                // Dbl Click at the table row
                if (e.getClickCount() == 2 && view.changeActionListener != null) {
                    view.changeActionListener.actionPerformed(view.table.getSelectedRow());
                }
            }
        });

        this.findButton = new JButton(getString("buttons.find"));
        this.findButton.addActionListener(e -> {
            if (this.findActionListener != null) {
                this.findActionListener.actionPerformed(null);
            }
        });

        this.addButton = new JButton(getString("buttons.add"));
        this.addButton.addActionListener(e -> {
            if (this.addActionListener != null) {
                this.addActionListener.actionPerformed(null);
            }
        });

        this.changeButton = new JButton(getString("buttons.changeSelected"));
        this.changeButton.addActionListener(e -> {
            int selectedRow = this.table.getSelectedRow();
            if (selectedRow != -1 && this.changeActionListener != null) {
                this.changeActionListener.actionPerformed(selectedRow);
            }
        });

        this.deleteButton = new JButton(getString("buttons.delete"));
        this.deleteButton.addActionListener(e -> {
            int[] selectedRows = this.table.getSelectedRows();
            if (selectedRows.length != 0 && confirmDeletion() && this.deleteActionListener != null) {
                List<Integer> selectedIndexes = IntStream.of(selectedRows).boxed().collect(Collectors.toList());
                this.deleteActionListener.actionPerformed(selectedIndexes);
            }
        });

        buildLayout();

        this.modelObserver = new ModelObserver();

        model.addObserver(this.modelObserver);
    }

    private void buildLayout() {
        this.contentPanel = new JPanel(new BorderLayout(10, 10));

        // ID column values should be aligned to the left;
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        this.table.getColumnModel().getColumn(0).setCellRenderer(renderer);
        addStringActionTableCellEditorToColumns();
        this.tableScrollPane = new JScrollPane(this.table);

        this.contentPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        this.contentPanel.add(rightPanel, BorderLayout.LINE_END);

        JPanel rightPanelInner = new JPanel(new GridLayout(4, 1, 0, 10));
        rightPanel.add(rightPanelInner);

        rightPanelInner.add(this.findButton);
        rightPanelInner.add(this.addButton);
        rightPanelInner.add(this.changeButton);
        rightPanelInner.add(this.deleteButton);
    }

    public int getSelectedIndex() {
        return this.table.getSelectedRow();
    }

    public int[] getSelectedIndexes() {
        return this.table.getSelectedRows();
    }


    public JPanel getContentPanel() {
        return this.contentPanel;
    }

    private boolean confirmDeletion() {
        int optionType = JOptionPane.OK_CANCEL_OPTION;
        int messageType = JOptionPane.QUESTION_MESSAGE;
        Object[] selValues = {ResourceBundles.getGuiBundle().getString("buttons.yes"),
                ResourceBundles.getGuiBundle().getString("buttons.cancel") };
        String message = ResourceBundles.getGuiBundle().getString("window.delete") + " : "
                + ResourceBundles.getEntityBundle().getString(model.getEntityNameKey());
        int result = JOptionPane.showOptionDialog(null,
                ResourceBundles.getGuiBundle().getString("message.deleteQuestion"), message,
                optionType, messageType, null, selValues,
                selValues[0]);

        return result == 0 ? true : false;
    }

    public void setModel(BaseEntityModel<? extends Entity> model) {
        this.model.removeObserver(modelObserver);
        this.model = model;
        model.addObserver(this.modelObserver);

        this.table.setModel(new EntityTableModel<>(model));
        this.tableScrollPane.setViewportView(this.table);

        revalidate();
    }

    private void addStringActionTableCellEditorToColumns() {
        JTextField textField = new JTextField();
        textField.setBorder(BorderFactory.createEmptyBorder());
        textField.setEditable(false);
        DefaultCellEditor editor = new DefaultCellEditor(textField);
        editor.setClickCountToStart(1);
        for (int columnIndex=0;columnIndex< table.getColumnCount(); columnIndex++) {
            if(((EntityField)this.model.getFields().get(columnIndex)).getType().isEntityClass()) {
//                this.table.getColumn(table.getColumnName(columnIndex)).setCellEditor(new EntityActionTableCellEditor(editor));
            }
        }
    }

    private void revalidate() {
        addStringActionTableCellEditorToColumns();
        this.contentPanel.invalidate();
        this.contentPanel.repaint();
    }

    public void setFindActionListener(ViewActionListener findActionListener) {
        this.findActionListener = findActionListener;
    }

    public void setAddActionListener(ViewActionListener addActionListener) {
        this.addActionListener = addActionListener;
    }

    public void setChangeActionListener(ViewActionListener changeActionListener) {
        this.changeActionListener = changeActionListener;
    }

    public void setDeleteActionListener(ViewActionListener deleteActionListener) {
        this.deleteActionListener = deleteActionListener;
    }

    private String getString(String key) {
        return ResourceBundles.getGuiBundle().getString(key);
    }
}