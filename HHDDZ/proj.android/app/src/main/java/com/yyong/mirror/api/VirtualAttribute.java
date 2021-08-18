package com.yyong.mirror.api;

import com.google.gson.annotations.SerializedName;

public class VirtualAttribute {
    @SerializedName("pkg")
    public String packageName;
    @SerializedName("atr")
    public String attribute;
}
