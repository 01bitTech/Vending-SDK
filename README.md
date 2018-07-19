01bit Vending SDK
===================

01bit vending SDK is using for developing vending controller box like B600 e.g.
All API is packaged in **StrawDevice.class**, add dependency on gradle and play the sample to get more information about our products.

[中文README](README_CN.md)

----------

##Importing to your project
Get aar file from sample/libs and add this dependency to your build.gradle file:

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

##Start

####StrawDevice.getInstance
Using **getInstance()** to get the instance

####StrawDevice.initialize
initialize() to init devices and get the init status in callback

```java

    strawDevice = StrawDevice.getInstance();
    strawDevice.initialize("/dev/ttyS3", new StrawDevice.OnDeviceInitializeListener() {
            @Override
            public void onDeviceInitialized(boolean success) {
                }
            }
        });

```

----------

##Controller API

####OnDeliveryListener
Delivery API is using for turn the motor and receive the delivery status
```java
turnMotor(int address, int timeout, int position, boolean isOpenIR, int height)
```
> **Tip:**

>- **address**  board address

>- **timeout**  delivery timeout (second)

>- **position** is the motor postion

>- **isOpenIR** open IR check or not

>- **height**  (not for use)

Using **setOnDeliveryListener(...)** as Android often does,  listening the motor callback. 
```java
public interface OnDeliveryListener {

    void onDeliveryFinished(int statusCode);
}
```


| Status Code       | Message           |
| ------------- |:-------------|
| 800      | Delivery success |
| 110      | IR not caching stuff |
| 111      | IR error |
| 120      | Motor overload |
| 121      | Motor undervoltage |
| 122      | Motor number is not exist |

####OnKeyEventListener
Using **setOnKeyEventListener(...)** the keyboard and set this listener, then you can receive the key click event
```java
public interface OnKeyEventListener {
    enum KeyboardType {BUTTON_KEYBOARD, NUMBER_KEYBOARD}

    void onKeyEvent(KeyboardType keyboardType, int keyCode);
}
```
> **Tip:** Currently only support  **KeyboardType.NUMBER_KEYBOARD**

####OnDeviceStatusChangeListener
Using **setOnDeviceStatusChangeListener** to get device status, for now, we support the temperature sensor and door sensor
```java
public interface OnDeviceStatusChangeListener {
    
    void onTemperatureChangeListener(float celcius);

    void onDoorStatusChangeListener(boolean isOpen);
}
```

-------------
##Debug

####Debug the command line
debug(boolean debug) for print more information to debug


-------------
##Update info

####1.0.3
1. add debug mode
2. change communication delay from 120ms to 60ms

####1.0.4
1. catch the load library exception

####1.1.0
1. public new method for motor

License
-------
    Copyright 2018 Xi’an 01bit Intelligence Technology Co., Ltd.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
