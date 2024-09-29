package com.noom.interview.fullstack.sleep.model

import com.noom.interview.fullstack.sleep.dto.SleepLogRequestDTO
import com.noom.interview.fullstack.sleep.enum.MorningFeelingEnum
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
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
    val timeInBedStart: LocalDateTime?,

    @Column(name = "time_in_bed_end", nullable = false)
    val timeInBedEnd: LocalDateTime?,

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
                totalTimeInBed = ChronoUnit.MINUTES.between(
                    sleepLogRequestDTO.timeInBedStart,
                    sleepLogRequestDTO.timeInBedEnd
                ),
                morningFeeling = sleepLogRequestDTO.morningFeeling
            )
        }
    }
}