package cn.beecloud.sdk_demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import cn.beecloud.BCActivity;
import cn.beecloud.BCArrayResultCallback;
import cn.beecloud.BCPay;
import cn.beecloud.BCPayCallback;

public class PayActivity extends BCActivity {

    // 标记当前所用的支付方式
    private static int PayTag = 0;

    Button btnWeChatOrder;
    Button btnAliPayOrder;
    Button btnUPPayOrder;

    ListView listViewOrder;
    List<Map<String, Object>> mListMaps = null;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        // Defines a Handler object that's attached to the UI thread.
        // 通过Handler.Callback()可消除内存泄漏警告 By ZhuChenglin
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
                if (msg.what == 1) {
                    System.out.println("mListMaps" + mListMaps.size());
                    ModelListAdapter adapter = new ModelListAdapter(PayActivity.this, mListMaps);
                    listViewOrder.setAdapter(adapter);
                } else if (msg.what == 2) {
                    //if (msg.obj.toString().equalsIgnoreCase("ALREADY_REFUNDING"))
                    Toast.makeText(PayActivity.this, "正在退款中...", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        listViewOrder = (ListView) findViewById(R.id.listViewOrder);
        listViewOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Map<String, Object> mapModel = mListMaps.get(position);

                String out_trade_no = (String) mapModel.get("out_trade_no");

                if (PayTag == 0) {
                    BCPay.getInstance(PayActivity.this).refundWxPayStartRefundAsync(out_trade_no, "201101120001", "1", "退款原因！",
                            new BCPayCallback() {
                                @Override
                                public void done(boolean b, String s) {
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = 2;
                                    msg.obj = s;
                                    mHandler.sendMessage(msg);
                                }
                            });
                } else if (PayTag == 1) {
                    BCPay.getInstance(PayActivity.this).reqAliRefundAsync(out_trade_no, "201101120001", "0.01", "退款原因！",
                            new BCPayCallback() {
                                @Override
                                public void done(boolean b, String s) {
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = 2;
                                    msg.obj = s;
                                    mHandler.sendMessage(msg);
                                }
                            });
                } else if (PayTag == 2) {
                    BCPay.getInstance(PayActivity.this).reqUnionRefundAsync(out_trade_no, "1", "201101120001", "退款原因",
                            new BCPayCallback() {
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
        btnWeChatOrder = (Button) findViewById(R.id.btnWeChatOrder);
        btnWeChatOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PayTag = 0;
                    BCPay pay = BCPay.getInstance(PayActivity.this);
                    pay.queryOrderByKeyAsync(BCPay.BCPayOrderKey.OrderKeyTraceID, "BeeCloud-Android",
                            BCPay.BCPayOrderType.BCPayWxPay, new BCArrayResultCallback() {
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
        btnAliPayOrder = (Button) findViewById(R.id.btnAliPayOrder);
        btnAliPayOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PayTag = 1;
                    BCPay pay = BCPay.getInstance(PayActivity.this);
                    pay.queryOrderByKeyAsync(BCPay.BCPayOrderKey.OrderKeyTraceID, "2088002375457915",
                            BCPay.BCPayOrderType.BCPayAliPay, new BCArrayResultCallback() {
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
                    BCPay pay = BCPay.getInstance(PayActivity.this);
                    pay.queryOrderByKeyAsync(BCPay.BCPayOrderKey.OrderKeyTraceID, "Android-UPPay", BCPay.BCPayOrderType.BCPayUPPay, new BCArrayResultCallback() {
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
                    System.out.println("btnUPPayOrder" + ex);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pay, menu);
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
