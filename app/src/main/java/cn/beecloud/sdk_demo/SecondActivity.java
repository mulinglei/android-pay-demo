package cn.beecloud.sdk_demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class SecondActivity extends Activity {

    private static final String TAG = "SecondActivity";
    Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        btnPay = (Button) findViewById(R.id.btnPay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

     /*
        //getConfig
        BCConfig.getConfigWithNameAsync("bool", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                Log.i(TAG, "getConfig");
                Log.i(TAG, result.getBool());
            }
        });

        BCConfig.getConfigWithNameAsync("int", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                Log.i(TAG, "getConfig");
                Log.i(TAG, result.getInt());
            }
        });
        BCConfig.getConfigWithNameAsync("long", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                Log.i(TAG, "getConfig");
                Log.i(TAG, result.getLong());
            }
        });
        BCConfig.getConfigWithNameAsync("float", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                Log.i(TAG, "getConfig");
                Log.i(TAG, result.getFloat());
            }
        });
        BCConfig.getConfigWithNameAsync("uuid", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                Log.i(TAG, "getConfig");
                Log.i(TAG, result.getUUID());
            }
        });
        BCConfig.getConfigWithNameAsync("date", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                Log.i(TAG, "getConfig");
                Log.i(TAG, result.getDate());
            }
        });
        BCConfig.getConfigWithNameAsync("string", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                Log.i(TAG, "getConfig");
                Log.i(TAG, result.getString());
            }
        });
        BCConfig.getConfigWithNameAsync("location", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                Log.i(TAG, "getConfig");
                Log.i(TAG, result.getLocation());
            }
        });

        //BCObject
        BCObject o1 = BCObject.newObject("chat");
        o1.setLocation("gps", BCLocation.locationWithLatitude(40.5, 116.0));
        o1.saveAsync(new BCCallBack() {
            @Override
            public void done(BCResult result) {

                BCQuery q = BCQuery.queryWithClassName("chat");
                BCLocation base_point = BCLocation.locationWithLatitude(40.5, 116.1);
                q.whereKeyNearLocation("gps", base_point, 10000);  // 选取基准点500米以内的用户

                q.findObjectsAsync(new BCQueryCallBack() {
                    public void done(BCQueryResult result) {
                        List<BCObject> objects = result.getObjects();x
                        Log.i(TAG, objects);
                        for (BCObject user : objects) {
                            Log.i(TAG, user.objectForKey("objectId"));
                        }
                    }
                });
            }
        });

        //orderByDistanceToLocation
        BCQuery q = BCQuery.queryWithClassName("chat");
        BCLocation base_point = BCLocation.locationWithLatitude(40.5, 116.1);
        q.whereKeyNearLocation("gps", base_point, 10000);
        BCLocation order_base_point = BCLocation.locationWithLatitude(50, 110);
        q.orderByDistanceToLocation("gps", order_base_point);
        q.findObjectsAsync(new BCQueryCallBack() {
            public void done(BCQueryResult result) {
                List<BCObject> users = result.getObjects();
                Log.i(TAG, users);
                for (BCObject user : users) {
                    Log.i(TAG, user.objectForKey("objectId"));
                }
            }
        });

        //getCurrentLocation
        Log.i(TAG, BCLocation.getCurrentLocation());

        //distance
        BCLocation address_bj = BCLocation.locationWithLatitude(39.92, 116.46);
        Log.i(TAG, address_bj.distanceInMetersTo(BCLocation.locationWithLatitude(39.92, 116.56)));

        //getAddressAsync
        address_bj.getAddressAsync(new BCAddressCallBack() {
            public void done(BCAddressResult result) {
                Log.i(TAG, result.getFormattedAddress());
                Log.i(TAG, result.getStreet());
                Log.i(TAG, result.getStreetNumber());
                Log.i(TAG, result.getDistrict());
                Log.i(TAG, result.getCity());
                Log.i(TAG, result.getProvince());
                Log.i(TAG, result.getCountry());
                Log.i(TAG, result.getCountryCode());
                Log.i(TAG, result.getContinent());
                Log.i(TAG, result.getTimeZone());
            }
        });

        // saveAsync
        BCObject saveObj = BCObject.newObject("infolist3");
        saveObj.setDouble("question2", 2.0);
        Log.i(TAG, saveObj.allKeys());
        Log.i(TAG, saveObj.userKeys());
        Log.i(TAG, saveObj.objectForKey("question2"));
        Log.i(TAG, saveObj.getDataTypeForKey("question2"));
        saveObj.saveAsync();

        saveObj = BCObject.newObject("infolist3");
        saveObj.setBool("question3", false);
        saveObj.setDouble("question2", 3.0);
        saveObj.removeObjectForKey("question3");
        saveObj.saveAsync(new BCCallBack() {
            public void done(BCResult result) {
                Log.i(TAG, result.isSuccess());
            }
        });


        // refreshAsync
        BCObject refresh = BCObject.existingObject("infolist3",
                "71cffa7e-da12-4178-bce0-7763ff45f5bc", false);
        refresh.refreshAsync();
        // refreshAsync
        refresh = BCObject.existingObject("infolist3",
                "3411042c-0b00-4f4b-ba0d-a0a165fe8339", false);
        refresh.refreshAsync(new BCCallBack() {
            public void done(BCResult result) {
                Log.i(TAG, result.isSuccess());
            }
        });

        // modifyAsync
        BCObject modifyObject = BCObject.existingObject("infolist3",
                "28b74a4c-a3b3-4019-890e-dccd98f47610", false);
        modifyObject.setBool("question3", false);
        modifyObject.saveAsync();
        // modifyAsync
        modifyObject = BCObject.existingObject("infolist3",
                "28b74a4c-a3b3-4019-890e-dccd98f47610", false);
        modifyObject.setDouble("question2", 5.0);
        modifyObject.saveAsync(new BCCallBack() {
            public void done(BCResult result) {
                Log.i(TAG, result.isSuccess());
            }
        });

        // deleteAsync
        BCObject deleteObject = BCObject.existingObject("infolist3",
                "c4ed6e95-742d-4453-89e6-26cf21c2cf5e", false);
        deleteObject.deleteAsync();
        // deleteAsync
        deleteObject = BCObject.existingObject("infolist3",
                "987b0994-d5bf-44a0-9a3a-f4d1825c2aaf", false);
        deleteObject.deleteAsync(new BCCallBack() {
            public void done(BCResult result) {
                Log.i(TAG, result.isSuccess());
            }
        });

        BCQuery q1 = BCQuery.queryWithClassName("infolist3");
        q1.setLimit(20);
        q1.setSkip(0);
        q1.whereKeyEqualTo("question2", 2);
        q1.orderByDescending("objectid");
        // List<String> keys =new ArrayList<String>();
        // keys.add("question3");
        // // q1.selectKeys(keys);
        // BCQueryResult q1res = q1.findObjects();
        // Log.i(TAG, q1res.getObjects());
        // for (BCObject obj:q1res.getObjects()) {
        // Log.i(TAG, obj.objectForKey("objectid"));

        // }

        // findObjectsAsync
        q1.findObjectsAsync(new BCQueryCallBack() {
            public void done(BCQueryResult result) {
                Log.i(TAG, result.getObjects());

                for (BCObject obj : result.getObjects()) {
                    Log.i(TAG, obj.objectForKey("question3"));
                }
            }
        });

        // getObjectAsync
        q1.getObjectAsync("28b74a4c-a3b3-4019-890e-dccd98f47610",
                new BCQueryCallBack() {
                    public void done(BCQueryResult result) {
                        Log.i(TAG, result.getSingleObject()
                                .objectForKey("question2"));

                    }
                });

        BCQuery q2 = new BCQuery();
        q2.initWithClassName("infolist3");
        q2.whereKeyEqualTo("aaee", 453);

        // countObjectsAsync
        q2.countObjectsAsync(new BCQueryCallBack() {
            public void done(BCQueryResult results) {
                Log.i(TAG, results.getCountObject());
            }
        });

        // deleteObjectsAsync
        q2.deleteObjectsAsync(new BCCallBack() {
            public void done(BCResult ret) {
                Log.i(TAG, ret.getErrorInfo());
                Log.i(TAG, ret.isSuccess());
            }
        });

        // modifyObject
        BCObject obj = BCObject.newObject("infolist3");
        obj.setDouble("question2", 8.0);
        BCQuery query = BCQuery.queryWithClassName("infolist3");
        query.whereKeyEqualTo("aaee", 453);
        query.modifyObjectsWithExampleAsync(obj, new BCCallBack() {
            public void done(BCResult result) {
                Log.i(TAG, result.isSuccess());
            }
        });
         */

    @Override
    public void onPause() {
        super.onPause();
        Log.i("SecondActivity", "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("SecondActivity", "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
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
