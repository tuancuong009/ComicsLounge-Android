package com.comics.lounge.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainEvent {
    private String status;

    @SerializedName("products")
    private List<EvetRetro> evetRetroList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<EvetRetro> getEvetRetroList() {
        return evetRetroList;
    }

    public void setEvetRetroList(List<EvetRetro> evetRetroList) {
        this.evetRetroList = evetRetroList;
    }
}
