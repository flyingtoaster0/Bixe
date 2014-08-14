package com.flyingtoaster.bikemapper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends Activity{
    private final String URL_SLIDINGUP_PANEL = "https://github.com/umano/AndroidSlidingUpPanel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
    }

    public void sendFeedback(View v) {
        String[] email = {getString(R.string.contact_email)};
        String appName = getString(R.string.app_name);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, appName + "Feedback");

        startActivity(Intent.createChooser(intent, "Send Feedback"));
    }

    public void onClickAboutSlidingUpPanel(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(URL_SLIDINGUP_PANEL));
        startActivity(i);
    }

    public void onTimClicked(View v) {

    }
}
