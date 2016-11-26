package com.gjorgjimarkov.caffeineperday;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class StartingActivity extends AppCompatActivity {

    private ImageView logo;
    private RelativeLayout enterLayout;
    private EditText age;
    private int years;
    private int dosemax=400;
    private AdView ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        ad = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);

        age = (EditText)findViewById(R.id.ageEditText);
        logo = (ImageView)findViewById(R.id.logo);
        enterLayout = (RelativeLayout)findViewById(R.id.enterLayout);
        enterLayout.setVisibility(RelativeLayout.INVISIBLE);

        StartingAnimation();

    }

    public void StartingAnimation() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Animation sliding = new TranslateAnimation(logo.getX(),logo.getX(),height /5, logo.getY());
        sliding.setInterpolator(new AccelerateDecelerateInterpolator());
        sliding.setStartOffset(1500);
        sliding.setDuration(600);

        logo.startAnimation(sliding);

        Animation showing = new AlphaAnimation(0.0f,1.0f);
        showing.setDuration(600);
        showing.setStartOffset(1500);

        enterLayout.startAnimation(showing);

        enterLayout.setVisibility(RelativeLayout.VISIBLE);
    }

    public void enterChosingActivity(View view) {
        if(age.getText().toString().isEmpty()) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(StartingActivity.this);
            alert.setIcon(R.drawable.alert_icon);
            alert.setTitle("Caffeine per Day");
            alert.setCancelable(true);
            alert.setMessage("You didn't put in your age." +
                    "\nFor healthy adults, the average dose of caffeine that can be consumed per day is up to " + dosemax + "mg.");
            alert.setNegativeButton("Calculate your Caffeine", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(StartingActivity.this,DrinksListActivity.class);
                    i.putExtra("dosemax", dosemax);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(i);
                }
            });
            alert.setNeutralButton("My Age", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {

                        }
                    });
                }
            });
            alert.show();
        }
        else {
            years = Integer.parseInt(age.getText().toString());
            if(years<18) {
                dosemax=100;
            }
            else {
                if(years>=18 && years <60) {
                    dosemax=400;
                }
                else {
                    dosemax=200;
                }
            }
            Intent i = new Intent(StartingActivity.this,DrinksListActivity.class);
            i.putExtra("dosemax", dosemax);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(i);
        }
    }
}
