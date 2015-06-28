//
// MainActivity.java
// BeeCloud SDK DEMO
// Zhu Chenglin
//
// Copyright (c) 2014 BeeCloud Inc. All rights reserved.
package cn.beecloud.sdk_demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.unionpay.UPPayAssistEx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.beecloud.BCActivity;
import cn.beecloud.BCPay;
import cn.beecloud.BeeCloud;
import cn.beecloud.async.BCArrayResultCallback;
import cn.beecloud.async.BCCallback;
import cn.beecloud.async.BCResult;

public class MainActivity extends BCActivity {
    private static final String TAG = "MainActivity";

    // 银联支付控件的状态
    public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;
    // 标记当前所用的支付方式
    private static int PayTag = 0;
    Button btnWeChatPay;
    Button btnWeChatOrder;
    Button btnAliPay;
    Button btnAliPayOrder;
    Button btnUPPay;
    Button btnUPPayOrder;
    ListView listViewOrder;
    List<Map<String, Object>> mListMaps = null;
    private Handler mHandler;

    Map<String, String> mapOptional = new HashMap<String, String>();

    String optionalKey = "android_optional_key";
    String optionalValue = "android_optional_value";

    ImageView imageWXQRCode;
    ImageView imageAliQRCode;
    Bitmap bitmapWXQRCode;
    Bitmap bitmapAliQRCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        // 推荐在主Activity里的onCreate函数中初始化BeeCloud.
        BeeCloud.setAppIdAndSecret(this, "c5d1cba1-5e3f-4ba0-941d-9b0a371fe719", "39a7a518-9ac8-4a9e-87bc-7885f33cf18c");
        BeeCloud.setNetworkTimeout(30000);
        */

