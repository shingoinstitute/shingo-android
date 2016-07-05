package org.shingo.shingoapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.shingo.shingoapp.R;

public class ModelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);
        TouchImageView img = (TouchImageView)findViewById(R.id.model);
        img.setImageResource(R.drawable.model);
    }
}
