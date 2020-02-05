package com.vtominator.qrocodile.View;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vtominator.qrocodile.R;

public class LoginActivity extends AppCompatActivity {

    private Context mContext = LoginActivity.this;
    private Button bLogin;
    private TextView tvMoreInfo;
    private ImageView ivLogo;
    private long startTime;
    private int buttonPressed;

//     ******* QR-SCAN nélküli teszteléshez beégetett változók *******
//     public static String restaurant="restaurant_2";
//     public static int table_id=1;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setContentView(R.layout.activity_login);

        ivLogo = findViewById(R.id.ivLogo);
        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();

                if (startTime == 0 || (currentTime - startTime > 5000)) {
                    startTime = currentTime;
                    buttonPressed = 0;
                } else {
                    buttonPressed++;
                    if (buttonPressed > 2 && buttonPressed <= 7)
                        Toast.makeText(mContext, "Még " + String.valueOf(7 - buttonPressed) + " kattintás szükséges a fejlesztői módba lépéshez", Toast.LENGTH_SHORT).show();
                }

                if (buttonPressed == 7) {
                    ScannerActivity.restaurant = "restaurant_2";
                    ScannerActivity.table_id = 1;

                    Toast.makeText(mContext, "Fejlesztői mód engedélyezve", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, MenucardActivity.class);
                    intent.putExtra("restaurant", ScannerActivity.restaurant);
                    intent.putExtra("table_id", ScannerActivity.table_id);
                    startActivity(intent);

                }
            }
        });


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


                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://qrocodile.nhely.hu/"));
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
