package mikhail.shell.video.hosting.data.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import mikhail.shell.video.hosting.data.converters.EnumConverterFactory
import mikhail.shell.video.hosting.domain.models.AuthModel
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import kotlin.time.Clock
import kotlin.time.Instant

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AuthApiTests {
    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)
    lateinit var server: MockWebServer
    lateinit var client: OkHttpClient
    lateinit var gson: Gson
    lateinit var enumConverterFactory: EnumConverterFactory
    lateinit var scalarsConverterFactory: ScalarsConverterFactory
    lateinit var gsonConverterFactory: GsonConverterFactory
    lateinit var retrofit: Retrofit
    lateinit var api: AuthApi
    @Before
    fun init() {
        val certificate = HeldCertificate.Builder()
            .addSubjectAlternativeName("localhost")
            .build()

        server = MockWebServer()
        val serverCertificates = HandshakeCertificates.Builder()
            .heldCertificate(certificate)
            .build()
        server.useHttps(serverCertificates.sslSocketFactory())
        server.start()

        val clientCertificates = HandshakeCertificates.Builder()
            .addTrustedCertificate(certificate.certificate)
            .build()
        client = OkHttpClient.Builder()
            .sslSocketFactory(clientCertificates.sslSocketFactory(), clientCertificates.trustManager)
            .build()
        gson = GsonBuilder()
            .registerTypeAdapter(Instant::class.java, object : TypeAdapter<Instant>() {
                override fun write(out: JsonWriter?, value: Instant?) {
                    out?.value(value?.toString())
                }

                override fun read(`in`: JsonReader?): Instant {
                    return `in`?.let { Instant.parse(it.nextString()) }?: Clock.System.now()
                }
            })
            .create()
        enumConverterFactory = EnumConverterFactory(gson)
        scalarsConverterFactory = ScalarsConverterFactory.create()
        gsonConverterFactory = GsonConverterFactory.create(gson)
        retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(client)
            .addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .build()

        api = retrofit.create()
    }
    @Test
    fun signIn_correctCredentials_returnsAuthModel() = runTest {
        val expectedAuthModel = AuthModel(
            token = "some_token",
            userId = 1
        )
        server.enqueue(
            MockResponse(
                code = 200,
                headers = standardJsonHeader,
                body = """
                    {
                        "token": "some_token",
                        "user_id": 1
                    }
                """.trimIndent()
            )
        )
        val actualAuthModel = api.signInWithPassword("some@email.com", "some_password")
        Assert.assertEquals(expectedAuthModel, actualAuthModel)
    }
    @Test
    fun signIn_notCorrectCredentials_throws400() {
        server.enqueue(
            MockResponse(code = 400)
        )
        Assert.assertThrows(HttpException::class.java) {
            runTest {
                try {
                    api.signInWithPassword("not_valid@email.com", "not_valid_password")
                } catch (e: Exception) {
                    if (e is HttpException && e.code() == 400) {
                        throw e
                    }
                }
            }
        }
    }
    @After
    fun clear() {
        server.close()
    }
}