package com.example.deliverywala.viewmodels;

import android.app.Application;

import com.example.deliverywala.base.RuntimePermissionViewModel;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends RuntimePermissionViewModel {
    private final MainRepository apiService;

    @Inject
    public MainViewModel(MainRepository apiService, Application application) {
        super(application);
        this.apiService = apiService;
    }

    /*
    public LiveData<List<Restaurants>> getTotalVisitor() {
        MutableLiveData<List<Restaurants>> result = new MutableLiveData<>();
        new Thread(() -> {
            try {
                List<Restaurants> restaurants = apiService.getTotalVisitor().get();
                result.postValue(restaurants);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        return result;
    }
    */
}