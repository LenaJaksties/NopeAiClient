package nopegamelogic

import java.lang.Integer.sum

class AILogic{

    fun calculateTurn(turnData: GameState):Move{
        var turnmove = Move(null,null,null,null,"Because I can!")
        // see card on top


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
            println(cardsToSend)
            if(cardsToSend.isEmpty() || cardsToSend.size > 3){
                println("Something Went Wrong Choosing a Card to Send")
            } else if(cardsToSend.size == 1){
                turnmove.card1 = Card(Type.valueOf(cardsToSend[0].type.type), Color.valueOf(cardsToSend[0].color.color),  cardsToSend[0].value,null,null,null)
            }else if(cardsToSend.size == 2){
                turnmove.card1 = Card(Type.valueOf(cardsToSend[0].type.type), Color.valueOf(cardsToSend[0].color.color),  cardsToSend[0].value,null,null,null)
                turnmove.card2 = Card(Type.valueOf(cardsToSend[1].type.type), Color.valueOf(cardsToSend[1].color.color),  cardsToSend[1].value,null,null,null)
            }else{
                turnmove.card1 = Card(Type.valueOf(cardsToSend[0].type.type), Color.valueOf(cardsToSend[0].color.color), cardsToSend[0].value,null,null,null)
                turnmove.card2 = Card(Type.valueOf(cardsToSend[1].type.type), Color.valueOf(cardsToSend[1].color.color), cardsToSend[1].value,null,null,null)
                turnmove.card3 = Card(Type.valueOf(cardsToSend[2].type.type), Color.valueOf(cardsToSend[2].color.color), cardsToSend[2].value,null,null,null)
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
            val inputColor = lastCard.color.color
            // check Colors and store all matching colors
            for (card in handCards){
                val currentCardColor = card.color.color
                // case 1 same color card              case 2  hand card consisting of two colors  case 3 top card consisting of two colors   case 4 hand card is multi card    case 5: both two colored values
                if(inputColor == currentCardColor || inputColor in currentCardColor || currentCardColor in inputColor || currentCardColor == Color.MULTI.color|| checkDoubleValuesForeEquality(card,lastCard)){
                    colorMatchingCard.add(card)

                }
            }

            // find possibles pairs and store them as an arraylist inside the fittingCards
            // case 1 card -> fitting number // special card
            //var tempCollocation = ArrayList<Card>()
            for(card in colorMatchingCard){
                if( card.value == lastCard.value || card.type != Type.NUMBER ){
                    var tempCollocation = ArrayList<Card>()
                    println("Gefunden auf OTTO DE: ${card.type} and  ${card.value}")
                    tempCollocation.add(card)
                    matchingCardCollocations.add(tempCollocation)
//                    tempCollocation.clear()
                }

            }
            println(matchingCardCollocations)
            // case 2 card -> two SAME color cards together make desired number -> 1+1; 2+1; 1+2; (maybe filter to no special cards?)
            for (i in 0 until colorMatchingCard.size) {
                val card = colorMatchingCard[i]
                for (j in i+1 until colorMatchingCard.size) {
                    val card2 = colorMatchingCard[j]
                    if(checkSameColor(card,card2) && (sum(card.value!!,card2.value!!) == lastCard.value)){
                        var tempCollocation = ArrayList<Card>()
                        tempCollocation.add(card)
                        tempCollocation.add(card2)
                        matchingCardCollocations.add(tempCollocation)
                        //tempCollocation.clear()
                    }
                }
            }
            // case 3 cards -> (rare) three SAME color cards together make desired number
            for (i in 0 until colorMatchingCard.size) {
                val card = colorMatchingCard[i]
                for (j in i+1 until colorMatchingCard.size) {
                    val card2 = colorMatchingCard[j]
                    for (k in j+1 until colorMatchingCard.size) {
                        val card3 = colorMatchingCard[k]
                        // check for same color
                        if((card.color.color == card2.color.color) && (card2.color.color == card3.color.color) && (sum(sum(card.value!!,card2.value!!),card3.value!!) == lastCard.value)){
                            var tempCollocation = ArrayList<Card>()
                            tempCollocation.add(card)
                            tempCollocation.add(card2)
                            tempCollocation.add(card3)
                            matchingCardCollocations.add(tempCollocation)
                            //tempCollocation.clear()
                        }
                    }
                }
            }
        for(i in 0 until matchingCardCollocations.size){
            println("$i : ${matchingCardCollocations[i].size} : ${matchingCardCollocations[i]}")
        }


        }else{
            // TODO special card handling
        }

        return matchingCardCollocations

    }

    private fun checkSameColor(card:Card, card2:Card):Boolean{
        var isSameColor = false
        if(card.color.color == card2.color.color || card.color.color in card2.color.color || card2.color.color in card.color.color || card2.color.color == Color.MULTI.color || card.color.color == Color.MULTI.color|| checkDoubleValuesForeEquality(card,card2)){
           isSameColor = true
        }

        return isSameColor
    }
    private fun checkSameColor(card:Card, card2:Card, card3:Card):Boolean{
        var isSameColor = false
        // check equality card 1 and 2
        if(card.color.color == card2.color.color || card.color.color in card2.color.color || card2.color.color in card.color.color || card2.color.color == Color.MULTI.color || card.color.color == Color.MULTI.color|| checkDoubleValuesForeEquality(card,card2)){
            // check equality card 2 and 3
            if(card2.color.color == card3.color.color || card2.color.color in card3.color.color || card3.color.color in card2.color.color || card3.color.color == Color.MULTI.color || card2.color.color == Color.MULTI.color|| checkDoubleValuesForeEquality(card2,card3)){
                // check equality card 1 and 3
                if(card.color.color == card3.color.color || card.color.color in card3.color.color || card3.color.color in card.color.color || card.color.color == Color.MULTI.color|| card3.color.color == Color.MULTI.color|| checkDoubleValuesForeEquality(card,card3)){
                    isSameColor = true
                }

            }
        }

        return isSameColor
    }
    private fun checkDoubleValuesForeEquality(card:Card, card2:Card):Boolean{
        // check for two-type colors
        val twoTypeColors = setOf(
            Color.RED_YELLOW, Color.BLUE_GREEN, Color.YELLOW_BLUE,
            Color.RED_BLUE, Color.RED_GREEN, Color.YELLOW_GREEN
        )

        if (card.color in twoTypeColors && card2.color in twoTypeColors) {
            val card1Colors = card.color.color.split("-")
            val card2Colors = card2.color.color.split("-")
            return card1Colors.any { it in card2Colors }
        }

        return false
    }


    private fun calculateSmartDecision(fittingCardInDeck: ArrayList< ArrayList<Card>>, turndata: GameState): ArrayList<Card> {
        var bestMoveCards = ArrayList<Card>()

        bestMoveCards.addAll(fittingCardInDeck[0])

        // in order (last one top priority)
        return bestMoveCards
    }




}

