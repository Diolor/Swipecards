package com.lorentzos.swipecards;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.lorentzos.flingswipe.OnExitListener;
import com.lorentzos.flingswipe.OnRecenterListener;
import com.lorentzos.flingswipe.OnScrollListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.lorentzos.flingswipe.internal.Direction;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MyActivity extends Activity {

	private ArrayList<String> list;
	private ArrayAdapter<String> arrayAdapter;

	@InjectView(R.id.frame)
	SwipeFlingAdapterView flingContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);
		ButterKnife.inject(this);

		list = new ArrayList<>();
		list.add("php");
		list.add("c");
		list.add("python");
		list.add("java");
		list.add("html");
		list.add("c++");
		list.add("css");
		list.add("javascript");

		arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, list);

		flingContainer.setAdapter(arrayAdapter);
		flingContainer.setOnExitListener(new OnExitListener() {
			@Override
			public void onExit(View view, @Direction int direction) {
				Log.i("MyActivity", "onExit " + direction);
				list.remove(0);
				arrayAdapter.notifyDataSetChanged();
			}
		});

		// Optionally add an OnItemClickListener
		flingContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i("MyActivity", "onItemClick " + position);
			}
		});

		flingContainer.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(View view, float scrollProgressPercent) {
				Log.i("MyActivity", "onScroll " + scrollProgressPercent);
			}
		});
		flingContainer.setOnRecenterListener(new OnRecenterListener() {
			@Override
			public void onRecenter(View view) {
				Log.i("MyActivity", "onRecenter");
			}
		});
	}

	@OnClick(R.id.right)
	public void swipeRight() {
		//Trigger the right event manually.
		flingContainer.swipeRight();
	}

	@OnClick(R.id.left)
	public void swipeLeft() {
		//Trigger the right event manually.
		flingContainer.swipeLeft();
	}

}
