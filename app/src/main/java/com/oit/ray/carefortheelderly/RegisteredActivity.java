package com.oit.ray.carefortheelderly;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisteredActivity extends AppCompatActivity {

//  宣告SQLite
    private ItemDAO itemDAO;
//  宣告Item
    private  Item item;

//  宣告表單
    private EditText Et_idcard,Et_email,Et_name,Et_birthday,Et_userPhone,Et_userAddress,Et_ICEName,Et_ICEPhone,Et_ICEAddress;
    private Spinner Sp_sex;
    private Button Bt_submit;
//  宣告字串
    String idcard,email,name,birthday,sex,photo="null",user_phone,user_address,ICE_name,ICE_phone,ICE_address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
//      呼叫SQLite
        itemDAO = new ItemDAO(getApplicationContext());


//      宣告表單內容
        Et_idcard = (EditText)findViewById(R.id.idcard);
        Et_email = (EditText)findViewById(R.id.email);
        Et_name = (EditText)findViewById(R.id.name);
        Et_birthday = (EditText)findViewById(R.id.birthday);
        Et_userPhone = (EditText)findViewById(R.id.user_phone);
        Et_userAddress = (EditText)findViewById(R.id.user_address);
        Et_ICEName = (EditText)findViewById(R.id.ICE_name);
        Et_ICEPhone = (EditText)findViewById(R.id.ICE_phone);
        Et_ICEAddress = (EditText)findViewById(R.id.ICE_address);
        Sp_sex = (Spinner)findViewById(R.id.sex);

        Bt_submit = (Button)findViewById(R.id.submit);
        Bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doRegistered();
                    }
                }).start();
            }
        });





    }

    private void doRegistered(){
//      宣告表單內容成字串
        idcard = Et_idcard.getText().toString();
        email = Et_email.getText().toString();
        name = Et_name.getText().toString();
        birthday = Et_birthday.getText().toString();
        sex = Sp_sex.getSelectedItem().toString();
        user_phone = Et_userPhone.getText().toString();
        user_address = Et_userAddress.getText().toString();
        ICE_name = Et_ICEName.getText().toString();
        ICE_phone = Et_ICEPhone.getText().toString();
        ICE_address = Et_ICEAddress.getText().toString();


        //先定義時間格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        //取得現在時間
        Date dt = new Date();
        //透過SimpleDateFormat的format方法將Date轉換為字串
        String dts = sdf.format(dt);


//      呼叫Item
        item = new Item(Long.valueOf(0),idcard,email,name,birthday,sex,dts,photo,user_phone,user_address,ICE_name,ICE_phone,ICE_address);
        itemDAO.insert(item);

        try {
            String result = DBConnector.executeQuery("INSERT INTO `user_list` " +
                    "(`idcard`, `email`, `name`, `birthday`, `sex`, `registration_date`, `photo`, " +
                    "`user_phone`, `user_address`, `ICE_name`, `ICE_phone`, `ICE_address`) " +
                    "VALUES ('"+idcard+"', '"+email+"', '"+name+"', '"+birthday+"', '"+sex+"', '"+dts+"', '"+photo+"', " +
                    "'"+user_phone+"', '"+user_address+"', '"+ICE_name+"', '"+ICE_phone+"', '"+ICE_address+"')");
            String trueR = result.replaceAll("\\s+","");//去除空白字元
            System.out.println(trueR);
        }catch (Exception e){
            Log.e("log_tag", e.toString());
        }

    }
}

