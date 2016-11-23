package com.ideationdesignservices.txtbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import com.flurry.android.FlurryAgent;

public class SplashScreenActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Thread() {
            public void run() {
                try {
                    AnonymousClass1.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    SplashScreenActivity.this.startActivity(new Intent(SplashScreenActivity.this.getApplicationContext(), IntroActivity.class));
                }
            }
        }.start();
    }

    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Txtbook.FLURRY_KEY);
    }

    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
