# flowbus

[![](https://jitpack.io/v/wangchenyan/flowbus.svg)](https://jitpack.io/#wangchenyan/flowbus)

使用 Flow 实现的 EventBus，支持粘性订阅，无需手动取消订阅，订阅者为空时自动销毁事件。

相比 LiveData，支持指定事件发送线程和接收线程。

```groovy
allprojects {
    repositories {
        //...
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
dependencies {
    implementation 'com.github.wangchenyan:flowbus:${VERSION}'
}
```