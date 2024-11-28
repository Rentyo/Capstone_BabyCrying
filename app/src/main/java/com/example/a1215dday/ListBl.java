package com.example.a1215dday;

public class ListBl {
    String Name;
    String time;
    boolean isChecked;
    int desc;
    int image;

    public ListBl(String name, String time, boolean isChecked, int desc, int image){
        this.Name = name;
        this.time = time;
        this.isChecked = isChecked;
        this.desc = desc;
        this.image = image;
    }

    public String getName()
    {
        return this.Name;
    }

    public boolean getChecked()
    {
        return this.isChecked;
    }
    public void setChecked(boolean isChecked){
        this.isChecked = isChecked;
    }

    public int getImage()
    {
        return this.image;
    }


}
