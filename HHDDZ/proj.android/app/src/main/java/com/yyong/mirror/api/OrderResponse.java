package com.yyong.mirror.api;

import com.google.gson.annotations.SerializedName;

public class OrderResponse {
    @SerializedName("appPayRequest")
    public String appPayRequest;

    @SerializedName("merOrderId")
    public String merOrderId;

    @Override
    public String toString() {
        return "OrderResponse{" +
                "appPayRequest='" + appPayRequest + '\'' +
                ", merOrderId='" + merOrderId + '\'' +
                '}';
    }
}
