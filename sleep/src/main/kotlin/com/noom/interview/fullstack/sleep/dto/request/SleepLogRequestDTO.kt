package com.noom.interview.fullstack.sleep.dto.request

import java.time.LocalDateTime

data class SleepLogRequestDTO (

    val timeInBedStart: LocalDateTime,

    val timeInBedEnd: LocalDateTime,

    val morningFeeling : String

)