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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.unionpay.UPPayAssistEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import cn.beecloud.BCAnalysis;
import cn.beecloud.BCPay;
import cn.beecloud.BCUtil;
import cn.beecloud.BeeCloud;
import cn.beecloud.async.BCCallback;
import cn.beecloud.async.BCResult;


public class ShoppingCartActivity extends ActionBarActivity {

    private static final String TAG = "ShoppingCartActivity";

    private String[] names = new String[]{
            "衣服", "裤子", "鞋子",
    };

    private String[] descs = new String[]{
            "我的新衣服", "我的新裤子", "我的新鞋子"
    };

    private int[] imageIds = new int[]{
            R.drawable.yifu, R.drawable.kuzi, R.drawable.xiezi
    };

    // 银联支付控件的状态
    public static final int PLUGIN_VALID = 0;
    public static final int PLUGIN_NOT_INSTALLED = -1;
    public static final int PLUGIN_NEED_UPGRADE = 2;

    Button btnShopping;
    ImageView imageQRCode;
    Bitmap bitmapQRCode;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        //BeeCloud
        BeeCloud.setAppIdAndSecret(this, "c5d1cba1-5e3f-4ba0-941d-9b0a371fe719", "39a7a518-9ac8-4a9e-87bc-7885f33cf18c");

        BCAnalysis.setUserId("BeeCloud Android User！");
        BCAnalysis.setUserGender(true);
        BCAnalysis.setUserAge(28);

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
                    imageQRCode.setImageBitmap(bitmapQRCode);
                }
                return true;
            }
        });

        btnShopping = (Button) findViewById(R.id.btnShopping);
        btnShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DialogPlus.Gravity.BOTTOM);
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

        //微信扫码支付测试
        /*imageQRCode = (ImageView) findViewById(R.id.imageQRCode);
        BCQRCodePay.getInstance().reqWXQRCodePayAsync("web wxpay", "1",
                BCUtil.generateRandomUUID().toString().replace("-", ""), 300, 300,
                new BCCallback() {
                    @Override
                    public void done(BCResult result) {
                        if (result.isSuccess()) {
                            String code_url = result.getMsgInfo();
                            try {
                                bitmapQRCode = createQRImage(code_url, 200, 200);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                            Message msg = mHandler.obtainMessage();
                            msg.what = 4;
                            mHandler.sendMessage(msg);
                        }

                    }
                }); */
    }

    /**
     * 处理银联手机支付控件返回的支付结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！";
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
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
                        Toast.makeText(ShoppingCartActivity.this, "Header clicked", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.footer_confirm_button:
                        Toast.makeText(ShoppingCartActivity.this, "Confirm button clicked", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.footer_close_button:
                        Toast.makeText(ShoppingCartActivity.this, "Close button clicked", Toast.LENGTH_LONG).show();
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

                        //老版本微信
                         /*BCPay.getInstance(ShoppingCartActivity.this).reqWXPaymentV2Async("test", "1",
                                BCUtil.generateRandomUUID().replace("-", ""), "BeeCloud-Android", mapOptional, new BCPayCallback() {
                                    @Override
                                    public void done(boolean b, String s) {
                                        Log.i(TAG, "reqWXPaymentAsync:" + b + "|" + s);
                                    }
                                });*/
                        //新版本微信
                        BCPay.getInstance(ShoppingCartActivity.this).reqWXPaymentV3Async("test", "1",
                                BCUtil.generateRandomUUID().replace("-", ""), "BeeCloud-Android", mapOptional, new BCCallback() {
                                    @Override
                                    public void done(BCResult bcResult) {
                                        Log.i(TAG, "reqWXPaymentAsync:" + bcResult.isSuccess() + "|" + bcResult.getMsgInfo());
                                    }
                                });
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
                                        Log.d(TAG, "支付宝支付:" + bcResult.isSuccess() + "|" + bcResult.getMsgInfo());
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
                        BCPay.getInstance(ShoppingCartActivity.this).reqUnionPaymentAsync("Android-UPPay", "Android-UPPay-body",
                                BCUtil.generateRandomUUID().replace("-", ""), "1", mapOptional, new BCCallback() {
                                    @Override
                                    public void done(BCResult bcResult) {
                                        Log.i(TAG, "btnUPPay:" + bcResult.isSuccess() + "|" + bcResult.getMsgInfo());

                                        int ret = Integer.valueOf(bcResult.getMsgInfo());
                                        if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
                                            // 需要重新安装控件
                                            Message msg = mHandler.obtainMessage();
                                            msg.what = 3;
                                            mHandler.sendMessage(msg);
                                        }
                                    }
                                });
                        break;
                }
            }
        };

        MySimpleAdapter adapter = new MySimpleAdapter(ShoppingCartActivity.this);
        showCompleteDialog(holder, gravity, adapter, clickListener, itemClickListener);
    }

    private void showCompleteDialog(Holder holder,
                                    DialogPlus.Gravity gravity,
                                    BaseAdapter adapter,
                                    OnClickListener clickListener,
                                    OnItemClickListener itemClickListener) {
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

    /**
     * 创建二维码
     *
     * @param url 要转换的地址或字符串,可以是中文
     * @return 二维码图片
     */
    public Bitmap createQRImage(String url, int qrWidth, int qrHeight) throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

        //图像数据转换，使用二维矩阵转换
        BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
        int[] pixels = new int[qrWidth * qrHeight];
        //按照二维码的算法，逐个生成二维码的图片，
        //两个for循环是图片横列扫描的结果
        for (int y = 0; y < qrHeight; y++) {
            for (int x = 0; x < qrWidth; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * qrWidth + x] = 0xff000000;
                } else {
                    pixels[y * qrWidth + x] = 0xffffffff;
                }
            }
        }
        //生成二维码图片的格式，使用ARGB_8888
        Bitmap bitmap = Bitmap.createBitmap(qrWidth, qrHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, qrWidth, 0, 0, qrWidth, qrHeight);

        return bitmap;
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
}
