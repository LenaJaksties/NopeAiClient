
import guiview.GUIMain
import socketinterface.*
import com.google.gson.Gson
import nopegamelogic.*
import kotlin.random.Random

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

    // Create an instance of the StartMenu class
    val menu = GUIMain()


    //Socket init
    if(token != null){
        val mSocket = socketinit(serverURL, token, menu)
        connect()
        restapi.connect(token)

    }else{
        println("Your login/ was not valid")
    }



    val testAILogic = AILogic()

    // create handcards
    val card1 = Card(Type.NUMBER, Color.RED,2,null,null,null)
    val card2 = Card(Type.NUMBER, Color.RED,1,null,null,null)
    val card3 = Card(Type.JOKER, Color.YELLOW_BLUE,3,null,null,null)
    val card4 = Card(Type.NUMBER, Color.BLUE,2,null,null,null)
    val card5 = Card(Type.NUMBER, Color.RED_BLUE,3,null,null,null)
    val card6 = Card(Type.JOKER, Color.RED_GREEN,2,null,null,null)
    val handcards = ArrayList<Card>()
    handcards.add(card1)
    handcards.add(card2)
    handcards.add(card3)
    handcards.add(card4)
    handcards.add(card5)
    handcards.add(card6)
    // top card and last top card
    val random = Random(System.currentTimeMillis())

// Generate a random Color
    val randomColor = Color.values()[random.nextInt(Color.values().size)]

// Generate a random Type
    val randomType = Type.values()[random.nextInt(Type.values().size)]


    val cardTop = Card(Type.NUMBER, randomColor, random.nextInt(3) + 1, null, null, null)
    print(cardTop)
    val cardLastTop = Card(Type.NUMBER, Color.RED,3,null,null,null)


    var turnData =  GameState(null,null,cardTop,cardLastTop,null,null,handcards,null,null,null,null,null,null,null,true,null,null)

    var TurnStuff = testAILogic.calculateTurn(turnData)

    println(TurnStuff)

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