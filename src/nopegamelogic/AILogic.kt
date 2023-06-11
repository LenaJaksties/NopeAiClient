package nopegamelogic


class AILogic{

    fun calculateTurn(turnData: GameState, aiPlayerName: String):Move{
        val turnmove = Move(MoveType.PUT,null,null,null,"Because I can!")
        // see card on top

        println()
        println("TopCard: ${turnData.topCard}")

        // print hand
        println("My hand: ")
        for(i in 0 until turnData.hand!!.size){
            println("$i : (${turnData.hand!![i].type}  ${turnData.hand!![i].color}  ${turnData.hand!![i].value})")
        }

        // see if player has possible card he needs to send and place them in a new ArrayList
        val fittingCardInDeck = findMatchingCards(turnData.hand!!, turnData.topCard!!, turnData.lastTopCard)

        // define Type of move
        if(fittingCardInDeck.isEmpty() && turnData.secondTurn == true) {
            turnmove.type = MoveType.NOPE
        } else if(fittingCardInDeck.isEmpty()){
            turnmove.type = MoveType.TAKE
        } else{
            turnmove.type = MoveType.PUT
        }

        if(turnmove.type == MoveType.PUT){

            // select cards to send
            val cardsToSend = calculateSmartDecision(fittingCardInDeck, turnData, aiPlayerName)
            if(cardsToSend.isEmpty() || cardsToSend.size > 3){
                println("Something Went Wrong Choosing a Card to Send")
            } else if(cardsToSend.size == 1){
                turnmove.card1 = Card(cardsToSend[0].type, cardsToSend[0].color, cardsToSend[0].value,null,null,null)
            }else if(cardsToSend.size == 2){
                turnmove.card1 = Card(cardsToSend[0].type, cardsToSend[0].color,  cardsToSend[0].value,null,null,null)
                turnmove.card2 = Card(cardsToSend[1].type, cardsToSend[1].color,  cardsToSend[1].value,null,null,null)
            }else{
                turnmove.card1 = Card(cardsToSend[0].type, cardsToSend[0].color, cardsToSend[0].value,null,null,null)
                turnmove.card2 = Card(cardsToSend[1].type, cardsToSend[1].color, cardsToSend[1].value,null,null,null)
                turnmove.card3 = Card(cardsToSend[2].type, cardsToSend[2].color, cardsToSend[2].value,null,null,null)
            }
        }
        return turnmove
    }
    private fun findMatchingCards(handCards: ArrayList<Card>, lastCard: Card, preLastCard: Card?): ArrayList<ArrayList<Card>> {

        var matchingCardCollocations =  ArrayList<ArrayList<Card>>()
        var colorMatchingCard = ArrayList<Card>()
        if(lastCard.type == Type.NUMBER){
            // look for colors
            val inputColor = lastCard.color
            // check Colors and store all matching colors
            colorMatchingCard = getCardsThatMatchColor(handCards, inputColor, colorMatchingCard)
            // add special cards that match the color
            matchingCardCollocations = addMatching1Cards(true,colorMatchingCard, matchingCardCollocations)
            // if else depending on amounts of cards that should be played out
            if(lastCard.value == 1){
                // add every single card that matches the color
                // skip special cards as they are already inside the collection
                matchingCardCollocations = addMatching1Cards(false,colorMatchingCard, matchingCardCollocations)
            }else if(lastCard.value == 2){
                // add all combinations of two cards that match the color
                matchingCardCollocations = addMatching2Cards(colorMatchingCard, lastCard, matchingCardCollocations)
            }else if(lastCard.value == 3){
                // add all combinations of three cards that match the color
                matchingCardCollocations = addMatching3Cards(colorMatchingCard, lastCard, matchingCardCollocations)
            } else{
                print("Something went wrong with the value of the top card")
            }
        }else if (lastCard.type == Type.JOKER || lastCard.type == Type.REBOOT){
            // put down a single card of own choice
            val inputColor = lastCard.color // Multi
            colorMatchingCard = getCardsThatMatchColor(handCards, inputColor, colorMatchingCard)
            // add special cards first
            matchingCardCollocations = addMatching1Cards(true,colorMatchingCard, matchingCardCollocations)
            // add all number cards
            matchingCardCollocations = addMatching1Cards(false,colorMatchingCard, matchingCardCollocations)
        }else if(lastCard.type == Type.SEE_THROUGH){
            // look at card beneath it (call function again and swap top card)
            if (preLastCard != null) {
                matchingCardCollocations = findMatchingCards(handCards, preLastCard,null)
            }else{
                // no card prior special rule: put down a single card of the color of the See through card
                val inputColor = lastCard.color
                colorMatchingCard = getCardsThatMatchColor(handCards, inputColor, colorMatchingCard)
                // add special cards first
                matchingCardCollocations = addMatching1Cards(true,colorMatchingCard, matchingCardCollocations)
                // add other matching number cards
                matchingCardCollocations = addMatching1Cards(false,colorMatchingCard, matchingCardCollocations)
            }
        }else if(lastCard.type == Type.SELECTION){
            // use color of selection card or given special selection color when multi and special value color for amount of cards
            var inputColor = Color.NULL;
            if(lastCard.color != Color.MULTI){
                // look for card color
                inputColor = lastCard.color
            }else{
                // use given color
                inputColor = lastCard.selectedColor!!
            }
            // check Colors and store all matching colors
            colorMatchingCard = getCardsThatMatchColor(handCards, inputColor, colorMatchingCard)
            // add special cards that match the color
            matchingCardCollocations = addMatching1Cards(true,colorMatchingCard, matchingCardCollocations)
            // if else depending on amounts of cards that should be played out the other player decided
            if(lastCard.selectValue == 1){
                // add every single card that matches the color
                // skip special cards as they are already inside the collection
                matchingCardCollocations = addMatching1Cards(false,colorMatchingCard, matchingCardCollocations)
            }else if(lastCard.selectValue == 2){
                // add all combinations of two cards that match the color
                matchingCardCollocations = addMatching2Cards(colorMatchingCard, lastCard, matchingCardCollocations)
            }else if(lastCard.selectValue == 3){
                // add all combinations of three cards that match the color
                matchingCardCollocations = addMatching3Cards(colorMatchingCard, lastCard, matchingCardCollocations)
            } else{
                print("Something went wrong with the value of the top card - There wasn't a Value given by the the player")
            }
        }else{
            // error card
            print("Something went wrong with the type of the top card")
        }

        //matchingCardCollocations = sortListBy(2,matchingCardCollocations)
        println("My Possibilities to sent: ")
        // print possible list of combinations that can be sent
        for(i in 0 until matchingCardCollocations.size){
            print("$i : ${matchingCardCollocations[i].size} :")
            for(j in 0 until matchingCardCollocations[i].size){
                print("(${matchingCardCollocations[i][j].type}  ${matchingCardCollocations[i][j].color}  ${matchingCardCollocations[i][j].value}) \t\t")

            }
            print("\n")
        }

        return matchingCardCollocations

    }
    /**
     * filters hand cards and selects only cards that match the color/ colors
     * @param handCards cards of the player
     * @param inputColor color of the top card of the discard pile
     * @param colorMatchingCard list of cards that will store matching cards
     * @return ArrayList<Card> list of cards that have a matching color
     */
    private fun getCardsThatMatchColor(handCards: ArrayList<Card>,inputColor: Color,colorMatchingCard: ArrayList<Card>) : ArrayList<Card> {
        if(inputColor.color == Color.MULTI.color){
            colorMatchingCard.addAll(handCards)
        }else{
            for (card in handCards) {
                val currentCardColor = card.color.color
                // case 1 same color card              case 2  hand card consisting of two colors  case 3 top card consisting of two colors   case 4 hand card is multi card    case 5: both two colored values -> check for similarity and compare it with last card
                if (inputColor.color == currentCardColor || inputColor.color in currentCardColor || currentCardColor in inputColor.color || currentCardColor == Color.MULTI.color || checkDoubleValuesForeEquality(
                        card,
                        inputColor
                    ).color in inputColor.color
                ) {
                    colorMatchingCard.add(card)

                }
            }
        }
        return colorMatchingCard
    }
    private fun addMatching1Cards(specialCards:Boolean,colorMatchingCard: ArrayList<Card>,matchingCardCollocations: ArrayList<ArrayList<Card>>):ArrayList<ArrayList<Card>>  {
        for (card in colorMatchingCard) {

            if(specialCards){
                // add only special cards to collection
                if(card.type != Type.NUMBER && card.type != Type.JOKER ){
                    val tempCollocation = ArrayList<Card>()
                    tempCollocation.add(card)
                    matchingCardCollocations.add(tempCollocation)
                }
            } else{
                // add only number cards or Joker to collection
                if (card.type == Type.NUMBER || card.type == Type.JOKER) {
                    val tempCollocation = ArrayList<Card>()
                    tempCollocation.add(card)
                    matchingCardCollocations.add(tempCollocation)
                }
            }
        }
        return matchingCardCollocations
    }
    private fun addMatching2Cards(colorMatchingCard: ArrayList<Card>,lastCard: Card,matchingCardCollocations: ArrayList<ArrayList<Card>>):ArrayList<ArrayList<Card>> {
        for (i in 0 until colorMatchingCard.size) {
            val card = colorMatchingCard[i]
            for (j in i + 1 until colorMatchingCard.size) {
                val card2 = colorMatchingCard[j]
                if (checkSameColor(card, card2, lastCard)) {
                    var tempCollocation = ArrayList<Card>()
                    tempCollocation.add(card)
                    tempCollocation.add(card2)
                    matchingCardCollocations.add(tempCollocation)
                }
            }
        }
        return matchingCardCollocations
    }
    private fun addMatching3Cards(colorMatchingCard: ArrayList<Card>,lastCard: Card,matchingCardCollocations: ArrayList<ArrayList<Card>>):ArrayList<ArrayList<Card>> {
        for (i in 0 until colorMatchingCard.size) {
            val card = colorMatchingCard[i]
            for (j in i + 1 until colorMatchingCard.size) {
                val card2 = colorMatchingCard[j]
                for (k in j + 1 until colorMatchingCard.size) {
                    val card3 = colorMatchingCard[k]
                    // check for same color
                    if (checkSameColor(card, card2, card3, lastCard)) {
                        var tempCollocation = ArrayList<Card>()
                        tempCollocation.add(card)
                        tempCollocation.add(card2)
                        tempCollocation.add(card3)
                        matchingCardCollocations.add(tempCollocation)
                    }
                }
            }
        }
        return matchingCardCollocations
    }
    private fun checkSameColor(card:Card, card2:Card, lastCard: Card):Boolean{
        var isSameColor = false
        if(card.color.color == card2.color.color || card.color.color in card2.color.color || card2.color.color in card.color.color || card2.color.color == Color.MULTI.color || card.color.color == Color.MULTI.color|| checkDoubleValuesForeEquality(card,card2).color in lastCard.color.color){
           isSameColor = true
        }

        return isSameColor
    }
    private fun checkSameColor(card:Card, card2:Card, card3:Card,lastCard: Card):Boolean{
        var isSameColor = false
        // check equality of card 1 and 2
        if(card.color.color == card2.color.color || card.color.color in card2.color.color || card2.color.color in card.color.color || card2.color.color == Color.MULTI.color || card.color.color == Color.MULTI.color|| checkDoubleValuesForeEquality(card,card2).color in lastCard.color.color){
            // check equality of card 2 and 3
            if(card2.color.color == card3.color.color || card2.color.color in card3.color.color || card3.color.color in card2.color.color || card3.color.color == Color.MULTI.color || card2.color.color == Color.MULTI.color|| checkDoubleValuesForeEquality(card2,card3).color in lastCard.color.color){
                // check equality of card 1 and 3
                if(card.color.color == card3.color.color || card.color.color in card3.color.color || card3.color.color in card.color.color || card.color.color == Color.MULTI.color|| card3.color.color == Color.MULTI.color|| checkDoubleValuesForeEquality(card,card3).color in lastCard.color.color){
                    isSameColor = true
                }
            }
        }
        return isSameColor
    }
    private fun checkDoubleValuesForeEquality(card:Card, card2:Card): Color {
        var matchingColor = Color.NULL
        // check for two-type colors
        val twoTypeColors = setOf(
            Color.RED_YELLOW, Color.BLUE_GREEN, Color.YELLOW_BLUE,
            Color.RED_BLUE, Color.RED_GREEN, Color.YELLOW_GREEN
        )

        if (card.color in twoTypeColors && card2.color in twoTypeColors) {
            val card1Colors = card.color.color.split("-")
            val card2Colors = card2.color.color.split("-")
            // Find the matching color between card1Colors and card2Colors and set matchingColor to the color or else to null
            matchingColor = card1Colors.find { it in card2Colors }?.let { Color.valueOf(it.toUpperCase()) } ?: Color.NULL
            }

        return matchingColor
    }
    private fun checkDoubleValuesForeEquality(card:Card, topCardColor:Color): Color {
        var matchingColor = Color.NULL
        // check for two-type colors
        val twoTypeColors = setOf(
            Color.RED_YELLOW, Color.BLUE_GREEN, Color.YELLOW_BLUE,
            Color.RED_BLUE, Color.RED_GREEN, Color.YELLOW_GREEN
        )

        if (card.color in twoTypeColors && topCardColor in twoTypeColors) {
            val card1Colors = card.color.color.split("-")
            val card2Colors = topCardColor.color.split("-")
            // Find the matching color between card1Colors and card2Colors and set matchingColor to the color or else to null
            matchingColor = card1Colors.find { it in card2Colors }?.let { Color.valueOf(it.toUpperCase()) } ?: Color.NULL
        }

        return matchingColor
    }


