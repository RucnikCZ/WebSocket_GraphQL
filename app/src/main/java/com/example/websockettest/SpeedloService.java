/*
 * Copyright (c) 2019. DEEP VISION s.r.o.
 * Author: Lubos Svetik
 * Project: Vision POS-TX
 */

package com.example.websockettest;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SpeedloService {
    @GET("/users/token")
    Single<SpeedloUserToken> getUserToken(@Query("username") String userName, @Query("password") String userPwd);
}
