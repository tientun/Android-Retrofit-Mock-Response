/*
 * Copyright (C) 2016. Tien Hoang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tientun.example.api;

import com.tientun.example.models.ResponseData;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 *
 */
public interface RestService {

    @GET("/user/login")
    Call<ResponseData> login(@Query("username") final String id,
                             @Query("pwd") final String pwd);

    @GET("/content/content")
    Call<ResponseData> content(@Query("param1") final String param1);

    @POST("/content/content")
    @FormUrlEncoded
    Call<ResponseData> postContent(@Field("param1") final String param1);
}