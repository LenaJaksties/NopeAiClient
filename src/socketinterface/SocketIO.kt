package socketinterface

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import guiview.GUIMain
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import nopegamelogic.Player
import nopegamelogic.Tournament
import nopegamelogic.TournamentInfo
import org.json.JSONArray
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections.singletonMap
import javax.swing.SwingUtilities


/*
 *
 *  game relevant data
 */

// Create an empty ArrayList
var tournamentList = ArrayList<Tournament>()

var currentTournament = Tournament(null,null,null,null,null,null, null, null,null)


/*
 * Socket code
 */


var mSocket : Socket? = null
var guiObject: GUIMain? = null
var emitter = Emitter.Listener {}
val gson = Gson()

fun socketinit(serverURL:String, access_token: Accesstoken, gui:GUIMain): Socket?{


    var token = access_token
    val gson = Gson()
    val tokenjwt = gson.toJson(token)
    guiObject = gui


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
    // put Socket.On functions here
    getCurrentTournaments()
    getPlayerInfo()
    getTournamentInfo()

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


fun createTournament(number: Int): TournamentInfo {

    val tournamentInfo = TournamentInfo(null,null,null,false,null)

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

                    currentTournament.id = tournamentInfo.tournamentId
                    currentTournament.bestOf = tournamentInfo.bestOf
                    currentTournament.currentSize = tournamentInfo.currentSize

                }else {
                        println("Error: tournament data is missing or incomplete")
                }
            }else{
                val errorMessage = getTournamentInfo.getJSONObject("error").getJSONObject("map").getString("message")
                tournamentInfo.error =errorMessage
                println("Error: tournament creation failed : $errorMessage")
            }
            println("Acknowledgement received - Success: CREATE Tournament ${result.toString()}")

        } else {
            // Handle acknowledgement failure
            println("Acknowledgement not received")
        }
    })
    Thread.sleep(1000)
    return tournamentInfo
}


fun getCurrentTournaments() {

    mSocket?.off("list:tournaments")
    mSocket?.on("list:tournaments", Emitter.Listener { args ->

        if (args[0] != null) {
            tournamentList.clear()

            val result = gson.toJson(args)
            val jsonTemp = JSONArray(result)
            val tourData = jsonTemp.getJSONObject(0)
            val myArrayList = tourData.getJSONArray("myArrayList")
            println(myArrayList.toString())
            val length = myArrayList.length()
            for (i in 0 until length) {
                val tournamentData = myArrayList.getJSONObject(i).getJSONObject("map")
                val id = tournamentData.getString("id")
                val createdAt = tournamentData.getString("createdAt")
                val currentSize = tournamentData.getInt("currentSize")
                val status = tournamentData.getString("status")
                val players = ArrayList<Player>()
                val getPlayerData = tournamentData.getJSONObject("players").getJSONArray("myArrayList")
                for (j in 0 until getPlayerData.length()) {
                    val newP = Player(null, getPlayerData.getJSONObject(j).getJSONObject("map").getString("username"),null)
                    players.add(newP)
                }
                val newTournament = Tournament(id, createdAt, currentSize, players, status,null, null,null,null)
                if (newTournament.id != null) {
                    println("New Entry found: ${tournamentList.size} TournamentID: ${newTournament.id}, current size: ${newTournament.currentSize}, createdAt:  ${newTournament.createdAt}, status:  ${newTournament.status}, players:  ${newTournament.players}")
                } else {
                    println("Error: tournament data is missing or incomplete")
                }

                tournamentList.add(newTournament)
            }
            //println("Received tournaments: ${result.toString()} \n")
            SwingUtilities.invokeLater {
                // update GUI components here
                guiObject?.updateTournamentList()
            }

        } else {
            println("No tournaments received")
        }
    })

}


fun getPlayerInfo() {
    mSocket?.off("tournament:playerInfo")
    mSocket?.on("tournament:playerInfo", Emitter.Listener { args ->
        if (args[0] != null) {
            val result = gson.toJson(args)
            val tournamentData = JSONArray(result).getJSONObject(0).getJSONObject("map")
            val tournamentId = tournamentData.getString("tournamentId")
            val currentSize = tournamentData.getInt("currentSize")
            val bestOf = tournamentData.getInt("bestOf")
            val message = tournamentData.getString("message")
            val playersArray = tournamentData.getJSONObject("players").getJSONArray("myArrayList")
            val players = ArrayList<Player>()
            for (i in 0 until playersArray.length()) {
                val playerData = playersArray.getJSONObject(i).getJSONObject("map")
                val newPlayer = Player(playerData.getString("id"), playerData.getString("username"),null)
                players.add(newPlayer)
            }
            currentTournament.id = tournamentId
            currentTournament.bestOf = bestOf
            currentTournament.currentSize = currentSize
            currentTournament.message = message
            currentTournament.players = players

            println("Received Tournament Data: ID: ${currentTournament.id} Message: ${currentTournament.message} Players: ${currentTournament.players} Best of: ${currentTournament.bestOf} Current Size: ${currentTournament.currentSize}")
            SwingUtilities.invokeLater {
                // update GUI components here
                guiObject?.updateCurrentTournamentList()
            }
        } else {
            println("No Player Data received")
        }
    })
}

