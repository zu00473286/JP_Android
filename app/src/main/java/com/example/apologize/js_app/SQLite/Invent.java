package com.example.apologize.js_app.SQLite;

/**
 * Created by CADLAB on 2016/8/11.
 */
public class Invent {
    private int Id;
    private String Itno;
    private String Itname;
    private String Itnoudf;
    private String Itunit;
    private double IN;
    private double AN;
    private double IAN;
    private double IBP;
    private double Money;
    private String Explan;
    private String ProductComposition;

    public Invent() {

    }

    public Invent(int Id, String Itno, String Itname, String Itnoudf, String Itunit, double IN, double AN, double IAN, double IBP, double Money,
                  String Explan, String ProductComposition) {
        this.Id = Id;
        this.Itno = Itno;
        this.Itname = Itname;
        this.Itnoudf = Itnoudf;
        this.Itunit = Itunit;
        this.IN = IN;
        this.AN = AN;
        this.IAN = IAN;
        this.IBP = IBP;
        this.Money = Money;
        this.Explan = Explan;
        this.ProductComposition = ProductComposition;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public void setItno(String Itno) {
        this.Itno = Itno;
    }

    public void setItname(String Itname) {
        this.Itname = Itname;
    }

    public void setItnoudf(String Itnoudf) {
        this.Itnoudf = Itnoudf;
    }

    public void setItunit(String Itunit) {
        this.Itunit = Itunit;
    }

    public void setIN(double IN) {
        this.IN = IN;
    }

    public void setAN(double AN) {
        this.AN = AN;
    }

    public void setIAN(double IAN) {
        this.IAN = IAN;
    }

    public void setIBP(double IBP) {
        this.IBP = IBP;
    }

    public void setMoney(double Money) {
        this.Money = Money;
    }

    public void setExplan(String Explan) {
        this.Explan = Explan;
    }

    public void setProductComposition(String ProductComposition) {
        this.ProductComposition = ProductComposition;
    }

    public int getId() {
        return Id;
    }

    public String getItno() {
        return Itno;
    }

    public String getItname() {
        return Itname;
    }

    public String getItnoudf() {
        return Itnoudf;
    }

    public String getItunit() {
        return Itunit;
    }

    public double getIN() {
        return IN;
    }

    public double getAN() {
        return AN;
    }

    public double getIAN() {
        return IAN;
    }

    public double getIBP() {
        return IBP;
    }

    public double getMoney() {
        return Money;
    }

    public String getExplan() {
        return Explan;
    }

    public String getProductComposition() {
        return ProductComposition;
    }
}
