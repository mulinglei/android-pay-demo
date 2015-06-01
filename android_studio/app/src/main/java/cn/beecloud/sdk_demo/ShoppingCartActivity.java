package cn.beecloud.sdk_demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.unionpay.UPPayAssistEx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.beecloud.BCAnalysis;
import cn.beecloud.BCPay;
import cn.beecloud.BCQRCodePay;
import cn.beecloud.BCUtil;
import cn.beecloud.BeeCloud;
import cn.beecloud.async.BCCallback;
import cn.beecloud.async.BCResult;
import cn.paypalm.pppayment.PPInterface;
import cn.paypalm.pppayment.global.ResponseDataToMerchant;


public class ShoppingCartActivity extends ActionBarActivity implements ResponseDataToMerchant {

    // 银联支付控件的状态
    public static final int PLUGIN_VALID = 0;
    public static final String PLUGIN_NOT_INSTALLED = "-1";
    public static final String PLUGIN_NEED_UPGRADE = "2";
    private static final String TAG = ShoppingCartActivity.class.getSimpleName();
    Button btnShopping;
    Button btnQueryAndRefund;
    ImageView imageWXQRCode;
    Bitmap bitmapWXQRCode;
    WebView webView;
    String sbHtml;

    private HashMap<String, String> userInfo = new HashMap<String, String>(); // phone
    // 手机，cardnum
    // 银行卡号，idcard
    // 身份证号，name
    // 开户姓名,
    // creditphone
    // 无卡支付手机号,errorexit
    // 银行卡四项信息页面出错时是否退出
    // yes
    // 时退出,
    // isedit银行卡四项信息是否可编辑
    // no
    // 为不可编辑

    private String[] names = new String[]{
            "衣服", "裤子", "鞋子",
    };
    private String[] descs = new String[]{
            "我的新衣服", "我的新裤子", "我的新鞋子"
    };
    private int[] imageIds = new int[]{
            R.drawable.yifu, R.drawable.kuzi, R.drawable.xiezi
    };
    private Handler mHandler;
    private static final String MERCHANT_ID = "1000002153";// 默认商户ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        BeeCloud.setAppIdAndSecret(this, "c5d1cba1-5e3f-4ba0-941d-9b0a371fe719", "39a7a518-9ac8-4a9e-87bc-7885f33cf18c");

        BeeCloud.setNetworkTimeout(10000);

        BCAnalysis.setUserId("BeeCloud Android User！");
        BCAnalysis.setUserGender(true);
        BCAnalysis.setUserAge(28);

        userInfo.put("phone", "");
        userInfo.put("cardnum", "");
        userInfo.put("idcard", "");
        userInfo.put("name", "");
        userInfo.put("creditphone", "");

