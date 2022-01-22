package com.yyong.mirror.personal.vo;

public class VipPrice {
    private long price;
    private String textPrice;

    public VipPrice(long price, String textPrice) {
        this.price = price;
        this.textPrice = textPrice;
    }

    public long getPrice() {
        return price;
    }

    public String getTextPrice() {
        return textPrice;
    }
}
