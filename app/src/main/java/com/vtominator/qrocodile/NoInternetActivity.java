package com.vtominator.qrocodile;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.vtominator.qrocodile.Utils.SharedPrefManager;

public class NoInternetActivity extends AppCompatActivity {
    private static final String TAG = "NoInternetActivity";
    private Context mContext = NoInternetActivity.this;
    private String previousIntentName;

    private SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();

        setContentView(R.layout.no_internet);

        Intent prevInt = getIntent();
        previousIntentName = prevInt.getStringExtra("previousIntentName");

        refreshMenuList();
        topToolbar();
    }

    @Override
    public void onBackPressed() {
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
        inflater.inflate(R.menu.order_menubar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.menuMenu:
                startActivity(new Intent(mContext, MenucardActivity.class));
                break;

            case R.id.menuCart:
                startActivity(new Intent(mContext, CartActivity.class));
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

    private void refreshMenuList() {
        pullToRefresh = findViewById(R.id.layoutSwipeRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (checkNetworkConnection()) {

                    if (previousIntentName.equals("OrderActivity")) {
                        startActivity(new Intent(mContext, OrderActivity.class));
                        finish();
                    } else if(previousIntentName.equals("CartActivity")){
                        startActivity(new Intent(mContext, CartActivity.class));
                        finish();
                    }

                }
                pullToRefresh.setRefreshing(false);
            }
        });
    }
}