        // Defines a Handler object that's attached to the UI thread.
        // 通过Handler.Callback()可消除内存泄漏警告 By Charlie Chu
        mHandler = new Handler(new Handler.Callback() {
            /**
             * Callback interface you can use when instantiating a Handler to avoid
             * having to implement your own subclass of Handler.
             *
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             */
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 3) {
                    //如果用户手机没有安装银联支付控件,则会提示用户安装
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingCartActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("完成支付需要安装银联支付控件，是否安装？");

                    builder.setNegativeButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    UPPayAssistEx.installUPPayPlugin(ShoppingCartActivity.this);
                                    dialog.dismiss();
                                }
                            });

                    builder.setPositiveButton("取消",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else if (msg.what == 4) {
                    imageWXQRCode.setImageBitmap(bitmapWXQRCode);
                } else if (msg.what == 5) {
                    webView.loadData(sbHtml, "text/html", "utf-8");
                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }
                    });
                }
                return true;
            }
        });

        btnShopping = (Button) findViewById(R.id.btnPay);
        btnShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DialogPlus.Gravity.BOTTOM);
            }
        });

        btnQueryAndRefund = (Button) findViewById(R.id.btnQueryAndRefund);
        btnQueryAndRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingCartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        List<Map<String, Object>> listItems = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("icon", imageIds[i]);
            listItem.put("name", names[i]);
            listItem.put("desc", descs[i]);
            listItems.add(listItem);
        }

       /*SimpleAdapter adapter = new SimpleAdapter(this, listItems,
                R.layout.list_item_shopping_cart,
                new String[]{"name", "icon", "desc"},
                new int[]{R.id.txtViewName, R.id.imageView, R.id.txtViewDesc});*/
        ShoppingAdapter adapter = new ShoppingAdapter(this, listItems);
        ListView listView = (ListView) findViewById(R.id.lstViewShoppingCart);
        listView.setAdapter(adapter);

        imageWXQRCode = (ImageView) findViewById(R.id.imageWXQRCode);
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);


        //微信扫码支付测试
        BCQRCodePay.getInstance().reqWXQRCodePayAsync("web wxpay", "1",
                BCUtil.generateRandomUUID().toString().replace("-", ""),
                new BCCallback() {
                    @Override
                    public void done(BCResult result) {
                        if (result.isSuccess()) {
                            String code_url = result.getMsgInfo();
                            try {
                                bitmapWXQRCode = BCUtil.createQRImage(code_url, 250, 250);
                            } catch (WriterException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            Message msg = mHandler.obtainMessage();
                            msg.what = 4;
                            mHandler.sendMessage(msg);
                        }

                    }
                });

        //支付宝网页支付，实际项目中建议将二维码放在一个单独的页面中显示。
        BCQRCodePay.getInstance().reqAliQRCodePayAsync("subject", "1", BCUtil.generateRandomUUID().toString().replace("-", ""),
                "http://www.beecloud.cn", null,
                new BCCallback() {

                    @Override
                    public void done(BCResult bcResult) {
                        try {
                            Log.e(TAG, bcResult.getMsgInfo());
                            sbHtml = bcResult.getMsgInfo();
                            Message msg = mHandler.obtainMessage();
                            msg.what = 5;
                            mHandler.sendMessage(msg);
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getMessage());
                        }
                    }
                });
    }

    /**
     * 处理银联手机支付控件返回的支付结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        String msg = "银联支付:";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg += "支付成功！";
        } else if (str.equalsIgnoreCase("fail")) {
            msg += "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg += "用户取消了支付";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showDialog(DialogPlus.Gravity gravity) {

        Holder holder = new ListHolder();

        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(DialogPlus dialog, View view) {
                switch (view.getId()) {
                    case R.id.header_container:
                        Toast.makeText(ShoppingCartActivity.this, "Header clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.footer_confirm_button:
                        Toast.makeText(ShoppingCartActivity.this, "Confirm button clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.footer_close_button:
                        Toast.makeText(ShoppingCartActivity.this, "Close button clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
                dialog.dismiss();
            }
        };

        OnItemClickListener itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.text_view);
                String clickItem = textView.getText().toString();
                dialog.dismiss();

                switch (clickItem) {
                    case "微信支付":
                        Map<String, String> mapOptional = new HashMap<>();
                        String optionalKey = "android_optional_key";
                        String optionalValue = "android_optional_value";
                        if (!BCUtil.isValidIdentifier(optionalKey) || !BCUtil.isValidIdentifier(optionalValue)) {
                            return;
                        }
                        mapOptional.put(optionalKey, optionalValue);
                        BCPay instance = BCPay.getInstance(ShoppingCartActivity.this);
                        Log.i(TAG, "isWXAppInstalledAndSupported: " + String.valueOf(instance.isWXAppInstalledAndSupported()));
                        Log.i(TAG, "isWXPaySupported: " + String.valueOf(instance.isWXPaySupported()));
                        Log.i(TAG, "isWXAppInstalled: " + String.valueOf(instance.isWXAppInstalled()));
                        Log.i(TAG, "isWXAppSupported: " + String.valueOf(instance.isWXAppSupported()));
                        BCPay.getInstance(ShoppingCartActivity.this).reqWXPaymentV3Async("test", "1",
                                BCUtil.generateRandomUUID().replace("-", ""), "BeeCloud-Android", mapOptional, new BCCallback() {
                                    @Override
                                    public void done(BCResult bcResult) {
                                        Log.i(TAG, "reqWXPaymentAsync:" + bcResult.isSuccess() + "|" + bcResult.getMsgInfo());
                                    }
                                });
                        ;
                        break;
                    case "支付宝支付":
                        mapOptional = new HashMap<>();
                        optionalKey = "android_optional_key";
                        optionalValue = "android_optional_value";
                        if (!BCUtil.isValidIdentifier(optionalKey) || !BCUtil.isValidIdentifier(optionalValue)) {
                            return;
                        }
                        mapOptional.put("paymentid", "");
                        mapOptional.put("consumptioncode", "consumptionCode");
                        mapOptional.put("money", "2");

                        BCPay.getInstance(ShoppingCartActivity.this).reqAliPaymentAsync("test", BCUtil.generateRandomUUID().replace("-", ""),
                                "订单标题", "对一笔交易的具体描述信息", "0.01", mapOptional, new BCCallback() {
                                    @Override
                                    public void done(BCResult bcResult) {
                                        Log.i(TAG, "支付宝支付:" + bcResult.isSuccess() + "|" + bcResult.getMsgInfo());
                                    }
                                });
                        break;
                    case "银联支付":
                        mapOptional = new HashMap<>();
                        optionalKey = "android_optional_key";
                        optionalValue = "android_optional_value";
                        if (!BCUtil.isValidIdentifier(optionalKey) || !BCUtil.isValidIdentifier(optionalValue)) {
                            return;
                        }
                        mapOptional.put(optionalKey, optionalValue);
                        BCPay.getInstance(ShoppingCartActivity.this).reqUnionPaymentByAPKAsync("Android-UPPay", "Android-UPPay-body",
                                BCUtil.generateRandomUUID().replace("-", ""), "1", mapOptional, new BCCallback() {
                                    @Override
                                    public void done(BCResult bcResult) {
                                        Log.i(TAG, "btnUPPay:" + bcResult.isSuccess() + "|" + bcResult.getMsgInfo());
                                        if (bcResult.getMsgInfo().equals(PLUGIN_NEED_UPGRADE) || bcResult.getMsgInfo().equals(PLUGIN_NOT_INSTALLED)) {
                                            // 需要重新安装控件
                                            Message msg = mHandler.obtainMessage();
                                            msg.what = 3;
                                            mHandler.sendMessage(msg);
                                        }
                                    }
                                });
                        break;
                    case "PP钱包支付":

                        PPInterface.startSafe(ShoppingCartActivity.this, MERCHANT_ID);
                        BCPay.getInstance(ShoppingCartActivity.this).reqPPPaymentAsync(new SimpleDateFormat("yyyyMMddHHmmss",
                                Locale.CHINESE).format(new Date()), "", "2015052513", "朱朱朱", "1", "100001", "", "http://www.test.com", "好东西", userInfo, "sdk2.2", ShoppingCartActivity.this);

                }
            }
        };

        MySimpleAdapter adapter = new MySimpleAdapter(ShoppingCartActivity.this);
        showCompleteDialog(holder, gravity, adapter, clickListener, itemClickListener);
    }


    private void showCompleteDialog(Holder holder, DialogPlus.Gravity gravity, BaseAdapter adapter,
                                    OnClickListener clickListener, OnItemClickListener itemClickListener) {
        final DialogPlus dialog = new DialogPlus.Builder(this)
                .setContentHolder(holder)
                .setHeader(R.layout.header)
                .setFooter(R.layout.footer)
                .setCancelable(true)
                .setGravity(gravity)
                .setAdapter(adapter)
                .setOnClickListener(clickListener)
                .setOnItemClickListener(itemClickListener)
                .create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void responseData(int arg0, String arg1) {
        switch (arg0) {
            case 0:
                break;
            case ResponseDataToMerchant.RESULT_PAYCODE_OK:
                Log.d(TAG, "支付成功");
                // Toast.makeText(this, "用户支付成功", Toast.LENGTH_LONG).show();
                break;
            case ResponseDataToMerchant.RESULT_PAYCODE_ERROR:
                Log.d(TAG, "支付失败");
                // Toast.makeText(this, "用户支付失败", Toast.LENGTH_LONG).show();
                break;
        }
    }
}