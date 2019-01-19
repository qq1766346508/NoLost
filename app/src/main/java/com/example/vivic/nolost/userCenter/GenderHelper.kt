package com.example.vivic.nolost.userCenter

object GenderHelper {

    const val MAN = "MAN"
    const val FEMALE = "FEMALE"
    const val SECRET = "SECRET"

    fun formatGender(gender: String): String {
        return when (gender) {
            "m", "man" -> MAN
            "f", "female" -> FEMALE
            else -> {
                SECRET
            }
        }
    }
}