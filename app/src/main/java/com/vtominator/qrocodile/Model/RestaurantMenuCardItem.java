package com.vtominator.qrocodile.Model;

import java.util.Random;

public class RestaurantMenuCardItem {

    private int id;
    private String picture;
    private String name;
    private int price;

    private int piece;

    private Random random = new Random();
    private boolean glutenFree = random.nextBoolean();
    private boolean lactoseFree = random.nextBoolean();


    public RestaurantMenuCardItem(int id, String picture, String name, int price) {
        this.id = id;
        this.picture = picture;
        this.name = name;
        this.price = price;
    }

    public int getId(){return id;}

    public String getPicture() {
        return picture;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getPiece() {
        return piece;
    }

    public void setPiece(int piece) {
        this.piece = piece;
    }


    public boolean isGlutenFree() {
        return glutenFree;
    }

    public boolean isLactoseFree() {
        return lactoseFree;
    }

}
