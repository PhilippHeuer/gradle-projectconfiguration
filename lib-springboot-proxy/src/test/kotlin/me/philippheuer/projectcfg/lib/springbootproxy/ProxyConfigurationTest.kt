package me.philippheuer.projectcfg.lib.springbootproxy

import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class ProxyConfigurationTest {

    @Test
    fun httpProxy() {
        val proxyAutoConfiguration : ProxyAutoConfiguration = mock()
        whenever(proxyAutoConfiguration.getEnvProperty("HTTP_PROXY")).thenReturn("http://myproxyhost:3128")
        whenever(proxyAutoConfiguration.getEnvProperty("NO_PROXY")).thenReturn("example.com,mynetwork.local")
        whenever(proxyAutoConfiguration.configureProxy()).thenCallRealMethod()
        whenever(proxyAutoConfiguration.setProxyProperty(isA(), isA())).thenCallRealMethod()

        proxyAutoConfiguration.configureProxy()
        verify(proxyAutoConfiguration).setProxyProperty("http.proxyHost", "myproxyhost")
        verify(proxyAutoConfiguration).setProxyProperty("http.proxyPort", "3128")
        verify(proxyAutoConfiguration).setProxyProperty("http.nonProxyHosts", "example.com|mynetwork.local")
    }

    @Test
    fun httpsProxy() {
        val proxyAutoConfiguration : ProxyAutoConfiguration = mock()
        whenever(proxyAutoConfiguration.getEnvProperty("HTTPS_PROXY")).thenReturn("http://myproxyhost:3128")
        whenever(proxyAutoConfiguration.getEnvProperty("NO_PROXY")).thenReturn("example.com,mynetwork.local")
        whenever(proxyAutoConfiguration.configureProxy()).thenCallRealMethod()
        whenever(proxyAutoConfiguration.setProxyProperty(isA(), isA())).thenCallRealMethod()

        proxyAutoConfiguration.configureProxy()
        verify(proxyAutoConfiguration).setProxyProperty("https.proxyHost", "myproxyhost")
        verify(proxyAutoConfiguration).setProxyProperty("https.proxyPort", "3128")
        verify(proxyAutoConfiguration).setProxyProperty("https.nonProxyHosts", "example.com|mynetwork.local")
    }

}
