package com.dedi.myhistory.api

import com.dedi.myhistory.data.AddressModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// kwy api = SAwlrrcyil6ScGJvV-MKnHlgv57YdF793-wa1Uy98kg

// https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox=41.8842,-87.6388&mode=retrieveAddresses&maxresults=1&gen=9&app_id=WujWfaQEkfooEWPw0xy7&app_code=hEwHa8j4W1AIkNddDwRHFg

interface ApiService{
    @GET("6.2/reversegeocode.json")
    fun requestAddress(
        @Query("prox") prox: String,
        @Query("mode") mode: String,
        @Query("maxresults") maxresults: Int,
        @Query("gen") gen: Int,
        @Query("app_id") app_id: String,
        @Query("app_code") app_code: String
    ): Call<AddressModel>
}

//interface ApiService{
//    @GET("6.2/reversegeocode.json/{prox}/{mode}/{apiKey}")
//    fun requestAddress(
//        @Path("prox") prox: String,
//        @Path("mode") mode: String,
//        @Path("apiKey") apiKey: String
//    ): Call<AddressModel>
//}