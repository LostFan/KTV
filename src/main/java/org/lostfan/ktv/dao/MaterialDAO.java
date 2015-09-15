package org.lostfan.ktv.dao;

import java.util.List;

import org.lostfan.ktv.domain.Material;


public interface MaterialDAO {

    public List<Material> getAllMaterials();

    public Material getMaterial(int id);

    public void save(Material material);

    public void update(Material material);

    public void delete(int id);
}
