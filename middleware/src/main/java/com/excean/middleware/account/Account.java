package com.excean.middleware.account;

import com.google.gson.annotations.SerializedName;

public class Account {

    private boolean enabled;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLogin() {
        return rid != null;
    }

    public Account() {
    }

    public Account(Account account) {
        flag = account.flag;
        vip = account.vip;
        rid = account.rid;
        nickName = account.nickName;
        sex = account.sex;
        birthday = account.birthday;
        headIconUrl = account.headIconUrl;
        endTime = account.endTime;
        phoneNum = account.phoneNum;
        curtime = account.curtime;
        passwordState = account.passwordState;
        isenc = account.isenc;
    }

    public String getAccount() {
        return phoneNum;
    }

    @SerializedName("flag")
    public int flag;
    @SerializedName("vip")
    public String vip;
    @SerializedName("rid")
    public String rid;

    @SerializedName("nickname")
    public String nickName;

    @SerializedName("sex")
    public String sex;

    @SerializedName("birthday")
    public String birthday;

    @SerializedName("headIconUrl")
    public String headIconUrl;

    @SerializedName("endTime")
    public String endTime;

    @SerializedName("phoneNum")
    public String phoneNum;

    @SerializedName("curtime")
    public String curtime;

    @SerializedName("issetPwd")
    public int passwordState;

    @SerializedName("isenc")
    public int isenc;

}
