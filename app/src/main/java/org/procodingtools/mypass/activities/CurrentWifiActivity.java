package org.procodingtools.mypass.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import org.procodingtools.mypass.R;
import org.procodingtools.mypass.adapters.recycler_views.WifiRecyclerAdapter;
import org.procodingtools.mypass.dialogs.WifiDetailsDialog;
import org.procodingtools.mypass.interfaces.callbacks.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.chainfire.libsuperuser.Shell;

public class CurrentWifiActivity extends AppCompatActivity {

    private AwesomeProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private WifiRecyclerAdapter adapter;
    public static Activity ACTIVITY;
    private List<Map<String, String>> list;
    private WifiDetailsDialog dialog;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_wifi);

        //init
        list = new ArrayList<>();
        ACTIVITY = this;
        dialog = new WifiDetailsDialog(CurrentWifiActivity.this);

        //init recycler view/adapter
        recyclerView = findViewById(R.id.store_wifi_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WifiRecyclerAdapter(list);
        recyclerView.setAdapter(adapter);
        //adding on item click listener
        adapter.addOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(String title, String key) {
                dialog.show(title, key, null);
            }
        });

        //init progress dialog (will be replaced by an animation ya feress XD)
        progressDialog = new AwesomeProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Checking for ★ROOT★");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        //checking if device is connected to a wifi access point
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            //checking for for root and getting password
            new AsyncTask<Void, Void, List<Map<String, String>>>() {

                @Override
                protected List<Map<String, String>> doInBackground(Void... voids) {
                    //cheking for root
                    //returning null if root not found
                    if (!Shell.SU.available())
                        return null;

                    //root found
                    //getting file content
                    List<String> result = Shell.SU.run("cat /data/misc/wifi/wpa_supplicant.conf");

                    //parsing file content
                    int i = -1;
                    while (i < result.size() - 1) {
                        i++;
                        String str = result.get(i);

                        //looking for "ssid"
                        if (str.contains("ssid=")) {
                            Map<String, String> map = new HashMap<>();

                            //getting ssid value
                            map.put("ssid", str.substring(str.indexOf('"') + 1, str.lastIndexOf('"')));
                            //looking for password value
                            while (i < result.size()) {
                                i++;
                                if (result.get(i).contains("psk=")) {
                                    str = result.get(i);
                                    map.put("key", str.substring(str.indexOf('"') + 1, str.lastIndexOf('"')));

                                    //adding map to list
                                    list.add(map);

                                    //breaking while loop
                                    break;
                                }
                            }
                        }

                    }
                    return list;
                }

                @Override
                protected void onPostExecute(final List<Map<String, String>> list) {
                    super.onPostExecute(list);
                    if (list == null) {
                        //on root not found
                        progressDialog.hide();
                        new AwesomeErrorDialog(CurrentWifiActivity.this)
                                .setButtonText("Ok")
                                .setCancelable(false)
                                .setMessage("Your device must be ROOTED to get the Magic")
                                .setErrorButtonClick(new Closure() {
                                    @Override
                                    public void exec() {
                                        ACTIVITY.finish();
                                    }
                                }).show();
                    } else {
                        //on root found
                        //getting current wifi ssid
                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(CurrentWifiActivity.this.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        String wifi = wifiInfo.getSSID();
                        //removing '"' chars from current SSID
                        wifi = wifi.substring(1, wifi.length() - 1);

                        //looking for current SSID from the wifis list
                        int i = -1;
                        while (i < list.size() - 1) {
                            i++;
                            //if ssid found
                            if (list.get(i).get("ssid").equals(wifi)) {
                                //closing progress dialog
                                progressDialog.hide();

                                //showing wifi details (name and password)
                                dialog.show(list.get(i).get("ssid"), list.get(i).get("key"), adapter);

                                //breaking while loop
                                break;
                            }
                        }
                    }
                }
            }.execute();
        } else {
            //on not connected to a wifi access point
            //closing progress dialog
            progressDialog.hide();

            //showing not connected error
            new AwesomeErrorDialog(CurrentWifiActivity.this)
                    .setButtonText("Ok")
                    .setCancelable(false)
                    .setMessage("Your device must be connected to a WIFI access point")
                    .setErrorButtonClick(new Closure() {
                        @Override
                        public void exec() {
                            //exit application
                            ACTIVITY.finish();
                        }
                    }).show();
        }


    }
}
