package com.example.a1215dday;

public class ListBl {
    String Name;
    String time;
    int ingredients;
    int desc;
    int image;

    public ListBl(String name, String time, int ingredients, int desc, int image){
        this.Name = name;
        this.time = time;
        this.ingredients = ingredients;
        this.desc = desc;
        this.image = image;
    }

    public String getName()
    {
        return this.Name;
    }

    public String getTime()
    {
        return this.time;
    }

    public int getIngredients()
    {
        return this.ingredients;
    }
    public int getImage()
    {
        return this.image;
    }


}
