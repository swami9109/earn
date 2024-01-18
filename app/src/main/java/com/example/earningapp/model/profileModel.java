package com.example.earningapp.model;

public class profileModel {

    private String name, email,image;
    private int coins, spins;

    public profileModel(){

    }

    public int getSpins() {
        return spins;
    }

    public void setSpins(int spins) {
        this.spins = spins;
    }

    public profileModel(String name, String email, String image, int coins, int spins) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.spins = spins;
        this.coins = coins;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
