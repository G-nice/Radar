package com.gnice.radar.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.gnice.radar.AppData;

import java.util.Date;

// 进行短信交换地理位置信息的模块  收发拦截短信
public class MySMSManager extends BroadcastReceiver {

    private IntentFilter receiveFilter;
    private Activity activity;

    public MySMSManager() {
        super();
    }

    public MySMSManager(Activity activity) {
        receiveFilter = new IntentFilter();
        receiveFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        receiveFilter.setPriority(999);
        this.activity = activity;
        activity.registerReceiver(this, receiveFilter);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //        Bundle bundle = intent.getExtras();
        //        Object[] pdus = (Object[]) bundle.get("pdus"); // 提取短信消息
        //
        //        SmsMessage[] messages = new SmsMessage[pdus.length];
        //
        //        for (int i = 0; i < messages.length; i++) {
        //            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        //        }
        //
        //        String address = messages[0].getOriginatingAddress(); // 获取发送方号码
        //        String fullMessage = "";
        //        for (SmsMessage message : messages) {
        //            fullMessage += message.getMessageBody(); // 获取短信内容
        //        }
        //        sender.setText(address);
        //        conte.setText(fullMessage);

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");  // 获得短信数据
        byte[] pdu = (byte[]) pdus[0];
        SmsMessage message = SmsMessage.createFromPdu(pdu);  //将字节数组封装为SmsMessage
        String messages = message.getMessageBody();  // 获得短信内容
        String date = new Date(message.getTimestampMillis()).toLocaleString();  // 获得短信时间
        String address = message.getOriginatingAddress();  // 获得发送方号码

        // 有些手机收到的号码 前面前缀 +86
        if (address.length() > 11) {
            address = address.substring(address.length() - 11);
        }

        // 经纬度"xxx.xxxx/yyy.yyyyy"用/分割的数值

        if (messages.equals("where are you?")) {
            Log.i("location request", "receive");
            // TODO: 回复自己的位置
            SmsManager manager = SmsManager.getDefault();
            String msg = String.format("%.4f/%.4f", AppData.myself.getLatitude(), AppData.myself.getLongitude());
            Log.i("location", msg);
            //            ArrayList<String> list = manager.divideMessage(msg);  //因为一条短信有字数限制，因此要将长短信拆分
            //            for(String text : list){
            manager.sendTextMessage(address, null, msg, null, null);
            //            }


            // TODO: 2016/10/16 fix
            abortBroadcast();
        } else {
            int cut = messages.indexOf('/');
            if (cut != -1) {
                // FTODO: 2016/10/16 fix change to used hashmap to find
                // // TODO: 2016/10/16 保证电话号码的唯一性
                PersonItem p = AppData.dictionary.get(address);
                // 保证电话为添加的人物的号码
                if (p != null) {
                    p.setPosition(Double.parseDouble(messages.substring(0, cut)), Double.parseDouble(messages.substring(cut + 1)));
                    AppData.databaseManager.updatePosition(p);
                    Toast.makeText(AppData.getInstance().getApplicationContext(), "Receive position from " + p.getName() + address, Toast.LENGTH_SHORT).show();

                    // TODO: 2016/10/16 fix
                    abortBroadcast();
                }



/*                for (PersonItem p : AppData.friendsList) {
                    if (address.equals(p.getPhoneNum())) {
                        try {
                            p.setPosition();

                            // 要保证 / 前后两个参数都能够进行双精度数解析才拦截短信广播
                            abortBroadcast();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }*/
            }

        }
        Log.i("receive SMS", "From: " + address + "Content: " + messages);
        // TODO： 需要修正
        // // TODO: 2016/10/19 添加发送信息以及对方接收信息的状态获取

    }


    public void unRegister() {
        activity.unregisterReceiver(this);
    }
}

