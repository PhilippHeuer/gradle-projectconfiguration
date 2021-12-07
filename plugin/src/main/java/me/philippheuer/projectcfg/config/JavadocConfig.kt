package me.philippheuer.projectcfg.config

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

interface JavadocConfig {

    // JavadocFeature: javadoc locale
    val javadocLocale: Property<String>

    // JavadocFeature: javadoc links
    val javadocLinks: ListProperty<String>

    // JavadocFeature: Mapping table, key = package, value = group name
    val javadocGroups: MapProperty<String, String>

    // JavadocFeature: javadocs should integrate lombok generated code?
    val javadocLombok: Property<Boolean>

    // JavadocFeature: custom overview html template
    val javadocOverviewTemplate: Property<String>

    // JavadocFeature: custom overview html template for aggregated javadocs
    val javadocOverviewAggregateTemplate: Property<String>

}