package top.greendami.amigo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import me.ele.amigo.Amigo
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.File

/**
 * Android O中ActivityManagerNative被废弃，ActivityManagerProxy被移除了，AMS采用了AIDL的方式实现。需要hook ActivityManager中的IActivityManagerSingleton。
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("Amigo", Environment.getExternalStorageDirectory().path + File.separator + "test.apk")

        var permission = ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {   // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
        permission = ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {   // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        }

        button.setOnClickListener {
            var file = File(Environment.getExternalStorageDirectory().path + File.separator + "test.apk")
            if (file.exists()) {
                Amigo.workLater(this, file) {
                    if (it) {
                        toast("更新成功！")
                        val intent = packageManager.getLaunchIntentForPackage(packageName)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        android.os.Process.killProcess(android.os.Process.myPid())
                    }
                }
            }
        }

    }
}
