01bit Vending SDK
===================

零壹售卖SDK主要用来开发智能售卖机控制盒的相关Android应用，所有API都被封装在**StrawDevice.class**，加入gradle依赖并运行sample就可以得到大部分API的使用方法.

----------

##加入依赖
从 sample/libs 路径中获取aar库文件，并在构建脚本中加入如下依赖代码:

```gradle
repositories {
    flatDir {
        dirs 'libs'
    }
}
dependencies {
    compile(name: 'vendingsdk-SDK-1.2.0', ext: 'aar')
    compile 'io.reactivex:rxandroid:1.0.1'
}
```

----------

##开始使用

####StrawDevice.getInstance
使用getInstance()方法来获取设备对象

####StrawDevice.initialize
使用initialize()方法来初始化设备，回调函数返回初始化结果，callback可以为null

```java

	//获取设备对象
  strawDevice = StrawDevice.getInstance();
  
	//初始化设备
        strawDevice.initialize("/dev/ttyS3", new StrawDevice.OnDeviceInitializeListener() {
            @Override
            public void onDeviceInitialized(boolean success) {
                }
            }
        });

```

----------

##主控 API

####OnDeliveryListener
出货API用来控制电机转动，使用**StrawDevice.OnDeliveryListener**收取电机转动状态
```java
turnMotor(int address, int timeout, int position, boolean isOpenIR, int height)
```
> **Tip:**

>- **address**  出货控制板地址，默认

>- **timeout**  出货超时时间设定(单位:秒)

>- **position** 指定转动的电机编号

>- **isOpenIR** 红外检测是否开启，当置为true时，请确保红外检测模块工作正常

>- **height** 升降机设备上升高度(单位:cm，近似)，无升降机设备传0

使用 **setOnDeliveryListener(...)** 就像其他Android Listener一样，用来监听回调信息. 
```java
public interface OnDeliveryListener {

	void onDeliveryFinished(int statusCode);
}
```


| Status Code       | Message           |
| ------------- |:-------------|
| 800      | 出货成功 |
| 110      | 红外未检测到出货 |
| 111      | 红外设备故障 |
| 120      | 过流/堵转/过压 |
| 121      | 低电流/欠压/断线 |
| 122      | 出货超时 |


####OnKeyEventListener
使用 **setOnKeyEventListener(...)** 来监听按键信息, 这样就可以获取到按键被按下的事件
```java
public interface OnKeyEventListener {
	enum KeyboardType {BUTTON_KEYBOARD, NUMBER_KEYBOARD}

	void onKeyEvent(KeyboardType keyboardType, int keyCode);
}
```
> **Tip:** 目前仅支持数字按键，即：  **KeyboardType.NUMBER_KEYBOARD**

####OnDeviceStatusChangeListener
使用 **setOnDeviceStatusChangeListener** 来获取设备状态, 目前支持温度和门锁状态的实时反馈，温度单位为摄氏度
```java
public interface OnDeviceStatusChangeListener {

	void onTemperatureChangeListener(float celcius);

	void onDoorStatusChangeListener(boolean isOpen);
}
```

-------------
##Debug

####Debug模式
debug(boolean debug) 打印更多指令用来捕捉异常

-------------
##Update info

####1.0.3
1. 增加调试模式
2. 调整通讯速率从120ms至60ms

####1.0.4
1. 捕获动态库加载异常

####1.1.0
1. 新增出货函数, 增加地址位输入

####1.2.0
1. 新增升降机参数

License
-------
    Copyright 2016 Xi’an 01bit Intelligence Technology Co., Ltd.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

