package com.vtominator.qrocodile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private Context mContext = LoginActivity.this;
    private Button bLogin;
    private TextView tvMoreInfo;

//     ******* QR-SCAN nélküli teszteléshez beégetett változók *******
//     public static String restaurant="restaurant_2";
//     public static int table_id=1;

    @Override
    public void onBackPressed() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_login);

        bLogin = findViewById(R.id.bLogin);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ScannerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvMoreInfo = findViewById(R.id.tvMoreInfo);
        tvMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.pcservices.nhely.hu/#home"));
                startActivity(browserIntent);
                //finish();
            }
        });

    }

    private void fullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
