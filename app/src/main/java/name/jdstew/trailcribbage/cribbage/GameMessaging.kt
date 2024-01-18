package name.jdstew.trailcribbage.cribbage

// listing of game states - first digit is 'phase', second digit is 'step'
const val GAME_START: Byte = 0
const val SELECT_OPPONENT: Byte = 10
const val CUT_START: Byte = 20 // deck shuffled and laid out, no card selected
const val CUT_MY_CUT: Byte = 21
const val CUT_OPPONENT_CUT: Byte = 22
const val DEAL_START: Byte = 30 // deck shuffled, delt, 6 cards up and 6 down
const val DEAL_PONE_COMPLETE: Byte = 31 // a player selected 2 cards for crib
const val DEAL_DEALER_COMPLETE: Byte = 32 // both players selected 2 cards for crib
const val DEAL_STARTER_CUT: Byte = 33 // Pone selected cut, next card shown up
const val DEAL_STARTER_SELECTED: Byte = 34
const val DEAL_STARTER_REVEALED: Byte = 35
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
	Next	From	|val1	|val2	|val3	|val4	|val5	|val6	|val7
byte0				|byte1	|byte2	|byte3	|byte4	|byte5	|byte6	|byte7
0	10		either	|0		|0		|0		|0		|0		|0		|0
  \ GAME_START - display splash screen
   \ navigate to SplashView from MainActivity.onCreate, and GameModel.processFinished

10	20		either	|0		|0		|0		|0		|0		|0		|0
  \ SELECT_OPPONENT - display list of opponents to connect with,
    and confirm that Trail Cribbage is available

	  Note: BLE advertising doesn't start until game state is 10 (SELECT_OPPONENT)

20	21|22	either	|0		|0		|0		|0		|0		|0		|0
  \ CUT_START - display cut screen
   \ navigate to CutView from GameModel.processCut

21	22|20|30 me 	|oc		|0		|0		|0		|0		|0		|0
  \ CUT_MY_CUT - display my selected card

	Note: they type 21 message is shared internally as a type 21 message,
	but is sent externally as a type 22 message

22	21|20|30 oppo	|oc		|0		|0		|0		|0		|0		|0
  \ CUT_OPPONENT_CUT - display both selected cards with next step indicated

30	31|32	dealer	|player|card1	|card2	|card3	|card4	|card5	|card6
  \ DEAL_START - display 6 delt cards
   \ navigate to DealView from GameModel.processCut

31	32		pone	|crib0	|crib1	|0		|0		|0		|0		|0
  \ DEAL_PONE_COMPLETE - display 2 opponent's crib cards (face down)

32	33		dealer	|crib2	|crib3	|0		|0		|0		|0		|0
  \ DEAL_DEALER_COMPLETE - display 2 dealer's crib cards (face down)

33	34		dealer	|0		|0		|0		|0		|0		|0		|0
  \ DEAL_STARTER_CUT - display second screen for selected starter card
   \ navigate to DealStarterCutView from GameModel.processDeal

34	35		pone	cI		|0		|0		|0		|0		|0		|0
  \ DEAL_STARTER_SELECTED - communicate starter index

35	40		dealer	cC		|0		|0		|0		|0		|dcp	|pcp
  \ DEAL_STARTER_REVEALED - reveal selected starter card

40	41		dealer	|0		|0		|0		|0		|0		|0		|0
  \ PLAY_START
   \ navigate to PlayView from GameModel.processDealStarter

41	42		pone	|sC		|sI		|eI		|0		|0		|0		|0
  \ PLAY_CARD_1

42	43		dealer	|sC		|sI		|eI		|0		|0		|dcp	|pcp
  \ PLAY_CARD_2

43	44|49	pone	|sC		|sI		|eI		|0		|0		|dcp	|pcp
  \ PLAY_CARD_3

44	45|49	either	|sC		|sI		|eI		|0		|0		|dcp	|pcp
  \ PLAY_CARD_4

45	46|49	either	|sC		|sI		|eI		|0		|0		|dcp	|pcp
  \ PLAY_CARD_5

46	47|49	either	|sC		|sI		|eI		|0		|0		|dcp	|pcp
  \ PLAY_CARD_6

47	48|49	either	|sC		|sI		|eI		|0		|0		|dcp	|pcp
  \ PLAY_CARD_7

48	50		either	|sC		|sI		|eI		|0		|0		|dcp	|pcp
  \ PLAY_CARD_8

49	44|45|47|48	either	|gC		|0		|0		|0		|0	|dcp	|pcp
  \ PLAY_GO

50	51		pone	hand1	hand2	hand3	hand4	|start	|dcp	|pcp
  \ SHOW_PONE_HAND
   \ navigate to ScoreView from GameModel.processScore

51	52		dealer	hand1	hand2	hand3	hand4	|start	|dcp	|pcp
  \ SHOW_DEALER_HAND

52	60		dealer	crib1	crib2	card3	card4	|start	|dcp	|pcp
  \ SHOW_DEALER_CRIB

60	30		pone	|0		|0		|0		|0		|0		|dcp	|pcp
  \ COMPLETION
   \ navigate to RoundFinishedView from GameModel.processPlay

70	20		either	|0		|0		|0		|0		|0		|0		|0
	\ FINISHED
	 \ navigate to GameFinishedView from GameModel.processDealStarter, .processPlay, and .processShow

