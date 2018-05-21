package com.jntele.troy.jntelelte;

/**
 * Created by lenovo on 2018/5/19.
 */

public class CellData {
    private String cell_id="";
    private String cell_name="";
    private String bbu_name="";
    private String producer="";
    private String rru_type ="";
    private String system_type="";
    private String station_name="";
    private String county="";
    private String source="";


    public String getCellId(){return cell_id;}
    public String getCellName(){return cell_name;}
    public String getBBUName(){return bbu_name;}
    public String getProducer(){return producer;}
    public String getRRUType (){return rru_type ;}
    public String getSystemType(){return system_type;}
    public String getStationName(){return station_name;}
    public String getCounty(){return county;}
    public String getSource(){return source;}

    public void setCellId(String info){cell_id=info;}
    public void setCellId(int info){cell_id=""+info;}
    public void setCellName(String info){cell_name=info;}
    public void setBBUName(String info){bbu_name=info;}
    public void setProducer(String info){producer=info;}
    public void setRRUType (String info){rru_type =info;}
    public void setSystemType(String info){system_type=info;}
    public void setStationName(String info){station_name=info;}
    public void setCounty(String info){county=info;}
    public void setSource(String info){source=info;}



}
