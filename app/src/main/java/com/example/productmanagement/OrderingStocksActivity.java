package com.example.productmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;


public class OrderingStocksActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering_stocks);
    }

    /*@Override
    public void itemClicked(){

        *//*View fragmentContainer = findViewById(R.id.fragment_View2output);
        if (fragmentContainer!=null) {

            OutputViewFragment outputView = new OutputViewFragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_View2output, outputView);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();*//*

        *//*}*//*
    }*/

}