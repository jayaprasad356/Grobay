package com.greymatter.grozzer.Model;

public class Product {
    private String pid,pname,pimage,pqty,pprice,pcategory,pdesc,ptype,poffer,pmrp;

    public Product() {
    }

    public Product(String pid, String pname, String pimage, String pqty, String pprice, String pcategory, String pdesc, String ptype, String poffer, String pmrp) {
        this.pid = pid;
        this.pname = pname;
        this.pimage = pimage;
        this.pqty = pqty;
        this.pprice = pprice;
        this.pcategory = pcategory;
        this.pdesc = pdesc;
        this.ptype = ptype;
        this.poffer = poffer;
        this.pmrp = pmrp;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPimage() {
        return pimage;
    }

    public void setPimage(String pimage) {
        this.pimage = pimage;
    }

    public String getPqty() {
        return pqty;
    }

    public void setPqty(String pqty) {
        this.pqty = pqty;
    }

    public String getPprice() {
        return pprice;
    }

    public void setPprice(String pprice) {
        this.pprice = pprice;
    }

    public String getPcategory() {
        return pcategory;
    }

    public void setPcategory(String pcategory) {
        this.pcategory = pcategory;
    }

    public String getPdesc() {
        return pdesc;
    }

    public void setPdesc(String pdesc) {
        this.pdesc = pdesc;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public String getPoffer() {
        return poffer;
    }

    public void setPoffer(String poffer) {
        this.poffer = poffer;
    }

    public String getPmrp() {
        return pmrp;
    }

    public void setPmrp(String pmrp) {
        this.pmrp = pmrp;
    }
}
