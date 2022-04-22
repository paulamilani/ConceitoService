package com.ifsp.conceitoservice

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LifetimeStartedService : Service() {

    //contador de segundos
    private var lifetime: Int = 0

    companion object {
        //para passar o lifetime entre Activity e Service
        val EXTRA_LIFETIME = "EXTRA_LIFETIME"
    }

    //thread de trabalho conta segundos no back
    private inner class WorkerThread: Thread() {
        var running = false
        override fun run() {
            running = true
            while(running) {
                //dorme 1 segundo
                sleep(1000)
                //envia o lifetime para a Activity
                sendBroadcast(Intent("ACTION_RECEIVE_LIFETIME").also {
                    it.putExtra(EXTRA_LIFETIME, ++lifetime)
                })
            }

        }
    }

    private lateinit var workerThread: WorkerThread

    override fun onCreate() {
        super.onCreate()
        workerThread = WorkerThread()
    }

    //só faz sentido se for serviço vinculado, senão retorna null
    override fun onBind(intent: Intent): IBinder? = null

    //chamado quando a Activity executa startService.
    //executa indefinidamente até que seja chamado o método stopSelf (serviço)
    //ou stopService (activity)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(!workerThread.running) {
            workerThread.start()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        workerThread.running = false
    }
}