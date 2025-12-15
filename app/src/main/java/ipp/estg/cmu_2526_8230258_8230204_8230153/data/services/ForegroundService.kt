package ipp.estg.cmu_2526_8230258_8230204_8230153.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import ipp.estg.cmu_2526_8230258_8230204_8230153.MainActivity
import ipp.estg.cmu_2526_8230258_8230204_8230153.R
import kotlinx.coroutines.*
import kotlin.random.Random

class ForegroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundAnchor()

        startSendingSmartNotifications()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun startForegroundAnchor() {
        val channelId = "SERVICE_ANCHOR_CHANNEL"
        createChannel(channelId, "Sistema de Monitorização", NotificationManager.IMPORTANCE_LOW)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Monitorização Ativa")
            .setContentText("A app está pronta para enviar alertas.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    private fun startSendingSmartNotifications() {
        serviceScope.launch {
            val tips = listOf(
                "💧 Não te esqueças de beber água!",
                "🍎 Já registaste o teu almoço hoje?",
                "⚖️ Hora de verificar o peso!",
                "🔥 Sabias que caminhar 30min queima ~150kcal?",
                "😴 Dormir bem ajuda a perder peso."
            )

            while (isActive) {
                delay(10000 * 60 * 60 * 4)

                val randomTip = tips.random()
                sendAlert(randomTip)
            }
        }
    }

    private fun sendAlert(message: String) {
        val channelId = "ALERTS_CHANNEL"
        createChannel(channelId, "Alertas de Saúde", NotificationManager.IMPORTANCE_HIGH)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Dica de Saúde 💡")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random.nextInt(), notification)
    }

    private fun createChannel(id: String, name: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, importance)
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}