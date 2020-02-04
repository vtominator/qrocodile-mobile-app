package com.vtominator.qrocodile;

import android.app.Dialog;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vtominator.qrocodile.Utils.Constants;
import com.vtominator.qrocodile.Utils.RestaurantMenuCardAdapter;
import com.vtominator.qrocodile.Utils.RestaurantMenuCardItem;
import com.vtominator.qrocodile.Utils.SharedPrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenucardActivity extends AppCompatActivity {
    private static ArrayList<RestaurantMenuCardItem> menuCard = new ArrayList<>();
    private Context mContext = MenucardActivity.this;

    private SwipeRefreshLayout pullToRefresh;

    private RecyclerView mRecycleView;
    private RestaurantMenuCardAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String restaurant = ScannerActivity.restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_menucard);

        refreshMenuList();

        topToolbar();
        loadRestaurantMenuItems();
    }


    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void fullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    private void topToolbar() {
        Toolbar topToolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(topToolbar);
    }

    @Override
    public void onBackPressed() {}

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menucard_menubar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.menuOrder:
                startActivity(new Intent(mContext, OrderActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

    private void listToView() {

        mRecycleView = findViewById(R.id.recycleView);
        mRecycleView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new RestaurantMenuCardAdapter(menuCard, getLocalClassName());

        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RestaurantMenuCardAdapter.OnItemClickListener() {


            @Override
            public void onMenuItemClick(int position) {
                final Dialog detailDialog = new Dialog(mContext);

                detailDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                detailDialog.setContentView(R.layout.dialog_window);

                final RestaurantMenuCardItem currentRestaurantMenuCardItem = menuCard.get(position);

                ImageView ivMenuItemPicture;
                Button bAdd, bRemove, bAddToCart;
                final TextView tvMenuItemName, tvPiece, tvPrice, tvCancel;

                ivMenuItemPicture = detailDialog.findViewById(R.id.ivMenuItemPicture);
                tvMenuItemName = detailDialog.findViewById(R.id.tvMenuItemName);
                bAdd = detailDialog.findViewById(R.id.bAdd);
                bRemove = detailDialog.findViewById(R.id.bRemove);
                tvPiece = detailDialog.findViewById(R.id.tvPiece);
                tvPrice = detailDialog.findViewById(R.id.tvPrice);
                bAddToCart = detailDialog.findViewById(R.id.bAddToCart);
                tvCancel = detailDialog.findViewById(R.id.tvCancel);

                String url = Constants.ROOT_URL + currentRestaurantMenuCardItem.getPicture();
                Picasso.get().load(url).into(ivMenuItemPicture);

                tvMenuItemName.setText(currentRestaurantMenuCardItem.getName());
                tvPrice.setText(String.valueOf(currentRestaurantMenuCardItem.getPrice()));

                bAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Integer.parseInt(tvPiece.getText().toString()) > 0) {
                            tvPiece.setText(String.valueOf(Integer.parseInt(tvPiece.getText().toString()) + 1));
                            tvPrice.setText(String.valueOf(Integer.parseInt(tvPiece.getText().toString()) * currentRestaurantMenuCardItem.getPrice()));
                        }
                    }
                });
                bRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Integer.parseInt(tvPiece.getText().toString()) > 1) {
                            tvPiece.setText(String.valueOf(Integer.parseInt(tvPiece.getText().toString()) - 1));
                            tvPrice.setText(String.valueOf(Integer.parseInt(tvPiece.getText().toString()) * currentRestaurantMenuCardItem.getPrice()));
                        }
                    }
                });

                bAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CartActivity.myMenuCard.contains(currentRestaurantMenuCardItem)) {
                            currentRestaurantMenuCardItem.setPiece(currentRestaurantMenuCardItem.getPiece() + Integer.parseInt(tvPiece.getText().toString()));
                        } else {
                            CartActivity.myMenuCard.add(currentRestaurantMenuCardItem);
                            currentRestaurantMenuCardItem.setPiece(Integer.parseInt(tvPiece.getText().toString()));
                        }
                        Toast.makeText(mContext, Integer.parseInt(tvPiece.getText().toString()) + " db " + currentRestaurantMenuCardItem.getName() + " a kosárba helyezve", Toast.LENGTH_SHORT).show();
                        detailDialog.dismiss();

                    }
                });

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        detailDialog.dismiss();
                    }
                });

                detailDialog.show();
            }
        });
    }

    private void loadRestaurantMenuItems() {
        menuCard.clear();
        if (checkNetworkConnection()) {
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_GETMENUCARD,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if (!jsonObject.getBoolean("error")) {

                                    JSONArray menucard = jsonObject.getJSONArray("menucard");

                                    for (int i = 0; i < menucard.length(); i++) {

                                        JSONObject menucardJSONObject = menucard.getJSONObject(i);

                                        int id = menucardJSONObject.getInt("id");
                                        String picture = menucardJSONObject.getString("picture");
                                        String name = menucardJSONObject.getString("name");
                                        int price = menucardJSONObject.getInt("price");

                                        Charset UTF8 = Charset.forName("UTF-8");
                                        Charset ISO = Charset.forName("ISO-8859-1");
                                        name = new String(name.getBytes(ISO), UTF8);

                                        RestaurantMenuCardItem restaurantMenuCardItem = new RestaurantMenuCardItem(id, picture, name, price);
                                        menuCard.add(restaurantMenuCardItem);

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
           // readFromLocalStorage();
        }
    }


}
