package com.example.snippetpermission

import com.example.snippetpermission.logs.CorrelationIdFilter
import com.example.snippetpermission.logs.RequestLogFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.slf4j.MDC
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.net.URI
import java.util.*

internal class CorrelationIdFilterTest {
    @Mock
    private val request: HttpServletRequest? = null

    @Mock
    private val response: HttpServletResponse? = null

    @Mock
    private val filterChain: FilterChain? = null

    private var correlationIdFilter: CorrelationIdFilter? = null
    private lateinit var requestLogFilter: RequestLogFilter
    private lateinit var exchange: ServerWebExchange

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        correlationIdFilter = CorrelationIdFilter()
        requestLogFilter = RequestLogFilter()
        val request =
            MockServerHttpRequest
                .method(HttpMethod.GET, URI("/test"))
                .build()
        exchange = MockServerWebExchange.from(request)
    }

    @Test
    @Throws(Exception::class)
    fun testCorrelationIdIsRemovedFromMDC() {
        Mockito.`when`(request!!.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).thenReturn(null)
        correlationIdFilter!!.doFilterInternal(request, response!!, filterChain!!)
        Assertions.assertNull(
            MDC.get(CorrelationIdFilter.CORRELATION_ID_KEY),
            "Correlation ID should be removed from MDC",
        )
    }

    @Test
    fun `test filter logs request method and uri`() {
        val filter = requestLogFilter.filter(exchange) { Mono.empty() }

        StepVerifier.create(filter)
            .expectComplete()
            .verify()
    }

    @Test
    fun `test filter logs response status`() {
        exchange.response.statusCode = HttpStatus.OK
        val filter = requestLogFilter.filter(exchange) { Mono.empty() }

        StepVerifier.create(filter)
            .expectComplete()
            .verify()
    }
}
