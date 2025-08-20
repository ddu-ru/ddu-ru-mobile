import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.gildongmu.ddu_ru_mobile.data.datastore.TokenPreferencesSerializer
import com.gildongmu.ddu_ru_mobile.proto.AuthToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

val Context.authTokenStore: DataStore<AuthToken> by
dataStore(
    fileName = "auth_tokens.pb",
    serializer = TokenPreferencesSerializer // Serializer<AuthToken>
)

class TokenDataStore(private val context: Context) {

    val authToken: Flow<AuthToken> = context.authTokenStore.data

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.authTokenStore.updateData { current ->
            current.toBuilder().setAccessToken(accessToken).setRefreshToken(refreshToken).build()
        }
    }

    suspend fun clearTokens() {
        context.authTokenStore.updateData { current ->
            current.toBuilder().clearAccessToken().clearRefreshToken().build()
        }
    }

    suspend fun hasValidTokens(): Boolean {
        val token = context.authTokenStore.data.first()
        return token.accessToken.isNotEmpty() && token.refreshToken.isNotEmpty()
    }
}
