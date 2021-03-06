package org.lostfan.ktv.model.searcher;

import java.util.ArrayList;
import java.util.List;

import org.lostfan.ktv.dao.DAOFactory;
import org.lostfan.ktv.dao.EntityDAO;
import org.lostfan.ktv.domain.Material;
import org.lostfan.ktv.model.EntityField;
import org.lostfan.ktv.model.EntityFieldTypes;

public class MaterialSearcherModel extends EntitySearcherModel<Material> {

    private List<EntityField> fields;

    public MaterialSearcherModel() {
        this.fields = new ArrayList<>();
        this.fields.add(new EntityField("material.id", EntityFieldTypes.Integer, Material::getId, Material::setId, false));
        this.fields.add(new EntityField("material.name", EntityFieldTypes.String, Material::getName, Material::setName));
        this.fields.add(new EntityField("material.price", EntityFieldTypes.Double, Material::getPrice, Material::setPrice));
        this.fields.add(new EntityField("material.unit", EntityFieldTypes.String, Material::getUnit, Material::setUnit));
    }

    @Override
    public Class getEntityClass() {
        return Material.class;
    }

    @Override
    public String getEntityNameKey() {
        return "materials";
    }

    @Override
    public List<EntityField> getFields() {
        return this.fields;
    }

    @Override
    protected EntityDAO<Material> getDao() {
        return DAOFactory.getDefaultDAOFactory().getMaterialDAO();
    }
}
