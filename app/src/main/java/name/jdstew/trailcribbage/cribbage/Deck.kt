package name.jdstew.trailcribbage.cribbage

import java.util.Random
import java.util.concurrent.atomic.AtomicInteger

object Deck {

    val SUIT_CLUBS: Int = 0
    val SUIT_DIAMONDS: Int = 1
    val SUIT_HEARTS: Int = 2
    val SUIT_SPADES: Int = 3

    // jack index values
    val CARD_JACK: IntArray = intArrayOf(10, 23, 36, 49)

    // 0-12 clubs, 13-25 diamonds, 26-38 hearts, 39-51 spades
    val CARD_RANK: IntArray = intArrayOf(
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13
    )

    // 0-12 clubs, 13-25 diamonds, 26-38 hearts, 39-51 spades
    val CARD_VALUE: IntArray = intArrayOf(
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10,
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10,
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10,
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10
    )

    // 0-12 clubs, 13-25 diamonds, 26-38 hearts, 39-51 spades
    val CARD_SUIT: IntArray = intArrayOf(
        0, 0, 0, 0, 0, 0, 0, 0, 0,  0,  0,  0,  0,
        1, 1, 1, 1, 1, 1, 1, 1, 1,  1,  1,  1,  1,
        2, 2, 2, 2, 2, 2, 2, 2, 2,  2,  2,  2,  2,
        3, 3, 3, 3, 3, 3, 3, 3, 3,  3,  3,  3,  3
    )

    val CARD_SUIT_NAME: Array<String> = arrayOf("clubs", "diamonds", "hearts", "spades")

    val CARD_RANK_NAME: Array<String> = arrayOf("ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king")

    fun getCardDisplayedName(cardIndex: Int): String {
        val sb: StringBuilder = StringBuilder()

        sb.append(CARD_RANK_NAME.get(cardIndex % 13))
        sb.append(" of ")
        sb.append(CARD_SUIT_NAME.get(CARD_SUIT.get(cardIndex)))

        return sb.toString()
    }

    fun isCardJack(cardIndex: Int): Boolean {
        return CARD_JACK.contains(cardIndex)
    }

    fun getCardRank(cardIndex: Int): Int {
        return CARD_RANK.get(cardIndex)
    }

    fun getCardValue(cardIndex: Int): Int {
        return CARD_VALUE.get(cardIndex)
    }

    fun getCardSuit(cardIndex: Int): Int {
        return CARD_SUIT.get(cardIndex)
    }

    fun getShuffledDeck(): ByteArray {
        val initialCardValue = AtomicInteger(0)
        val deck = ByteArray(52)
        for (i in 0..51) {
            deck.set(i, initialCardValue.getAndIncrement().toByte())
        }

        val random = Random()
        for (i in 0..51) {
            val r: Int = i + random.nextInt(52 - i)

            val temp: Byte = deck.get(r)
            deck.set(r, deck.get(i))
            deck.set(i, temp)
        }

        return deck
    }

    fun main() {
        println(getCardDisplayedName(0) + " is a jack: " + isCardJack(0))
        println(getCardDisplayedName(10) + " is a jack: " + isCardJack(10))

        println(getCardDisplayedName(0) + " has a rank of: " + getCardRank(0))
        println(getCardDisplayedName(10) + " has a rank of: " + getCardRank(10))
        println(getCardDisplayedName(12) + " has a rank of: " + getCardRank(12))

        println(getCardDisplayedName(0) + " has a value of: " + getCardValue(0))
        println(getCardDisplayedName(10) + " has a value of: " + getCardValue(10))
        println(getCardDisplayedName(12) + " has a value of: " + getCardValue(12))

        println(getCardDisplayedName(0) + " is a suit of: " + getCardSuit(0))
        println(getCardDisplayedName(14) + " is a suit of: " + getCardSuit(14))
        println(getCardDisplayedName(28) + " is a suit of: " + getCardSuit(28))
        println(getCardDisplayedName(42) + " is a suit of: " + getCardSuit(42))

        println("Shuffled deck: " + getShuffledDeck())
    }
}

fun main() {
    println("Shuffled deck: ")
    val deck: ByteArray = Deck.getShuffledDeck()
    for (c in deck) {
        print(Deck.getCardDisplayedName(c.toInt()) + ", ")
    }


}