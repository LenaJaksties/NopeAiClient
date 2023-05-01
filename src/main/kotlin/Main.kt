
import guiview.GUIMain
import javax.swing.*
import socketinterface.*
import io.socket.client.IO
import io.socket.client.Socket
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.socket.client.Ack
import io.socket.emitter.Emitter
import org.json.JSONObject

/*
 * Main
 */

fun main(args: Array<String>) {

    //Server URL
    val serverURL = "https://nope-server.azurewebsites.net/"

    val restapi = RestApi()
//    //Register
//    restapi.registerUser()

    //Login
    // TODO : catch a nullpointer if access token is null
    val token = restapi.userLogin()

    //Socket init
    if(token != null){
        val mSocket = socketinit(serverURL, token)
        connect()
        restapi.connect(token)

    }else{
        println("Your login/ was not valid")
    }




    // Create an instance of the StartMenu class
    val menu = GUIMain()

    val gson = Gson()
    //val jsonTournamentCreate = gson.toJson(tc)

//    mSocket?.emit("tournament:create", 3, Ack { ackData ->
//        // Handle acknowledgement data or failure
//        if (ackData != null) {
//            val result = gson.toJson(ackData)
//            val lol = JSONObject.stringToValue(ackData.toString())
//            println(lol)
//            //val jsonMap = Gson().fromJson(result, Map::class.java)
//            println("Acknowledgement received - Success: ${result.toString()}")
//            //println(jsonMap["tournamentId"])
//
//        } else {
//            // Handle acknowledgement failure
//            println("Acknowledgement not received")
//        }
//    })

    // socket.on -> empfangen der events + json datein
    // je nach event wird andere socket.on ausgeführt (Listener)
    //val socket = mSocket!!.connect()
    //socket.on("game:state" ){

    // println("Please play a card within 10 seconds")

    // Listen for player movepayload and send acknowledgement to server
    //val movepayload = calculateMove()
    //ack(movepayload)
    //}

}