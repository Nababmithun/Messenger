package com.example.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by AtifNaseem on 10/29/2017.
 */

public class SMSReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    //firebase user
    private FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    String currentUserId;
    DatabaseReference rootRef;
    int i;


    @Override
    public void onReceive(Context context, Intent intent) {


        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("message");

        Bundle bundle = intent.getExtras();
        if(intent.getAction().equalsIgnoreCase("android.provider.Telephony.SMS_RECEIVED")) {
            if (bundle != null) {
                Object[] sms = (Object[]) bundle.get(SMS_BUNDLE);
                String smsMsg = "";
                String meg = "";
                String megNumber = "";

                SmsMessage smsMessage;
                for (int i = 0; i < sms.length; i++) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        String format = bundle.getString("format");
                        smsMessage = SmsMessage.createFromPdu((byte[]) sms[i],format);
                    }else{
                        smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                    }


                    String msgBody = smsMessage.getMessageBody().toString();
                    String msgAddress = smsMessage.getOriginatingAddress();

                    smsMsg += "SMS from : " + msgAddress + "\n";
                    smsMsg += msgBody + "\n";
                    megNumber = msgAddress;
                    meg = msgBody;

                    HashMap<String , Object> dataMap = new HashMap<>();
                    dataMap.put("Message Body",meg);
                    dataMap.put("Number",megNumber);

                        rootRef.setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    //  Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                                    Log.d("mr","save");
                                }
                            }
                        });





                }
                Toast.makeText(context, ""+smsMsg, Toast.LENGTH_SHORT).show();
               /* MainActivity inst = MainActivity.Instance();
                inst.setMeg(smsMsg);*/
            }
        }
    }





}
