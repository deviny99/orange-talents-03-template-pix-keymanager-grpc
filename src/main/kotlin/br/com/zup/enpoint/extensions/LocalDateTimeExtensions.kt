package br.com.zup.enpoint.extensions

import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toGrpcTimestamp(): Timestamp {
    val instant = this.atZone(ZoneId.of("UTC")).toInstant()
    return Timestamp.newBuilder()
        .setSeconds(instant.epochSecond)
        .setNanos(instant.nano)
        .build()
}