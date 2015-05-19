package com.lorentzos.swipecards;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.lorentzos.flingswipe.Direction;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MyActivity extends Activity {

    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;
    private int i;

    @InjectView(R.id.frame) SwipeFlingAdapterView flingContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.inject(this);


        al = new ArrayList<>();
        al.add("php");
        al.add("c");
        al.add("python");
        al.add("java");
        al.add("html");
        al.add("c++");
        al.add("css");
        al.add("javascript");

        arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, al );


        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCardExit(int direction, Object dataObject) {

                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

                if (Direction.hasLeft(direction)){
                    makeToast(MyActivity.this, "Left!");
                } else if (Direction.hasRight(direction)){
                    makeToast(MyActivity.this, "Right!");
                } else if (Direction.hasTop(direction)){
                    makeToast(MyActivity.this, "Top!");
                } else if (Direction.hasBottom(direction)){
                    makeToast(MyActivity.this, "Bottom!");
                } else {
                    makeToast(MyActivity.this, "No known direction!");
                }
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                al.add("XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercentHorizontal, float scrollProgressPercentVertical) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercentHorizontal < 0 ? -scrollProgressPercentHorizontal : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercentHorizontal > 0 ? scrollProgressPercentHorizontal : 0);
                view.findViewById(R.id.item_swipe_bottom_indicator).setAlpha(scrollProgressPercentVertical < 0 ? -scrollProgressPercentVertical : 0);
                view.findViewById(R.id.item_swipe_top_indicator).setAlpha(scrollProgressPercentVertical > 0 ? scrollProgressPercentVertical : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                makeToast(MyActivity.this, "Clicked!");
            }
        });

    }

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.right)
    public void right() {
        /**
         * Trigger the right event manually.
         */
        flingContainer.getTopCardListener().selectRight();
    }

    @OnClick(R.id.left)
    public void left() {
        flingContainer.getTopCardListener().selectLeft();
    }

    @OnClick(R.id.top)
    public void top() {
        flingContainer.getTopCardListener().selectTop();
    }

    @OnClick(R.id.bottom)
    public void bottom() {
        flingContainer.getTopCardListener().selectBottom();
    }



}
