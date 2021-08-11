package com.excean.mirror.api;

import com.google.gson.annotations.SerializedName;

public class Feedback {
    @SerializedName("contact")
    public String contact;
    @SerializedName("fmsg")
    public String content;
    @SerializedName("type")
    public int type;
}
