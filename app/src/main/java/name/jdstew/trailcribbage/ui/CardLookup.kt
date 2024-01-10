package name.jdstew.trailcribbage.ui

import name.jdstew.trailcribbage.R

const val DEFAULT_CARD_INDEX = -1
const val FACE_CARD_INDEX = 52

object CardLookup {
    private val cardMap = mapOf(
        DEFAULT_CARD_INDEX to R.drawable.card_hole,
        0 to R.drawable.card_0,
        1 to R.drawable.card_1,
        2 to R.drawable.card_2,
        3 to R.drawable.card_3,
        4 to R.drawable.card_4,
        5 to R.drawable.card_5,
        6 to R.drawable.card_6,
        7 to R.drawable.card_7,
        8 to R.drawable.card_8,
        9 to R.drawable.card_9,
        10 to R.drawable.card_10,
        11 to R.drawable.card_11,
        12 to R.drawable.card_12,
        13 to R.drawable.card_13,
        14 to R.drawable.card_14,
        15 to R.drawable.card_15,
        16 to R.drawable.card_16,
        17 to R.drawable.card_17,
        18 to R.drawable.card_18,
        19 to R.drawable.card_19,
        20 to R.drawable.card_20,
        21 to R.drawable.card_21,
        22 to R.drawable.card_22,
        23 to R.drawable.card_23,
        24 to R.drawable.card_24,
        25 to R.drawable.card_25,
        26 to R.drawable.card_26,
        27 to R.drawable.card_27,
        28 to R.drawable.card_28,
        29 to R.drawable.card_29,
        30 to R.drawable.card_30,
        31 to R.drawable.card_31,
        32 to R.drawable.card_32,
        33 to R.drawable.card_33,
        34 to R.drawable.card_34,
        35 to R.drawable.card_35,
        36 to R.drawable.card_36,
        37 to R.drawable.card_37,
        38 to R.drawable.card_38,
        39 to R.drawable.card_39,
        40 to R.drawable.card_40,
        41 to R.drawable.card_41,
        42 to R.drawable.card_42,
        43 to R.drawable.card_43,
        44 to R.drawable.card_44,
        45 to R.drawable.card_45,
        46 to R.drawable.card_46,
        47 to R.drawable.card_47,
        48 to R.drawable.card_48,
        49 to R.drawable.card_49,
        50 to R.drawable.card_50,
        51 to R.drawable.card_51,
        FACE_CARD_INDEX to R.drawable.face_card
    )

    fun getCardDrawableID(index: Int): Int {
        return cardMap[index]!!
    }

    fun getCardIndex(drawableID: Int): Int {
        cardMap.forEach{
            if (it.value == drawableID) return it.key
        }
        return DEFAULT_CARD_INDEX
    }

}