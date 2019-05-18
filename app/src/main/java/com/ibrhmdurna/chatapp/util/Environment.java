package com.ibrhmdurna.chatapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibrhmdurna.chatapp.R;
import com.ibrhmdurna.chatapp.main.SearchActivity;
import com.ibrhmdurna.chatapp.main.WriteActivity;
import com.ibrhmdurna.chatapp.settings.SettingsActivity;

import java.util.Objects;

public class Environment {

    private enum State{
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private State state;

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
        Objects.requireNonNull(context.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        context.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_icon);
    }

    public void toolbarProcess(final Activity context){

        final TextView toolbarTitle = context.findViewById(R.id.toolbar_title);

        AppBarLayout appBarLayout = context.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            private State state;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (i == 0){
                    if(state != State.EXPANDED){
                        toolbarTitle.setVisibility(View.GONE);
                    }
                    state = State.EXPANDED;
                }
                else if(Math.abs(i) >= appBarLayout.getTotalScrollRange()){
                    if(state != State.COLLAPSED){
                        toolbarTitle.setVisibility(View.VISIBLE);
                    }
                    state = State.COLLAPSED;
                }
                else{
                    if(state != State.IDLE){


                        if(toolbarTitle.getVisibility() == View.VISIBLE){
                            Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                            toolbarTitle.setAnimation(fadeOut);
                        }
                        else{
                            Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                            toolbarTitle.setAnimation(fadeIn);
                        }
                    }
                    state = State.IDLE;
                }
            }
        });

        ImageButton backBtn = context.findViewById(R.id.toolbar_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
    }

    public void toolbarProcessMain(final Activity context){

        final TextView toolbarTitle = context.findViewById(R.id.toolbar_title);

        AppBarLayout appBarLayout = context.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            private State state;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (i == 0){
                    if(state != State.EXPANDED){
                        toolbarTitle.setVisibility(View.GONE);
                    }
                    state = State.EXPANDED;
                }
                else if(Math.abs(i) >= appBarLayout.getTotalScrollRange()){
                    if(state != State.COLLAPSED){
                        toolbarTitle.setVisibility(View.VISIBLE);
                    }
                    state = State.COLLAPSED;
                }
                else{
                    if(state != State.IDLE){


                        if(toolbarTitle.getVisibility() == View.VISIBLE){
                            Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                            toolbarTitle.setAnimation(fadeOut);
                        }
                        else{
                            Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                            toolbarTitle.setAnimation(fadeIn);
                        }
                    }
                    state = State.IDLE;
                }
            }
        });

        ImageButton writeBtn = context.findViewById(R.id.toolbar_write_btn);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent writeIntent = new Intent(context, WriteActivity.class);
                context.startActivity(writeIntent);
            }
        });

        ImageButton searchBtn = context.findViewById(R.id.toolbar_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(context, SearchActivity.class);
                context.startActivity(searchIntent);
            }
        });
    }

    public void toolbarProcessSearchBar(final Activity context){

        final TextView toolbarTitle = context.findViewById(R.id.toolbar_title);
        final LinearLayout searchInputLayout = context.findViewById(R.id.search_layout);
        final EditText searchInput = context.findViewById(R.id.search_input);
        final ImageButton searchBtn = context.findViewById(R.id.toolbar_search_btn);

        toolbarTitle.setVisibility(View.VISIBLE);

        AppBarLayout appBarLayout = context.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (i == 0){
                    if(state != State.EXPANDED){
                        toolbarTitle.setVisibility(View.GONE);
                    }
                    state = State.EXPANDED;
                }
                else if(Math.abs(i) >= appBarLayout.getTotalScrollRange()){
                    if(state != State.COLLAPSED){
                        if(searchInputLayout.getVisibility() != View.VISIBLE){
                            toolbarTitle.setVisibility(View.VISIBLE);
                        }
                    }
                    state = State.COLLAPSED;
                }
                else{
                    if(state != State.IDLE){


                        if(toolbarTitle.getVisibility() == View.VISIBLE){
                            Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                            toolbarTitle.setAnimation(fadeOut);
                        }
                        else{
                            Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                            toolbarTitle.setAnimation(fadeIn);
                        }
                    }
                    state = State.IDLE;
                }
            }
        });

        ImageButton backBtn = context.findViewById(R.id.toolbar_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchInputLayout.getVisibility() == View.VISIBLE) {
                    toolbarTitle.setVisibility(View.GONE);
                    toolbarTitle.clearAnimation();
                    searchInputLayout.setVisibility(View.GONE);
                    searchBtn.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);

                    if(state == State.COLLAPSED){
                        toolbarTitle.setVisibility(View.VISIBLE);
                    }
                    else if (state == State.EXPANDED){
                        toolbarTitle.setVisibility(View.GONE);
                    }
                }
                else {
                    context.onBackPressed();
                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(500);
                toolbarTitle.setVisibility(View.GONE);
                searchInputLayout.setAnimation(fadeIn);
                searchInputLayout.setVisibility(View.VISIBLE);
                searchBtn.setVisibility(View.GONE);
                searchInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    public void toolbarProcessSubtitle(final Activity context){

        final LinearLayout toolbarTitleLayout = context.findViewById(R.id.toolbar_title_layout);


        AppBarLayout appBarLayout = context.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            private State state;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (i == 0){
                    if(state != State.EXPANDED){
                        toolbarTitleLayout.setVisibility(View.GONE);
                    }
                    state = State.EXPANDED;
                }
                else if(Math.abs(i) >= appBarLayout.getTotalScrollRange()){
                    if(state != State.COLLAPSED){
                        toolbarTitleLayout.setVisibility(View.VISIBLE);
                    }
                    state = State.COLLAPSED;
                }
                else{
                    if(state != State.IDLE){

                        if(toolbarTitleLayout.getVisibility() == View.VISIBLE){
                            Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                            toolbarTitleLayout.setAnimation(fadeOut);
                        }
                        else{
                            Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                            toolbarTitleLayout.setAnimation(fadeIn);
                        }
                    }
                    state = State.IDLE;
                }
            }
        });

        ImageButton backBtn = context.findViewById(R.id.toolbar_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
    }
}
