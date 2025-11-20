package com.coursework.jsontool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SchemaExportService {

    private final ObjectMapper objectMapper;

    public SchemaExportService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String generateMarkdownTable(String schemaContent) {
        StringBuilder markdown = new StringBuilder();

        try {
            JsonNode schemaNode = objectMapper.readTree(schemaContent);
            JsonNode properties = schemaNode.get("properties");

            if (properties == null || !properties.isObject()) {
                return "## Schema Structure\n\nNo properties found to export.";
            }

            JsonNode requiredNode = schemaNode.get("required");
            List<String> requiredFields = requiredNode != null && requiredNode.isArray()
                    ? StreamSupport.stream(requiredNode.spliterator(), false)
                    .filter(JsonNode::isTextual)
                    .map(JsonNode::asText)
                    .collect(Collectors.toList())
                    : java.util.Collections.emptyList();

            markdown.append("## Schema Structure\n\n");
            markdown.append("| Property | Type | Required | Description |\n");
            markdown.append("|:---|:---|:---:|:---|\n");

            properties.fields().forEachRemaining(entry -> {
                String name = entry.getKey();
                JsonNode prop = entry.getValue();

                String type = prop.has("type") ? prop.get("type").asText() : "N/A";
                String description = prop.has("description") ? prop.get("description").asText().replaceAll("\n", " ") : "—";

                String required = requiredFields.contains(name) ? "Yes" : "No";

                markdown.append(String.format("| %s | %s | %s | %s |\n", name, type, required, description));
            });

        } catch (Exception e) {
            markdown.append("## Export Error\n\nFailed to parse JSON Schema: " + e.getMessage());
        }

        return markdown.toString();
    }
}