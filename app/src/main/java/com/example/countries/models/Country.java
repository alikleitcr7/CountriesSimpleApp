package com.example.countries.models;

public class Country {
    public String Name;
    public String Code;
    public float Rating;
    public String ImageUrl;

    public Country() {
    }

    public Country(String name, String code, float rating, String imageUrl) {
        Name = name;
        Code = code;
        Rating = rating;
        ImageUrl = imageUrl;
    }
}
