package com.example.deliverywala.viewmodels;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;

public class MainRepositoryImpl implements MainRepository {
    private final ApiService apiService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Inject
    public MainRepositoryImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    /*
    @Override
    public Future<List<Restaurants>> getTotalVisitor() {
        return executorService.submit(() -> {
            List<Restaurants> totalVisitor = null;
            try {
                totalVisitor = apiService.getTotalVisitor(
                    "TotalVisitor",
                    "Emp_code", 
                    "OBJ_SEC",
                    "UserType"
                );
            } catch (Exception e) {
                Log.e("getObjSec", e.toString());
            }
            return totalVisitor;
        });
    }
    */
}