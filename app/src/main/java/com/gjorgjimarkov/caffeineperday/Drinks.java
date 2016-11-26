package com.gjorgjimarkov.caffeineperday;

/**
 * Created by gjorgjim on 6/6/16.
 */
public class Drinks {
    private String name;
    private String ml;
    private int caff;
    private int id;
    private int number;

    public Drinks(String n,String m,int c,int i,int nu) {
        super();
        name=n;
        ml=m;
        id=i;
        caff=c;
        number=nu;


    }
    public String getName() {
        return name;
    }
    public String getMl() {
        return ml;
    }
    public int getCaff() {
        return caff;
    }
    public int getId() { return id;}
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    public void incraseNumber() {
        number++;
    }
}
