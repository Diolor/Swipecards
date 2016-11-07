package com.lorentzos.swipecards;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MyActivity extends Activity {

    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;
    private int i;
    private boolean left_enable_switch = false;
    private boolean right_enable_switch = false;

    @Bind(R.id.frame) SwipeFlingAdapterView flingContainer;
    @Bind(R.id.left_enable_switch) ToggleButton lToggle;
    @Bind(R.id.right_enable_switch) ToggleButton rToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.bind(this);

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

          final SwipeFlingAdapterView.onFlingListener listener =
                        new SwipeFlingAdapterView.onFlingListener() {

            @Override
            public Pair<Boolean,Boolean> isEnabled() {
                return new Pair<>(left_enable_switch,right_enable_switch);
            }

            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                makeToast(MyActivity.this, "Left!");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                makeToast(MyActivity.this, "Right!");
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
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        };

        flingContainer.setFlingListener(listener);

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                makeToast(MyActivity.this, "Clicked!");
            }
        });

        lToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                left_enable_switch = isChecked;
            }
        });

        rToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                right_enable_switch = isChecked;
            }
        });
    }

    static void makeToast(Context ctx, String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }


}
