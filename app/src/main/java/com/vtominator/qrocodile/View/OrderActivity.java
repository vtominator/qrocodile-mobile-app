package com.vtominator.qrocodile.View;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vtominator.qrocodile.Model.Constants;
import com.vtominator.qrocodile.Control.RestaurantMenuCardAdapter;
import com.vtominator.qrocodile.Model.RestaurantMenuCardItem;
import com.vtominator.qrocodile.Control.SharedPrefManager;
import com.vtominator.qrocodile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private Context mContext = OrderActivity.this;
    private static ArrayList<RestaurantMenuCardItem> myMenuCard = new ArrayList<>();

    private SwipeRefreshLayout pullToRefresh;

    private RecyclerView mRecycleView;
    private RestaurantMenuCardAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String restaurant = ScannerActivity.restaurant;
    private int table_id = ScannerActivity.table_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();

        setContentView(R.layout.activity_order);
        refreshMenuList();

        topToolbar();
        loadRestaurantMenuItems();
    }

    private void fullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void topToolbar() {
        Toolbar topToolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(topToolbar);
    }

    @Override
    public void onBackPressed() {

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.order_menubar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.menuMenu:
                startActivity(new Intent(mContext, MenucardActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.menuCart:
                startActivity(new Intent(mContext, CartActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.menuLogout:
                SharedPrefManager.getInstance(this).logout();
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
                break;

        }
        return true;
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void listToView() {

        mRecycleView = findViewById(R.id.recycleView);
        mRecycleView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new RestaurantMenuCardAdapter(myMenuCard, getLocalClassName());

        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);
    }

    private void loadRestaurantMenuItems() {
        myMenuCard.clear();
        if (checkNetworkConnection()) {
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GETORDER,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (!jsonObject.getBoolean("error")) {

                                    JSONArray order = jsonObject.getJSONArray("order");

                                    for (int i = 0; i < order.length(); i++) {

                                        JSONObject orderJSONObject = order.getJSONObject(i);

                                        int id = orderJSONObject.getInt("id");
                                        String picture = orderJSONObject.getString("picture");
                                        String name = orderJSONObject.getString("name");
                                        int price = orderJSONObject.getInt("price");
                                        int piece = orderJSONObject.getInt("piece");

                                        Charset UTF8 = Charset.forName("UTF-8");
                                        Charset ISO = Charset.forName("ISO-8859-1");
                                        name = new String(name.getBytes(ISO), UTF8);


                                        RestaurantMenuCardItem restaurantMenuCardItem = new RestaurantMenuCardItem(id, picture, name, price);
                                        restaurantMenuCardItem.setPiece(piece);
                                        myMenuCard.add(restaurantMenuCardItem);
                                    }
                                } else {
                                    String message = jsonObject.getString("message");
                                    Charset UTF8 = Charset.forName("UTF-8");
                                    Charset ISO = Charset.forName("ISO-8859-1");
                                    message = new String(message.getBytes(ISO), UTF8);

                                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                }

                                listToView();

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.getMessage();
                    if (error instanceof TimeoutError)
                        message = "Átmeneti gond a szerverrel. Dolgozunk a probléma megoldásán.";
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                }

            }) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("restaurant", restaurant);
                    params.put("table_id", String.valueOf(table_id));

                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    500, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            Volley.newRequestQueue(this).add(stringRequest);
        } else {
            Intent intent = new Intent(mContext, NoInternetActivity.class);
            intent.putExtra("previousIntentName", getLocalClassName());
            startActivity(intent);
            finish();
        }
    }

    private void refreshMenuList() {
        pullToRefresh = findViewById(R.id.layoutSwipeRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRestaurantMenuItems();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

}
