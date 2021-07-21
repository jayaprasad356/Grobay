package com.greymatter.grozzer.Model;

public class Cart {
    private String pid,needed,total;

    public Cart() {
    }

    public Cart(String pid, String needed, String total) {
        this.pid = pid;
        this.needed = needed;
        this.total = total;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getNeeded() {
        return needed;
    }

    public void setNeeded(String needed) {
        this.needed = needed;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
