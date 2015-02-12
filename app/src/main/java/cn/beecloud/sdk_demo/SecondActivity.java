package cn.beecloud.sdk_demo;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import cn.beecloud.BCActivity;


public class SecondActivity extends BCActivity {

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
                System.out.println("getConfig");
                System.out.println(result.getBool());
            }
        });

        BCConfig.getConfigWithNameAsync("int", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                System.out.println("getConfig");
                System.out.println(result.getInt());
            }
        });
        BCConfig.getConfigWithNameAsync("long", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                System.out.println("getConfig");
                System.out.println(result.getLong());
            }
        });
        BCConfig.getConfigWithNameAsync("float", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                System.out.println("getConfig");
                System.out.println(result.getFloat());
            }
        });
        BCConfig.getConfigWithNameAsync("uuid", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                System.out.println("getConfig");
                System.out.println(result.getUUID());
            }
        });
        BCConfig.getConfigWithNameAsync("date", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                System.out.println("getConfig");
                System.out.println(result.getDate());
            }
        });
        BCConfig.getConfigWithNameAsync("string", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                System.out.println("getConfig");
                System.out.println(result.getString());
            }
        });
        BCConfig.getConfigWithNameAsync("location", new BCConfigCallBack() {
            @Override
            public void done(BCConfigResult result) {
                System.out.println("getConfig");
                System.out.println(result.getLocation());
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
                        List<BCObject> objects = result.getObjects();
                        System.out.println(objects);
                        for (BCObject user : objects) {
                            System.out.println("----------------");
                            System.out.println(user.objectForKey("objectId"));
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
                System.out.println(users);
                for (BCObject user : users) {
                    System.out.println("----------------");
                    System.out.println(user.objectForKey("objectId"));
                }
            }
        });

        //getCurrentLocation
        System.out.println(BCLocation.getCurrentLocation());

        //distance
        BCLocation address_bj = BCLocation.locationWithLatitude(39.92, 116.46);
        System.out.println(address_bj.distanceInMetersTo(BCLocation.locationWithLatitude(39.92, 116.56)));

        //getAddressAsync
        address_bj.getAddressAsync(new BCAddressCallBack() {
            public void done(BCAddressResult result) {
                System.out.println(result.getFormattedAddress());
                System.out.println(result.getStreet());
                System.out.println(result.getStreetNumber());
                System.out.println(result.getDistrict());
                System.out.println(result.getCity());
                System.out.println(result.getProvince());
                System.out.println(result.getCountry());
                System.out.println(result.getCountryCode());
                System.out.println(result.getContinent());
                System.out.println(result.getTimeZone());
            }
        });

        // saveAsync
        BCObject saveObj = BCObject.newObject("infolist3");
        saveObj.setDouble("question2", 2.0);
        System.out.println(saveObj.allKeys());
        System.out.println(saveObj.userKeys());
        System.out.println(saveObj.objectForKey("question2"));
        System.out.println(saveObj.getDataTypeForKey("question2"));
        saveObj.saveAsync();

        saveObj = BCObject.newObject("infolist3");
        saveObj.setBool("question3", false);
        saveObj.setDouble("question2", 3.0);
        saveObj.removeObjectForKey("question3");
        saveObj.saveAsync(new BCCallBack() {
            public void done(BCResult result) {
                System.out.println(result.isSuccess());
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
                System.out.println(result.isSuccess());
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
                System.out.println(result.isSuccess());
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
                System.out.println(result.isSuccess());
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
        // System.out.println(q1res.getObjects());
        // for (BCObject obj:q1res.getObjects()) {
        // System.out.println(obj.objectForKey("objectid"));
        // }

        // findObjectsAsync
        q1.findObjectsAsync(new BCQueryCallBack() {
            public void done(BCQueryResult result) {
                System.out.println(result.getObjects());
                for (BCObject obj : result.getObjects()) {
                    System.out.println(obj.objectForKey("question3"));
                }
            }
        });

        // getObjectAsync
        q1.getObjectAsync("28b74a4c-a3b3-4019-890e-dccd98f47610",
                new BCQueryCallBack() {
                    public void done(BCQueryResult result) {
                        System.out.println(result.getSingleObject()
                                .objectForKey("question2"));
                    }
                });

        BCQuery q2 = new BCQuery();
        q2.initWithClassName("infolist3");
        q2.whereKeyEqualTo("aaee", 453);

        // countObjectsAsync
        q2.countObjectsAsync(new BCQueryCallBack() {
            public void done(BCQueryResult results) {
                System.out.println(results.getCountObject());
            }
        });

        // deleteObjectsAsync
        q2.deleteObjectsAsync(new BCCallBack() {
            public void done(BCResult ret) {
                System.out.println(ret.getErrorInfo());
                System.out.println(ret.isSuccess());
            }
        });

        // modifyObject
        BCObject obj = BCObject.newObject("infolist3");
        obj.setDouble("question2", 8.0);
        BCQuery query = BCQuery.queryWithClassName("infolist3");
        query.whereKeyEqualTo("aaee", 453);
        query.modifyObjectsWithExampleAsync(obj, new BCCallBack() {
            public void done(BCResult result) {
                System.out.println(result.isSuccess());
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
