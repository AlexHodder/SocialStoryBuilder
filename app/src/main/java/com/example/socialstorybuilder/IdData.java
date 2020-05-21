package com.example.socialstorybuilder;

import androidx.annotation.Nullable;

/**
 * Class to handle pairs of strings
 */
public class IdData {
    private String id;
    private String data;

    /**
     * Constructor
     * @param key set as id
     * @param value set as data
     */
    public IdData(String key, String value){
        this.id = key;
        this.data = value;
    }

    /**
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id to replace id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return data
     */
    public String getData() {
        return data;
    }

    /**
     *
     * @param data to replace data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Method to override equals method, to check if ID's of pairs are equal.
     * @param obj to be compared
     * @return true if objects ID's are equal, false if not
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof IdData){
            return this.getId().equals(((IdData) obj).getId());
        }
        return false;
    }
}
