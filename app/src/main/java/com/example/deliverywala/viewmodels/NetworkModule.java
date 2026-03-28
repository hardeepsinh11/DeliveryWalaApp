package com.example.deliverywala.viewmodels;

import com.example.deliverywala.util.Constants;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    // public static final String BASE_URL = "http://exam.marwadieducation.edu.in/";
    // public static final String BASE_URL = CommonUrl;

    @Singleton
    @Provides
    public OkHttpClient provideOkHttp() {
        return new OkHttpClient.Builder().build();
    }

    @Singleton
    @Provides
    @Named("loggingInterceptor")
    public HttpLoggingInterceptor provideLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Provides
    public Retrofit provideRetrofit() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .build();
            
        return new Retrofit.Builder()
            .baseUrl(Constants.CommonURL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .build();
    }

    /*@Provides
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
            .baseUrl("https://howtodoandroid.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();
    }*/

    @Provides
    public ApiService provideApiClient(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}