package com.jntele.troy.jntelelte;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissionUtil.OnRequestPermissionsResultCallbacks{

    // 手机基础信息界面元素
    private TextView changjiaView;
    private TextView xinghaoView;
    private TextView systemView;
    private TextView networkView;
    private TextView locationtypeView;
    private TextView locationView;
    private TextView imei1View;
    //   private  TextView imei2View;
    private TextView iesi1View;
    //   private  TextView iesi2View;


    // LTE网络信息界面元素
    private TextView enbView;
    private TextView cellIdView;
    private TextView pciView;
    private TextView tacView;
    private TextView ciView;
    private TextView rsrpView;
    private TextView rsrqView;
    private TextView sinrView;
    // LTE基站信息界面元素
    private TextView bbuNameView;
    private TextView rruNameView;
    private TextView stationNameView;
    private TextView xitongView;
    private TextView producerView;
    private TextView rruTypeView;
    // CDMA网络信息界面元素
    private TextView nidView;
    private TextView cidView;
    private TextView sidView;
    private TextView cdmaEcioView;
    private TextView cdmaDbmView;
    private TextView evdoEcioView;
    private TextView evdoDbmView;
    private TextView evdoSnrView;

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
        // 基站数据库处理
        initDataBase();
        // 手机权限处理
        initPermission();
        // 手机界面相关
        initUI();
        // 手机信号状态
        initSignal();
        // 手机网络状态
        initNetwork();
        showPhoneInfo();
        // 手机位置处理
        initLocation();
    }

    /***********************************************************************/
    private final int REQUEST_CODE = 101;

    // 判断手机权限
    private void initPermission() {
//        PermissionUtil.getPhoneStatePermissions(this,REQUEST_CODE_PHONESTATE);
//        PermissionUtil.getFilePermissions(this,REQUEST_CODE_PHONESTATE);
        PermissionUtil.requestPerssions(this, REQUEST_CODE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
        PermissionUtil.getPhoneStatePermissions(this, REQUEST_CODE);
        PermissionUtil.getFilePermissions(this, REQUEST_CODE);
        PermissionUtil.getLocationPermissions(this, REQUEST_CODE);
        PermissionUtil.getNetStatePermissions(this, REQUEST_CODE);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms, boolean isAllGranted) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms, boolean isAllDenied) {

    }

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

    // 界面初始化以及显示手机基础信息
    private void initUI() {
        // 初始化手机基础信息界面
        changjiaView = (TextView) findViewById(R.id.changjia);
        xinghaoView = (TextView) findViewById(R.id.xinghao);
        systemView = (TextView) findViewById(R.id.system);
        networkView = (TextView) findViewById(R.id.network);
        locationtypeView = (TextView) findViewById(R.id.locationtype);
        locationView = (TextView) findViewById(R.id.location);
        imei1View = (TextView) findViewById(R.id.imei1);
//        imei2View = (TextView)findViewById(R.id.imei2);
        iesi1View = (TextView) findViewById(R.id.iesi1);
//        iesi2View = (TextView)findViewById(R.id.iesi2);
        setInfo(changjiaView, "厂家：", android.os.Build.BRAND);
        setInfo(xinghaoView, "型号：", android.os.Build.MODEL);
        setInfo(systemView, "系统：", String.format("Android %s", android.os.Build.VERSION.RELEASE));
        // 初始化网络信息界面
        enbView = (TextView) findViewById(R.id.enodeb);
        cellIdView = (TextView) findViewById(R.id.cellid);
        ciView = (TextView) findViewById(R.id.ci);
        tacView = (TextView) findViewById(R.id.tac);
        pciView = (TextView) findViewById(R.id.pci);
        rsrpView = (TextView) findViewById(R.id.rsrp);
        rsrqView = (TextView) findViewById(R.id.rsrq);
        sinrView = (TextView) findViewById(R.id.sinr);

        nidView = (TextView) findViewById(R.id.nid);
        sidView = (TextView) findViewById(R.id.sid);
        cidView = (TextView) findViewById(R.id.cid);

        cdmaDbmView = (TextView) findViewById(R.id.cdmadbm);
        cdmaEcioView = (TextView) findViewById(R.id.cdmaecio);

        evdoDbmView = (TextView) findViewById(R.id.evdodbm);
        evdoEcioView = (TextView) findViewById(R.id.evdoecio);
        evdoSnrView = (TextView) findViewById(R.id.evdosnr);

        // 初始化基站信息界面
        bbuNameView = (TextView) findViewById(R.id.bbuname);
        rruNameView = (TextView) findViewById(R.id.cellname);
        stationNameView = (TextView) findViewById(R.id.stationname);
        xitongView = (TextView) findViewById(R.id.xitong);
        producerView = (TextView) findViewById(R.id.producer);
        rruTypeView = (TextView) findViewById(R.id.rrutype);
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
        setInfo(networkView, "网络：", String.format("%s(%s)", getNetWorkType(), teleManager.getNetworkOperatorName()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("TroyInfo", "Need READ_PHONE_STATE Permission");
            return;
        }
        setInfo(imei1View, "IMEI：", teleManager.getDeviceId());
        setInfo(iesi1View, "IMSI：", teleManager.getSubscriberId());
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
    private void initSignal(){
        // 监听手机网络状态
        conManger = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        teleManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        TeleListener teleListener = new TeleListener();
        teleManager.listen(teleListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_CELL_INFO);
    }
    // 手机信号变化监听
    private class TeleListener extends PhoneStateListener {

        // 小区信息变化时
        public void onCellInfoChanged(List<CellInfo> cellList) {
            super.onCellInfoChanged(cellList);
            if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("TroyInfo","Need ACCESS_COARSE_LOCATION Permission");
                return;
            }
            List<CellInfo> allCellInfo = teleManager.getAllCellInfo();
            CellInfoLte cellInfoLte;
            CellInfoCdma cellInfoCdma;
            int tmp = 0;
            if (allCellInfo != null) {

                for (CellInfo cellInfo : allCellInfo) {
                    if (cellInfo instanceof CellInfoLte) {
                        cellInfoLte = (CellInfoLte) cellInfo;
//                        Log.d("TroyInfo",cellInfoLte.toString());
                        if (cellInfoLte.isRegistered()) {
                            CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();
                            setInfo(tacView,"TAC ",""+cellIdentity.getTac());
                            setInfo(pciView,"PCI ",""+cellIdentity.getPci());
                            int ci = cellIdentity.getCi();
                            setInfo(ciView,"CI ","" + ci);
                            showStationInfo("" + ci);
                            int enb = ci/256;
                            setInfo(enbView,"eNB ",""+enb);
                            setInfo(cellIdView,"CellID ",""+(ci-enb*256));
                            break;
                        }
                    }
                }
                for (CellInfo cellInfo : allCellInfo){
                    if(cellInfo instanceof CellInfoCdma){
                        cellInfoCdma = (CellInfoCdma)cellInfo;
                        if(cellInfoCdma.isRegistered()) {
                            CellIdentityCdma cellIdentity = cellInfoCdma.getCellIdentity();
                            setInfo(nidView,"NID ",""+cellIdentity.getNetworkId());
                            setInfo(sidView,"SID ",""+cellIdentity.getSystemId());
                            setInfo(cidView,"CID",""+cellIdentity.getBasestationId());
                            break;
                        }
                    }
                }
            }
        }
        // 信号信息变化时
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int tmp = 0;
            String tmpInfo = "";
            try {
                // LTE网管状态
                tmp = (int) signalStrength.getClass().getMethod("getLteRsrp").invoke(signalStrength);
                if ((tmp <= -120) || (tmp >= -1))
                    tmpInfo = "";
                else
                    tmpInfo = "" + tmp;
                setInfo(rsrpView, "RSRP ", tmpInfo);
                tmp = (int) signalStrength.getClass().getMethod("getLteRsrq").invoke(signalStrength);
                if ((tmp <= -120) || (tmp >= -1))
                    tmpInfo = "";
                else
                    tmpInfo = "" + tmp;
                setInfo(rsrqView, "RSRQ ", tmpInfo);
                tmp = (int) signalStrength.getClass().getMethod("getLteSignalStrength").invoke(signalStrength);
                if (tmp == 99)
                    tmpInfo = "";
                else
                    tmpInfo = "" + tmp;
                setInfo(sinrView, "SS ", tmpInfo);
//                Log.d("TroyTest", "CQI：" + signalStrength.getClass().getMethod("getLteCqi").invoke(signalStrength));
//                Log.d("TroyTest", "Rssnr：" + signalStrength.getClass().getMethod("getLteRssnr").invoke(signalStrength));

                // CDMA网管状态
                tmp = (int) signalStrength.getClass().getMethod("getCdmaDbm").invoke(signalStrength);
                if ((tmp <= -120) || (tmp >= -1))
                    tmpInfo = "";
                else
                    tmpInfo = "" + tmp;
                setInfo(cdmaDbmView, "1XRx", tmpInfo);
                tmp = (int) signalStrength.getClass().getMethod("getCdmaEcio").invoke(signalStrength);
                if ((tmp <= -120) || (tmp >= -1))
                    tmpInfo = "";
                else
                    tmpInfo = "" + tmp;
                setInfo(cdmaEcioView, "1XEcio", tmpInfo);
                tmp = (int) signalStrength.getClass().getMethod("getEvdoDbm").invoke(signalStrength);
                if ((tmp <= -120) || (tmp >= -1))
                    tmpInfo = "";
                else
                    tmpInfo = "" + tmp;
                setInfo(evdoDbmView, "DoRx", tmpInfo);

                tmp = (int) signalStrength.getClass().getMethod("getEvdoEcio").invoke(signalStrength);
                if ((tmp <= -120) || (tmp >= -1))
                    tmpInfo = "";
                else
                    tmpInfo = "" + tmp;
                setInfo(evdoEcioView, "DoEcio", tmpInfo);
                tmp = (int) signalStrength.getClass().getMethod("getEvdoSnr").invoke(signalStrength);
                if ((tmp == -1) || (tmp == 255))
                    tmpInfo = "";
                else
                    tmpInfo = "" + tmp;
                setInfo(evdoSnrView, "SNR ", tmpInfo);

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

    }

    // 更新/获取基站信息
    private void showStationInfo(String ci)
    {
        CellData cd = util.getCellInfo(ci);
        setInfo(bbuNameView,"BBU：",cd.getBBUName());
        setInfo(rruNameView,"RRU：",cd.getCellName());
        setInfo(stationNameView,"设计名：",cd.getStationName());
        setInfo(xitongView,"系统：",cd.getSystemType());
        setInfo(producerView,"厂家：",cd.getProducer());
        setInfo(rruTypeView,"RRU型号：",cd.getRRUType());
    }

    /***********************************************************************/
    private void initLocation() {
        Log.d("TroyInfo", "initLocation");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> list = locationManager.getProviders(true);
        if (list.contains(LocationManager.GPS_PROVIDER)) {
            //是否为GPS位置控制器
            locationProvider = LocationManager.GPS_PROVIDER;

        } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            //是否为网络位置控制器
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("TroyInfo","Need ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION Permission");
            return;
        }
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            showLocation(location);
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
            showLocation(location);

        }
    };
    private void showLocation(Location location)
    {
        Log.d("TroyInfo","showLocation");
        if(locationProvider == LocationManager.NETWORK_PROVIDER)
            setInfo(locationtypeView,"定位：","网络");
        else if(locationProvider == LocationManager.GPS_PROVIDER)
            setInfo(locationtypeView,"定位：","GPS");
        setInfo(locationView,"",String.format("(%.5f,%.5f)",location.getLongitude(),location.getLatitude()));
    }

    /***********************************************************************/
    private void setInfo(TextView view,String name,String info){
        SpannableStringBuilder infos=new SpannableStringBuilder(String.format("%s%s", name, info));
        infos.setSpan(new ForegroundColorSpan(Color.parseColor("#F8DC10")),0,name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(infos);
    }

}
