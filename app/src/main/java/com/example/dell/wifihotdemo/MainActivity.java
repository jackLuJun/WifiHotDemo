package com.example.dell.wifihotdemo;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.edit_account)
    EditText mAccount;
    @BindView(R.id.edit_psw)
    EditText mPwd;

    @BindView(R.id.text_account)
    TextView account;

    @BindView(R.id.text_pwd)
    TextView textViewPwd;


    @BindView(R.id.btn_create)
    Button btn_create;


    @BindView(R.id.btn_close)
    Button btn_close;
    private WifiManager wifiManager;
    private String userAccount;
    private String passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


     /*   mAccount = (EditText)findViewById(R.id.edit_account);
        mPwd = (EditText)findViewById(R.id.edit_psw);
        account = (TextView)findViewById(R.id.text_account);
        textViewPwd = (TextView)findViewById(R.id.text_pwd);
        btn_create = (Button)findViewById(R.id.btn_create);
        btn_close = (Button)findViewById(R.id.btn_close);*/

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);



        btn_create.setOnClickListener(this);
        btn_close.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_create:  //创建wifi热点

                userAccount = mAccount.getText().toString();
                passWord = mPwd.getText().toString();
                createWifiHot();
                break;

            case R.id.btn_close: //关闭wifi热点
                closeWifiHot();
                break;
        }
    }


    /**
     * 创建热点
     */
    private void createWifiHot() {
        if (wifiManager.isWifiEnabled()) {
            //如果wifi处于打开状态，则关闭wifi,
            wifiManager.setWifiEnabled(false);
        }
        WifiConfiguration config = new WifiConfiguration();


        if(userAccount.isEmpty()){
            config.SSID = "wifi热点测试";
            Log.e("LJ", "默认账号");
        }else{
            config.SSID  = userAccount;
            Log.e("LJ自定义账号", config.SSID);
        }

        if(passWord.isEmpty()){
            config.preSharedKey = "123456789";
        }else{
            config.preSharedKey = passWord;

        }
        config.hiddenSSID = false;  //设置为true为隐藏wifi热点，可能搜索不到。
        config.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN);//开放系统认证
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        //通过反射调用设置热点
        try {
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean enable = (Boolean) method.invoke(wifiManager, config, true);
            if (enable) {
                Log.e("LJ", "创建热点成功");
                account.setText( config.SSID);
                textViewPwd.setText(config.preSharedKey);

                Toast.makeText(this, "创建热点成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "创建热点失败，请重新创建", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // textview.setText("创建热点失败");
            Toast.makeText(this, "创建热点失败", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 关闭热点
     */
    private void closeWifiHot() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
            Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(wifiManager, config, false);
            Toast.makeText(this, "关闭热点", Toast.LENGTH_SHORT).show();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.e("LJ", "关闭热点1");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.e("LJ", "关闭热点2");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.e("LJ", "关闭热点3");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.e("LJ", "关闭热点4");
        }

    }
}
