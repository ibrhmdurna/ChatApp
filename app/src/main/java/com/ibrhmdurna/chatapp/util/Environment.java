package com.ibrhmdurna.chatapp.util;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ibrhmdurna.chatapp.R;

public class Environment {

    private static Environment instance;

    private Environment(){}

    public static synchronized Environment getInstance(){
        if(instance == null){
            synchronized (Environment.class){
                instance = new Environment();
            }
        }
        return instance;
    }

    public void toolbarProcess(AppCompatActivity context, int id){
        Toolbar toolbar = context.findViewById(id);
        context.setSupportActionBar(toolbar);
        context.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_icon);
    }
}
