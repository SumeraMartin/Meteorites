package com.sumera.meteorites.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by martin on 05/09/16.
 */

public class Meteorite extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("recclass")
    private String recclass;

    @SerializedName("mass")
    private double mass;

    @SerializedName("year")
    private Date year;

    @SerializedName("reclat")
    private String langtitude;

    @SerializedName("reclong")
    private String longtitude;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRecclass(String recclass) {
        this.recclass = recclass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public void setYear(Date year) {
        this.year = year;
    }

    public void setLangtitude(String langtitude) {
        this.langtitude = langtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getRecclass() {
        return recclass;
    }

    public double getMass() {
        return mass;
    }

    public Date getYear() {
        return year;
    }

    public String getLangtitude() {
        return langtitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    /**
     * Sort from biggest mass to smallest
     * @param meteorites
     */
    public static void sortByMass(List<Meteorite> meteorites) {
        Collections.sort(meteorites, new Comparator<Meteorite>() {
            @Override
            public int compare(Meteorite lhs, Meteorite rhs) {
                return Double.valueOf(rhs.getMass()).compareTo(lhs.getMass());
            }
        });
    }

}
