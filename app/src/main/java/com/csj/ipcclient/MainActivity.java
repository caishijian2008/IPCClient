package com.csj.ipcclient;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etNum;
    private Button btnQuery;
    private TextView tvShow;
    private IBinder mIBinder;
    private ServiceConnection personConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIBinder = service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //绑定远程Service
        Intent service = new Intent("android.intent.action.IPCService");
        service.setPackage("com.csj.ipcserver");
        bindService(service, personConn, Service.BIND_AUTO_CREATE);
        btnQuery.setOnClickListener(this);
    }

    private void initView() {
        etNum = (EditText) findViewById(R.id.etNum);
        btnQuery = (Button) findViewById(R.id.btnQuery);
        tvShow = (TextView) findViewById(R.id.tvShow);
    }

    @Override
    public void onClick(View v) {
        int num = Integer.parseInt(etNum.getText().toString());
        if (mIBinder == null) {
            Toast.makeText(MainActivity.this, "未连接服务端或服务端被异常杀死", Toast.LENGTH_SHORT).show();
        } else {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            String _result = null;
            try {
                _data.writeInterfaceToken("IPCService");
                _data.writeInt(num);
                mIBinder.transact(0x001, _data, _reply, 0);
                _reply.readException();
                tvShow.setText(_result);
                etNum.setText("");
            } catch (RemoteException e) {
                e.printStackTrace();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }
    }
}
