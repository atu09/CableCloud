package com.mtaj.mtaj_08.cableplus_new;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by MTAJ-08 on 8/30/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    String msg1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
       Log.d(TAG, "Notification Message Body: " + remoteMessage.getData().get("message"));


      //  sendNotification(remoteMessage.getData().get("message"));

      /*  StringTokenizer tokens = new StringTokenizer(msg, "_");
        String msg1=tokens.nextToken();
        String cmpid=tokens.nextToken();
        String custid=tokens.nextToken();*/

       // String msg=remoteMessage.getData().get("message");

      //  String[] separated = msg.split("_");
     // String msg1 = separated[0];
     //   String cmpid=separated[1];
     //  String custid=separated[2];

        //Calling method to generate notification
        sendNotification(remoteMessage.getFrom(),remoteMessage.getData().get("message"),remoteMessage.getCollapseKey());
    }


    /*private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cableplus_64)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification , notificationBuilder.build());
    }*/


    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String from,String messageBody,String type)
    {
        try {

        if(messageBody!=null) {

            String custid, cmpid;

            final  String[] separated = messageBody.split("_");
            msg1 = separated[0];

            Intent intent =  new Intent(this, ComplainDetails.class);;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // final String cmid=cmpid;
            //   final String cuid=custid;

            if (type.equals("comment")) {

                cmpid = separated[1];

                intent = new Intent(this, Comments_List_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("Complainid", cmpid);
                intent.putExtra("From","Notification");

            }

            if (type.equals("new_complain")) {


                cmpid = separated[1];
                custid = separated[2];

                intent = new Intent(this, ComplainDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("complainId", cmpid);
                intent.putExtra("customerId", custid);
            }

            if (type.equals("assign")) {

                cmpid = separated[1];
                custid = separated[2];

                intent = new Intent(this, ComplainDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("complainId", cmpid);
                intent.putExtra("customerId", custid);
            }

            if (type.equals("resolve")) {


                cmpid = separated[1];
                custid = separated[2];

                intent = new Intent(this, ComplainDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("complainId", cmpid);
                intent.putExtra("customerId", custid);
            }

            if (type.equals("reopen")) {


                cmpid = separated[1];
                custid = separated[2];

                intent = new Intent(this, ComplainDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("complainId", cmpid);
                intent.putExtra("customerId", custid);
            }



                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.cableplus_64_login)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg1))
                        .setContentTitle("Cable Plus Notification")
                        .setContentText(msg1)
                        .setAutoCancel(true)
                        .setSound(uri)
                        .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_launcher))
                        .setContentIntent(pendingIntent);

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    if(!type.equals("badge"))
                    {
                        notificationManager.notify(0, notificationBuilder.build());
                    }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
