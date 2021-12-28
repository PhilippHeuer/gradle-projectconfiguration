package me.philippheuer.projectcfg.config

import org.gradle.api.provider.Property

interface CheckstyleConfig {

    // checkstyle version
    val checkstyleToolVersion: Property<String>

    // name of the ruleset to use
    val checkstyleRuleSet: Property<String>

}