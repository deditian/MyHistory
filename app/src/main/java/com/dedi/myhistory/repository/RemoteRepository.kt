package com.dedi.myhistory.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dedi.myhistory.api.ApiService
import com.dedi.myhistory.data.AddressModel

import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory






class RemoteRepository : RemoteCallback{
    val TAG = "RemoteRepository"

    //link for generate = https://developer.here.com/projects/PROD-159447f6-4445-4313-b05a-0cba7679993e

    private val HTTP_API_DOMAIN_HERE = "https://reverse.geocoder.api.here.com/"
    private var apiService: ApiService? = null


    init {


        val logInter = HttpLoggingInterceptor()
        logInter.setLevel(HttpLoggingInterceptor.Level.BODY)
        val mIntercepter = OkHttpClient.Builder()
            .cookieJar(CookieJar.NO_COOKIES)
            .addInterceptor(logInter)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(HTTP_API_DOMAIN_HERE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(mIntercepter)
            .build()

        apiService = retrofit.create(ApiService::class.java)
        Log.i(TAG, "code_responese $retrofit $apiService")

    }


    override fun getAddress(prox:String, mode:String, maxresults: Int, gen:Int,app_id:String,app_code:String): LiveData<AddressModel> {
        val data = MutableLiveData<AddressModel>()
        apiService?.requestAddress(prox, mode, maxresults, gen,app_id,app_code)?.enqueue(object : Callback<AddressModel> {
            override fun onResponse(call: Call<AddressModel>, response: Response<AddressModel>) {
                if (response.code() == 200 || response.isSuccessful) {
                    data.value = response.body()
                    Log.i(TAG, "code_responese"+response.code())

                }
                Log.i(TAG, "code_responese "+response.code())
            }

            override fun onFailure(call: Call<AddressModel>, t: Throwable) {
                data.value=null
                Log.i(TAG, "code_responese null"+ t.printStackTrace())
            }
        })

        return data
    }



}