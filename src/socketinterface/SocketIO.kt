package socketinterface

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections.singletonMap


/*
 *
 *  game relevant data
 */

// Create an empty ArrayList
val tournamentList = ArrayList<Tournament>()






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
    getCurrentTournaments()
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


fun createTournament(number: Int): TournamentCreateInfo{

    val tournamentInfo = TournamentCreateInfo(null,null,null,false,null)

    mSocket?.emit("tournament:create", number, Ack { ackData ->
        // Handle acknowledgement data or failure

        if (ackData != null) {
            val result = gson.toJson(ackData)
            val jsonArray = JSONArray(result)

            // get data about tournament creation
            val getTournamentInfo = jsonArray.getJSONObject(0).getJSONObject("map")
            val success = getTournamentInfo.getBoolean("success")
            val error = getTournamentInfo.get("error")
            tournamentInfo.error = error
            tournamentInfo.success = success
            if(success){
                // get tournament data
                val getTournamentData = jsonArray.getJSONObject(0).getJSONObject("map").getJSONObject("data").getJSONObject("map")
                val tournamentId = getTournamentData.getString("tournamentId")
                val currentSize = getTournamentData.getInt("currentSize")
                val bestOf = getTournamentData.getInt("bestOf")
                tournamentInfo.tournamentId = tournamentId
                tournamentInfo.bestOf = bestOf
                tournamentInfo.currentSize = currentSize

                if (tournamentInfo.tournamentId != null) {
                    println("Tournament created with ID: ${tournamentInfo.tournamentId}, current size: ${tournamentInfo.currentSize}, best of: ${tournamentInfo.bestOf}")
                }else {
                        println("Error: tournament data is missing or incomplete")
                }
            }else{
                val errorMessage = getTournamentInfo.getJSONObject("error").getJSONObject("map").getString("message")
                tournamentInfo.error =errorMessage
                println("Error: tournament creation failed : $errorMessage")
            }
            println("Acknowledgement received - Success: ${result.toString()}")

        } else {
            // Handle acknowledgement failure
            println("Acknowledgement not received")
        }
    })
    Thread.sleep(1000)
    return tournamentInfo
}
data class TournamentCreateInfo(
    var tournamentId: String?,
    var bestOf: Int?,
    var currentSize: Int?,
    var success: Boolean,
    var error: Any?
)


fun getCurrentTournaments() {


    mSocket?.on("list:tournaments", Emitter.Listener { args ->

        if (args[0] != null) {
            tournamentList.clear()

            val result = gson.toJson(args)
            val jsonTemp = JSONArray(result)
            val tourData = jsonTemp.getJSONObject(0)
            val myArrayList = tourData.getJSONArray("myArrayList")
            for (i in 0 until myArrayList.length()) {
                val tournamentData = myArrayList.getJSONObject(i).getJSONObject("map")
                val id = tournamentData.getString("id")
                val createdAt = tournamentData.getString("createdAt")
                val currentSize = tournamentData.getInt("currentSize")
                val status = tournamentData.getString("status")
                val players = ArrayList<Player>()
                val getPlayerData = tournamentData.getJSONObject("players").getJSONArray("myArrayList")
                for (j in 0 until getPlayerData.length()) {
                    val newP = Player(getPlayerData.getJSONObject(j).getJSONObject("map").getString("username"))
                    players.add(newP)
                }
                val newTournament = Tournament(id, createdAt, currentSize, players, status)
                if (newTournament.id != null) {
                    println("New Entry found: ${tournamentList.size} TournamentID: ${newTournament.id}, current size: ${newTournament.currentSize}, createdAt:  ${newTournament.createdAt}, status:  ${newTournament.status}, players:  ${newTournament.players}")
                } else {
                    println("Error: tournament data is missing or incomplete")
                }

                tournamentList.add(newTournament)
            }
            println("Received tournaments: ${result.toString()}")

        } else {
            println("No tournaments received")
        }

    })

}

