package me.philippheuer.projectcfg.lib.springbootproxy

import me.philippheuer.projectcfg.lib.springbootproxy.util.ConfigureProxy
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
open class ProxyAutoConfiguration {
    init {
        ConfigureProxy().configureProxy()
    }
}