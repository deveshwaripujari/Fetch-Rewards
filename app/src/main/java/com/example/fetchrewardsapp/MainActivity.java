package com.example.fetchrewardsapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fetch Rewards Items");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchData();
    }

    private void fetchData() {
        Log.d(TAG, "fetchData called");
        ApiService apiService = RetrofitInstance.getApiService();
        Call<List<Item>> call = apiService.getItems();

        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                Log.d(TAG, "onResponse called");
                if (response.isSuccessful() && response.body() != null) {
                    List<Item> items = response.body();

                    // Filter out items with blank or null names
                    List<Item> filteredItems = new ArrayList<>();
                    for (Item item : items) {
                        if (item.getName() != null && !item.getName().trim().isEmpty()) {
                            filteredItems.add(item);
                        }
                    }

                    // Sort items by listId and then by the numerical part of the name
                    Collections.sort(filteredItems, new Comparator<Item>() {
                        @Override
                        public int compare(Item o1, Item o2) {
                            int compareListId = Integer.compare(o1.getListId(), o2.getListId());
                            if (compareListId == 0) {
                                // Extract the numerical part from the name and compare it
                                Integer num1 = Integer.valueOf(o1.getName().replaceAll("\\D+", ""));
                                Integer num2 = Integer.valueOf(o2.getName().replaceAll("\\D+", ""));
                                return num1.compareTo(num2);
                            }
                            return compareListId;
                        }
                    });

                    List<Item> processedItems = new ArrayList<>();
                    int currentListId = -1;
                    for (Item item : filteredItems) {
                        if (item.getListId() != currentListId) {
                            processedItems.add(new Item(item.getListId(), "List ID: " + item.getListId(), Item.TYPE_HEADER));
                            currentListId = item.getListId();
                        }
                        processedItems.add(new Item(item.getListId(), item.getName(), Item.TYPE_ITEM));
                    }

                    recyclerView.setAdapter(new ItemAdapter(processedItems));
                } else {
                    Log.e(TAG, "Failed to retrieve items");
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e(TAG, "Error fetching items", t);
            }
        });
    }
}
