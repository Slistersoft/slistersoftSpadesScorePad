package com.slistersoft.slistersoftspadesscorepad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;


public class CUSTOM_FUNCTIONS extends Activity {

    private Context callingContext;


    public CUSTOM_FUNCTIONS(Context context) {

        callingContext = context;

    }

    public boolean getPreference(Context context, String key, boolean defaultValue){

        try{

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            return prefs.getBoolean(key,defaultValue);

        }
        catch(Exception e){
            MsgBox(this,e.getMessage(),true);
            return defaultValue;
        }

    }

    public String getPreference(Context context, String key, String defaultValue){

        try {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            return prefs.getString(key, defaultValue);

        }
        catch (Exception e){
            return defaultValue;
        }

    }



    public void MsgBox(Context activity, String message, boolean angryMsg){

        if(angryMsg){

            try{
                vibrateDevice(activity);
            }
            catch(Exception e){}
        }



        try{

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_btn_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();



        }
        catch(Exception e){}

    }

    public void MsgBox(String message, boolean angryMsg){

        MsgBox(callingContext, message, angryMsg);

    }

    public void MsgBox(String message){

        MsgBox(message, false);

    }

    public void showToast(String message, int durationMS){
        try{
            Toast.makeText(callingContext, message, durationMS).show();
        }
        catch(Exception e){}
    }

    public void showToast(String message){
        showToast(message, 5000);
    }


    public void vibrateDevice(Context con){

        try{
            //No time was set so use default 500 milliseconds

            //check for vibration preference

            boolean vibrationEnabled =  getPreference(con, con.getResources().getString(R.string.pref_key_vibration), con.getResources().getBoolean(R.bool.pref_default_vibration));

            if(!vibrationEnabled)
                return;

            int defaultVibrationDurationInMS = 100;
            vibrateDevice(con, defaultVibrationDurationInMS);
        }
        catch(Exception e){}

    }

    public void vibrateDevice(Context con, int vibTimeInMS){

        try {

            Vibrator v = (Vibrator) con.getSystemService(VIBRATOR_SERVICE);
            v.vibrate(vibTimeInMS);
        }
        catch (Exception ex){

        }
    }

    public boolean amIWinning(int teamNumber, int t1Score, int t2Score){

        if (teamNumber == 1) {
            return t1Score > t2Score;
        } else
            return teamNumber == 2 && t2Score > t1Score;

    }


}
