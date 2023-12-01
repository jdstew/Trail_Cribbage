package name.jdstew.trailcribbage.cribbage

import name.jdstew.trailcribbage.cribbage.Deck.isCardJack
import name.jdstew.trailcribbage.cribbage.Deck.getCardValue
import name.jdstew.trailcribbage.cribbage.Deck.getCardRank
import name.jdstew.trailcribbage.cribbage.Deck.getCardSuit

object Scoring {

//	lateinit var scoringViewModel: ScoringViewModel

    private fun scoreStarter(starter: Int): Int  {
        return if (isCardJack(starter)) {
    //			scoringViewModel.append("2 points for dealer's heels(J)")
            print("2 points for dealer's heels(J), ")
            2
        } else {
            0
        }
    }

    private fun scorePlay(playedCards: IntArray, startIndex: Int, nextIndex: Int): Int {
        var score = 0

        // check for '15' ... sum of all cards
        var stackSum = 0
        for (i in startIndex until nextIndex) {
            stackSum += getCardValue(playedCards[i])
        }
        if (stackSum == 15) {
            score += 2
//			scoringViewModel.append("15 for 2 in play stack")
            print("15 for 2 in play stack, ")
        }

        // check for pairs ... working backwards from last card
        val playedCardsCount: Int = nextIndex - startIndex
        if (playedCardsCount > 1) {
            var index: Int = nextIndex - 1
            while (index >= startIndex && getCardValue(playedCards[--index]) == getCardValue(
                    playedCards[nextIndex - 1]
                )) { /* do nothing */ }

            when(playedCardsCount - (index + 1)) {
                2 -> {
                    score += 2
//					scoringViewModel.append("2 of a kind for 2 in play stack")
                    print("2 of a kind for 2 in play stack, ")
                }
                3 -> {
                    score += 6
//					scoringViewModel.append("3 of a kind for 6 in play stack")
                    print("3 of a kind for 6 in play stack, ")
                }
                4 -> {
                    score += 12
//					scoringViewModel.append("4 of a kind for 12 in play stack")
                    print("4 of a kind for 12 in play stack, ")
                }
            }
        }

        // check for run ... last 3 or more cards
        if (playedCardsCount > 2) {
            var isRun = true
            var runSize = 3
            do {
                val topCards = IntArray(runSize)
                var i = 0
                var j = nextIndex - 1
                while (j >= 0 && j > nextIndex - 1 - runSize) {
                    topCards[i++] = getCardValue(playedCards[j--])
                }
                topCards.sort()
                for (k in 1 until topCards.size) {
                    if (topCards[k] - topCards[k - 1] != 1) {
                        isRun = false
                        break
                    }
                }
            } while (isRun && ++runSize <= playedCardsCount)

            if (--runSize > 2) {
                score += runSize
//				scoringViewModel.append("run of " + runSize + " for " + runSize + " in play stack")
                print("run of $runSize for $runSize in play stack, ")
            }
        }

        return score
    }

