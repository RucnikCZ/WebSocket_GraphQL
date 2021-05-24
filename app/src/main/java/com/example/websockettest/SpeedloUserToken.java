/*
 * Copyright (c) 2019. DEEP VISION s.r.o.
 * Author: Lubos Svetik
 * Project: Vision POS-TX
 */

package com.example.websockettest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SpeedloUserToken {

    @Expose
    @SerializedName("token")
    private String token;

    @Expose
    @SerializedName("userId")
    private String userId;

    @Expose
    @SerializedName("code")
    private String code;

    @Expose
    @SerializedName("error")
    private String error;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
