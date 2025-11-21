package com.example.flexibletimer.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "alerts",
    foreignKeys = [
        ForeignKey(
            entity = Timer::class,
            parentColumns = ["id"],
            childColumns = ["timer_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Alert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "timer_id", index = true)
    val timerId: Long,

    @ColumnInfo(name = "offset_ms")
    val offsetMs: Long,

    @ColumnInfo(name = "sound_uri")
    val soundUri: String?
)
