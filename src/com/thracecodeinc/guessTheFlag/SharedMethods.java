package com.thracecodeinc.guessTheFlag;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by Samurai on 4/19/15.
 */
public class SharedMethods {
    static AlertDialog resetDialog;
    private static int exitGame;
    private static Intent i = null;
    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static void networkModePopup(final Context context){

        String title = "";
        String message = "";
        if (context instanceof MyActivity) {
            title = "Internet connection lost";
            message = "Would you like to continue playing offline?";
            i = new Intent(context, OfflineGameClass.class);
            exitGame = 1;

        } else if (context instanceof OfflineGameClass){
            title = "Internet connection on";
            message = "Would you like to go back to the map?";
            i = new Intent(context, MyActivity.class);
            exitGame = 2;
        }

        else if (context instanceof startPage){
            title = "Internet connection is off";
            message = "Would you like to play offline?";
            i = new Intent(context, OfflineGameClass.class);
            exitGame = 3;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);

        builder.setMessage(message);

        builder.setCancelable(false);
        builder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (exitGame!=3)
                            context.startActivity(i);
                        else
                            startPage.playOfflineButton.performClick();
                    }
                }
        );
        builder.setNegativeButton("no",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (exitGame==1) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            resetDialog.dismiss();
                        }

                    }
                }
        );

        resetDialog = builder.create();
        resetDialog.show();

    }

    public static int emoticon(float score){
        if (score < 25)
            return R.drawable.very_bad;
        else if (score < 50)
            return R.drawable.smiley_bad;
        else if (score < 70)
            return R.drawable.smiley_average;
        else if (score < 90)
            return R.drawable.better;
        else
            return R.drawable.excellent;

    }

    public static TextView customText(Context context){
        TextView title = new TextView(context);
        title.setText(R.string.reset_quiz);
        title.setBackgroundColor(context.getResources().getColor(R.color.wallet_holo_blue_light));
        //title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        return title;
    }


}
