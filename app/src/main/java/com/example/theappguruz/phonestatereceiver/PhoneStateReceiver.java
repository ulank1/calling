package com.example.theappguruz.phonestatereceiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by theappguruz on 07/05/16.
 */
public class PhoneStateReceiver extends BroadcastReceiver {
    private static boolean incomingCall = false;

    WindowManager.LayoutParams params;
    private static WindowManager windowManager;
    private static ViewGroup windowLayout;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //
                /*
                try {
                    //Грязноватый хак, рекомендуемый многими примерами в сети, но не обязательный
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    //ну и ладно
                }*/
                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                incomingCall = true;
                Log.e("Show window: ", phoneNumber);
                showWindow(context, phoneNumber);

            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //Телефон находится в режиме звонка (набор номера / разговор) - закрываем окно, что бы не мешать
                if (incomingCall) {
                    Log.e("Close window.", ".");
                    closeWindow(context);
                    incomingCall = false;
                }
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                //Телефон находится в ждущем режиме - это событие наступает по окончанию разговора
                //или в ситуации "отказался поднимать трубку и сбросил звонок"
                if (incomingCall) {
                    Log.e("Close window.", "sss");
                    closeWindow(context);
                    incomingCall = false;
                }
            }
        }
    }

    private void showWindow(final Context context, String phone) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP;

        windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info, null);

       // TextView textViewNumber = (TextView) windowLayout.findViewById(R.id.textViewNumber);
        Button buttonClose = (Button) windowLayout.findViewById(R.id.buttonClose);
        Button button = (Button) windowLayout.findViewById(R.id.button_zv);
     //   textViewNumber.setText(phone);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWindow(context);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TAGGG", "sdddssddqqwww");

                Intent intent = new Intent(Intent.ACTION_CALL);

                String prefix = "**67*0706530305#";
                prefix = Uri.encode(prefix);

                intent.setData( Uri.parse("tel:"+prefix+"#"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(intent);

            }
        });

        windowManager.addView(windowLayout, params);
    }

    private void closeWindow(Context context) {

        Log.e("TAG", "SSSSSS");
        if (windowLayout != null) {
        windowManager.removeView(windowLayout);
            windowLayout=null;


        }
    }
}

