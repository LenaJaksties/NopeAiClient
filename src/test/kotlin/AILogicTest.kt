//package nopegamelogic
//
////import kotlin.test.Test
//import kotlin.*
//import kotlin.test.BeforeTest
//import kotlin.test.assertEquals
//
//import org.junit.jupiter.api.Test
//
//
//class AILogicTest {
//    val testAILogic = AILogic()
//
//    // create handcards
//    val card1 = Card(Type.NUMBER,Color.RED,2,null,null,null)
//    val card2 = Card(Type.NUMBER,Color.RED,1,null,null,null)
//    val card3 = Card(Type.NUMBER,Color.YELLOW_BLUE,3,null,null,null)
//    val card4 = Card(Type.NUMBER,Color.BLUE,2,null,null,null)
//    val card5 = Card(Type.NUMBER,Color.RED_BLUE,1,null,null,null)
//    val card6 = Card(Type.NUMBER,Color.RED_GREEN,2,null,null,null)
//    val handcards = ArrayList<Card>()
//
//    // top card and last top card
//    val cardTop = Card(Type.NUMBER,Color.RED,3,null,null,null)
//    val cardLastTop = Card(Type.NUMBER,Color.RED_BLUE,2,null,null,null)
//
//    var turndata =  GameState(null,null,cardTop,cardLastTop,null,null,handcards,null,null,null,null,null,null,null,false,null,null)
//
//
//    @BeforeTest
//    fun setUp() {
//        handcards.add(card1)
//        handcards.add(card2)
//        handcards.add(card3)
//        handcards.add(card4)
//        handcards.add(card5)
//        handcards.add(card6)
//
//        turndata =  GameState(null,null,cardTop,cardLastTop,null,null,handcards,null,null,null,null,null,null,null,false,null,null)
//    }
//
//    @Test
//    fun `test CalculateTurn`() {
//        val card1 = Card(Type.NUMBER,Color.RED,2,null,null,null)
//        val card2 = Card(Type.NUMBER,Color.RED,1,null,null,null)
//        val expected = Move(MoveType.PUT, card1,card2,null,"Because I can")
//        assertEquals(expected, testAILogic.calculateTurn(turndata))
//    }
//}