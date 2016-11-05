package com.lorentzos.swipecards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lorentzos.flingswipe.SwipeOperator;

public class SimpleViewActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_view);

		View view = findViewById(R.id.simpleView);

		view.setOnTouchListener(new SwipeOperator());

	}
}
