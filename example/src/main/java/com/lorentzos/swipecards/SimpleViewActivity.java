package com.lorentzos.swipecards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lorentzos.flingswipe.FlingCardListener;

public class SimpleViewActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_view);

		View view = findViewById(R.id.activity_simple_view);
		((View) view.getParent()).setOnTouchListener(new FlingCardListener(view, 0, new FlingCardListener.FlingListener() {
			@Override
			public void onCardExited() {

			}

			@Override
			public void leftExit(Object dataObject) {

			}

			@Override
			public void rightExit(Object dataObject) {

			}

			@Override
			public void onClick(Object dataObject) {

			}

			@Override
			public void onScroll(float scrollProgressPercent) {

			}
		}));

	}
}
