package com.manan.dev.shineymca.Models;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

public class Coordinator implements Comparable<Coordinator>{

    private String coordName;
    private String coordPhone;
    @Exclude
    private String CoordId;

    public Coordinator(String CoordName, String CoordPhone) {
        this.coordName = CoordName;
        this.coordPhone = CoordPhone;
    }

    public Coordinator() {
    }

    public String getCoordName() {
        return coordName;
    }

    public void setCoordName(String mCoordName) {
        this.coordName = mCoordName;
    }

    public String getCoordPhone() {
        return coordPhone;
    }

    public void setmCoordPhone(String mCoordPhone) {
        this.coordPhone = mCoordPhone;
    }

    public String getCoordId() {
        return CoordId;
    }

    public void setCoordId(String mCoordId) {
        this.CoordId = mCoordId;
    }

    @Override
    public int compareTo(@NonNull Coordinator coordinator) {
        return this.getCoordName().compareTo(coordinator.getCoordName());
    }

}