fun getTournamentInfo() {
    mSocket?.off("tournament:info")
    mSocket?.on("tournament:info", Emitter.Listener { args ->
        if (args[0] != null) {
            val result = gson.toJson(args)
            val jsonArray = JSONArray(result)
            val jsonObject = jsonArray.getJSONObject(0).getJSONObject("map")
            val tournamentId = jsonObject.getString("tournamentId")
            val currentSize = jsonObject.getInt("currentSize")
            val message = jsonObject.getString("message")
            val status = jsonObject.getString("status")
//            val winnerObj = jsonObject.getJSONObject("winner")
//            val winnerId = winnerObj.getString("id")
//            val winnerUsername = winnerObj.getString("username")
            val hostObj = jsonObject.getJSONObject("host").getJSONObject("map")
            val hostId = hostObj.getString("id")
            val hostUsername = hostObj.getString("username")
            val playersArray = jsonObject.getJSONObject("players").getJSONArray("myArrayList")
            val players = ArrayList<Player>()
            for (i in 0 until playersArray.length()) {
                val playerObj = playersArray.getJSONObject(i).getJSONObject("map")
                val playerId = playerObj.getString("id")
                val playerUsername = playerObj.getString("username")
                val playerScore = playerObj.getInt("score")
                val player = Player(playerId, playerUsername, playerScore)
                players.add(player)
            }
            val host = Player(hostId, hostUsername, null)
            //val winner = Player(winnerId, winnerUsername, null)
            val winner = Player(null, null, null)
            val tournament = Tournament(tournamentId, currentTournament.createdAt, currentSize, players, status, currentTournament.bestOf, message, host, winner)
            currentTournament = tournament
            println("Received Tournament Info Data: ID: ${currentTournament.id} Message: ${currentTournament.message} Players: ${currentTournament.players} Best of: ${currentTournament.bestOf} Current Size: ${currentTournament.currentSize} Current Host: ${currentTournament.host} Current Winner: ${currentTournament.winner}")
            println("Received Tournament info: ${result.toString()} \n")
            SwingUtilities.invokeLater {
                // update GUI components here
                guiObject?.updateCurrentTournamentList()
            }
        } else {
            println("No Tournament Data received")
        }
    })
}


fun joinTournament(id:String): TournamentInfo{
    val tournamentInfo = TournamentInfo(null,null,null,false,null)
    mSocket?.emit("tournament:join", id, Ack { ackData ->
        if (ackData != null) {
            val result = gson.toJson(ackData)
            val jsonArray = JSONArray(result)

            // get data about tournament that you join
            val getTournamentInfo = jsonArray.getJSONObject(0).getJSONObject("map")
            val success = getTournamentInfo.getBoolean("success")
            val error = getTournamentInfo.get("error")
            tournamentInfo.success = success
            if(success){

                tournamentInfo.error = error
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
            }else{
                val errorMessage = getTournamentInfo.getJSONObject("error").getJSONObject("map").getString("message")
                tournamentInfo.error = errorMessage
                println("Error: tournament join  failed : $errorMessage")
            }
            println("Acknowledgement received - Success: JOIN Tournament  ${jsonArray.toString()}")
            getCurrentTournaments()
        } else {
            // Handle acknowledgement failure
            println("Acknowledgement not received")
        }
    })
    Thread.sleep(1000)
    return tournamentInfo

}

fun leaveTournament():TournamentInfo{
    val tournamentInfo = TournamentInfo(null,null,null,false,null)
    mSocket?.emit("tournament:leave",  Ack { ackData ->
        if (ackData != null) {
            val result = gson.toJson(ackData)
            val jsonArray = JSONArray(result)

            // get data about tournament that you leave
            val getTournamentInfo = jsonArray.getJSONObject(0).getJSONObject("map")
            val success = getTournamentInfo.getBoolean("success")
            val error = getTournamentInfo.get("error")
            tournamentInfo.success = success
            if(success){

                tournamentInfo.error = error
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
            }else{
                val errorMessage = getTournamentInfo.getJSONObject("error").getJSONObject("map").getString("message")
                tournamentInfo.error = errorMessage
                println("Error: tournament leave  failed : $errorMessage")
            }
            println("Acknowledgement received - Success: LEAVE Tournament ${result.toString()}")
            getCurrentTournaments()
        } else {
            // Handle acknowledgement failure
            println("Acknowledgement not received")
        }
    })
    Thread.sleep(1000)
    return tournamentInfo
}

fun startTournament():TournamentInfo{
    val tournamentInfo = TournamentInfo(null,null,null,false,null)
    mSocket?.emit("tournament:start",  Ack { ackData ->
        if (ackData != null) {
            val result = gson.toJson(ackData)
            val jsonArray = JSONArray(result)

            // get data about tournament that you start
            val getTournamentInfo = jsonArray.getJSONObject(0).getJSONObject("map")
            val success = getTournamentInfo.getBoolean("success")
            val error = getTournamentInfo.get("error")
            tournamentInfo.success = success
            if(success){

                tournamentInfo.error = error
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
            }else{
                val errorMessage = getTournamentInfo.getJSONObject("error").getJSONObject("map").getString("message")
                tournamentInfo.error = errorMessage
                println("Error: tournament start  failed : $errorMessage")
            }
            println("Acknowledgement received - Success: START Tournament ${result.toString()}")
            getCurrentTournaments()
        } else {
            // Handle acknowledgement failure
            println("Acknowledgement not received")
        }
    })
    Thread.sleep(1000)
    return tournamentInfo
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