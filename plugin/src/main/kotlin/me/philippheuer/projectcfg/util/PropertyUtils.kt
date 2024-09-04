package me.philippheuer.projectcfg.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import java.io.File
import java.util.Properties

class PropertyUtils {
    companion object {
        val yamlObjectMapper: ObjectMapper = YAMLMapper()

        /**
         * Process .properties resource file with the given default properties
         */
        fun processProperties(file: File, defaultProperties: Map<String, String>, overwrite: Boolean) {
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }

            val prop = Properties()
            if (file.isFile) {
                prop.load(file.bufferedReader())
            }
            if (overwrite) {
                defaultProperties.forEach { (key, value) -> prop.setProperty(key, value) }
            } else {
                defaultProperties.forEach { (key, value) -> prop.putIfAbsent(key, value) }
            }

            prop.store(file.bufferedWriter(), null)
        }

        /**
         * Process .yaml resource file with the given default properties
         */
        fun processYamlProperties(file: File, defaultProperties: Map<String, String>, overwrite: Boolean) {
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }

            val rootNode = yamlObjectMapper.readValue(file, JsonNode::class.java) as ObjectNode
            defaultProperties.forEach { (key, value) -> setJsonNodeValue(rootNode, key, value, overwrite) }

            var yamlString = yamlObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode)
            if (yamlString.startsWith("---")) {
                yamlString = yamlString.substringAfter("\n")
            }
            file.writeText(yamlString)
        }

        private fun setJsonNodeValue(rootNode: ObjectNode, path: String, value: String, overwrite: Boolean) {
            val keys = path.split(".")

            // navigate to the correct node (create if not exists)
            var currentNode: ObjectNode = rootNode
            for (i in 0 until keys.size - 1) {
                currentNode = currentNode.withObject(keys[i])
            }
            val finalKey = keys.last()

            // set value, if not exists or overwrite is enabled
            if (overwrite || !currentNode.has(finalKey)) {
                currentNode.put(finalKey, value)
            }
        }
    }
}
