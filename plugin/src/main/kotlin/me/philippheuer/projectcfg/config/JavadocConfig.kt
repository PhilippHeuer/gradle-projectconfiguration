package me.philippheuer.projectcfg.config

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

interface JavadocConfig {

    // javadoc locale
    val javadocLocale: Property<String>

    // javadoc encoding
    val javadocEncoding: Property<String>

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

}