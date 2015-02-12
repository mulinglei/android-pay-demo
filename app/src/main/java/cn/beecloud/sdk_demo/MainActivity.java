package cn.beecloud.sdk_demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import cn.beecloud.BCAnalysis;
import cn.beecloud.BCPay;
import cn.beecloud.BCPayCallback;
import cn.beecloud.BCUtil;
import cn.beecloud.BeeCloud;

public class MainActivity extends Activity {

    Button btnWeChatPay;
    Button btnWeChatQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BeeCloud.setAppKey("39a7a518-9ac8-4a9e-87bc-7885f33cf18c", this);
        BCAnalysis.setUserId("我是朱朱朱！");
        BCAnalysis.setUserGender(true);
        BCAnalysis.setUserAge(88);

        final BCPay bcPay = BCPay.sharedInstance(MainActivity.this);

        btnWeChatPay = (Button) findViewById(R.id.btnWeChatPay);
        btnWeChatPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    bcPay.reqWXPaymentAsync("test", "1", BCUtil.generateRandomUUID().replace("-", ""), "BeeCloud-Android", new BCPayCallback() {
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
        btnWeChatQuery = (Button) findViewById(R.id.btnWeChatQuery);
        btnWeChatQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //bcPay.
                } catch (Exception e) {
                    System.out.println("e.getMessage():" + e.getMessage());
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
