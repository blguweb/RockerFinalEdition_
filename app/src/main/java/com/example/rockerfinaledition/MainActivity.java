package com.example.rockerfinaledition;

//*android:theme="@android:style/Theme.Translucent"
 //*android:background="@android:color/transparent"*/
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class MainActivity extends AppCompatActivity {
    private ImageView ImageLock;
    private ImageView ImageUnlock;
    private BluetoothSPP bt;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageLock=(ImageView)findViewById(R.id.image_lock);
        ImageUnlock=(ImageView)findViewById(R.id.image_unlock);
        button=(Button)findViewById(R.id.button_lock);
        ImageUnlock.setVisibility(View.GONE);
        bt=new BluetoothSPP(this);

        //监听
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageLock.setVisibility(View.GONE);
                        ImageUnlock.setVisibility(View.VISIBLE);
                        bt.send("unlock",true);
                    }
                });
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext()
                                , "Connection lost", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext()
                                , "Unable to connect", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        //定位权限
        if(Build.VERSION.SDK_INT>=23){
            //判断是否有权限
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
//向用户解释，为什么要申请该权限
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(MainActivity.this,"The app requires a Bluetooth device to be turned on.", Toast.LENGTH_SHORT).show();
                }
            }
        }

//        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
//            @Override
//            public void onDataReceived(byte[] data, String message) {
//                if (message=="1"){
//                    Intent intent=new Intent(MainActivity.this,RockerUI.class);
//                    startActivity(intent);
//                }
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!bt.isBluetoothEnabled()) {
            // Do somthing if bluetooth is disable
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            // Do something if bluetooth is already enable
            if (!bt.isServiceAvailable())
                setup();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        bt.startService(BluetoothState.DEVICE_OTHER);
    }

    private void setup() {
        bt.setupService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//maybe it is a faulty.
        switch (requestCode) {
            case BluetoothState.REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setup();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    //Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "拒绝授权", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case BluetoothState.REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    bt.connect(data);
                }
                break;
        }
    }
}