oc - own card | opponent's card
cI = cut index
cC = cut card
sC = selected card
sI = starting index
eI = ending index
gC = go count
dcp = dealer's cumulative points
pcp = op[pone]nt's cumulative points
 */

object GameMessaging {

    private val gameSequence = mapOf(
        GAME_START to setOf(SELECT_OPPONENT),
        SELECT_OPPONENT to setOf(CUT_START),
        CUT_START to setOf(CUT_MY_CUT, CUT_OPPONENT_CUT),
        CUT_MY_CUT to setOf(CUT_START, CUT_OPPONENT_CUT, DEAL_START),
        CUT_OPPONENT_CUT to setOf(CUT_START, CUT_MY_CUT, DEAL_START), // if both cut cards match value
        DEAL_START to setOf(DEAL_PONE_COMPLETE, DEAL_DEALER_COMPLETE),
        DEAL_PONE_COMPLETE to setOf(DEAL_DEALER_COMPLETE, DEAL_STARTER_CUT), // need to check for both
        DEAL_DEALER_COMPLETE to setOf(DEAL_PONE_COMPLETE, DEAL_STARTER_CUT), // need to check for both
        DEAL_STARTER_CUT to setOf(DEAL_STARTER_SELECTED),
        DEAL_STARTER_SELECTED to setOf(DEAL_STARTER_REVEALED),
        DEAL_STARTER_REVEALED to setOf(PLAY_START),
        PLAY_START to setOf(PLAY_CARD_1),
        PLAY_CARD_1 to setOf(PLAY_CARD_2),
        PLAY_CARD_2 to setOf(PLAY_CARD_3),
        PLAY_CARD_3 to setOf(PLAY_CARD_4, PLAY_GO),
        PLAY_CARD_4 to setOf(PLAY_CARD_5, PLAY_GO),
        PLAY_CARD_5 to setOf(PLAY_CARD_6, PLAY_GO),
        PLAY_CARD_6 to setOf(PLAY_CARD_7, PLAY_GO),
        PLAY_CARD_7 to setOf(PLAY_CARD_8, PLAY_GO),
        PLAY_CARD_8 to setOf(SHOW_PONE_HAND),
        PLAY_GO to setOf(PLAY_CARD_4, PLAY_CARD_5, PLAY_CARD_6, PLAY_CARD_7, PLAY_CARD_8, PLAY_GO, SHOW_PONE_HAND),
        SHOW_PONE_HAND to setOf(SHOW_DEALER_HAND),
        SHOW_DEALER_HAND to setOf(SHOW_DEALER_CRIB),
        SHOW_DEALER_CRIB to setOf(COMPLETION),
        COMPLETION to setOf(DEAL_START, FINISHED),
        FINISHED to setOf(GAME_START)
    )

    private val stateMap = mapOf(
        GAME_START to "GAME_START",
                SELECT_OPPONENT to "SELECT_OPPONENT",
                CUT_START to "CUT_START",
                CUT_MY_CUT to "CUT_MY_CUT",
                CUT_OPPONENT_CUT to "CUT_OPPONENT_CUT",
                DEAL_START to "DEAL_START",
                DEAL_PONE_COMPLETE to "DEAL_PONE_COMPLETE",
                DEAL_DEALER_COMPLETE to "DEAL_DEALER_COMPLETE",
                DEAL_STARTER_CUT to "DEAL_STARTER_CUT",
                DEAL_STARTER_SELECTED to "DEAL_STARTER_SELECTED",
                DEAL_STARTER_REVEALED to "DEAL_STARTER_REVEALED",
                PLAY_START to "PLAY_START",
                PLAY_CARD_1 to "PLAY_CARD_1",
                PLAY_CARD_2 to "PLAY_CARD_2",
                PLAY_CARD_3 to "PLAY_CARD_3",
                PLAY_CARD_4 to "PLAY_CARD_4",
                PLAY_CARD_5 to "PLAY_CARD_5",
                PLAY_CARD_6 to "PLAY_CARD_6",
                PLAY_CARD_7 to "PLAY_CARD_7",
                PLAY_CARD_8 to "PLAY_CARD_8",
                PLAY_GO to "PLAY_GO",
                SHOW_PONE_HAND to "SHOW_PONE_HAND",
                SHOW_DEALER_HAND to "SHOW_DEALER_HAND",
                SHOW_DEALER_CRIB to "SHOW_DEALER_CRIB",
                COMPLETION to "COMPLETION",
                FINISHED to "FINISHED"
    )

    fun isMessageLogical(lastMessage: ByteArray, message: ByteArray): Boolean {
        if (!gameSequence.containsKey(lastMessage[0])) false
        val nextPossibleStates = gameSequence[lastMessage[0]]
        if (nextPossibleStates!!.contains(message[0])) true
        return false
    }

    fun getStateString(stateValue: Byte): String? {
        return stateMap[stateValue]
    }

    fun getBlankMessage(): ByteArray {
        return ByteArray(8)
    }

    fun getCutSelectedMessage(cardIndex: Int): ByteArray {
        return byteArrayOf(CUT_MY_CUT, cardIndex.toByte(), 0, 0, 0, 0, 0, 0)
    }
}