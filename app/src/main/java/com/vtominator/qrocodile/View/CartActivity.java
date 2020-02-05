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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vtominator.qrocodile.Model.Constants;
import com.vtominator.qrocodile.Control.RestaurantMenuCardAdapter;
import com.vtominator.qrocodile.Model.RestaurantMenuCardItem;
import com.vtominator.qrocodile.Control.SharedPrefManager;
import com.vtominator.qrocodile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private Context mContext = CartActivity.this;
    protected static ArrayList<RestaurantMenuCardItem> myMenuCard = new ArrayList<>();

    private SwipeRefreshLayout pullToRefresh;

    private String restaurant = ScannerActivity.restaurant;
    private int table_id = ScannerActivity.table_id;

    private int sumPrice;
    private TextView tvSumPrice;
    private Button bOrder;

    private RecyclerView mRecycleView;
    private RestaurantMenuCardAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_cart);
        loadRestaurantMenuItems();
        refreshMenuList();
    }

    private void fullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void topToolbar() {
        Toolbar topToolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(topToolbar);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cart_menubar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.menuMenu:
                startActivity(new Intent(mContext, MenucardActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.menuOrder:
                startActivity(new Intent(mContext, OrderActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;

            case R.id.menuLogout:
                SharedPrefManager.getInstance(this).logout();
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {

    }

    private void loadRestaurantMenuItems() {
        if (checkNetworkConnection()) {

            tvSumPrice = findViewById(R.id.tvSumPrice);
            bOrder = findViewById(R.id.bOrder);

            sumPrice = 0;
            if (!myMenuCard.isEmpty()) {
                for (RestaurantMenuCardItem currentRestaurantMenuCardItem : myMenuCard) {
                    sumPrice += (currentRestaurantMenuCardItem.getPrice() * currentRestaurantMenuCardItem.getPiece());
                }
                bOrder.setEnabled(true);
                bOrder.setAlpha(1);

            }

            tvSumPrice.setText(String.valueOf(sumPrice));

            bOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addOrder(myMenuCard);
                    myMenuCard.clear();
                }
            });

            topToolbar();

            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                    myMenuCard.remove(viewHolder.getAdapterPosition());
                    if (myMenuCard.isEmpty()) {
                        bOrder.setEnabled(false);
                        bOrder.setAlpha(.5f);
                    }
                    sumPrice = 0;
                    for (RestaurantMenuCardItem currentRestaurantMenuCardItem : myMenuCard) {
                        sumPrice += (currentRestaurantMenuCardItem.getPrice() * currentRestaurantMenuCardItem.getPiece());
                    }
                    tvSumPrice.setText(String.valueOf(sumPrice));
                    mAdapter.notifyDataSetChanged();
                }
            };

            mRecycleView = findViewById(R.id.recycleView);
            mRecycleView.setHasFixedSize(true);

            mLayoutManager = new LinearLayoutManager(this);

            mAdapter = new RestaurantMenuCardAdapter(myMenuCard, getLocalClassName());

            mRecycleView.setLayoutManager(mLayoutManager);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecycleView);
            mRecycleView.setAdapter(mAdapter);


        } else {
            Intent intent = new Intent(mContext, NoInternetActivity.class);
            intent.putExtra("previousIntentName", getLocalClassName());
            startActivity(intent);
            finish();
        }
    }

    private void addOrder(ArrayList<RestaurantMenuCardItem> myMenuCard) {

        for (RestaurantMenuCardItem currentRestaurantMenuCardItem : myMenuCard) {
            final int food_id = currentRestaurantMenuCardItem.getId();
            final int piece = currentRestaurantMenuCardItem.getPiece();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_ADDORDER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");

                        Charset UTF8 = Charset.forName("UTF-8");
                        Charset ISO = Charset.forName("ISO-8859-1");
                        message = new String(message.getBytes(ISO), UTF8);

                        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                        if (!jsonObject.getBoolean("error")) {

                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("restaurant", restaurant);
                    params.put("table_id", String.valueOf(table_id));
                    params.put("food_id", String.valueOf(food_id));
                    params.put("piece", String.valueOf(piece));
                    return params;

                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(stringRequest);
        }

    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
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
