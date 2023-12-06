package name.jdstew.trailcribbage.cribbage

class ScoringReport {
    val announcements: MutableList<String> = mutableListOf()
    var score: Int = 0;

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("+$score points")
        announcements.forEach{
            sb.append(it)
        }

        return sb.toString()
    }
}