package nopegamelogic


class AILogic{

    fun calculateTurn(turnData: GameState):Move{
        var turnmove = Move(MoveType.PUT,null,null,null,"Because I can!")
        // see card on top

        println()
        println("TopCard: ${turnData.topCard}")

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
            val cardsToSend = calculateSmartDecision(fittingCardInDeck, turnData)
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
        var cardMatches = false
        var matchingCardCollocations =  ArrayList<ArrayList<Card>>()

        if(lastCard.type == Type.NUMBER){
            // look for colors
            var colorMatchingCard = ArrayList<Card>()
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
                // add all combinations f three cards that match the color
                matchingCardCollocations = addMatching3Cards(colorMatchingCard, lastCard, matchingCardCollocations)
            } else{
                print("Something went wrong with the value of the top card")
            }
        }else if (lastCard.type == Type.JOKER){
            // TODO special card handling
        }else if(lastCard.type == Type.REBOOT){

        }else if(lastCard.type == Type.SEE_THROUGH){
            // look at card beneath it
        }else if(lastCard.type == Type.SELECTION){

        }else{
            // error card
            print("Something went wrong with the type of the top card")
        }

        // print possible list of combinations that can be sent
        for(i in 0 until matchingCardCollocations.size){
            println("$i : ${matchingCardCollocations[i].size} : ${matchingCardCollocations[i]}")
        }

        return matchingCardCollocations

    }

    /**
     * filters hand cards and selects only cards that match the color/ colors
     * @param handCards cards of the player
     * @param inputColor color of the top card of the discard pile
     * @param colorMatchingCard list of cards that will store matching cards
     * @return colorMatchingCard: list of cards who have a fitting color
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

    private fun addMatching1Cards(
        specialCards:Boolean,
        colorMatchingCard: ArrayList<Card>,
        matchingCardCollocations: ArrayList<ArrayList<Card>>
    ):ArrayList<ArrayList<Card>>  {
        for (card in colorMatchingCard) {

            if(specialCards){
                // add only special cards to collection
                if(card.type != Type.NUMBER ){
                    var tempCollocation = ArrayList<Card>()
                    tempCollocation.add(card)
                    matchingCardCollocations.add(tempCollocation)
                }
            } else{
                // add only number cards to collection
                if (card.type == Type.NUMBER) {
                    var tempCollocation = ArrayList<Card>()
                    tempCollocation.add(card)
                    matchingCardCollocations.add(tempCollocation)
                }
            }
        }
        return matchingCardCollocations
    }

    private fun addMatching3Cards(
        colorMatchingCard: ArrayList<Card>,
        lastCard: Card,
        matchingCardCollocations: ArrayList<ArrayList<Card>>
    ):ArrayList<ArrayList<Card>> {
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

    private fun addMatching2Cards(
        colorMatchingCard: ArrayList<Card>,
        lastCard: Card,
        matchingCardCollocations: ArrayList<ArrayList<Card>>
    ):ArrayList<ArrayList<Card>> {
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


    private fun calculateSmartDecision(fittingCardInDeck: ArrayList< ArrayList<Card>>, turndata: GameState): ArrayList<Card> {
        var bestMoveCards = ArrayList<Card>()

        // TODO add smart decision

        // Priority:
        // 1. Joker


        bestMoveCards.addAll(fittingCardInDeck[0])


        // in order (last one top priority)
        return bestMoveCards
    }




}