    fun scoreShow(showedCards: IntArray, starter: Int): Int {
        var score = 0

        val cardValues = IntArray(5)
        for (i in 0..3) {
            cardValues[i] = getCardValue(showedCards[i])
        }
        cardValues[4] = getCardValue(starter)

        // check for combinations of '15'
        // sum 2 cards... 10 combinations
        for (i in 0..3) {
            for (j in (i+1)..4) {
                if (cardValues[i] + cardValues[j] == 15) {
                    score +=2
//					scoringViewModel.append("15 for 2")
                    print("15 for 2 (2 cards), ")
                }
            }
        }

        // sum 3 cards... 10 combinations
        for (i in 0..2) {
            for (j in (i+1)..3) {
                for (k in (j+1)..4) {
                    if (cardValues[i] + cardValues[j] + cardValues[k] == 15) {
                        score +=2
//						scoringViewModel.append("15 for 2")
                        print("15 for 2 (3 cards), ")
                    }
                }
            }
        }

        // sum 4 cards...
        if (cardValues[0] + cardValues[1] + cardValues[2] + cardValues[3] == 15) {
            score +=2
//			scoringViewModel.append("15 for 2 (4 cards)")
            print("15 for 2, ")
        }
        if (cardValues[0] + cardValues[1] + cardValues[2] + cardValues[4] == 15) {
            score +=2
//			scoringViewModel.append("15 for 2 (4 cards)")
            print("15 for 2, ")
        }
        if (cardValues[0] + cardValues[1] + cardValues[3] + cardValues[4] == 15) {
            score +=2
//			scoringViewModel.append("15 for 2 (4 cards)")
            print("15 for 2, ")
        }
        if (cardValues[0] + cardValues[2] + cardValues[3] + cardValues[4] == 15) {
            score +=2
//			scoringViewModel.append("15 for 2 (4 cards)")
            print("15 for 2, ")
        }
        if (cardValues[1] + cardValues[2] + cardValues[3] + cardValues[4] == 15) {
            score +=2
//			scoringViewModel.append("15 for 2 (4 cards)")
            print("15 for 2, ")
        }

        // sum 5 cards...
        if (cardValues[0] + cardValues[1] + cardValues[2] + cardValues[3] + cardValues[4] == 15) {
            score +=2
//			scoringViewModel.append("15 for 2 (5 cards)")
            print("15 for 2, ")
        }

        // check for run ... last 3 or more cards
        var maxRunSize = 0
        val cardsRanked = IntArray(5)
        for (i in 0..3) {
            cardsRanked[i] = getCardRank(showedCards[i])
        }
        cardsRanked[4] = getCardRank(starter)
        cardsRanked.sort()

        for (start in 0..2) {
            for (end in 2..4) {
                var isRun = true
                for (i in (start+1)..end) {
                    if (cardsRanked[i] - cardsRanked[i - 1] != 1) {
                        isRun = false
                    }
                }
                if (isRun && (end - start + 1 > maxRunSize)) maxRunSize = end - start + 1
            }
        }

        if (maxRunSize > 2) {
            score += maxRunSize
//			scoringViewModel.append("run of " + maxRunSize + " for " + maxRunSize)
            print("run of $maxRunSize for $maxRunSize, ")
        }


        // check for pairs
        val cardRanks = IntArray(5)
        for (i in 0..3) {
            cardRanks[i] = getCardRank(showedCards[i])
        }
        cardRanks[4] = getCardRank(starter)

        for (rank in 1..13) {
            when (cardRanks.count{ card -> card == rank}) {
                2 -> {
                    score += 2
//					scoringViewModel.append("2 of a kind for 2")
                    print("2 of a kind for 2, ")
                }
                3 -> {
                    score += 6
//					scoringViewModel.append("3 of a kind for 6")
                    print("3 of a kind for 6, ")
                }
                4 -> {
                    score += 12
//					scoringViewModel.append("4 of a kind for 12")
                    print("4 of a kind for 12, ")
                }
            }
        }

        // check for jack of the same suit as starter "nobs"
        for (i in 0..3) {
            if (getCardRank(showedCards[i]) == 11 && getCardSuit(showedCards[i]) == getCardSuit(starter)) {
                ++score
//				scoringViewModel.append("jack same suit as starter for nobs")
                print("jack same suit as starter for nobs, ")
            }
        }

        return score
    }

    fun scoreShowInHand(showedCards: IntArray, starter: Int): Int {
        var score: Int = scoreShow(showedCards, starter)

        // check for a 4 or 5-card flush in hand
        // get suit of first card in crib
        val firstCardSuit: Int = getCardSuit(showedCards[0])

        // compare each card in crib
        for (i in 1..3) {
            if (getCardSuit(showedCards[i]) != firstCardSuit) {
                return score
            }
        }

        score += 4
//		scoringViewModel.append("flush in hand for 4")
        print("flush in hand for 4, ")

        // compare starter card
        if (getCardSuit(starter) != firstCardSuit) {
            return score
        }

//		scoringViewModel.append("flush with starter for 1")
        print("flush with starter for 1, ")
        return score + 1
    }

