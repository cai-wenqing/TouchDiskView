package com.cwq.touchdiskview;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/2/6 0006.
 */

public class USBTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "USBTestActivity测试";

    TextView tv_info;
    EditText et_content;
    Button btn_refresh;

    StringBuilder sb;
    UsbDevice usbDevice;
    UsbManager usbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbtest);
        tv_info = findViewById(R.id.usbtest_tv_info);
        et_content = findViewById(R.id.usbtest_et_content);
        btn_refresh = findViewById(R.id.usbtest_btn_refresh);
        btn_refresh.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.usbtest_btn_refresh:
                String content = et_content.getText().toString().trim();
                if (TextUtils.isEmpty(content))
                    usbPrint("打印测试....");
                else
                    usbPrint(content);
                break;
        }
    }


    private void usbPrint(String content) {
        sb = new StringBuilder();
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> map = usbManager.getDeviceList();

        if (!map.isEmpty()) {
            for (UsbDevice device : map.values()) {
                int VendorID = device.getVendorId();
                int ProductID = device.getProductId();
                sb.append("VendorID:" + VendorID + ",ProductID:" + ProductID + "\n");
                if (VendorID == 10473 && ProductID == 649) {
                    usbDevice = device;

                    if (!usbManager.hasPermission(usbDevice)) {
                        Toast.makeText(this, "没有权限操作USB设备！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    sb.append("设备接口个数：" + usbDevice.getInterfaceCount() + "\n");
                    UsbInterface usbInterface = usbDevice.getInterface(0);

                    sb.append("分配端点个数：" + usbInterface.getEndpointCount() + "\n");
                    UsbEndpoint outEndpoint = usbInterface.getEndpoint(0);

                    UsbDeviceConnection connection = usbManager.openDevice(usbDevice);
                    connection.claimInterface(usbInterface, true);

                    // 打印数据
                    content = content + "\n\n\n\n\n\n";
                    byte[] printData = null;
                    try {
                        printData = content.getBytes("gbk");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    int out = connection.bulkTransfer(outEndpoint, printData, printData.length, 5000);
                    // 关闭连接
                    connection.close();
                }
            }
            tv_info.setText(sb.toString());
        } else {
            tv_info.setText("设备为空");
        }
    }
}
