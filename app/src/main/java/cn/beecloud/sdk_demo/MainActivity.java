package cn.beecloud.sdk_demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import cn.beecloud.BCAnalysis;
import cn.beecloud.BCArrayResultCallback;
import cn.beecloud.BCPay;
import cn.beecloud.BCPayCallback;
import cn.beecloud.BCUtil;
import cn.beecloud.BeeCloud;

public class MainActivity extends Activity {

    private static int PayTag = 0;
    Button btnWeChatPay;
    Button btnWeChatOrder;
    Button btnAliPay;
    Button btnAliPayOrder;
    Button btnAliPayRefund;
    ListView listViewOrder;
    List<Map<String, Object>> mListMaps = null;
    private Handler mHandler = new Handler() {
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                System.out.println("mListMaps" + mListMaps.size());
                ModelListAdapter adapter = new ModelListAdapter(MainActivity.this, mListMaps);
                listViewOrder.setAdapter(adapter);
            } else if (msg.what == 2) {
                //if (msg.obj.toString().equalsIgnoreCase("ALREADY_REFUNDING")) {
                    Toast.makeText(MainActivity.this, "正在退款中...", Toast.LENGTH_LONG).show();
                //}
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BeeCloud.setAppKey("39a7a518-9ac8-4a9e-87bc-7885f33cf18c", this);
        BCAnalysis.setUserId("BeeCloud User！");
        BCAnalysis.setUserGender(true);
        BCAnalysis.setUserAge(88);

        listViewOrder = (ListView) findViewById(R.id.listViewOrder);
        listViewOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String, Object> mapModel = mListMaps.get(position);
                String out_trade_no = (String) mapModel.get("out_trade_no");
                if (PayTag == 0) {
                    BCPay.sharedInstance(MainActivity.this).refundWxPayStartRefundAsync(out_trade_no, "201101120001", "1", "我就是没有退款原因！", new BCPayCallback() {
                        @Override
                        public void done(boolean b, String s) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = 2;
                            msg.obj = s;
                            mHandler.sendMessage(msg);
                        }
                    });
                } else if (PayTag == 1) {
                    BCPay.sharedInstance(MainActivity.this).reqAliRefundAsync(out_trade_no, "201101120001", "0.01", "我就是没有退款原因！", new BCPayCallback() {
                        @Override
                        public void done(boolean b, String s) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = 2;
                            msg.obj = s;
                            mHandler.sendMessage(msg);
                        }
                    });
                }
            }
        });

        btnWeChatPay = (Button) findViewById(R.id.btnWeChatPay);
        btnWeChatPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BCPay.sharedInstance(MainActivity.this).reqWXPaymentAsync("test", "1", BCUtil.generateRandomUUID().replace("-", ""), "BeeCloud-Android", new BCPayCallback() {
                        @Override
                        public void done(boolean b, String s) {

                            System.out.println("reqWXPaymentAsync:" + b + "|" + s);
                        }
                    });
                } catch (Exception e) {
                    System.out.println("e.getMessage():" + e.getMessage());
                }
            }
        });
        btnWeChatOrder = (Button) findViewById(R.id.btnWeChatOrder);
        btnWeChatOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PayTag = 0;
                    BCPay pay = BCPay.sharedInstance(MainActivity.this);
                    pay.queryOrderByKeyAsync(BCPay.BCPayOrderKey.OrderKeyTraceID, "BeeCloud-Android", BCPay.BCPayOrderType.BCPayWxPay, new BCArrayResultCallback() {
                        @Override
                        public void done(List<Map<String, Object>> lstMaps) {
                            System.out.println("lstMaps" + lstMaps.size());
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

        btnAliPay = (Button) findViewById(R.id.btnAliPay);
        btnAliPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BCPay.sharedInstance(MainActivity.this).reqAliPaymentAsync("test", BCUtil.generateRandomUUID().replace("-", ""), "订单标题", "对一笔交易的具体描述信息", "0.01", new BCPayCallback() {
                        @Override
                        public void done(boolean b, String s) {
                            System.out.println("btnAliPay:" + b + "|" + s);
                        }
                    });
                } catch (Exception e) {
                    System.out.println("e.getMessage():" + e.getMessage());
                }
            }
        });
        btnAliPayOrder = (Button) findViewById(R.id.btnAliPayOrder);
        btnAliPayOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PayTag = 1;
                    BCPay pay = BCPay.sharedInstance(MainActivity.this);
                    pay.queryOrderByKeyAsync(BCPay.BCPayOrderKey.OrderKeyTraceID, "2088002375457915", BCPay.BCPayOrderType.BCPayAliPay, new BCArrayResultCallback() {
                        @Override
                        public void done(List<Map<String, Object>> lstMaps) {
                            System.out.println("lstMaps" + lstMaps.size());
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
