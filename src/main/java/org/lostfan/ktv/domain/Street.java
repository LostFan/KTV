package org.lostfan.ktv.domain;

public class Street extends DefaultEntity {

    private Integer id;

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
