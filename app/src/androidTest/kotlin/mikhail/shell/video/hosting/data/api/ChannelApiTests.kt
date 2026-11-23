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
import mikhail.shell.video.hosting.data.converters.EnumConverterFactory
import mikhail.shell.video.hosting.data.dto.ChannelCreationErrorResponse
import mikhail.shell.video.hosting.data.dto.ChannelDto
import mikhail.shell.video.hosting.data.dto.ChannelEditingErrorResponse
import mikhail.shell.video.hosting.data.dto.ChannelForUserDto
import mikhail.shell.video.hosting.data.dto.EditingActionDto
import mikhail.shell.video.hosting.data.repositories.ChannelCreationRequest
import mikhail.shell.video.hosting.data.repositories.ChannelEditingRequest
import mikhail.shell.video.hosting.data.utils.parseErrorResponse
import mikhail.shell.video.hosting.domain.errors.FileError
import mikhail.shell.video.hosting.domain.errors.TextError
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
import retrofit2.HttpException
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
    lateinit var scalarsConverterFactory: ScalarsConverterFactory
    lateinit var gsonConverterFactory: GsonConverterFactory
    lateinit var enumConverterFactory: EnumConverterFactory
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
    fun createChannel_validRequest_serializesChannelCreationRequestAndReturnsChannelDto() =
        runTest {
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

    @Test
    fun createChannel_notValidRequest_serializesChannelCreationRequestAndReturnsErrorResponse() =
        runTest {
            // TODO: multiply for different test cases? (for different errors)
            val expectedErrorResponse = ChannelCreationErrorResponse(
                titleError = TextError.EXISTS,
                aliasError = TextError.EXISTS,
                descriptionError = TextError.EMPTY,
                headerError = FileError.NOT_VALID,
                logoError = FileError.NOT_VALID
            )
            val creationRequest = ChannelCreationRequest(
                title = "New channel",
                alias = "new_channel",
                description = "New description here."
            )
            var actualErrorResponse: ChannelCreationErrorResponse? = null
            server.enqueue(
                MockResponse(
                    code = 400,
                    headers = standardJsonHeader,
                    body = """
                        {
                            "title_error": "exists",
                            "alias_error": "exists",
                            "description_error": "empty",
                            "header_error": "not_valid",
                            "logo_error": "not_valid"
                        }
                    """.trimIndent()
                )
            )
            try {
                api.createChannel(
                    channel = creationRequest,
                    logo = null, // TODO
                    header = null // TODO
                )
            } catch (e: Exception) {
                if (e !is HttpException || e.code() != 400) {
                    throw e
                }
                actualErrorResponse =
                    gson.parseErrorResponse<ChannelCreationErrorResponse>(e.response()!!)
            }
            Assert.assertEquals(expectedErrorResponse, actualErrorResponse)
        }

    @Test
    fun editChannel_validRequest_serializesEditingRequestAndReturnsChannelDto() = runTest {
        val editingActions = mapOf(
            "keep" to EditingActionDto.KEEP,
            "remove" to EditingActionDto.REMOVE,
            "edit" to EditingActionDto.EDIT
        )
        editingActions.forEach { (string, action) ->
            val channelEditingRequest = ChannelEditingRequest(
                channelId = 1,
                title = "Edited channel",
                alias = "edited_channel",
                description = "Edited description.",
                logoAction = action,
                headerAction = action
            )
            server.enqueue(
                MockResponse(
                    code = 200,
                    headers = standardJsonHeader,
                    body = """
                        {
                            "channel_id": 1,
                            "owner_id": 1,
                            "title": "Edited channel",
                            "alias": "edited_channel",
                            "description": "Edited description.",
                            "subscribers": 100
                        }
                    """.trimIndent()
                )
            )
            val actualEditedChannel = api.editChannel(
                channel = channelEditingRequest,
                logo = null, // TODO
                header = null // TODO
            )
            val recordedRequest = server.takeRequest()
            val actualChannelEditingRequest =
                JsonParser.parseString(recordedRequest.getJsonPart("channel"))
            val expectedChannelEditingRequest = JsonParser.parseString(
                """
                {
                    "channel_id": 1,
                    "title": "Edited channel",
                    "alias": "edited_channel",
                    "description": "Edited description.",
                    "logo_action": "$string",
                    "header_action": "$string"
                }
            """.trimIndent()
            )
            Assert.assertEquals(expectedChannelEditingRequest, actualChannelEditingRequest)
            val expectedEditedChannel = ChannelDto(
                channelId = 1,
                ownerId = 1,
                title = "Edited channel",
                alias = "edited_channel",
                description = "Edited description.",
                subscribers = 100
            )
            Assert.assertEquals(expectedEditedChannel, actualEditedChannel)
        }
    }

    @Test
    fun editChannel_notValidRequest_serializesChannelEditingRequestAndReturnsErrorResponse() =
        runTest {
            // TODO: multiply for different test cases? (for different errors and actions)
            val expectedErrorResponse = ChannelEditingErrorResponse(
                titleError = TextError.EXISTS,
                aliasError = TextError.EXISTS,
                descriptionError = TextError.EMPTY,
                headerError = FileError.NOT_VALID,
                logoError = FileError.NOT_VALID
            )
            val editingRequest = ChannelEditingRequest(
                channelId = 1,
                title = "Edited channel",
                alias = "edited_channel",
                description = "Edited description here.",
                logoAction = EditingActionDto.KEEP,
                headerAction = EditingActionDto.KEEP
            )
            var actualErrorResponse: ChannelEditingErrorResponse? = null
            server.enqueue(
                MockResponse(
                    code = 400,
                    headers = standardJsonHeader,
                    body = """
                        {
                            "title_error": "exists",
                            "alias_error": "exists",
                            "description_error": "empty",
                            "header_error": "not_valid",
                            "logo_error": "not_valid"
                        }
                    """.trimIndent()
                )
            )
            try {
                api.editChannel(
                    channel = editingRequest,
                    logo = null, // TODO
                    header = null // TODO
                )
            } catch (e: Exception) {
                if (e !is HttpException || e.code() != 400) {
                    throw e
                }
                actualErrorResponse = gson.parseErrorResponse<ChannelEditingErrorResponse>(e.response()!!)
            }
            Assert.assertEquals(expectedErrorResponse, actualErrorResponse)
        }

    @Test
    fun getChannelsByOwner_validOwnerId_deserializesChannelDtos() = runTest {
        val expectedChannels = listOf(
            ChannelDto(
                channelId = 1L,
                ownerId = 1,
                title = "Tech News",
                alias = "tech_news",
                description = "Latest news and updates from the technology world.",
                subscribers = 12500L
            ),
            ChannelDto(
                channelId = 2L,
                ownerId = 1,
                title = "Gaming Hub",
                alias = "gaming_hub",
                description = "Gaming news, reviews, and discussions.",
                subscribers = 8700L
            ),
            ChannelDto(
                channelId = 3L,
                ownerId = 1,
                title = "Daily Science",
                alias = "daily_science",
                description = "Discover interesting scientific facts and research.",
                subscribers = 23400L
            ),
            ChannelDto(
                channelId = 4L,
                ownerId = 1,
                title = "Programming Tutorials",
                alias = "code_academy",
                description = "Tutorials about Kotlin, Java, C++, and software development.",
                subscribers = 5600L
            )
        )
        server.enqueue(
            MockResponse(
                code = 200,
                headers = standardJsonHeader,
                body = """
                    [
                      {
                        "channel_id": 1,
                        "owner_id": 1,
                        "title": "Tech News",
                        "alias": "tech_news",
                        "description": "Latest news and updates from the technology world.",
                        "subscribers": 12500
                      },
                      {
                        "channel_id": 2,
                        "owner_id": 1,
                        "title": "Gaming Hub",
                        "alias": "gaming_hub",
                        "description": "Gaming news, reviews, and discussions.",
                        "subscribers": 8700
                      },
                      {
                        "channel_id": 3,
                        "owner_id": 1,
                        "title": "Daily Science",
                        "alias": "daily_science",
                        "description": "Discover interesting scientific facts and research.",
                        "subscribers": 23400
                      },
                      {
                        "channel_id": 4,
                        "owner_id": 1,
                        "title": "Programming Tutorials",
                        "alias": "code_academy",
                        "description": "Tutorials about Kotlin, Java, C++, and software development.",
                        "subscribers": 5600
                      }
                    ]
                """.trimIndent()
            )
        )
        val actualChannels = api.getChannelsByOwner(1, 0, 10)
        Assert.assertEquals(expectedChannels, actualChannels)
    }

    @Test
    fun getSubscriptions_validSubscriberId_deserializesChannelDtos() = runTest {
        val expectedChannels = listOf(
            ChannelDto(
                channelId = 1L,
                ownerId = 1,
                title = "Tech News",
                alias = "tech_news",
                description = "Latest news and updates from the technology world.",
                subscribers = 12500L
            ),
            ChannelDto(
                channelId = 2L,
                ownerId = 1,
                title = "Gaming Hub",
                alias = "gaming_hub",
                description = "Gaming news, reviews, and discussions.",
                subscribers = 8700L
            ),
            ChannelDto(
                channelId = 3L,
                ownerId = 1,
                title = "Daily Science",
                alias = "daily_science",
                description = "Discover interesting scientific facts and research.",
                subscribers = 23400L
            ),
            ChannelDto(
                channelId = 4L,
                ownerId = 1,
                title = "Programming Tutorials",
                alias = "code_academy",
                description = "Tutorials about Kotlin, Java, C++, and software development.",
                subscribers = 5600L
            )
        )
        server.enqueue(
            MockResponse(
                code = 200,
                headers = standardJsonHeader,
                body = """
                    [
                      {
                        "channel_id": 1,
                        "owner_id": 1,
                        "title": "Tech News",
                        "alias": "tech_news",
                        "description": "Latest news and updates from the technology world.",
                        "subscribers": 12500
                      },
                      {
                        "channel_id": 2,
                        "owner_id": 1,
                        "title": "Gaming Hub",
                        "alias": "gaming_hub",
                        "description": "Gaming news, reviews, and discussions.",
                        "subscribers": 8700
                      },
                      {
                        "channel_id": 3,
                        "owner_id": 1,
                        "title": "Daily Science",
                        "alias": "daily_science",
                        "description": "Discover interesting scientific facts and research.",
                        "subscribers": 23400
                      },
                      {
                        "channel_id": 4,
                        "owner_id": 1,
                        "title": "Programming Tutorials",
                        "alias": "code_academy",
                        "description": "Tutorials about Kotlin, Java, C++, and software development.",
                        "subscribers": 5600
                      }
                    ]
                """.trimIndent()
            )
        )
        val actualChannels = api.getSubscriptions(0, 10)
        Assert.assertEquals(expectedChannels, actualChannels)
    }

    @Test
    fun subscribe_validRequest_serializesRequestAndDeserializesChannelForUserDto() = runTest {
        mapOf(
            "subscribed" to Subscription.SUBSCRIBED,
            "not_subscribed" to Subscription.NOT_SUBSCRIBED
        ).forEach { (string, expectedSubscription) ->
            val expectedChannel = ChannelForUserDto(
                channelId = 3,
                ownerId = 1,
                title = "Daily Science",
                alias = "daily_science",
                description = "Discover interesting scientific facts and research.",
                subscribers = 23400,
                subscription = expectedSubscription
            )
            server.enqueue(
                MockResponse(
                    code = 200,
                    headers = standardJsonHeader,
                    body = """
                        {
                            "channel_id": 3,
                            "owner_id": 1,
                            "title": "Daily Science",
                            "alias": "daily_science",
                            "description": "Discover interesting scientific facts and research.",
                            "subscribers": 23400,
                            "subscription": "$string"
                          }
                    """.trimIndent()
                )
            )

            val expectedChannelId = 1L
            val expectedMessagingToken = "some_token"
            val actualChannel =
                api.subscribe(expectedChannelId, expectedSubscription, expectedMessagingToken)

            val recordedRequest = server.takeRequest()

            val actualChannelId = recordedRequest.url.queryParameter("channel_id")?.toLong()
            Assert.assertEquals(expectedChannelId, actualChannelId)
            val actualSubscription = recordedRequest.url.queryParameter("subscription")
            Assert.assertEquals(gson.toJsonTree(expectedSubscription).asString, actualSubscription)
            val actualMessagingToken = recordedRequest.headers["Messaging-Token"]
            Assert.assertNotNull(expectedMessagingToken, actualMessagingToken)

            Assert.assertEquals(expectedChannel, actualChannel)
        }
    }

    @After
    fun clean() {
        server.close()
    }
}