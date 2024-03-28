package com.cicerodev.workmanagerappexample

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.cicerodev.workmanagerappexample.MyWorker.Companion.CHANNEL_ID
import com.cicerodev.workmanagerappexample.databinding.ActivityMainBinding
import java.security.Permission
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val NOTIFICATION_PERMISSION = android.Manifest.permission.POST_NOTIFICATIONS
    private val NOTIFICATION_PERMISSION_REQUEST_CODE  = 100
    private val permissoesNecessarias = arrayOf(
        android.Manifest.permission.POST_NOTIFICATIONS
    )
    private val CHANNEL_ID = "my_channel_id"

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permissão concedida, agora você pode lidar com as notificações
                // por exemplo, exibir uma notificação
//                showNotification()
            } else {
                // Permissão negada, você pode lidar com isso aqui
                // por exemplo, exibir uma mensagem para o usuário informando que as notificações não serão mostradas
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPermissions()
        binding.apply {
            btnUmaVez.setOnClickListener {
                myOneTimeWork()

            }
            btnPeriodicamente.setOnClickListener {
                myPeriodicWork()

            }
        }
    }







    private fun requestPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val notGrantedPermissions=permissoesNecessarias.filterNot { permission->
                ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED
            }
            if (notGrantedPermissions.isNotEmpty()){
                val showRationale=notGrantedPermissions.any { permission->
                    shouldShowRequestPermissionRationale(permission)
                }
                if (showRationale){
                    AlertDialog.Builder(this)
                        .setTitle("Storage Permission")
                        .setMessage("Storage permission is needed in order to show images and videos")
                        .setNegativeButton("Cancel"){dialog,_->
                            Toast.makeText(this, "Read media storage permission denied!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        .setPositiveButton("OK"){_,_->
                            notificationPermissionLauncher.launch(notGrantedPermissions[0])
                        }
                        .show()
                }else{
                    notificationPermissionLauncher.launch(notGrantedPermissions[0])
                }
            }
        }
    }

    private fun myPeriodicWork() {
        val nextWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(nextWorkRequest)
    }

    private fun myOneTimeWork() {

        val workRequest = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)

    }
}