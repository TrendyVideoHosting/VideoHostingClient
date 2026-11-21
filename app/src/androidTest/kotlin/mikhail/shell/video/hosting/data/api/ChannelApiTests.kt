package mikhail.shell.video.hosting.data.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import mikhail.shell.video.hosting.data.dto.ChannelDto
import mikhail.shell.video.hosting.data.dto.ChannelForUserDto
import mikhail.shell.video.hosting.data.repositories.ChannelCreationRequest
import mikhail.shell.video.hosting.domain.models.Subscription
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import kotlin.time.Clock
import kotlin.time.Instant

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ChannelApiTests {
    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)
    lateinit var server: MockWebServer
    lateinit var client: OkHttpClient
    lateinit var gson: Gson
    lateinit var gsonConverterFactory: GsonConverterFactory
    lateinit var scalarsConverterFactory: ScalarsConverterFactory
    lateinit var retrofit: Retrofit
    lateinit var api: ChannelApi

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
            .sslSocketFactory(
                clientCertificates.sslSocketFactory(),
                clientCertificates.trustManager
            )
            .build()
        gson = GsonBuilder()
            .registerTypeAdapter(Instant::class.java, object : TypeAdapter<Instant>() {
                override fun write(out: JsonWriter?, value: Instant?) {
                    out?.value(value?.toString())
                }

                override fun read(`in`: JsonReader?): Instant {
                    return `in`?.let { Instant.parse(it.nextString()) } ?: Clock.System.now()
                }
            })
            .create()
        gsonConverterFactory = GsonConverterFactory.create(gson)
        scalarsConverterFactory = ScalarsConverterFactory.create()
        retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(client)
            .addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .build()

        api = retrofit.create()
    }

    @Test
    fun fetchChannelDetails_validChannelId_returnsChannelForUserDto() = runTest {
        val expectedSubscribedTo = ChannelForUserDto(
            channelId = 1,
            ownerId = 1,
            title = "Some channel",
            alias = "some_channel",
            description = "Some description here.",
            subscribers = 100,
            subscription = Subscription.SUBSCRIBED
        )
        server.enqueue(
            MockResponse(
                code = 200,
                headers = standardJsonHeader,
                body = """
                        {
                            "channel_id": 1,
                            "owner_id": 1,
                            "title": "Some channel",
                            "alias": "some_channel",
                            "description": "Some description here.",
                            "subscribers": 100,
                            "subscription": "subscribed"
                        }
                    """.trimIndent()
            )
        )
        val actualSubscribedTo = api.fetchChannelDetails(1)
        Assert.assertEquals(expectedSubscribedTo, actualSubscribedTo)

        val expectedNotSubscribedTo = ChannelForUserDto(
            channelId = 100,
            ownerId = 2,
            title = "Channel number 100",
            alias = null,
            description = null,
            subscribers = 55,
            subscription = Subscription.NOT_SUBSCRIBED
        )
        server.enqueue(
            MockResponse(
                code = 200,
                headers = standardJsonHeader,
                body = """
                        {
                            "channel_id": 100,
                            "owner_id": 2,
                            "title": "Channel number 100",
                            "alias": null,
                            "description": null,
                            "subscribers": 55,
                            "subscription": "not_subscribed"
                        }
                    """.trimIndent()
            )
        )
        val actualNotSubscribedTo = api.fetchChannelDetails(100)
        Assert.assertEquals(expectedNotSubscribedTo, actualNotSubscribedTo)
    }

    @Test
    fun createChannel_validData_serializesChannelCreationRequestAndReturnsChannelDto() = runTest {
        val channelCreationRequest = ChannelCreationRequest(
            title = "New channel",
            alias = "new_channel",
            description = "New description here."
        )
        server.enqueue(
            MockResponse(
                code = 200,
                headers = standardJsonHeader,
                body = """
                    {
                        "channel_id": 100,
                        "owner_id": 2,
                        "title": "New channel",
                        "alias": "new_channel",
                        "description": "New description here.",
                        "subscribers": 0
                    }
                """.trimIndent()
            )
        )
        val actualCreatedChannel = api.createChannel(
            channel = channelCreationRequest,
            logo = null, // TODO
            header = null // TODO
        )
        val request = server.takeRequest()
        val expectedRequestJson = JsonParser.parseString(
            """
            {
                "title": "New channel",
                "alias": "new_channel",
                "description": "New description here."
            }
        """.trimIndent()
        )
        val actualRequestJson = JsonParser.parseString(request.getJsonPart("channel")!!)
        Assert.assertEquals(expectedRequestJson, actualRequestJson)

        val expectedCreatedChannel = ChannelDto(
            channelId = 100,
            ownerId = 2,
            title = "New channel",
            alias = "new_channel",
            description = "New description here.",
            subscribers = 0
        )
        Assert.assertEquals(expectedCreatedChannel, actualCreatedChannel)
    }

    @After
    fun clean() {
        server.close()
    }
}