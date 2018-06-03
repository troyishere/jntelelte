package com.jntele.troy.jntelelte;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.Nullable;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityLte;
import android.telephony.SignalStrength;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jntele.troy.jntelelte.CellData;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Objects;

import static java.lang.Math.abs;

/**
 * Created by lenovo on 2018/5/26.
 */

public class InfoView extends LinearLayout {



    // 手机基础信息界面元素
    private TextView changjiaView;
    private TextView xinghaoView;
    private TextView systemView;
    public TextView networkView;
    public TextView locationtypeView;
    public TextView locationView;
    public TextView imei1View;
    public TextView imei2View;
    public TextView iesi1View;
    public TextView iesi2View;
    // LTE网络信息界面元素
    private TextView enbView;
    private TextView cellIdView;
    private TextView pciView;
    private TextView tacView;
    private TextView ciView;
    private TextView rsrpView;
    private TextView rsrqView;
    private TextView sinrView;
    private TextView freqView;
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
    private TextView bidView;

    //
    private LinearLayout ltenetInfo;
    private LinearLayout ltestationInfo;
    private LinearLayout cdmanetInfo;

    public InfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.info_layout, this);


        // 初始化手机基础信息界面
        changjiaView = (TextView) findViewById(R.id.changjia);
        xinghaoView = (TextView) findViewById(R.id.xinghao);
        systemView = (TextView) findViewById(R.id.system);
        networkView = (TextView) findViewById(R.id.network);
        locationtypeView = (TextView) findViewById(R.id.locationtype);
        locationView = (TextView) findViewById(R.id.location);
        imei1View = (TextView) findViewById(R.id.imei1);
        imei2View = (TextView)findViewById(R.id.imei2);
        iesi1View = (TextView) findViewById(R.id.iesi1);
        iesi2View = (TextView)findViewById(R.id.iesi2);
        // 初始化LTE网络信息界面
        enbView = (TextView) findViewById(R.id.enodeb);
        cellIdView = (TextView) findViewById(R.id.cellid);
        ciView = (TextView) findViewById(R.id.ci);
        tacView = (TextView) findViewById(R.id.tac);
        pciView = (TextView) findViewById(R.id.pci);
        rsrpView = (TextView) findViewById(R.id.rsrp);
        rsrqView = (TextView) findViewById(R.id.rsrq);
        sinrView = (TextView) findViewById(R.id.sinr);
        freqView = (TextView) findViewById(R.id.freq);

        // 初始化基站信息界面
        bbuNameView = (TextView) findViewById(R.id.bbuname);
        rruNameView = (TextView) findViewById(R.id.cellname);
        stationNameView = (TextView) findViewById(R.id.stationname);
        xitongView = (TextView) findViewById(R.id.xitong);
        producerView = (TextView) findViewById(R.id.producer);
        rruTypeView = (TextView) findViewById(R.id.rrutype);

        // 初始化CDMA网络信息界面
        nidView = (TextView) findViewById(R.id.nid);
        sidView = (TextView) findViewById(R.id.sid);
        cidView = (TextView) findViewById(R.id.cid);
        bidView = (TextView) findViewById(R.id.bid);
        cdmaDbmView = (TextView) findViewById(R.id.cdmadbm);
        cdmaEcioView = (TextView) findViewById(R.id.cdmaecio);
        evdoDbmView = (TextView) findViewById(R.id.evdodbm);
        evdoEcioView = (TextView) findViewById(R.id.evdoecio);
        evdoSnrView = (TextView) findViewById(R.id.evdosnr);

        //
        ltenetInfo = (LinearLayout) findViewById(R.id.ltenetinfo);
        ltestationInfo = (LinearLayout) findViewById(R.id.ltestationinfo);
        cdmanetInfo = (LinearLayout) findViewById(R.id.cdmanetinfo);

        setInfo(changjiaView, "厂家：", android.os.Build.BRAND);
        setInfo(xinghaoView, "型号：", android.os.Build.MODEL);
        setInfo(systemView, "系统：", String.format("Android %s", android.os.Build.VERSION.RELEASE));
    }

    public void setNetwork(String network,String operator){
        setInfo(networkView, "数据：", String.format("%s(%s)", network, operator));
    }

    public void setPhoneID(String imei,String imsi,int num){
        if(num==0) {
            setInfo(imei1View, "IMEI：", imei);
            setInfo(iesi1View, "IMSI：", imsi);
            imei2View.setVisibility(View.GONE);
            iesi2View.setVisibility(View.GONE);
        }else if(num==1){
            setInfo(imei1View, "IMEI1：", imei);
            setInfo(iesi1View, "IMSI1：", imsi);
            imei2View.setVisibility(View.VISIBLE);
            iesi2View.setVisibility(View.VISIBLE);
        }else{
            setInfo(imei2View, "IMEI2：", imei);
            setInfo(iesi2View, "IMSI2：", imsi);
        }
    }

    public void setLocationType(String locationType){
        setInfo(locationtypeView,"定位：",locationType);
    }

    public void setLocation(Location location){
        setInfo(locationView,"",String.format(Locale.getDefault(),"(%.5f,%.5f)",location.getLongitude(),location.getLatitude()));
    }

    public void setLteNetInfo(CellIdentityLte cellIdentity){

        int ci = cellIdentity.getCi();
        int enb = ci / 256;
        setInfo(enbView,"eNB ","" + enb);
        setInfo(ciView, "CI ", "" + ci);
        setInfo(tacView, "TAC ", "" + cellIdentity.getTac());
        setInfo(pciView, "PCI ", "" + cellIdentity.getPci());
        setInfo(cellIdView, "CellID ", "" + (ci - enb * 256));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setInfo(freqView,"频段 ","" + cellIdentity.getEarfcn());
        }
    }

    public void setLteSignalInfo(SignalStrength signalStrength) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {


        String tmpInfo;
        int tmp = (int) signalStrength.getClass().getMethod("getLteRsrp").invoke(signalStrength);
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

        tmp = (int) signalStrength.getClass().getMethod("getLteRssnr").invoke(signalStrength);
        if(abs(tmp)>300)
            tmpInfo = "";
        else
            tmpInfo = String.format(Locale.getDefault(),"%.1f",0.1*tmp);
        setInfo(sinrView, "RSSNR ",tmpInfo);

    }

    public void setLteStationInfo(CellData cd){
        if(Objects.equals(cd.getBBUName(), ""))
            unshowLteStationView();
        else {
            showLteStationView();
            setInfo(bbuNameView, "BBU：", cd.getBBUName());
            setInfo(rruNameView, "RRU：", cd.getCellName());
            setInfo(stationNameView, "站点名：", cd.getStationName());
            setInfo(xitongView, "系统：", cd.getSystemType());
            setInfo(producerView, "厂家：", cd.getProducer());
            setInfo(rruTypeView, "RRU型号：", cd.getRRUType());
        }
    }

    public void setCdmaNetInfo(CellIdentityCdma cellIdentity){
        setInfo(nidView, "NID ", "" + cellIdentity.getNetworkId());
        setInfo(sidView, "SID ", "" + cellIdentity.getSystemId());
        int cid = cellIdentity.getBasestationId();
        setInfo(cidView, "CID ", "" + cid);
        int x = cid / (16 * 16);
        int y = x / 16;
        int z = cid - x * 16 * 16 + y * 16 * 16;
        setInfo(bidView, "BID ", "" + z);
    }

    public void setCdmaSignalInfo(SignalStrength signalStrength) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{
        String tmpInfo;
        int tmp = (int) signalStrength.getClass().getMethod("getCdmaDbm").invoke(signalStrength);
        if ((tmp <= -120) || (tmp >= -1))
            tmpInfo = "";
        else
            tmpInfo = "" + tmp;
        setInfo(cdmaDbmView, "1XRx", tmpInfo);
        tmp = (int) signalStrength.getClass().getMethod("getCdmaEcio").invoke(signalStrength);
        if ((tmp <= -120) || (tmp >= -1))
            tmpInfo = "";
        else
            tmpInfo = "" + 0.1 * tmp;
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
            tmpInfo = "" + 0.1 * tmp;
        setInfo(evdoEcioView, "DoEcio", tmpInfo);
        tmp = (int) signalStrength.getClass().getMethod("getEvdoSnr").invoke(signalStrength);
        if ((tmp == -1) || (tmp == 255))
            tmpInfo = "";
        else
            tmpInfo = "" + tmp;
        setInfo(evdoSnrView, "SNR ", tmpInfo);
    }

    protected void setInfo(TextView view, String name, String info){
        if((name==null)||(name==""))
            view.setText(info);
        else {
            if ((info == null) || (info == ""))
                info = " ";
            SpannableStringBuilder infos = new SpannableStringBuilder(String.format("%s%s", name, info));
            infos.setSpan(new ForegroundColorSpan(Color.parseColor("#F8DC10")), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(infos);
        }
    }

    public void showLteNetView(){
        ltenetInfo.setVisibility(View.VISIBLE);
    }
    public void unshowLteNetView(){
        ltenetInfo.setVisibility(View.GONE);
    }
    public void showLteStationView(){
        ltestationInfo.setVisibility(View.VISIBLE);
    }
    public void unshowLteStationView(){
        ltestationInfo.setVisibility(View.GONE);
    }

    public void showCdmaNetView(){
        cdmanetInfo.setVisibility(View.VISIBLE);
    }
    public void unshowCdmaNetView(){
        cdmanetInfo.setVisibility(View.GONE);
    }
}
