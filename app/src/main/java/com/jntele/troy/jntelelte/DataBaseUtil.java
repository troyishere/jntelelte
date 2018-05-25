package com.jntele.troy.jntelelte;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
//import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lenovo on 2018/5/19.
 */

public class DataBaseUtil {
    private Context context;
    private String DB_NAME = "jntele.db";// 数据库的名字
    private String TB_NAME = "jntele";//表格的名字
    private String DATABASE_PATH;// 数据库在手机里的路径
    private SQLiteDatabase db;

    public DataBaseUtil(Context context) {
        this.context = context;
        String packageName = context.getPackageName();
        DATABASE_PATH = "/data/data/" + packageName + "/databases/";
    }

    /**
     * 判断数据库是否存在
     *
     * @return false or true
     */
    public boolean checkDataBase() {
        SQLiteDatabase db = null;
        try {
            String databaseFilename = DATABASE_PATH + DB_NAME;
            db = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {

        }
        if (db != null) {
            db.close();
        }
        return db != null ? true : false;
    }


    /**
     * 复制数据库到手机指定文件夹下
     *
     * @throws IOException
     */
    public void copyDataBase() throws IOException {
        String databaseFilenames = DATABASE_PATH + DB_NAME;
        File dir = new File(DATABASE_PATH);
        if (!dir.exists())// 判断文件夹是否存在，不存在就新建一个
            dir.mkdir();
        FileOutputStream os = new FileOutputStream(databaseFilenames);// 得到数据库文件的写入流
        InputStream is = context.getResources().openRawResource(R.raw.jntele);
        byte[] buffer = new byte[8192];
        int count = 0;
        while ((count = is.read(buffer)) > 0) {
            os.write(buffer, 0, count);
            os.flush();
        }
        is.close();
        os.close();
    }


    public CellData getCellInfo(String ci)
    {
        ContentValues value = new ContentValues();
        CellData cd = new CellData();
        openDatabase();
        Cursor cursor = db.query(TB_NAME, null, "cell_id=?", new String[] { ci }, null, null, null);
        while (cursor.moveToNext()) {

//            cd.setCellId(cursor.getString(cursor.getColumnIndex("cell_id")));
            cd.setCellName(cursor.getString(cursor.getColumnIndex("cell_name")));
            cd.setBBUName(cursor.getString(cursor.getColumnIndex("bbu_name")));
            cd.setProducer((cursor.getString(cursor.getColumnIndex("producer"))).indexOf('N')!=-1?"诺基亚":"华为");
            cd.setRRUType (cursor.getString(cursor.getColumnIndex("rru_type")));
            cd.setSystemType((cursor.getString(cursor.getColumnIndex("system_type"))).indexOf('I')!=-1?"室分":"室外");
            cd.setStationName(cursor.getString(cursor.getColumnIndex("station_name")));
//            cd.setCounty(cursor.getString(cursor.getColumnIndex("county")));
//            cd.setSource(cursor.getString(cursor.getColumnIndex("source")));
        }
//        closeDatabase();
        return cd;
    }

    private void openDatabase() {
        if (db == null) {
            db = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH + "/" + DB_NAME, null);;
        }
    }
    private void closeDatabase() {
        if (db != null) {
            db.close();
        }
    }
}
