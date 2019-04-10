package com.thryv.bible.models;

/**
 * Created by ell on 10/12/16.
 */

public class Book {
    private int id;
    private String abbreviation = "";
    private String name;

    @Override
    public String toString() {
        return name != null ? name : super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Book){
            Book other = (Book) obj;
            return this.getId() == other.getId() || this.getAbbreviation().equals(other.getAbbreviation());
        }else {
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
