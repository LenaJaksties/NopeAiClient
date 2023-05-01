package socketinterface

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URI
import java.net.URISyntaxException
import io.socket.emitter.Emitter
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.util.Collections.singletonMap
import org.json.JSONObject;
import io.socket.client.Ack





/*
 * Socket code
 */


var mSocket : Socket? = null

val gson = Gson()

fun socketinit(serverURL:String, access_token: Accesstoken): Socket?{


    var token = access_token
    val gson = Gson()
    val tokenjwt = gson.toJson(token)


    try {
        val opts = IO.Options.builder()
            .setTransports(arrayOf(io.socket.engineio.client.transports.WebSocket.NAME))
            //.setQuery("token=$tokenjwt")
            .setAuth(singletonMap("token", token.accessToken))
            .build()
        mSocket = IO.socket(URI.create("https://nope-server.azurewebsites.net/"), opts)
    } catch (e: URISyntaxException) {
        e.printStackTrace()
    }

    return mSocket

}

fun connect() {
    val socket = mSocket!!.connect()
    socket.on(Socket.EVENT_CONNECT,{println("Connected")}).on(Socket.EVENT_CONNECT_ERROR, {println(it.joinToString())})
}
fun disconnect() {
    mSocket!!.disconnect()
}

// TODO braucht der Emitter einen Acknowledge? : Ack acknowl
fun emit(event: String, data: JsonObject): Emitter?{
    return mSocket ?.emit(event, data)
}
fun emit(event: String, data: JsonArray): Emitter?{
    return mSocket ?.emit(event, data)
}
fun emit(event: String, data: String): Emitter?{
    return mSocket ?.emit(event, data)
}

fun createTournament(number: Int){
    mSocket?.emit("tournament:create", number, Ack { ackData ->
        // Handle acknowledgement data or failure
        if (ackData != null) {
            val result = gson.toJson(ackData)
            val lol = JSONObject.stringToValue(ackData.toString())
            println(lol)
            //val jsonMap = Gson().fromJson(result, Map::class.java)
            println("Acknowledgement received - Success: ${result.toString()}")
            //println(jsonMap["tournamentId"])

        } else {
            // Handle acknowledgement failure
            println("Acknowledgement not received")
        }
    })
}

class Socket_events(){
    // TODO events:
    // Events return a String like this: public static final String EVENT_MESSAGE
    // object Connect : SocketEvent<Unit>
    // object Connecting : SocketEvent<Unit>
    // object Disconnect : SocketEvent<Unit>
    // object Error : SocketEvent<Throwable>
    // object Message : SocketEvent<Any>
    // object Reconnect : SocketEvent<Unit>
    // object ReconnectAttempt : SocketEvent<Int>
    // object Ping : SocketEvent<Unit>
    // object Pong : SocketEvent<Unit>
}