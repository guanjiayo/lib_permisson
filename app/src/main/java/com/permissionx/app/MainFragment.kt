package com.permissionx.app

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.permissionx.app.databinding.FragmentMainBinding
import com.permissionx.guolindev.PermissionX

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val context = context!!

        //一些基本使用
        binding.makeRequestBtn.setOnClickListener {
            PermissionX.init(this)
                .permissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,
//                    Manifest.permission.READ_CALENDAR,
//                    Manifest.permission.READ_CALL_LOG,
//                    Manifest.permission.READ_CONTACTS,
//                    Manifest.permission.READ_PHONE_STATE,
//                    Manifest.permission.BODY_SENSORS,
//                    Manifest.permission.ACTIVITY_RECOGNITION,
//                    Manifest.permission.SEND_SMS,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .explainReasonBeforeRequest()
                .setDialogTintColor(Color.parseColor("#1972e8"), Color.parseColor("#8ab6f5"))
                .onExplainRequestReason { scope, deniedList, beforeRequest ->
                    //比方说用户拒绝了多个权限,但我们认为比方电话权限适必须要有的情况,可以自己过滤一下
                    //再弹窗告诉用户
                    //deniedList.filter { it==Manifest.permission.READ_PHONE_STATE }

                    //权限第一次被拒绝回调,可以提示用户为什么需要这个权限
                    val message = "PermissionX needs following permissions to continue"
                    scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
//                    val message = "Please allow the following permissions in settings"
//                    val dialog = CustomDialogFragment(message, deniedList)
//                    scope.showRequestReasonDialog(dialog)
                }
                .onForwardToSettings { scope, deniedList ->
                    val message = "Please allow following permissions in settings"
                    val dialog = CustomDialogFragment(message, deniedList)
                    scope.showForwardToSettingsDialog(dialog)
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        Toast.makeText(activity, "All permissions are granted", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            activity,
                            "The following permissions are denied：$deniedList",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        //定位权限
        binding.backgroundLocation.setOnClickListener {
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
        }

        //虚浮窗权限
        binding.alertWindow.setOnClickListener {
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
        }

        //修改系统设置权限
        binding.writeSettings.setOnClickListener {
            PermissionX.init(activity)
                .permissions(Manifest.permission.WRITE_SETTINGS)
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

        //管理外部存储权限
        binding.manageExtStorage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 30) {
                PermissionX.init(activity)
                    .permissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                    .onExplainRequestReason { scope, deniedList ->
                        val message = "PermissionX需要您同意以下权限才能正常使用"
                        scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
                    }
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            Toast.makeText(activity, "所有申请的权限都已通过", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            } else {
                Toast.makeText(activity, "低版本权限不用管", Toast.LENGTH_SHORT).show()
            }
        }

        //安装未知来源应用权限
        binding.requestInstall.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
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
                            Toast.makeText(activity, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }else {
                Toast.makeText(activity, "低版本权限不用管", Toast.LENGTH_SHORT).show()
            }
        }

        //蓝牙权限
        binding.bluetooth.setOnClickListener {
            val requestList = ArrayList<String>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestList.add(Manifest.permission.BLUETOOTH_SCAN)
                requestList.add(Manifest.permission.BLUETOOTH_ADVERTISE)
                requestList.add(Manifest.permission.BLUETOOTH_CONNECT)
            }else{
                Toast.makeText(activity, "低版本权限按老方案", Toast.LENGTH_SHORT).show()
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
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}