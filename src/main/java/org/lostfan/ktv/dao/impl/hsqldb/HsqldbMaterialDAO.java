package org.lostfan.ktv.dao.impl.hsqldb;

import org.lostfan.ktv.dao.MaterialDAO;
import org.lostfan.ktv.domain.Material;
import org.lostfan.ktv.domain.MaterialConsumption;
import org.lostfan.ktv.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HsqldbMaterialDAO implements MaterialDAO {

    private Connection getConnection() {
        return ConnectionManager.getManager().getConnection();
    }

    public List<Material> getAll() {
        List<Material> materials = new ArrayList<>();
        try {
            Statement statement = getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM \"material\"");
            while (rs.next()) {
                Material material = new Material();
                material.setId(rs.getInt("id"));
                material.setPrice(rs.getInt("price"));
                material.setName(rs.getString("name"));
                material.setUnit(rs.getString("unit"));
                materials.add(material);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return materials;
    }

    public Material get(int id) {
        Material material = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"material\" where \"id\" = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                material = new Material();
                material.setId(rs.getInt("id"));
                material.setPrice(rs.getInt("price"));
                material.setName(rs.getString("name"));
                material.setUnit(rs.getString("unit"));

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return material;
    }

    public void save(Material material) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "INSERT INTO \"material\" (\"name\", \"price\", \"unit\") VALUES(?, ?, ?)");
            preparedStatement.setString(1, material.getName());
            preparedStatement.setInt(2, material.getPrice());
            preparedStatement.setString(3, material.getUnit());
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void update(Material material) {
        if (get(material.getId()) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "UPDATE \"material\" set \"name\" = ?, \"price\" = ?, \"unit\" = ? where \"id\" = ?");
                preparedStatement.setString(1, material.getName());
                preparedStatement.setInt(2, material.getPrice());
                preparedStatement.setString(3, material.getUnit());
                preparedStatement.setInt(4, material.getId());
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException("Update nonexistent element");
        }
    }

    public void delete(int id) {
        if(get(id) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "DELETE FROM  \"material\" where \"id\" = ?");
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException("Delete nonexistent element");
        }
    }
}
