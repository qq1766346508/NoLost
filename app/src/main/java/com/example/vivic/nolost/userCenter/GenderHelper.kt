package com.example.vivic.nolost.userCenter

object GenderHelper {

    const val MAN = "man"
    const val FEMALE = "female"
    const val SECRET = "secret"

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