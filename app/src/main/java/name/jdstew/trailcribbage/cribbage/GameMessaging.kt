package name.jdstew.trailcribbage.cribbage

// listing of game states - first digit is 'phase', second digit is 'step'
const val GAME_START: Byte = 10
const val CUT_START: Byte = 20 // deck shuffled and laid out, no card selected
const val CUT_MY_CUT: Byte = 21
const val CUT_OPPONENT_CUT: Byte = 22
const val DEAL_START: Byte = 30 // deck shuffled, delt, 6 cards up and 6 down
const val DEAL_PONE_COMPLETE: Byte = 31 // a player selected 2 cards for crib
const val DEAL_DEALER_COMPLETE: Byte = 32 // both players selected 2 cards for crib
const val DEAL_STARTER_CUT: Byte = 33 // Pone selected cut, next card shown up
const val DEAL_STARTER_SELECTED: Byte = 34
const val PLAY_START: Byte = 40 // thru 48 for each card played
const val PLAY_CARD_1: Byte = 41
const val PLAY_CARD_2: Byte = 42
const val PLAY_CARD_3: Byte = 43
const val PLAY_CARD_4: Byte = 44
const val PLAY_CARD_5: Byte = 45
const val PLAY_CARD_6: Byte = 46
const val PLAY_CARD_7: Byte = 47
const val PLAY_CARD_8: Byte = 48
const val PLAY_GO: Byte = 49
const val SHOW_PONE_HAND: Byte = 50
const val SHOW_DEALER_HAND: Byte = 51
const val SHOW_DEALER_CRIB: Byte = 52
const val COMPLETION: Byte = 60
const val FINISHED: Byte = 70

/*
State
	Next	From		|val1	|val2	|val3	|val4	|val5	|val6	|val7
byte0					|byte1	|byte2	|byte3	|byte4	|byte5	|byte6	|byte7
10	20			either	|0		|0		|0		|0		|0		|0		|0
  \display splash screen
20	21			either*	|0		|0		|0		|0		|0		|0		|0
  \display cut screen
21	22			both	|oc		|0		|0		|0		|0		|0		|0
  \display opponent's selected card
	22					|oc		|0		|0		|0		|0		|0		|0
22	20|30		both	|20|30	|0		|0		|0		|0		|0		|0
  \display both selected cards with next step indicated
30	31|32		dealer	|card1	|card2	|card3	|card4	|card5	|card6	|0
  \display 6 delt cards
31	32			pone	|crib1	|crib2	|0		|0		|0		|0		|0
  \display 2 opponent's crib cards (face down)
32	33			dealer	|0		|0		|0		|0		|0		|0		|0
  \display 2 dealer's crib cards (face down)
33	34			pone	cI		|0		|0		|0		|0		|0		|0
  \display second screen for selected starter card
34	40			dealer	cC		|0		|0		|0		|0		|dcp	|pcp
  \display starter card
40	41			dealer	|0		|0		|0		|0		|0		|0		|0
  \
41	42			pone	|sc		|sI		|eI		|0		|0		|0		|0
  \
42	43			dealer	|sc		|sI		|eI		|0		|0		|dcp	|pcp
  \
43	44|49		pone	|sc		|sI		|eI		|0		|0		|dcp	|pcp
  \
44	45|49		either	|sc		|sI		|eI		|0		|0		|dcp	|pcp
  \
45	46|49		either	sc		sI		eI		|0		|0		|dcp		|pcp
  \
46	47|49		either	sc		sI		eI		|0		|0		|dcp		|pcp
  \
47	48|49		either	sc		sI		eI		|0		|0		|dcp		|pcp
  \
48	50			either	sc		sI		eI		|0		|0		|dcp		|pcp
  \
49	44|45|47|48	either	|0		|0		|0		|0		|0		|dcp		|pcp
  \
50	51			pone	hand1	hand2	hand3	hand4	|0		|dcp		|pcp
  \
51	52			dealer	hand1	hand2	hand3	hand4	|0		|dcp		|pcp
  \
52	60			dealer	crib1	crib2	card3	card4	|0		|dcp		|pcp
  \
60	30			pone	|0		|0		|0		|0		|0		|dcp		|pcp
  \
70	20			either	|0		|0		|0		|0		|0		|0		|0
  \

* - sync message
oc - own card / opponent's card
cI = cut index
cC = cut card
sc = selected card
sI = starting index
eI = ending index
dcp = dealer's cumulative points
pcp = op[pone]nt's cumulative points
 */

object GameMessaging {

    val gameSequence = mapOf(
        GAME_START to setOf(CUT_START),
        CUT_START to setOf(CUT_MY_CUT, CUT_OPPONENT_CUT),
        CUT_MY_CUT to setOf(CUT_START, CUT_OPPONENT_CUT, DEAL_START),
        CUT_OPPONENT_CUT to setOf(CUT_START, CUT_MY_CUT, DEAL_START), // if both cut cards match value
        DEAL_START to setOf(DEAL_PONE_COMPLETE, DEAL_DEALER_COMPLETE),
        DEAL_PONE_COMPLETE to setOf(DEAL_DEALER_COMPLETE, DEAL_STARTER_CUT), // need to check for both
        DEAL_DEALER_COMPLETE to setOf(DEAL_PONE_COMPLETE, DEAL_STARTER_CUT), // need to check for both
        DEAL_STARTER_CUT to setOf(DEAL_STARTER_SELECTED),
        DEAL_STARTER_SELECTED to setOf(PLAY_START),
        PLAY_START to setOf(PLAY_CARD_1),
        PLAY_CARD_1 to setOf(PLAY_CARD_2),
        PLAY_CARD_2 to setOf(PLAY_CARD_3),
        PLAY_CARD_3 to setOf(PLAY_CARD_4, PLAY_GO),
        PLAY_CARD_4 to setOf(PLAY_CARD_5, PLAY_GO),
        PLAY_CARD_5 to setOf(PLAY_CARD_6, PLAY_GO),
        PLAY_CARD_6 to setOf(PLAY_CARD_7, PLAY_GO),
        PLAY_CARD_7 to setOf(PLAY_CARD_8, PLAY_GO),
        PLAY_CARD_8 to setOf(SHOW_PONE_HAND),
        PLAY_GO to setOf(PLAY_CARD_4, PLAY_CARD_5, PLAY_CARD_6, PLAY_CARD_7, PLAY_CARD_8, SHOW_PONE_HAND),
        SHOW_PONE_HAND to setOf(SHOW_DEALER_HAND),
        SHOW_DEALER_HAND to setOf(SHOW_DEALER_CRIB),
        SHOW_DEALER_CRIB to setOf(COMPLETION),
        COMPLETION to setOf(DEAL_START, FINISHED),
        FINISHED to setOf(GAME_START)
    )

    fun isMessageLogical(currentState: Byte, message: ByteArray): Boolean {
        if (!gameSequence.containsKey(currentState)) false
        val nextPossibleStates = gameSequence[currentState]
        if (nextPossibleStates!!.contains(message[0])) true
        return false
    }

    fun getBlankMessage(): ByteArray {
        return ByteArray(8)
    }

    fun getCutSelectedMessage(cardIndex: Int): ByteArray {
        return byteArrayOf(CUT_MY_CUT, cardIndex.toByte(), 0, 0, 0, 0, 0, 0)
    }
}