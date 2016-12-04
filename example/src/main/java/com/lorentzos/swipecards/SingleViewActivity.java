package com.lorentzos.swipecards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lorentzos.flingswipe.internal.CardEventListener;
import com.lorentzos.flingswipe.internal.Direction;
import com.lorentzos.flingswipe.internal.SwipeOperator;

public class SingleViewActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_view);

		View view = findViewById(R.id.simpleView);

		view.setOnTouchListener(new SwipeOperator(15f, new CardEventListener() {
			@Override
			public void onCardExited(View view, @Direction int direction) {

			}

			@Override
			public void onScroll(View view, float scrollProgressPercent, @Direction int direction) {

			}

			@Override
			public void onRecenter(View view) {

			}

			@Override
			public void onClick(View view) {

			}
		}));

	}
}
