package me.philippheuer.projectcfg.lib.springbootproxy

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import java.net.URI
import java.net.URISyntaxException
import javax.annotation.PostConstruct
import kotlin.system.exitProcess

/**
 * This will configure the http proxy as any other tool would by respecting the HTTP_PROXY / HTTPS_PROXY env vars
 */
@Configuration
open class ProxyAutoConfiguration {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun postConstruct() = configureProxy()

    fun configureProxy() {
        // no_proxy
        var noProxy = getEnvProperty("NO_PROXY").orEmpty()
        if (noProxy.isEmpty()) {
            noProxy = "localhost|127.0.0.1|*.local|.local"
        }

        // http_proxy
        val httpProxy = getEnvProperty("HTTP_PROXY")
        if (httpProxy != null && httpProxy.isNotBlank()) {
            try {
                val proxyUri = URI(httpProxy)
                setProxyProperty("http.proxyHost", proxyUri.host)
                setProxyProperty("http.proxyPort", proxyUri.port.toString())
                setProxyProperty("http.nonProxyHosts", noProxy.replace(",", "|"))
            } catch (ex: URISyntaxException) {
                log.error("failed to configure proxy - $httpProxy is not valid (expected format: http://1.2.3.4:9000)")
                exitProcess(1)
            }
        }

        // https_proxy
        val httpsProxy = getEnvProperty("HTTPS_PROXY")
        if (httpsProxy != null && httpsProxy.isNotBlank()) {
            try {
                val proxyUri = URI(httpsProxy)
                setProxyProperty("https.proxyHost", proxyUri.host)
                setProxyProperty("https.proxyPort", proxyUri.port.toString())
                setProxyProperty("https.nonProxyHosts", noProxy.replace(",", "|"))
            } catch (ex: URISyntaxException) {
                log.error("failed to configure proxy - $httpsProxy is not valid (expected format: http://1.2.3.4:9000)")
                exitProcess(1)
            }
        }
    }

    fun setProxyProperty(key: String, value: String) {
        try {
            System.setProperty(key, value)
        } catch (ex: SecurityException) {
            log.error("failed to configure proxy - a security manager exists and its checkPermission method doesn't allow setting of $key")
            exitProcess(1)
        }
    }

    fun getEnvProperty(key: String): String? {
        return System.getenv(key)
    }
}