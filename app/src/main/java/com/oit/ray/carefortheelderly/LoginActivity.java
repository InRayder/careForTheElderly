package com.oit.ray.carefortheelderly;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 * 通過電子郵件/密碼提供登錄的登錄屏幕。
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    //  宣告SQLite
    private ItemDAO itemDAO;
    //  宣告Item
    private  Item item;
    //  宣告字串
    String idcard,email,name,birthday,sex,registration_date,photo,user_phone,user_address,ICE_name,ICE_phone,ICE_address;
    /**
     * Id to identity READ_CONTACTS permission request.
     * 標識READ_CONTACTS權限請求。
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * 包含已知用戶名和密碼的虛擬身份驗證存儲。
     * TODO: remove after connecting to a real authentication system.
     * TODO：在連接到真實的身份驗證系統後刪除。
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     * 跟踪登錄任務，以確保我們可以取消請求。
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    // UI引用。
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private CheckBox cAutoLogin;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        // 設置登錄表單。
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        cAutoLogin = (CheckBox)findViewById(R.id.checkAutoLogin);



        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        Button mRegistrationButton = (Button) findViewById(R.id.registration_button);
//      進行登入認證
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
//      進入註冊畫面
        mRegistrationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    產生Inter物件
                    Intent intent = new Intent();
//                  指定從LoginActivity切換至RegisteredActivity
                    intent.setClass(LoginActivity.this,RegisteredActivity.class);
//                  啟動指定之Actitivy
                    startActivity(intent);
//                  結束目前執行的Actitivy
//                    LoginActivity.this.finish();
                }catch (Exception e){}

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        IsSetAutoLoin();
    }

    private void IsSetAutoLoin(){
        // 建立資料庫物件
        itemDAO = new ItemDAO(getApplicationContext());
        item = itemDAO.get(Long.valueOf(1));
        if(itemDAO.get(Long.valueOf(1)) != null){
            int AutoLogin = item.getAutoLogin();
            System.out.println("getAutoLogin(L)"+item.getAutoLogin());
            if(AutoLogin == 1){
                System.out.println("自動登入成功!");
//                  產生Inter物件
                Intent intent = new Intent();
//                  指定從LoginActivity切換至MainActivity
                intent.setClass(LoginActivity.this, MainActivity.class);
//                  啟動指定之Actitivy
                startActivity(intent);
//                  結束目前執行的Actitivy
                LoginActivity.this.finish();
            }
        }

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     * 在權限請求已完成時收到回調。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     * 嘗試登錄或註冊登錄表單指定的帳戶。
     * 如果存在表單錯誤（無效的電子郵件，缺少字段等），
     * 則會顯示錯誤，並且不會進行實際的登錄嘗試。
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        int autoLogin = 0;
        // Reset errors.
        // 重置錯誤。
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        // 在登錄嘗試時存儲值。
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        if(cAutoLogin.isChecked()){
            autoLogin = 1;
        }


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        // 如果用戶輸入了一個密碼，請檢查有效的密碼。
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        // 檢查有效的電子郵件地址。
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            // 有一個錯誤; 不要嘗試登錄並將第一個表單字段集中到一個錯誤。
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            // 顯示進度微調器，並啟動後台任務以執行用戶登錄嘗試。
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, autoLogin);
            mAuthTask.execute((Void) null);
/*
//          產生Inter物件
            Intent intent = new Intent();
//          指定從LoginActivity切換至MainActivity
            intent.setClass(LoginActivity.this, MainActivity.class);
//          啟動指定之Actitivy
            startActivity(intent);
//          結束目前執行的Actitivy
            LoginActivity.this.finish();
            */
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        // TODO：將此替換為您自己的邏輯
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        // TODO：將此替換為您自己的邏輯
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     * 顯示進度UI並隱藏登錄表單。
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        //在Honeycomb MR2上，我們有ViewPropertyAnimator API，允許非常容易的動畫。 如果可用，請使用這些API來淡化進度微調器。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                //檢索設備用戶的“個人資料”聯繫人的數據行。
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                // 僅選擇電子郵件地址。
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                //首先顯示主電子郵件地址。 請注意，如果用戶沒有指定主電子郵件地址，則不會有主電子郵件地址。
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        //創建適配器以告訴AutoCompleteTextView在其下拉列表中顯示什麼。
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     * 表示用於驗證用戶的異步登錄/註冊任務。
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final int mAutoLogin;

        UserLoginTask(String email, String password, int mAutoLogin) {
            mEmail = email;
            mPassword = password;
            this.mAutoLogin = mAutoLogin;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // TODO：嘗試對網絡服務進行身份驗證。

            try {
                String result = DBConnector.executeQuery("SELECT * FROM `user_list` WHERE `email` LIKE '"+mEmail+"' AND `idcard` LIKE '"+mPassword+"'");
                String trueR = result.replaceAll("\\s+","");//去除空白字元
                System.out.println(trueR);

                if(!trueR.equals("null")){
                    //呼叫SQLite
                    itemDAO = new ItemDAO(getApplicationContext());
                    itemDAO.deleteAll();


                    System.out.println("Login:登入成功");
                    JSONArray jsonArray = new JSONArray(trueR);
                    JSONObject jsonData = jsonArray.getJSONObject(0);
                    idcard = jsonData.getString("idcard");
                    email = jsonData.getString("email");
                    name = jsonData.getString("name");
                    birthday = jsonData.getString("birthday");
                    sex = jsonData.getString("sex");
                    registration_date = jsonData.getString("registration_date");
                    photo = jsonData.getString("photo");
                    user_phone = jsonData.getString("user_phone");
                    user_address = jsonData.getString("user_address");
                    ICE_name = jsonData.getString("ICE_name");
                    ICE_phone = jsonData.getString("ICE_phone");
                    ICE_address = jsonData.getString("ICE_address");
//                  呼叫Item
                    item = new Item(Long.valueOf(0),idcard,email,name,birthday,sex,registration_date,photo,user_phone,user_address,ICE_name,ICE_phone,ICE_address,mAutoLogin);
                    itemDAO.insert(item);

//                  產生Inter物件
                    Intent intent = new Intent();
//                  指定從LoginActivity切換至MainActivity
                    intent.setClass(LoginActivity.this, MainActivity.class);
//                  啟動指定之Actitivy
                    startActivity(intent);
//                  結束目前執行的Actitivy
                    LoginActivity.this.finish();

                }else{
                    System.out.println("Login:登入失敗");
                    return false;
                }
            }catch (Exception e){
                Log.e("log_tag", e.toString());
            }

//
//            try {
//                // Simulate network access.
//                // 模擬網絡訪問。
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                return false;
//            }

//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    // 帳戶存在，如果密碼匹配則返回true。
//                    return pieces[1].equals(mPassword);
//                }
//            }

            // TODO: register the new account here.
            //TODO：在此註冊新帳戶。
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

