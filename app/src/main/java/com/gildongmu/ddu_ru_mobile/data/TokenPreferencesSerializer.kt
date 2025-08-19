import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

data class AuthToken(
        val accessToken: String = "",
        val refreshToken: String = "",
)

object AuthTokenSerializer : Serializer<AuthToken> {
    override val defaultValue: AuthToken = AuthToken()

    override suspend fun readFrom(input: InputStream): AuthToken {
        // TODO: AuthToken.parseFrom(input) 사용
        return AuthToken()
    }

    override suspend fun writeTo(t: AuthToken, output: OutputStream) {
        // TODO: t.writeTo(output) 사용
    }
}
