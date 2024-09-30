package com.noom.interview.fullstack.sleep.model

import com.noom.interview.fullstack.sleep.dto.SleepLogRequestDTO
import com.noom.interview.fullstack.sleep.enums.MorningFeelingEnum
import com.noom.interview.fullstack.sleep.utils.DateUtils
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "sleep_log")
data class SleepLog (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "sleep_date", nullable = false)
    val sleepDate: LocalDate = LocalDate.now(),

    @Column(name = "time_in_bed_start", nullable = false)
    val timeInBedStart: LocalTime?,

    @Column(name = "time_in_bed_end", nullable = false)
    val timeInBedEnd: LocalTime?,

    @Column(name = "total_time_in_bed", nullable = false)
    val totalTimeInBed: Long,

    @Column(name = "morning_feeling", nullable = false)
    @Enumerated(EnumType.STRING)
    val morningFeeling: MorningFeelingEnum?

) {
    companion object {
        fun fromDTO(sleepLogRequestDTO: SleepLogRequestDTO, user: User): SleepLog {
            return SleepLog(
                user = user,
                timeInBedStart = sleepLogRequestDTO.timeInBedStart,
                timeInBedEnd = sleepLogRequestDTO.timeInBedEnd,
                totalTimeInBed = DateUtils.getDurationBetweenTimes(sleepLogRequestDTO.timeInBedStart, sleepLogRequestDTO.timeInBedEnd).toMinutes(),
                morningFeeling = sleepLogRequestDTO.morningFeeling
            )
        }
    }
}