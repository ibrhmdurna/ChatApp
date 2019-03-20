package com.ibrhmdurna.chatapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ibrhmdurna.chatapp.Application.App;
import com.ibrhmdurna.chatapp.R;

public class Environment {
    public static void toolbarProcess(AppCompatActivity context, int id){
        Toolbar toolbar = context.findViewById(id);
        context.setSupportActionBar(toolbar);
        context.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_icon);
    }
}
