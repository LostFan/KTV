package org.lostfan.ktv.view.entity;

import org.lostfan.ktv.domain.Entity;
import org.lostfan.ktv.model.*;
import org.lostfan.ktv.model.entity.EntityModel;
import org.lostfan.ktv.utils.*;
import org.lostfan.ktv.view.EntityInnerTableView;
import org.lostfan.ktv.view.FormView;
import org.lostfan.ktv.view.components.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class EntityView extends FormView {

  protected class EntityFormField extends FormField<Integer> {

        private EntityPanel panel;

        public EntityFormField(EntityField entityField, Entity entity) {
            this(entityField, entity, null);
        }

        public EntityFormField(EntityField entityField, Entity entity, EntityPanel jComboBox ) {
            super(entityField.getTitleKey());

            if(jComboBox == null) {
                this.panel = EntityPanelFactory.createEntityPanel(entityField.getType());
            } else {
                this.panel = jComboBox;
            }

            this.panel.setParentView(EntityView.this);

            if (entity != null) {
                Object value = entityField.get(entity);
                if(value != null) {
                    this.panel.setSelectedId((Integer) value);
                    value = this.panel.getSelectedEntity().getName();
                    ((JTextField) ((this.panel.getComboBox()).getEditor().getEditorComponent())).setText((String) value);
                }
            }
            ((EntityComboBoxModel)this.panel.getComboBox().getModel()).addEntitySelectedListener(e -> fireValueChanged(getValue()));

            addValueListener(v -> {
                System.out.println("entity new value: " + v);
            });
        }

        @Override
        public JComponent getInputComponent() {
            return this.panel;
        }

        @Override
        public void setValue(Integer value) {
            if(value != null) {
                this.panel.setSelectedId(value);
                ((JTextField) ((this.panel.getComboBox()).getEditor().getEditorComponent()))
                        .setText(this.panel.getSelectedEntity().getName());
            }
        }

        @Override
        public Integer getValue() {
            return  this.panel.getSelectedEntity() != null ? this.panel.getSelectedEntity().getId() : null;
        }
    }

    private JButton addButton;
    private JButton cancelButton;
    private EntityInnerTableView innerTableView;

    private EntityModel<? extends Entity> model;

    private ViewActionListener addActionListener;
    private ViewActionListener cancelActionListener;
    protected ViewActionListener changeActionListener;

    private Entity entity;
    private Map<EntityField, FormField> entityFormFieldMap;

    public EntityView(EntityModel model) {
        this(model, null);
    }

    public EntityView(EntityModel model, Entity entity) {
        this.entity = entity;
        this.model = model;

        setTitle(getEntityString(model.getEntityNameKey()));

        entityFormFieldMap = new HashMap<>();

        this.addButton = new JButton(getGuiString(entity == null ? "buttons.add" : "buttons.change"));
        this.addButton.addActionListener(e -> {
            if (this.addActionListener != null) {
                // Stop to edit the inner table view
                if (this.innerTableView != null) {
                    this.innerTableView.stopEditing();
                }
                this.addActionListener.actionPerformed(buildEntity());
            }
        });

        this.cancelButton = new JButton(getGuiString("buttons.cancel"));
        this.cancelButton.addActionListener(e -> {
            if (this.cancelActionListener != null) {
                this.cancelActionListener.actionPerformed(null);
            }
            hide();
        });

        for (EntityField entityField : model.getFields()) {
            if (!entityField.isEditable()) {
                continue;
            }
            addFormField(createFormField(entityField, entity), entityField);
        }

        buildLayout();

        show();
    }

    private void buildLayout() {
        getContentPanel().setLayout(new BorderLayout(10, 10));
        getContentPanel().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        getContentPanel().add(getFieldPanel(), BorderLayout.PAGE_START);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        getContentPanel().add(buttonPanel, BorderLayout.SOUTH);
    }

    private FormField createFormField(EntityField entityField) {
        FormField formField;
        switch (entityField.getType()) {
            case String:
                formField = new StringFormField(entityField.getTitleKey());
                break;
            case MultilineString:
                formField = new MultilineStringFormField(entityField.getTitleKey());
                break;
            case Integer:
                formField = new IntegerFormField(entityField.getTitleKey());
                break;
            case Double:
                formField = new BigDecimalFormField(entityField.getTitleKey());
                break;
            case BigDecimal:
                formField = new BigDecimalFormField(entityField.getTitleKey());
                break;
            case Boolean:
                formField = new BooleanFormField(entityField.getTitleKey());
                break;
            case Date:
                formField = new DateFormField(entityField.getTitleKey());
                break;
            default:
                formField =  entityField.getType().isEntityClass() ? new EntityFormField(entityField , this.entity)
                        : new StringFormField(entityField.getTitleKey());
        }
        return formField;
    }

    protected FormField createFormField(EntityField entityField, Entity entity) {
        FormField field = createFormField(entityField);
        if (entity != null) {
            field.setValue(entityField.get(entity));
        }
        return field;
    }

    protected void addFormField(FormField formField, EntityField entityField) {
        super.addFormField(formField);
        entityFormFieldMap.put(entityField, formField);
    }

    /**
     * Assembles values of the input fields into a Entity object.
     */
    protected Entity buildEntity() {
        Entity entity = this.entity;
        if (entity == null) {
            entity = createNewEntity();
        }

        for (Map.Entry<EntityField, FormField> entry : this.entityFormFieldMap.entrySet()) {
            entry.getKey().set(entity, entry.getValue().getValue());
        }

        if (this.innerTableView != null) {
            this.innerTableView.getFullEntityField().set(entity, this.innerTableView.getEntityList());
        }

        return entity;
    }

    /**
     * Return a new empty Entity instance.
     * Default implementation based on EntityModel::createNewEntity
     * Override this method to change the returned Entity type
     * @return
     */
    protected Entity createNewEntity() {
        return this.model.createNewEntity();
    }

    public void setAddActionListener(ViewActionListener addActionListener) {
        this.addActionListener = addActionListener;
    }

    public void setCancelActionListener(ViewActionListener cancelActionListener) {
        this.cancelActionListener = cancelActionListener;
    }

    public void setChangeActionListener(ViewActionListener changeActionListener) {
        this.changeActionListener = changeActionListener;
    }

    public ViewActionListener getChangeActionListener() {
        return this.changeActionListener;
    }

    protected void setInnerTable(EntityInnerTableView innerTableView) {
        this.innerTableView = innerTableView;
        getContentPanel().add(innerTableView.getContentPanel(), BorderLayout.CENTER);
    }
}
