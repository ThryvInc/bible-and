package com.thryv.bible.models;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;

import java.util.Date;

/**
 * Created by ell on 12/24/16.
 */

public class NagController {
    protected static final String PREFS_NAME = "nagger";
    protected Activity activity;

    public NagController(Activity activity) {
        this.activity = activity;
    }

    public void startNag(final String freeLink, final String paidLink){
        final int appOpens = getNumberOfOpens(activity);
        if (appOpens != 0 && appOpens % 7 - 3 == 0){
            if (getLastNagged().before(new Date(new Date().getTime() - 3 * 24 * 60 * 60 * 1000))){
                if (canNagRating()){
                    if (freeLink != null){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (activity != null && !activity.isFinishing()){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    builder.setTitle("Sorry to interrupt...");
                                    builder.setMessage("...but would you mind terribly taking a moment to rate this app?");
                                    builder.setCancelable(false);
                                    builder.setNeutralButton("Yes, I'd be\ndelighted!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            setCanNagRating(false);
                                            dialogInterface.cancel();
                                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(freeLink)));
                                        }
                                    });
                                    builder.setNegativeButton("Nope,\nnever", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            setCanNagRating(false);
                                            dialog.cancel();
                                        }
                                    });
                                    builder.setPositiveButton("Mmmm,\nmaybe later", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    builder.create().show();
                                }
                            }
                        }, 17000);
                    }
                }else if (canNagApp()){
                    if (paidLink != null){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (activity != null && !activity.isFinishing()){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    builder.setTitle("Hey!");
                                    builder.setMessage("You seem to be enjoying the app, would you like to help support an independent app developer and download the paid version?");
                                    builder.setCancelable(false);
                                    builder.setNeutralButton("Yes, I'd be\ndelighted!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            setCanNagApp(false);
                                            dialogInterface.cancel();
                                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(paidLink)));
                                        }
                                    });
                                    builder.setNegativeButton("I prefer\nclicking ads", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            setCanNagApp(false);
                                            dialog.cancel();
                                        }
                                    });
                                    builder.setPositiveButton("Mmmm,\nmaybe later", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    builder.create().show();
                                }
                            }
                        }, 17000);
                    }
                }
            }
        }
    }

    protected boolean canNagRating(){
        return activity.getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).getBoolean("canNagRating", true);
    }

    protected boolean canNagApp(){
        return false;//activity.getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).getBoolean("canNagApp", true);
    }

    protected void setCanNagRating(boolean canNag){
        SharedPreferences.Editor editor = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).edit();
        editor.putBoolean("canNagRating", canNag).apply();
    }

    protected void setCanNagApp(boolean canNag){
        SharedPreferences.Editor editor = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE).edit();
        editor.putBoolean("canNagApp", canNag).apply();
    }

    protected Date getLastNagged(){
        SharedPreferences preferences = activity.getApplicationContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
        if (preferences.getLong("lastNagged", 0) == 0){
            return new Date(preferences.getLong("lastNagged", 0));
        }else {
            return new Date(preferences.getLong("lastNagged", new Date().getTime()));
        }
    }

    public static int getNumberOfOpens(Context context){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("Analytics", Context.MODE_PRIVATE);
        return preferences.getInt("number_of_app_opens", 0);
    }

    public static void incrementNumberOfOpens(Context context){
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences("Analytics", Context.MODE_PRIVATE);
        int numberOfOpens = preferences.getInt("number_of_app_opens", 0);
        numberOfOpens++;
        preferences.edit().putInt("number_of_app_opens", numberOfOpens).apply();
    }
}
