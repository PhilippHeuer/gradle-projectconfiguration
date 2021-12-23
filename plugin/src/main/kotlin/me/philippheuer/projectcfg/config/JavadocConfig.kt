package me.philippheuer.projectcfg.config

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

interface JavadocConfig {

    // javadoc locale
    val javadocLocale: Property<String>

    // javadoc encoding
    val javadocEncoding: Property<String>

    // automatically link javadocs from other dependencies via javadoc.io
    val javadocAutoLinking: Property<Boolean>

    // javadoc links
    val javadocLinks: ListProperty<String>

    // Mapping table, key = package, value = group name
    val javadocGroups: MapProperty<String, String>

    // javadocs should integrate lombok generated code?
    val javadocLombok: Property<Boolean>

    // custom overview html template
    val javadocOverviewTemplate: Property<String>

    // custom overview html template for aggregated javadocs
    val javadocOverviewAggregateTemplate: Property<String>

    /**
     * linting options
     *
     * accessibility – checks for the issues to be detected by an accessibility checker (for example, no caption or summary attributes specified in a table tag)
     * html – detects high-level HTML issues, like putting block elements inside inline elements or not closing elements that require an end tag
     * missing – checks for missing Javadoc comments or tags (for example, a missing comment or class, or a missing @return tag or similar tag on a method)
     * reference – checks for issues relating to the references to Java API elements from Javadoc tags (for example, item not found in @see, or a bad name after @param)
     * syntax – checks for low-level issues like unescaped angle brackets (< and >) and ampersands (&) and invalid Javadoc tags
     */
    val javadocLint: ListProperty<String>

}