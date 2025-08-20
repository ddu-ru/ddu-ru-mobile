package com.gildongmu.ddu_ru_mobile.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.gildongmu.ddu_ru_mobile.proto.AuthToken
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object TokenPreferencesSerializer : Serializer<AuthToken> {
    override val defaultValue: AuthToken = AuthToken.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AuthToken {
        try {
            return AuthToken.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", e)
        }
    }

    override suspend fun writeTo(t: AuthToken, output: OutputStream) {
        t.writeTo(output)
    }
}
