package com.sumera.meteorites.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by martin on 05/09/16.
 */

public class Meteorite {

    @SerializedName("name")
    private String m_name;

    @SerializedName("id")
    private String m_id;

    @SerializedName("recclass")
    private String m_recclass;

    @SerializedName("mass")
    private BigDecimal m_mass;

    @SerializedName("year")
    private Timestamp m_year;

    @SerializedName("reclat")
    private String m_langtitude;

    @SerializedName("reclong")
    private String m_longtitude;

    public String getName() {
        return m_name;
    }

    public String getId() {
        return m_id;
    }

    public String getRecclass() {
        return m_recclass;
    }

    public BigDecimal getMass() {
        return m_mass;
    }

    public Timestamp getYear() {
        return m_year;
    }

    public String getLangtitude() {
        return m_langtitude;
    }

    public String getLongtitude() {
        return m_longtitude;
    }

}
