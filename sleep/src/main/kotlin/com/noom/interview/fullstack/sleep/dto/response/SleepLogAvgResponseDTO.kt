package com.noom.interview.fullstack.sleep.dto.response

data class SleepLogAvgResponseDTO (

    var startDate: String?,
    var endDate: String?,
    var avgTimeInBedStart: String?,
    var avgTimeInBedEnd: String?,
    var avgTotalTime: String,
    var morningFeelings: HashMap<String, Int>
)