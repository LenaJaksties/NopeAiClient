
import guiview.GUIMain
import socketinterface.*
import nopegamelogic.*
import kotlin.random.Random

/**
 * Main to start the Card Game Nope
 */

fun main(args: Array<String>) {
    val user = "winter"
    //Server URL
    val serverURL = "https://nope-server.azurewebsites.net/"

    val restapi = RestApi()

    //Register
//    restapi.registerUser()

    //Login
    val token = restapi.userLogin(user)

    // Create an instance of the StartMenu class
    val menu = GUIMain()

    //Socket init
    if(token != null){
        val mSocket = socketinit(serverURL, token, menu, user)
        connect()
        restapi.connect(token)
    }else{
        println("Your login/ was not valid")
    }


    /**
     * Temporary Testing card logic
     */

    val testAILogic = AILogic()

    // create handcards
    val card1 = Card(Type.NUMBER, Color.RED,2,null,null,null)
    val card2 = Card(Type.NUMBER, Color.RED,1,null,null,null)
    val card3 = Card(Type.JOKER, Color.YELLOW_BLUE,3,null,null,null)
    val card4 = Card(Type.NUMBER, Color.BLUE,2,null,null,null)
    val card5 = Card(Type.NUMBER, Color.RED_BLUE,3,null,null,null)
    val card6 = Card(Type.NUMBER, Color.RED_GREEN,2,null,null,null)
    val card7 = Card(Type.NUMBER, Color.BLUE_GREEN,1,null,null,null)
    val card8 = Card(Type.NUMBER, Color.YELLOW_BLUE,1,null,null,null)
    val card9 = Card(Type.NUMBER, Color.BLUE,1,null,null,null)
    val handCards = ArrayList<Card>()
    handCards.add(card1)
    handCards.add(card2)
    handCards.add(card3)
    handCards.add(card4)
    handCards.add(card5)
    handCards.add(card6)
    handCards.add(card7)
    handCards.add(card8)
    handCards.add(card9)
    // top card and last top card
    val random = Random(System.currentTimeMillis())

// Generate a random Color
    val randomColor = Color.values()[random.nextInt(Color.values().size)]

// Generate a random Type
    val randomType = Type.values()[random.nextInt(Type.values().size)]


    val cardTop = Card(Type.NUMBER, Color.RED_BLUE, 3, null, null, null)

    val cardLastTop = Card(Type.NUMBER, Color.RED,3,null,null,null)


    var turnData =  GameState(null,null,cardTop,cardLastTop,null,null,handCards,null,null,null,null,null,null,null,true,null,null)
    var turnMove = testAILogic.calculateTurn(turnData)
    println(turnMove)
    println()


    val cardS= Card(Type.SELECTION, Color.BLUE,null,null,3,null)
    val cardS1 = Card(Type.SEE_THROUGH, Color.YELLOW,null,null,null,null)
    val cardS2 = Card(Type.JOKER, Color.MULTI,1,null,null,null)
    val cardS3 = Card(Type.REBOOT, Color.MULTI,null,null,null,null)

    // tests for special cards

    println("SELECTION Card")
    turnData =  GameState(null,null,cardS,cardLastTop,null,null,handCards,null,null,null,null,null,null,null,true,null,null)
    turnMove = testAILogic.calculateTurn(turnData)
    println(turnMove)
    println()
    println("SEE THROUGH CARD (FIRST CARD)")
    turnData =  GameState(null,null,cardS1,null,null,null,handCards,null,null,null,null,null,null,null,true,null,null)
    turnMove = testAILogic.calculateTurn(turnData)
    println(turnMove)
    println()
    println("SEE THROUGH CARD (WITH PRIOR CARD)")
    turnData =  GameState(null,null,cardS1,cardLastTop,null,null,handCards,null,null,null,null,null,null,null,true,null,null)
    turnMove = testAILogic.calculateTurn(turnData)
    println(turnMove)
    println()
    println("JOKER CARD")
    turnData =  GameState(null,null,cardS2,cardLastTop,null,null,handCards,null,null,null,null,null,null,null,true,null,null)
    turnMove = testAILogic.calculateTurn(turnData)
    println(turnMove)
    println()
    println("REBOOT CARD")
    turnData =  GameState(null,null,cardS3,cardLastTop,null,null,handCards,null,null,null,null,null,null,null,true,null,null)
    turnMove = testAILogic.calculateTurn(turnData)
    println(turnMove)
    println()


}