    private fun calculateSmartDecision(fittingCardInDeck: ArrayList< ArrayList<Card>>, turnData: GameState, aiPlayerName: String): ArrayList<Card> {
        var bestMoveCards = ArrayList<Card>()

        // Use Special Cards before Normal Cards and Jokers
        // Priority:
        // 1. Reboot
        // 2. See through
        for(i in 0 until fittingCardInDeck.size){
            // find reboot special card
            if(fittingCardInDeck[i].size == 1 && fittingCardInDeck[i][0].type == Type.REBOOT){
                // use reboot card
                bestMoveCards.addAll(fittingCardInDeck[i])
                return bestMoveCards
            }
        }
        for(i in 0 until fittingCardInDeck.size){
            // find see through special card
            if(fittingCardInDeck[i].size == 1 && fittingCardInDeck[i][0].type == Type.SEE_THROUGH){
                // use see through card
                bestMoveCards.addAll(fittingCardInDeck[i])
                return bestMoveCards
            }
        }
        // get player hand of own ai-player and opponent
        var myHandSize = 0
        var opponentHandSize = 0
        if(turnData.players?.get(0)?.username == aiPlayerName){
            // found your own user
            myHandSize = turnData.players!![0].handSize!!
            opponentHandSize = turnData.players!![1].handSize!!
        } else{
            // found opponent
            opponentHandSize = turnData.players!![0].handSize!!
            myHandSize = turnData.players!![1].handSize!!
        }

        var colorAmount = Array(11){0}
        for (x in 0 until (turnData.hand?.size ?: 0)){
            if(turnData.hand!![x].color == Color.RED){
                colorAmount[0] ++
            } else if(turnData.hand!![x].color == Color.BLUE){
                colorAmount[1] ++
            } else if(turnData.hand!![x].color == Color.GREEN){
                colorAmount[2] ++
            } else if(turnData.hand!![x].color == Color.YELLOW){
                colorAmount[3] ++
            } else if(turnData.hand!![x].color == Color.RED_YELLOW){
                colorAmount[4] ++
            } else if(turnData.hand!![x].color == Color.BLUE_GREEN){
                colorAmount[5] ++
            } else if(turnData.hand!![x].color == Color.YELLOW_BLUE){
                colorAmount[6] ++
            } else if(turnData.hand!![x].color == Color.RED_BLUE){
                colorAmount[7] ++
            } else if(turnData.hand!![x].color == Color.RED_GREEN){
                colorAmount[8] ++
            } else if(turnData.hand!![x].color == Color.YELLOW_GREEN){
                colorAmount[9] ++
            }else if(turnData.hand!![x].color == Color.MULTI){
                colorAmount[10] ++
            }else{
                println("Card didn't have a color value to use for comparison")
            }
        }
        var redColorsInHand = colorAmount[0]+ colorAmount[4]+ colorAmount[7]+ colorAmount[8]+ colorAmount[10]
        var blueColorsInHand = colorAmount[1]+ colorAmount[5]+ colorAmount[6]+ colorAmount[7]+ colorAmount[10]
        var greenColorsInHand = colorAmount[2]+ colorAmount[6]+ colorAmount[8]+ colorAmount[9]+ colorAmount[10]
        var yellowColorsInHand = colorAmount[3]+ colorAmount[4]+ colorAmount[6]+ colorAmount[9]+ colorAmount[10]

        var colorMostInHand = Color.NULL
        if(redColorsInHand >= blueColorsInHand && redColorsInHand >= greenColorsInHand && redColorsInHand >= yellowColorsInHand){
            // red most common
            colorMostInHand = Color.RED
        }else if(blueColorsInHand >= redColorsInHand && blueColorsInHand >= greenColorsInHand && blueColorsInHand >= yellowColorsInHand){
            colorMostInHand = Color.BLUE
        }else if(greenColorsInHand >= redColorsInHand && greenColorsInHand >= blueColorsInHand && greenColorsInHand >= yellowColorsInHand){
            colorMostInHand = Color.GREEN
        }else if(yellowColorsInHand >= redColorsInHand && yellowColorsInHand >= blueColorsInHand && yellowColorsInHand >= greenColorsInHand){
            colorMostInHand = Color.YELLOW
        }else{
            println("Most amount of Color Value couldn't be detected")
        }

        // make priority List depending on user hand size
        var priorityCardPossibilities = ArrayList< ArrayList<Card>>()

        if(myHandSize <=5){
            /*
            Priority:
            Joker
            double-colored 3
            double-colored 2
            single-colored 3
            double-colored 1
            single-colored 2
            single-colored 1
             */
            bestMoveCards = applyPriorityValue("Joker",1, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Double-Color",3, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Double-Color",2, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Single-Color",3, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Double-Color",1, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Single-Color",2, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Single-Color",1, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }

        }else if(myHandSize > 5 && opponentHandSize > 6){
            /*
            Priority:
            double-colored 3
            double-colored 2
            single-colored 3
            single-colored 2
            Joker
            double-colored 1
            single-colored 1
             */

            bestMoveCards = applyPriorityValue("Double-Color",3, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Double-Color",2, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Single-Color",3, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Single-Color",2, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Joker",1, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Double-Color",1, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Single-Color",1, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }

        }else if(myHandSize > 5 && opponentHandSize>3){
            /*
            Priority:
            double-colored 2
            single-colored 2
            Joker
            double-colored 1
            single-colored 1
            double-colored 3
            single-colored 3
             */
            bestMoveCards = applyPriorityValue("Double-Color",2, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Single-Color",2, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Joker",1, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Double-Color",1, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Single-Color",1, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Double-Color",3, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Single-Color",3, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }


        } else if(myHandSize > 5 && opponentHandSize <=3){
            /*
            Priority:
            Joker
            double-colored 1
            single-colored 1
            double-colored 2
            single-colored 2
            double-colored 3
            single-colored 3
             */
            bestMoveCards = applyPriorityValue("Joker",1, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Double-Color",1, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Single-Color",1, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Double-Color",2, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Single-Color",2, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Double-Color",3, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }
            if(bestMoveCards.isEmpty()){
                bestMoveCards = applyPriorityValue("Single-Color",3, fittingCardInDeck, turnData, bestMoveCards, colorMostInHand)
            }


        }else{
            print("my hand size or Opponent hand size not found for comparison")
            bestMoveCards.addAll(fittingCardInDeck[0])
        }
        if(bestMoveCards.isEmpty()){
            println("Error in choosing a smart move no move was chosen! ")
        }
        // in order (last one top priority)
        return bestMoveCards
    }

    private fun applyPriorityValue(
        filterType: String,
        filterValue: Int,
        fittingCardInDeck: ArrayList<ArrayList<Card>>,
        turnData: GameState,
        bestMoveCards: ArrayList<Card>,
        colorMostInHand: Color
    ): ArrayList<Card> {
        var priorityCardPossibilities = ArrayList< ArrayList<Card>>()
        priorityCardPossibilities = filterPossibilityList(fittingCardInDeck, filterType, filterValue, priorityCardPossibilities)
        if (priorityCardPossibilities.isNotEmpty()) {
            // print possibilities:


            // select best move possibility to send
            priorityCardPossibilities = sortListBy(filterValue, priorityCardPossibilities)
            for(i in 0 until priorityCardPossibilities.size){
                print("Priority Values: ")
                for(j in 0 until priorityCardPossibilities[i].size){
                    print("(${priorityCardPossibilities[i][j].type}  ${priorityCardPossibilities[i][j].color}  ${priorityCardPossibilities[i][j].value}) \t\t")

                }
                print("\n")
            }
            for (i in 0 until priorityCardPossibilities.size) {
                var hasMatch = ArrayList<ArrayList<Card>>()
                hasMatch = findMatchingCards(turnData.hand!!, priorityCardPossibilities[i].last(), null)
                if (hasMatch.size == 0) {
                    // found match where own player cant put a card on
                    bestMoveCards.addAll(priorityCardPossibilities[i])
                    return bestMoveCards
                }
            }
            val twoTypeColors = setOf(
                Color.RED_YELLOW, Color.BLUE_GREEN, Color.YELLOW_BLUE,
                Color.RED_BLUE, Color.RED_GREEN, Color.YELLOW_GREEN
            )
            // check if the card before the last card has the same type and should be sent last
            for(i in 0 until priorityCardPossibilities.size){
                if(priorityCardPossibilities[i].size > 1){
                    var lastCard = priorityCardPossibilities[i].last()
                    for(j in priorityCardPossibilities[i].size -2 downTo 0){
                        // check if conditions are met
                        if(priorityCardPossibilities[i][j].value == lastCard.value && priorityCardPossibilities[i][j].type == lastCard.type && (priorityCardPossibilities[i][j].color == lastCard.color || priorityCardPossibilities[i][j].color in twoTypeColors && lastCard.color in twoTypeColors || priorityCardPossibilities[i][j].color !in twoTypeColors && lastCard.color !in twoTypeColors)){
                            var hasMatch = ArrayList<ArrayList<Card>>()
                            hasMatch = findMatchingCards(turnData.hand!!, priorityCardPossibilities[i][j], null)
                            if (hasMatch.size == 0) {
                                // found match where own player cant put a card on
                                // swap cards in arraylist
                                priorityCardPossibilities[i] = swap2Cards(priorityCardPossibilities[i], lastCard, priorityCardPossibilities[i][j])
                                bestMoveCards.addAll(priorityCardPossibilities[i])
                                return bestMoveCards
                            }
                        }

                    }
                }

            }
            for (i in 0 until priorityCardPossibilities.size) {
                if (colorMostInHand.color in priorityCardPossibilities[i].last().color.color || colorMostInHand.color == priorityCardPossibilities[i].last().color.color) {
                    // found match for color that own player has most
                    bestMoveCards.addAll(priorityCardPossibilities[i])
                    return bestMoveCards
                }
                // check other cards in current possibility
                if(priorityCardPossibilities[i].size > 1){
                    var lastCard = priorityCardPossibilities[i].last()
                    for(j in priorityCardPossibilities[i].size -2 downTo 0){
                        // check if conditions are met
                        if(priorityCardPossibilities[i][j].value == lastCard.value && priorityCardPossibilities[i][j].type == lastCard.type && (priorityCardPossibilities[i][j].color == lastCard.color || priorityCardPossibilities[i][j].color in twoTypeColors && lastCard.color in twoTypeColors || priorityCardPossibilities[i][j].color !in twoTypeColors && lastCard.color !in twoTypeColors)){

                            if (colorMostInHand.color in priorityCardPossibilities[i][j].color.color || colorMostInHand.color == priorityCardPossibilities[i][j].color.color) {
                                // found match where own player cant put a card on
                                // swap cards in arraylist
                                priorityCardPossibilities[i] = swap2Cards(priorityCardPossibilities[i], lastCard, priorityCardPossibilities[i][j])
                                bestMoveCards.addAll(priorityCardPossibilities[i])
                                return bestMoveCards
                            }
                        }

                    }
                }

            }
            bestMoveCards.addAll(priorityCardPossibilities[0])
            return bestMoveCards
        }else{
            return bestMoveCards
        }
    }

    /**
     * swaps two cards inside the input Arraylist of cards
     */
    private fun swap2Cards(cards: ArrayList<Card>, lastCard: Card, card: Card): ArrayList<Card> {
        val lastIndex = cards.indexOf(lastCard)
        val cardIndex = cards.indexOf(card)

        // Check if both cards are present in the list
        if (lastIndex != -1 && cardIndex != -1) {
            // swap cards
            val temp = cards[lastIndex]
            cards[lastIndex] = cards[cardIndex]
            cards[cardIndex] = temp
        }

        return cards

    }

    /**
     * Filters a list by the input Type and Value and return a filtered list of lists of cards
     */
    private fun filterPossibilityList(
        fittingCardInDeck: ArrayList<ArrayList<Card>>,
        filterType: String,
        filterValue: Int,
        priorityCardPossibilities: ArrayList<ArrayList<Card>>
    ): ArrayList<ArrayList<Card>> {

        val twoTypeColors = setOf(
            Color.RED_YELLOW, Color.BLUE_GREEN, Color.YELLOW_BLUE,
            Color.RED_BLUE, Color.RED_GREEN, Color.YELLOW_GREEN
        )

        for (i in 0 until fittingCardInDeck.size) {

            for (j in 0 until fittingCardInDeck[i].size) {

                if(filterType == "Double-Color"){
                    // find possibilities with double value cards
                    if (fittingCardInDeck[i][j].color in twoTypeColors && fittingCardInDeck[i][j].value == filterValue) {

                        var tempCollocation = ArrayList<Card>()
                        tempCollocation.addAll(fittingCardInDeck[i])
                        priorityCardPossibilities.add(tempCollocation)
                        break;
                    }
                }else if(filterType == "Single-Color"){
                    if (fittingCardInDeck[i][j].color !in twoTypeColors && fittingCardInDeck[i][j].value == filterValue) {

                        var tempCollocation = ArrayList<Card>()
                        tempCollocation.addAll(fittingCardInDeck[i])
                        priorityCardPossibilities.add(tempCollocation)
                        break;
                    }
                }else if(filterType == "Joker"){
                    if (fittingCardInDeck[i][j].type == Type.JOKER && fittingCardInDeck[i][j].value == filterValue) {

                        var tempCollocation = ArrayList<Card>()
                        tempCollocation.addAll(fittingCardInDeck[i])
                        priorityCardPossibilities.add(tempCollocation)
                        break;
                    }

                }else{
                    print("There was given a filter type that is unknown")
                }

            }
        }
        return priorityCardPossibilities
    }

    /**
     * Sort a List by a given input value
     * Puts the input type last
     * 0 -> Joker
     * 1 -> Value 1
     * 2 -> Value 2
     * 3 -> Value 3
     */
    private fun sortListBy(number: Int, priorityCardPossibilities: ArrayList<ArrayList<Card>>): ArrayList<ArrayList<Card>>{

        val twoTypeColors = setOf(
            Color.RED_YELLOW, Color.BLUE_GREEN, Color.YELLOW_BLUE,
            Color.RED_BLUE, Color.RED_GREEN, Color.YELLOW_GREEN
        )
        for (x in 0 until priorityCardPossibilities.size){
            val tempCardListWith1 = ArrayList<Card>()
            val tempCardListWith1DoubleColor = ArrayList<Card>()
            val tempCardListWith1Joker = ArrayList<Card>()
            val tempCardListWith2 = ArrayList<Card>()
            val tempCardListWith2DoubleColor = ArrayList<Card>()
            val tempCardListWith3 = ArrayList<Card>()
            val tempCardListWith3DoubleColor = ArrayList<Card>()
            val tempCardListWithNull = ArrayList<Card>()
            // temporary Store Cards in ArrayLists for later sorting
            for(y in 0 until priorityCardPossibilities[x].size){
                if(priorityCardPossibilities[x][y].value == 1){
                    // differentiate between Joker single-color and double-color
                    if(priorityCardPossibilities[x][y].type == Type.JOKER){
                        tempCardListWith1Joker.add(priorityCardPossibilities[x][y])
                    } else if(priorityCardPossibilities[x][y].color in twoTypeColors){
                        tempCardListWith1DoubleColor.add(priorityCardPossibilities[x][y])
                    }else{
                        tempCardListWith1.add(priorityCardPossibilities[x][y])
                    }
                }else if(priorityCardPossibilities[x][y].value == 2){
                    if(priorityCardPossibilities[x][y].color in twoTypeColors){
                        tempCardListWith2DoubleColor.add(priorityCardPossibilities[x][y])
                    }else {
                        tempCardListWith2.add(priorityCardPossibilities[x][y])
                    }
                }else if(priorityCardPossibilities[x][y].value == 3){
                    if(priorityCardPossibilities[x][y].color in twoTypeColors){
                        tempCardListWith3DoubleColor.add(priorityCardPossibilities[x][y])
                    }else {
                        tempCardListWith3.add(priorityCardPossibilities[x][y])
                    }
                }else{
                    tempCardListWithNull.add(priorityCardPossibilities[x][y])
                }
            }

            priorityCardPossibilities[x].clear()
            priorityCardPossibilities[x].addAll(tempCardListWithNull)

            if(number == 0){
                priorityCardPossibilities[x].addAll(tempCardListWith3)
                priorityCardPossibilities[x].addAll(tempCardListWith3DoubleColor)
                priorityCardPossibilities[x].addAll(tempCardListWith2)
                priorityCardPossibilities[x].addAll(tempCardListWith2DoubleColor)
                priorityCardPossibilities[x].addAll(tempCardListWith1)
                priorityCardPossibilities[x].addAll(tempCardListWith1DoubleColor)
                // store joker last
                priorityCardPossibilities[x].addAll(tempCardListWith1Joker)
            }else if(number == 1){
                priorityCardPossibilities[x].addAll(tempCardListWith3)
                priorityCardPossibilities[x].addAll(tempCardListWith3DoubleColor)
                priorityCardPossibilities[x].addAll(tempCardListWith2)
                priorityCardPossibilities[x].addAll(tempCardListWith2DoubleColor)
                priorityCardPossibilities[x].addAll(tempCardListWith1Joker)
                // store Cards with Value 1 last
                priorityCardPossibilities[x].addAll(tempCardListWith1)
                priorityCardPossibilities[x].addAll(tempCardListWith1DoubleColor)
            }else if (number == 2){
                priorityCardPossibilities[x].addAll(tempCardListWith1)
                priorityCardPossibilities[x].addAll(tempCardListWith1Joker)
                priorityCardPossibilities[x].addAll(tempCardListWith1DoubleColor)
                priorityCardPossibilities[x].addAll(tempCardListWith3)
                priorityCardPossibilities[x].addAll(tempCardListWith3DoubleColor)
                // store Cards with Value 2 last
                priorityCardPossibilities[x].addAll(tempCardListWith2)
                priorityCardPossibilities[x].addAll(tempCardListWith2DoubleColor)
            }else if(number == 3){
                priorityCardPossibilities[x].addAll(tempCardListWith1)
                priorityCardPossibilities[x].addAll(tempCardListWith1Joker)
                priorityCardPossibilities[x].addAll(tempCardListWith1DoubleColor)
                priorityCardPossibilities[x].addAll(tempCardListWith2)
                priorityCardPossibilities[x].addAll(tempCardListWith2DoubleColor)
                // store Cards with Value 3 last
                priorityCardPossibilities[x].addAll(tempCardListWith3)
                priorityCardPossibilities[x].addAll(tempCardListWith3DoubleColor)
            }else{
                println("Illegal sorting value used")
            }
        }
        return priorityCardPossibilities
    }



}

