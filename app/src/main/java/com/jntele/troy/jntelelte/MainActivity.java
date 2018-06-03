package com.jntele.troy.jntelelte;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
//import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // 界面模块及可见性
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private InfoView infoView;
    private InfoDialogFragment infoDialogFragment;
//    private TextView textView;


    private NetWorkChangeReceiver networkChangeReceiver;
    private IntentFilter intentFilter;
    private TelephonyManager teleManager;
    private ConnectivityManager conManger;
    private DataBaseUtil util;
    private String locationProvider = "";
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        infoDialogFragment = new InfoDialogFragment();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reStartActivity();
            }
        });
        infoView = (InfoView) findViewById(R.id.infoview);

//        textView = (TextView)findViewById(R.id.test);
//        textView.setVisibility(View.GONE);
//        infoView.setVisibility(View.GONE);
        // 手机权限处理
        initPermission();
        // 基站数据库处理
        initDataBase();
        // 手机信号状态
        initSignal();
        // 手机网络状态
        initNetwork();
        showPhoneInfo();
        // 手机位置处理
        initLocation();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /***********************************************************************/
    private final int REQUEST_CODE = 101;

    // 判断手机权限
    private void initPermission() {
        PermissionUtil.requestPermissions(this, REQUEST_CODE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                //Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /***********************************************************************/
    // 数据库初始化
    private void initDataBase() {
        util = new DataBaseUtil(this);
        if (!util.checkDataBase()) {
            try {
                util.copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***********************************************************************/
    // 网络状态初始化
    private void initNetwork() {

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetWorkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    // 监听网络变化
    private class NetWorkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            showPhoneInfo();
        }
    }

    // 网络状态信息
    private void showPhoneInfo() {
        infoView.setNetwork(getNetWorkType(), teleManager.getSimOperatorName());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            Log.d("TroyInfo", "Need READ_PHONE_STATE Permission");
            return;
        }
        boolean ret = false;
        for (Method m : teleManager.getClass().getDeclaredMethods()) {

            if (m.getName().equals("getPhoneCount")) {
                ret = true;
                break;
            }
        }
        if(ret&&(teleManager.getPhoneCount() == 2)){
            infoView.setPhoneID(teleManager.getDeviceId(0),teleManager.getSubscriberId(),1);
            String imei2 = teleManager.getDeviceId(1);
            infoView.setPhoneID((imei2!=null? imei2:""),"",2);
        }else{
            infoView.setPhoneID(teleManager.getDeviceId(),teleManager.getSubscriberId(),0);
        }
    }

    // 手机上网状态
    private String getNetWorkType() {
        //结果返回值
        String netType = "Unknow";
        //获取NetworkInfo对象
        NetworkInfo networkInfo = conManger.getActiveNetworkInfo();
        //NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null) {
            netType = "None";
        } else {//否则 NetworkInfo对象不为空 则获取该networkInfo的类型
            int nType = networkInfo.getType();
            if (nType == ConnectivityManager.TYPE_WIFI) {
                netType = "Wifi";
            } else if (nType == ConnectivityManager.TYPE_MOBILE) {
                if (!teleManager.isNetworkRoaming()) {
                    netType = networkInfo.getSubtypeName();
                }
            } else {
                netType = "Unknow";
            }
        }
        return netType;
    }

    /***********************************************************************/
    private void initSignal() {
        // 监听手机网络状态
        conManger = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        teleManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        TeleListener teleListener = new TeleListener();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            teleManager.listen(teleListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_CELL_INFO);// | PhoneStateListener.LISTEN_SERVICE_STATE);
        }
    }

    // 手机信号变化监听
    private class TeleListener extends PhoneStateListener {
        // 小区信息变化时
        @Override
        public void onCellInfoChanged(List<CellInfo> cellList) {
            super.onCellInfoChanged(cellList);
            @SuppressLint("MissingPermission") List<CellInfo> allCellInfo = teleManager.getAllCellInfo();
            CellInfoLte cellInfoLte;
            CellInfoCdma cellInfoCdma;

            infoView.unshowLteNetView();
            infoView.unshowLteStationView();
            infoView.unshowCdmaNetView();

            int tmp = 0;
            if (allCellInfo != null) {

                for (CellInfo cellInfo : allCellInfo) {
                    if (cellInfo instanceof CellInfoLte) {
                        cellInfoLte = (CellInfoLte) cellInfo;
                        if (cellInfoLte.isRegistered()) {
                            infoView.showLteNetView();
                            CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();
                            String test="CellIdentityLte:";
//                            Method[] methods= cellIdentity.getClass().getDeclaredMethods();
//                            for(Method method:methods){
//                                if(method.getName().contains("get"))
//                                    test = String.format("%s\n%s",test , method.getName());
//                            }
//                            textView.setText(test);
                            int ci = cellIdentity.getCi();
                            infoView.setLteNetInfo(cellIdentity);
                            CellData cellData = util.getCellInfo(""+ci);

                            String operator = teleManager.getSimOperatorName();
//                            textView.setText(operator);
                            if(operator.contains("中国电信"))
                                infoView.setLteStationInfo(cellData);
                        }
                        break;
                    }
                }
            }

            for (CellInfo cellInfo : allCellInfo) {
                if (cellInfo instanceof CellInfoCdma) {
                    cellInfoCdma = (CellInfoCdma) cellInfo;
                    if (cellInfoCdma.isRegistered()) {
                        infoView.showCdmaNetView();
                        CellIdentityCdma cellIdentity = cellInfoCdma.getCellIdentity();
                        infoView.setCdmaNetInfo(cellIdentity);
                        break;
                    }
                }

            }
        }
        // 信号信息变化时
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int tmp = 0;
            String tmpInfo = "";
            try {
                // LTE网管状态
                infoView.setLteSignalInfo(signalStrength);

                // CDMA网管状态
                infoView.setCdmaSignalInfo(signalStrength);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        

//        @Override
//        public void onServiceStateChanged(ServiceState serviceState) {
//            super.onServiceStateChanged(serviceState);
////            String tmp = serviceState.getOperatorAlphaShort();
////            String tmp = "" + (int) serviceState.getClass().getMethod("getNetworkType").invoke(serviceState);
//            String tmp;
////            Log.d("TroyInfoServiceState",tmp);
//            try {
//                tmp = "" + (int) serviceState.getClass().getMethod("getNetworkType").invoke(serviceState);
//                Log.d("TroyInfoServiceState",tmp);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//
//        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            finish();
            return true;
        }else if(id == R.id.action_info){

            infoDialogFragment.show("说明", getString(R.string.app_info), "确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }, getFragmentManager());
            return true;
        }else if(id == R.id.action_settings){
            infoDialogFragment.show("更新", getString(R.string.app_un), "确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }, getFragmentManager());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /***********************************************************************/
    @SuppressLint("MissingPermission")
    private void initLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> list = locationManager.getProviders(true);
        if (list.contains(LocationManager.GPS_PROVIDER)) {
            //是否为GPS位置控制器
            locationProvider = LocationManager.GPS_PROVIDER;

        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            //是否为网络位置控制器
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        if(Objects.equals(locationProvider, "")){
            Log.d("TroyInfo","Provider Null");
            Toast.makeText(getApplicationContext(), "请至少打开网络定位！",   Toast.LENGTH_SHORT).show();
        }else {
            infoView.setLocationType(locationProvider);
            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location != null) {
                infoView.setLocation(location);
            }
        }
    }
    LocationListener locationListener =  new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            infoView.setLocation(location);
        }
    };

    private void reStartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