fun joinTournament(id:String){
    mSocket?.emit("tournament:join", id, Ack { ackData ->
        if (ackData != null) {
            val result = gson.toJson(ackData)
            val jsonArray = JSONArray(result)

            // get data about tournament creation
//            val getTournamentInfo = jsonArray.getJSONObject(0).getJSONObject("map")
//            val success = getTournamentInfo.getBoolean("success")
//            val error = getTournamentInfo.get("error")

//            if(success){
                // get tournament data
//                val getTournamentData = jsonArray.getJSONObject(0).getJSONObject("map").getJSONObject("data").getJSONObject("map")
//                val tournamentId = getTournamentData.getString("tournamentId")
//                val currentSize = getTournamentData.getInt("currentSize")
//                val bestOf = getTournamentData.getInt("bestOf")


//                //if (tournamentInfo.tournamentId != null) {
//                    //println("Tournament created with ID: ${tournamentInfo.tournamentId}, current size: ${tournamentInfo.currentSize}, best of: ${tournamentInfo.bestOf}")
//                }else {
//                println("Error: tournament data is missing or incomplete")
//                }
//            }else{
//                val errorMessage = getTournamentInfo.getJSONObject("error").getJSONObject("map").getString("message")
//                tournamentInfo.error =errorMessage
//                println("Error: tournament creation failed : $errorMessage")
//            }
            println("Acknowledgement received - Success: ${result.toString()}")
            getCurrentTournaments()
        } else {
            // Handle acknowledgement failure
            println("Acknowledgement not received")
        }
    })

}

fun leaveTournament(){
    mSocket?.emit("tournament:leave",  Ack { ackData ->
        if (ackData != null) {
            val result = gson.toJson(ackData)
            val jsonArray = JSONArray(result)

            // get data about tournament creation
//            val getTournamentInfo = jsonArray.getJSONObject(0).getJSONObject("map")
//            val success = getTournamentInfo.getBoolean("success")
//            val error = getTournamentInfo.get("error")

//            if(success){
            // get tournament data
//                val getTournamentData = jsonArray.getJSONObject(0).getJSONObject("map").getJSONObject("data").getJSONObject("map")
//                val tournamentId = getTournamentData.getString("tournamentId")
//                val currentSize = getTournamentData.getInt("currentSize")
//                val bestOf = getTournamentData.getInt("bestOf")


//                //if (tournamentInfo.tournamentId != null) {
//                    //println("Tournament created with ID: ${tournamentInfo.tournamentId}, current size: ${tournamentInfo.currentSize}, best of: ${tournamentInfo.bestOf}")
//                }else {
//                println("Error: tournament data is missing or incomplete")
//                }
//            }else{
//                val errorMessage = getTournamentInfo.getJSONObject("error").getJSONObject("map").getString("message")
//                tournamentInfo.error =errorMessage
//                println("Error: tournament creation failed : $errorMessage")
//            }
            println("Acknowledgement received - Success: ${result.toString()}")
            getCurrentTournaments()
        } else {
            // Handle acknowledgement failure
            println("Acknowledgement not received")
        }
    })
}

//    msocket?.on("list:tournaments", Emitter.Listener { args ->
//            //System.out.println(args[0]); // info String printen
//            var tournaments_info_full = JSONArray()
//            tournaments_info_full = args[0] as JSONArray
//            val collectionType: Type = object : TypeToken<Collection<Tournament_info?>?>() {}.getType()
//            val tournaments: ArrayList<Tournament_info> =
//                gson.fromJson<ArrayList<Tournament_info>>(tournaments_info_full.toString(), collectionType)
//            println()
//            println("test obj <tournaments>:")
//            println("tournaments.size()  =  " + tournaments.size)
//            for (i in tournaments.indices) {
//                println()
//                System.out.println("id: " + tournaments[i].getId())
//                System.out.println("createdAt: " + tournaments[i].getCreatedAt())
//                System.out.println("status: " + tournaments[i].getStatus())
//                println("currentSize: " + Integer.toString(tournaments[i].getCurrentSize()))
//                println("players")
//                for (j in 0 until tournaments[i].getPlayers().size()) {
//                    System.out.println("username: " + tournaments[i].getPlayers().get(j).getUsername())
//                }
//            }
//        })



data class Tournament(
    var id: String?,
    var createdAt: String?,
    var currentSize: Int?,
    var players: ArrayList<Player>?,
    var status: String?
)
data class Player(
    var username: String?

)



//fun getCurrentTournaments(textArea: JTextArea): JTextArea{
//    mSocket?.on("list:tournaments"){  ackData ->
//
//        if(ackData != null){
//            val result = gson.toJson(ackData)
//
//            println("Acknowledgement received - Success: ${result.toString()}")
//
//            textArea.text = result
//
//        }else{
//            println("Nothing not received")
//
//
//        }}
//    Thread.sleep(1000)
//    return textArea
//}





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