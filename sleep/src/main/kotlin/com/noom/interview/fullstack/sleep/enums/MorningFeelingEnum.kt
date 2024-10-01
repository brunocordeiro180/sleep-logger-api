package com.noom.interview.fullstack.sleep.enums;

import com.noom.interview.fullstack.sleep.exception.SleepLogException

enum class MorningFeelingEnum(val value : String) {
    BAD("Bad"),
    OK("Ok"),
    GOOD("Good");

    companion object {
        fun fromValue(value: String): MorningFeelingEnum {
            return values().find { it.value.equals(value, ignoreCase = true) }
                ?: throw SleepLogException("Invalid morning feeling value: '$value'. Allowed values are: ${values().joinToString { it.name }}")
        }
    }
}
