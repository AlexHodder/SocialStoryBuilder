package com.example.socialstorybuilder;

import androidx.annotation.Nullable;

public class IdData {
    private String id;

    private String data;

    public IdData(String key, String value){
        this.id = key;
        this.data = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof IdData){
            return this.getId().equals(((IdData) obj).getId());
        }
        return false;
    }
}