    fun scoreShowCrib(showedCards: IntArray, starter: Int): Int {
        val score: Int = scoreShow(showedCards, starter)

        // check for a 5-card flush

        // get suit of first card in crib
        val firstCardSuit: Int = getCardSuit(showedCards[0])

        // compare each card in crib
        for (i in 1..3) {
            if (getCardSuit(showedCards[i]) != firstCardSuit) {
                return score
            }
        }

        // compare starter card
        if (getCardSuit(starter) != firstCardSuit) {
            return score
        }

//		scoringViewModel.append("flush in crib and starter for 5")
        print("flush in crib and starter for 5, ")
        return score + 5
    }

    fun main() {
        println(" 0 <==> " + scoreStarter(0) +  " scoreStarter(A)")
        println(" 2 <==> " + scoreStarter(10) + " scoreStarter(J)")

        var played = intArrayOf(9, 4, 17, 30, 43, 0, -1, -1)
        println(" 0 <==> " + scorePlay(played, 0, 1) + " played: {10}")
        println(" 2 <==> " + scorePlay(played, 0, 2) + " played: {10, 5}")
        println(" 2 <==> " + scorePlay(played, 0, 3) + " played: {10, 5, 5}")
        println(" 6 <==> " + scorePlay(played, 0, 4) + " played: {10, 5, 5, 5}")
        println("12 <==> " + scorePlay(played, 0, 5) + " played: {10, 5, 5, 5, 5}")
        println(" 0 <==> " + scorePlay(played, 0, 6) + " played: {10, 5, 5, 5, 5, A}")
        // ^ note sum of cards is performed elsewhere

        played = intArrayOf(0, 1, 2, 3, 4, -1, -1, -1)
        println(" 0 <==> " + scorePlay(played, 0, 1) + " played: {A}")
        println(" 0 <==> " + scorePlay(played, 0, 2) + " played: {A, 2}")
        println(" 3 <==> " + scorePlay(played, 0, 3) + " played: {A, 2, 3}")
        println(" 4 <==> " + scorePlay(played, 0, 4) + " played: {A, 2, 3, 4}")
        println(" 7 <==> " + scorePlay(played, 0, 5) + " played: {A, 2, 3, 4, 5}")
        // ^ note sum of cards is performed elsewhere

        val cards0: IntArray = intArrayOf(2, 3, 4, 9)
        println("13 <==> " + scoreShowInHand(cards0, 35) + " in-hand: {3C,4C,5C,10C} {10H}")

        val cards1: IntArray = intArrayOf(2, 3, 4, 5)
        println("13 <==> " + scoreShowInHand(cards1, 9) + " in-hand: {3C,4C,5C,6C} {10C}")

        val cards2: IntArray = intArrayOf(5, 20, 34, 10)
        println(" 3 <==> " + scoreShowInHand(cards2, 2) + " in-hand: {6C, 8D, 9H, JC} {3C}")

        val cards3: IntArray = intArrayOf(3, 5, 7, 10)
        println(" 5 <==> " + scoreShowInHand(cards3, 12) + " in-hand: {4C,6C,8C,JC} {KC}")

        val cards4: IntArray = intArrayOf(3, 5, 7, 10)
        println(" 5 <==> " + scoreShowCrib(cards4, 12) + " in-crib: {4C,6C,8C,JC} {KC}")

        val cards5: IntArray = intArrayOf(3, 5, 7, 10)
        println(" 2 <==> " + scoreShowCrib(cards5, 23) + " in-crib: {4C,6C,8C,JC} {JD}")

        val cards6: IntArray = intArrayOf(3, 16, 29, 42)
        println(" 2 <==> " + scoreShowCrib(cards6, 23) + " in-crib: {3C,3D,3S,3H} {KD}")

        val cards7: IntArray = intArrayOf(43, 17, 30, 10)
        println(" 29 <==> " + scoreShowInHand(cards7, 4) + " in-crib: {5H,5D,5S,JC} {5C}")
    }
}