[项目来源:  https://github.com/guolindev/PermissionX 郭神封装的框架](https://github.com/guolindev/PermissionX)

使用说明:

#### 基本使用

```kotlin

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
            .onExplainRequestReason { scope, deniedList ->
				val message = "拍照功能需要您同意相册和定位权限"
				val ok = "确定"
                scope.showRequestReasonDialog(deniedList, message, ok)
            }
            .onForwardToSettings { scope, deniedList ->
				val message = "您需要去设置当中同意相册和定位权限"
				val ok = "确定"
                scope.showForwardToSettingsDialog(deniedList, message, ok)
            }
            .request { _, _, _ ->
                takePicture()
            }
    }

    fun takePicture() {
        Toast.makeText(this, "开始拍照", Toast.LENGTH_SHORT).show()
    }

}

```

#### 情况一:

```kotlin
PermissionX.init(this)
    .permissions(Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE)
	.explainReasonBeforeRequest()
    .onExplainRequestReason { scope, deniedList, beforeRequest ->
        if (beforeRequest) {
            //首次申请权限,弹我们自己的Dialog提醒用户
            showRequestReasonDialog(deniedList, "为了保证程序正常工作，请您同意以下权限申请", "我已明白")
        } else {
            //再次申请权限,过滤其他权限,保留摄像机权限为必要权限
            //如果不需要过滤,按原来的即可    
            val filteredList = deniedList.filter {
                it == Manifest.permission.CAMERA
            }
            showRequestReasonDialog(filteredList, "摄像机权限是程序必须依赖的权限", "我已明白")
        }
    }
```

#### 特殊权限

- 后台定位权限申请,由于该权限时api29(Android10)才添加的,因此这样写,低版本的用另外的定位权限即可

```kotlin

val permissionList = ArrayList<String>()
permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
if (Build.VERSION.SDK_INT >= 29) {
    permissionList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
}
PermissionX.init(this)
    .permissions(permissionList)
    .request { allGranted, grantedList, deniedList ->
        if (allGranted) {
            Toast.makeText(activity, "所有申请的权限都已通过", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
        }
    }

```

- 悬浮窗权限

```

PermissionX.init(activity)
    .permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
    .onExplainRequestReason { scope, deniedList ->
        val message = "PermissionX需要您同意以下权限才能正常使用"
        scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
    }
    .request { allGranted, grantedList, deniedList ->
        if (allGranted) {
            Toast.makeText(activity, "所有申请的权限都已通过", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
        }
    }

//--清单文件--

<manifest xmlns:android="http://schemas.android.com/apk/res/android"
package="com.permissionx.app">

<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

</manifest>

```

- 修改设置权限

```

PermissionX.init(activity)
    .permissions(Manifest.permission.WRITE_SETTINGS)
    //...

//---清单文件---
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.permissionx.app">
    
    <uses-permission android:name="android.permission.WRITE_SETTINGS" />

</manifest>


```

- 管理外部存储权限

```

if (Build.VERSION.SDK_INT >= 30) {
    PermissionX.init(this)
        .permissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        //...
}

//---清单文件---
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.permissionx.app">
    
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />

</manifest>

```

- "允许安装未知来源的应用" 权限

```
PermissionX.init(activity)
    .permissions(Manifest.permission.REQUEST_INSTALL_PACKAGES)
    .onExplainRequestReason { scope, deniedList ->
        val message = "PermissionX需要您同意以下权限才能正常使用"
        scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
    }
    .request { allGranted, grantedList, deniedList ->
        if (allGranted) {
            Toast.makeText(activity, "所有申请的权限都已通过", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
        }
    }

//---清单文件----

<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.permissionx.app">
    
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />

</manifest>


```

- 蓝牙权限

```

val requestList = ArrayList<String>()
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    requestList.add(Manifest.permission.BLUETOOTH_SCAN)
    requestList.add(Manifest.permission.BLUETOOTH_ADVERTISE)
    requestList.add(Manifest.permission.BLUETOOTH_CONNECT)
}
if (requestList.isNotEmpty()) {
    PermissionX.init(activity)
        .permissions(requestList)
        .explainReasonBeforeRequest()
        .onExplainRequestReason { scope, deniedList ->
            val message = "PermissionX需要您同意以下权限才能正常使用"
            scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
        }
        .request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                Toast.makeText(activity, "所有申请的权限都已通过", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
            }
        }
}


//---清单文件----

<manifest>
    <!-- Request legacy Bluetooth permissions on older devices. -->
    <uses-permission android:name="android.permission.BLUETOOTH"
                     android:maxSdkVersion="30" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"
                     android:maxSdkVersion="30" />
                     
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    ...
</manifest>

```

#### 情况三:

设置Dialog的主题颜色

```kotlin
PermissionX.init(this)
    .permissions(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO)
    .setDialogTintColor(Color.parseColor("#008577"), Color.parseColor("#83e8dd"))
    //...
```