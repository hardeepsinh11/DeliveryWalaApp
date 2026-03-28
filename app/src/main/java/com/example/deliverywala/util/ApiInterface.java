package com.example.deliverywala.util;

import com.example.deliverywala.model.FoodDetails;
import com.example.deliverywala.model.RootModel;
import com.example.deliverywala.model.User;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    String CONTENT_TYPE = "application/json";
    String SERVER_KEY = "AAAAwzMznv4:APA91bGgSjmD5pFbgMwhWnG5hilS-bEq8X3K3MWRdPsRJIlgf6vunz1HI-DlbvJghq7BP289E_VJvZLJN9mVkjkQfBS2sNg2Q144kEgfKqLG3yOgUDMTyWbtSiJfieSfnKLWmIdJAUDk";

    @Headers({
        "Authorization: key=" + SERVER_KEY,
        "Content-Type:" + CONTENT_TYPE
    })
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body RootModel root);

    @POST("register")
    Call<ResponseBody> registerUser(@Body User user);

    @POST("login")
    Call<ResponseBody> loginUser(@Body HashMap<String, String> loginData);

    // ૨. પાયથોન માંથી બધી ફૂડ આઈટમ્સ મેળવવા માટે
    @GET("food-items")
    Call<List<FoodDetails>> getFoodItems();

    // ૩. ઓર્ડર સેવ કરવા માટે
    @POST("place-order")
    Call<ResponseBody> placeOrder(@Body Object orderData);

}