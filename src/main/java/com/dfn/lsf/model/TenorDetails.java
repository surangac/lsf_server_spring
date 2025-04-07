package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by Atchuthan on 5/25/2015.
 */
public class TenorDetails {
    private String id;
    private List<Tenor> tenors;

    public List<Tenor> getTenors() {
        return tenors;
    }

    public void setTenors(List<Tenor> tenors) {
        this.tenors = tenors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
