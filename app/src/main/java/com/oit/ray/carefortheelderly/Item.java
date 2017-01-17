package com.oit.ray.carefortheelderly;

/**
 * Created by InRay on 2017/1/2.
 */

public class Item implements java.io.Serializable {


//  編號、身分證字號、電子信箱、姓名、生日、性別、註冊日期、照片、使用址電話、使用者地址、緊急人姓名、緊急人電話、緊急人地址、自動登入
    private Long id;
    private String idcard;
    private String email;
    private String name;
    private String birthday;
    private String sex;
    private String registration_date;
    private String photo;
    private String user_phone;
    private String user_address;
    private String ICE_name;
    private String ICE_phone;
    private String ICE_address;
    private Integer autoLogin;

    public Item() {
    }

    public Item(Long id, String idcard, String email, String name, String birthday, String sex,
                String registration_date, String photo, String user_phone, String user_address,
                String ICE_name, String ICE_phone, String ICE_address,Integer autoLogin) {
        this.id = id;
        this.idcard = idcard;
        this.email = email;
        this.name = name;
        this.birthday = birthday;
        this.sex = sex;
        this.registration_date = registration_date;
        this.photo = photo;
        this.user_phone = user_phone;
        this.user_address = user_address;
        this.ICE_name = ICE_name;
        this.ICE_phone = ICE_phone;
        this.ICE_address = ICE_address;
        this.autoLogin = autoLogin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(String registration_date) {
        this.registration_date = registration_date;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getICE_name() {
        return ICE_name;
    }

    public void setICE_name(String ICE_name) {
        this.ICE_name = ICE_name;
    }

    public String getICE_phone() {
        return ICE_phone;
    }

    public void setICE_phone(String ICE_phone) {
        this.ICE_phone = ICE_phone;
    }

    public String getICE_address() {
        return ICE_address;
    }

    public void setICE_address(String ICE_address) {
        this.ICE_address = ICE_address;
    }

    public Integer getAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(Integer autoLogin) {
        this.autoLogin = autoLogin;
    }
}
