package com.house.keeping.service.entity;

import lombok.Data;

@Data
public class PointEntity {
    private double longitude;
    private double latitude;

    public PointEntity(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    @Override
    public String toString() {
        return longitude + "," + latitude;
    }
}
