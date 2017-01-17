package com.oit.ray.carefortheelderly;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by InRay on 2017/1/2.
 */
//資料表功能類別
public class ItemDAO {
//  表格名稱
    public static final String TABLE_NAME = "basic";
//  編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";
//  其他表格欄位名稱
    public static final String IDCARD_COLUMN = "idcard";
    public static final String EMAIL_COLUMN = "email";
    public static final String NAME_COLUMN = "name";
    public static final String BIRTHDAY_COLUMN = "birthday";
    public static final String SEX_COLUMN = "sex";
    public static final String REGISTRATIONDATE_COLUMN = "registration_date";
    public static final String PHOTO_COLUMN = "photo";
    public static final String USERPHONE_COLUMN = "user_phone";
    public static final String USERADDRESS_COLUMN = "user_address";
    public static final String ICENAME_COLUMN = "ICE_name";
    public static final String ICEPHONE_COLUMN = "ICE_phone";
    public static final String ICEADDRESS_COLUMN = "ICE_address";
    public static final String AUTOLOGIN = "AutoLogin";
//  使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                IDCARD_COLUMN + " TEXT NOT NULL, " +
                EMAIL_COLUMN  + " TEXT NOT NULL, " +
                NAME_COLUMN  + " TEXT NOT NULL, " +
                BIRTHDAY_COLUMN  + " TEXT NOT NULL, " +
                SEX_COLUMN  + " TEXT NOT NULL, " +
                REGISTRATIONDATE_COLUMN + " TEXT NOT NULL, " +
                PHOTO_COLUMN  + " TEXT NOT NULL, " +
                USERPHONE_COLUMN  + " TEXT NOT NULL, " +
                USERADDRESS_COLUMN  + " TEXT NOT NULL, " +
                ICENAME_COLUMN  + " TEXT NOT NULL, " +
                ICEPHONE_COLUMN  + " TEXT NOT NULL, " +
                ICEADDRESS_COLUMN  + " TEXT NOT NULL, " +
                AUTOLOGIN  + " INTEGER NOT NULL)" ;
//  資料庫物件
    private SQLiteDatabase db;

//  建構子，一般的應用都不需要修改
    public ItemDAO(Context context){
        db = SQLite_userList.getDatabase(context);
    }
//  關閉資料庫，一般的應用都不需要修改
    public void close(){
        db.close();
    }
//  新增參數指定的物件
    public Item insert(Item item){
//      建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

//      加入ContentValues物件包裝的新增資料
//      第一個參數是欄位名稱，第二個參數是欄位的資料
        cv.put(IDCARD_COLUMN, item.getIdcard());
        cv.put(EMAIL_COLUMN, item.getEmail());
        cv.put(NAME_COLUMN, item.getName());
        cv.put(BIRTHDAY_COLUMN, item.getBirthday());
        cv.put(SEX_COLUMN, item.getSex());
        cv.put(REGISTRATIONDATE_COLUMN, item.getRegistration_date());
        cv.put(PHOTO_COLUMN, item.getPhoto());
        cv.put(USERPHONE_COLUMN, item.getUser_phone());
        cv.put(USERADDRESS_COLUMN, item.getUser_address());
        cv.put(ICENAME_COLUMN, item.getICE_name());
        cv.put(ICEPHONE_COLUMN, item.getICE_phone());
        cv.put(ICEADDRESS_COLUMN, item.getICE_address());
        cv.put(AUTOLOGIN, item.getAutoLogin());

//      新增一筆資料並取得編號
//      第一個參數是表格名稱
//      第二個參數是沒有指定欄位值的預設值
//      第三個參數是包裝新增資料的ContentValues物件
        Long id = db.insert(TABLE_NAME,null,cv);
//      設定編號
        item.setId(id);
//      回傳結果
        return item;
    }

//  修改參數指定的物件
    public  boolean update(Item item){
//      建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

//      加入ContentValues物件包裝的修改資料
//      第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(IDCARD_COLUMN, item.getIdcard());
        cv.put(EMAIL_COLUMN, item.getEmail());
        cv.put(NAME_COLUMN, item.getName());
        cv.put(BIRTHDAY_COLUMN, item.getBirthday());
        cv.put(SEX_COLUMN, item.getSex());
        cv.put(REGISTRATIONDATE_COLUMN, item.getRegistration_date());
        cv.put(PHOTO_COLUMN, item.getPhoto());
        cv.put(USERPHONE_COLUMN, item.getUser_phone());
        cv.put(USERADDRESS_COLUMN, item.getUser_address());
        cv.put(ICENAME_COLUMN, item.getICE_name());
        cv.put(ICEPHONE_COLUMN, item.getICE_phone());
        cv.put(ICEADDRESS_COLUMN, item.getICE_address());
        cv.put(AUTOLOGIN, item.getAutoLogin());

//      設定修改資料的條件為編號
//      格式為"欄位名稱=資料"
        String where = KEY_ID + "=" +item.getId();

//      執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME,cv,where,null) > 0;
    }

//  刪除參數指定編號的資料
    public boolean delete(long id){
//      設定條件為編號，格式為"欄位名稱=資料"
        String where = KEY_ID + "=" +id;
//      刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME,where,null) > 0;
    }

//  刪除所有資料
    public boolean deleteAll(){
//      設定條件為編號，格式為"欄位名稱=資料"
        String where = KEY_ID + ">=" + "0";
//      刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME,where,null) > 0;
    }


//  讀取所有記事資料
    public List<Item> getAll(){
        List<Item> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME,null,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

//  讀取指定編號的資料物件
    public Item get(Long id){
//      準備回傳結果用的物件
        Item item = null;
//      使用編號為查詢條件
        String where = KEY_ID + "=" + id;
//      執行查詢
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);
//      如果有查詢結果
        if (result.moveToFirst()) {
//          讀取包裝一筆資料的物件
            item = getRecord(result);
        }

//      關閉Cursor物件
        result.close();
//      回傳結果
        return item;
    }

//  把Cursor目前的資料包裝為物件
    public Item getRecord(Cursor cursor){
//      準備回傳結果用的物件
        Item result = new Item();

        result.setId(cursor.getLong(0));
        result.setIdcard(cursor.getString(1));
        result.setEmail(cursor.getString(2));
        result.setName(cursor.getString(3));
        result.setBirthday(cursor.getString(4));
        result.setSex(cursor.getString(5));
        result.setRegistration_date(cursor.getString(6));
        result.setPhoto(cursor.getString(7));
        result.setUser_phone(cursor.getString(8));
        result.setUser_address(cursor.getString(9));
        result.setICE_name(cursor.getString(10));
        result.setICE_phone(cursor.getString(11));
        result.setICE_address(cursor.getString(12));
        result.setAutoLogin(cursor.getInt(13));

//      回傳結果
        return result;
    }

//  取得資料數量
    public int getCount(){
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
            }

        return result;
    }



}
