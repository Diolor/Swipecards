package com.lorentzos.swipecards;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.lorentzos.flingswipe.onFlingListener;

import java.util.ArrayList;


public class MyActivity extends Activity implements onFlingListener {


    private View v;
    private float originalX;
    private float originalY;
    private float centerY;
    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        al = new ArrayList<String>();
        al.add("php");
        al.add("c");
        al.add("python");
        al.add("java");

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.item, R.id.helloText, al );

        flingContainer.setRemoveObjectsListener(this);
        flingContainer.setAdapter(arrayAdapter);

    }

    @Override
    public void removeFirstObjectInAdapter() {
        Log.d("LIST", "removed ovject!");
        al.remove(0);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLeftCardExit() {
        Toast.makeText(this,"Left!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRightCardExit() {
        Toast.makeText(this,"Right!",Toast.LENGTH_SHORT).show();
    }


}