        // Defines a Handler object that's attached to the UI thread.
        // 通过Handler.Callback()可消除内存泄漏警告 By ZhuChenglin
        mHandler = new Handler(new Handler.Callback() {
            /**
             * Callback interface you can use when instantiating a Handler to
             * avoid having to implement your own subclass of Handler.
             *
             * handleMessage() defines the operations to perform when the
             * Handler receives a new Message to process.
             */
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1) {
                    System.out.println("mListMaps" + mListMaps.size());
                    ModelListAdapter adapter = new ModelListAdapter(
                            MainActivity.this, mListMaps);
                    listViewOrder.setAdapter(adapter);
                } else if (msg.what == 2) {
                    // if
                    // (msg.obj.toString().equalsIgnoreCase("ALREADY_REFUNDING"))
                    Toast.makeText(MainActivity.this, "正在退款中...",
                            Toast.LENGTH_LONG).show();
                } else if (msg.what == 3) {
                    // 如果用户手机没有安装银联支付控件,则会提示用户安装
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("完成支付需要安装银联支付控件，是否安装？");
                    builder.setNegativeButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    UPPayAssistEx
                                            .installUPPayPlugin(MainActivity.this);
                                    dialog.dismiss();
                                }
                            });
                    builder.setPositiveButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else if (msg.what == 4) {
                    imageWXQRCode.setImageBitmap(bitmapWXQRCode);
                } else if (msg.what == 5) {
                    imageAliQRCode.setImageBitmap(bitmapAliQRCode);
                }
                return true;
            }
        });


        listViewOrder = (ListView) findViewById(R.id.listViewOrder);
        listViewOrder
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Map<String, Object> mapModel = mListMaps.get(position);
                        String out_trade_no = (String) mapModel
                                .get("out_trade_no");
                        if (PayTag == 0) {
                            BCPay.getInstance(MainActivity.this)
                                    .refundWxPayStartRefundAsync(out_trade_no,
                                            "201101120001", "1", "退款原因！",
                                            new BCCallback() {
                                                @Override
                                                public void done(
                                                        BCResult bcResult) {
                                                    Message msg = mHandler
                                                            .obtainMessage();
                                                    msg.what = 2;
                                                    msg.obj = bcResult
                                                            .getMsgInfo();
                                                    mHandler.sendMessage(msg);
                                                }
                                            });
                        } else if (PayTag == 1) {
                            BCPay.getInstance(MainActivity.this)
                                    .reqAliRefundAsync(out_trade_no,
                                            "201101120001", "0.01", "退款原因！",
                                            new BCCallback() {
                                                @Override
                                                public void done(
                                                        BCResult bcResult) {
                                                    Message msg = mHandler
                                                            .obtainMessage();
                                                    msg.what = 2;
                                                    msg.obj = bcResult
                                                            .getMsgInfo();
                                                    mHandler.sendMessage(msg);
                                                }
                                            });
                        } else if (PayTag == 2) {
                            BCPay.getInstance(MainActivity.this)
                                    .reqUnionRefundAsync(out_trade_no, "1",
                                            "201101120001", "退款原因",
                                            new BCCallback() {
                                                @Override
                                                public void done(
                                                        BCResult bcResult) {
                                                    Message msg = mHandler
                                                            .obtainMessage();
                                                    msg.what = 2;
                                                    msg.obj = bcResult
                                                            .getMsgInfo();
                                                    mHandler.sendMessage(msg);
                                                }
                                            });
                        }
                    }
                });

        btnWeChatOrder = (Button) findViewById(R.id.btnWeChatOrder);
        btnWeChatOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PayTag = 0;
                    BCPay pay = BCPay.getInstance(MainActivity.this);
                    pay.queryOrderByKeyAsync(
                            BCPay.BCPayOrderKey.OrderKeyTraceID,
                            "BeeCloud-Android",
                            BCPay.BCPayOrderType.BCPayWxPay,
                            new BCArrayResultCallback() {
                                @Override
                                public void done(
                                        List<Map<String, Object>> lstMaps) {
                                    System.out.println("lstMaps"
                                            + lstMaps.size());
                                    mListMaps = lstMaps;
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = 1;
                                    mHandler.sendMessage(msg);
                                }
                            });
                } catch (Exception ex) {
                    System.out.println("btnWeChatPay" + ex);
                }
            }
        });

        btnAliPayOrder = (Button) findViewById(R.id.btnAliPayOrder);
        btnAliPayOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PayTag = 1;
                    BCPay pay = BCPay.getInstance(MainActivity.this);
                    pay.queryOrderByKeyAsync(
                            BCPay.BCPayOrderKey.OrderKeyTraceID,
                            "2088002375457915",
                            BCPay.BCPayOrderType.BCPayAliPay,
                            new BCArrayResultCallback() {
                                @Override
                                public void done(
                                        List<Map<String, Object>> lstMaps) {
                                    System.out.println("lstMaps"
                                            + lstMaps.size());
                                    mListMaps = lstMaps;
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = 1;
                                    mHandler.sendMessage(msg);
                                }
                            });
                } catch (Exception ex) {
                    System.out.println("btnAliPay" + ex);
                }
            }
        });

        btnUPPayOrder = (Button) findViewById(R.id.btnUPPayOrder);
        btnUPPayOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PayTag = 2;
                    BCPay pay = BCPay.getInstance(MainActivity.this);
                    pay.queryOrderByKeyAsync(
                            BCPay.BCPayOrderKey.OrderKeyTraceID,
                            "Android-UPPay", BCPay.BCPayOrderType.BCPayUPPay,
                            new BCArrayResultCallback() {
                                @Override
                                public void done(
                                        List<Map<String, Object>> lstMaps) {
                                    System.out.println("lstMaps"
                                            + lstMaps.size());
                                    mListMaps = lstMaps;
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = 1;
                                    mHandler.sendMessage(msg);
                                }
                            });
                } catch (Exception ex) {
                    System.out.println("btnUPPayOrder" + ex);
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("SDK-DEMO", "SDK-DEMO:onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("SDK-DEMO", "SDK-DEMO:onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}