package com.example.rendimientoplanta.base.utils.instances
import android.util.Log
import java.io.IOException
import java.net.*

object Network{
    private val TAG = "Conectividad"
    // Consulta la conexión con el servidor 110
    fun connectedTo(): Boolean {
        var network = false
        val thread = Thread {
            try {
                network = if (verificarConectividad("192.168.200.110", 80)) {
                    Log.d(TAG, "Conexión segura")
                    true
                } else {
                    Log.d(TAG, "Conexión insegura")
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
        thread.join()
        return network
    }

    // Consulta la conexión con el servidor de google (para verificar la conexión estable a internet)
    fun connectedToInternet(): Boolean {
        var network = false
        val thread = Thread {
            try {
                network = if (verificarConectividad("www.google.com", 80)) {
                    Log.d(TAG, "Conexión segura")
                    true
                } else {
                    Log.d(TAG, "Conexión insegura")
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
        thread.join()
        return network
    }

    private fun verificarConectividad(ip: String, port: Int): Boolean {
        return try {
            val sock = Socket()
            sock.connect(InetSocketAddress(ip, port), 500)
            sock.close()
            true
        } catch (e: IOException) {
            false
        }
    }

}