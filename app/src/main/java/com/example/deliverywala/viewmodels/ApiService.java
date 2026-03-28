package com.example.deliverywala.viewmodels;

import com.example.deliverywala.model.Restaurants;
import java.util.List;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    
    @FormUrlEncoded
    @POST("jwsVisitorGatePass.aspx")
    List<Restaurants> getTotalVisitor(
        @Field("RequestType") String RequestType,
        @Field("UserID") String UserID,
        @Field("ObjSec") String ObjSec,
        @Field("UT") String UT
    );
}