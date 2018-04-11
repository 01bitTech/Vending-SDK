package com.o1bit.vending.sdk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.straw.vending.lib.device.StrawDevice;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static final int MAX_MOTOR_NUMBER = 100;

    private Subscription checkAllSubscription;

    private int checkAllCount;

    private StrawDevice strawDevice;

    @Bind(R.id.motor_address)
    TextView motorAddress;

    @Bind(R.id.controller_version)
    TextView controllerVersionText;

    @Bind(R.id.temperature)
    TextView temperatureText;

    @Bind(R.id.door_status)
    TextView doorStatusText;

    @Bind(R.id.key_event)
    TextView keyEventText;

    @Bind(R.id.motor_number)
    EditText motorNumber;

    @Bind(R.id.ir_switcher)
    SwitchCompat irSwitch;

    @Bind(R.id.all_motor_test_result)
    TextView allMotorTestResultText;

    @Bind(R.id.updown_hight)
    EditText updownHightView;

    private int checkAllErrorCode = 0;

    private boolean isTesting;

    @OnClick(R.id.all_motor_button)
    void onTestAllMotor(View view) {
        checkAll();
    }

    @OnClick(R.id.motor_button)
    void onMotorButtonClick(Button button) {
        String text = motorNumber.getText().toString();
        if (!text.isEmpty()) {
            int position = Integer.parseInt(text);

            int address = 0;
            String addressStr = motorAddress.getText().toString();
            if (!addressStr.isEmpty()) {
                address = Integer.parseInt(addressStr);
            }

            int height = 0;
            String heightStr = updownHightView.getText().toString();
            if (!heightStr.isEmpty()) {
                height = Integer.parseInt(heightStr);
            }

            boolean isIrOpened = irSwitch.isChecked();

            if (position > 0) {
                strawDevice.turnMotor(address, 20, (byte) position, (byte) (isIrOpened ? 1 : 0), (byte) height);
            } else {
                printToastLog("货道序号有误");
            }
        } else {
            printToastLog("Please input the motor number");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        strawDevice = StrawDevice.getInstance();
        strawDevice.debug(true);

        strawDevice.setOnKeyEventListener(new StrawDevice.OnKeyEventListener() {
            @Override
            public void onKeyEvent(KeyboardType keyboardType, int keycode) {

                keyEventText.setText("按键: " + (char) keycode);
            }
        });

        strawDevice.setOnDeviceStatusChangeListener(new StrawDevice.OnDeviceStatusChangeListener() {
            @Override
            public void onTemperatureChangeListener(float temperature) {
                if (temperature > 100) {
                    temperatureText.setText("温度: --");
                } else {
                    temperatureText.setText("温度: " + temperature);
                }
            }

            @Override
            public void onDoorStatusChangeListener(boolean isOpened) {
                doorStatusText.setText("Door status: " + (isOpened ? "Opened" : "Closed"));
            }

            @Override
            public void onConnected() {
                printToastLog("通讯正常!");
            }

            @Override
            public void onDisConnected() {
                printToastLog("通讯异常!");
            }
        });

        strawDevice.setOnDeliveryListener(new StrawDevice.OnDeliveryListener() {
            @Override
            public void onDeliveryFinished(int errorCode) {
                if (errorCode != 800) {
                    printToastLog("错误代码: " + errorCode);
                } else {
                    printToastLog("货道正常");
                }
                checkAllErrorCode = errorCode;
                isTesting = false;
            }
        });

        strawDevice.initialize("/dev/ttyS3", new StrawDevice.OnDeviceInitializeListener() {
            @Override
            public void onDeviceInitialized(boolean success) {
                if (success) {
                    controllerVersionText.setText("驱动版本: " + strawDevice.getMainVersion());
                } else {
                    printToastLog("Device Initialized Failed");
                }
            }
        });
    }

    public void printToastLog(String message) {
        Observable.just(message)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String message) {
                        return message != null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String message) {
                        Log.d(TAG, message);
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAll() {
        allMotorTestResultText.setText("开始测试....");

        checkAllErrorCode = 800;
        checkAllCount = 0;

        if (checkAllSubscription != null && !checkAllSubscription.isUnsubscribed()) {
            checkAllSubscription.unsubscribe();
        }

        checkAllSubscription = Observable.interval(300, TimeUnit.MILLISECONDS)
                .filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return !isTesting() && checkAllCount < MAX_MOTOR_NUMBER;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (isCatchError()) {
                            allMotorTestResultText.setText("测试成功货道: " + (checkAllCount - 1) + "个");
                            checkAllSubscription.unsubscribe();
                            isTesting = false;
                        } else {
                            allMotorTestResultText.setText("正在测试第: " + (checkAllCount + 1) + "个货道");
                            checkSingle(checkAllCount++);
                        }
                    }
                });
    }

    private void checkSingle(int checkCount) {
        isTesting = true;
        int address = 0;
        String addressStr = motorAddress.getText().toString();
        if (!addressStr.isEmpty()) {
            address = Integer.parseInt(addressStr);
        }
        strawDevice.turnMotor(address, checkCount, false);
    }

    public boolean isTesting() {
        return isTesting;
    }

    public boolean isCatchError() {
        return checkAllErrorCode != 800;
    }

}
