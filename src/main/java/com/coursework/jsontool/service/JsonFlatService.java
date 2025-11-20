package com.coursework.jsontool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

@Service
public class JsonFlatService {

    private final ObjectMapper objectMapper;

    public JsonFlatService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String flattenJson(String jsonContent) {
        try {
            JsonNode root = objectMapper.readTree(jsonContent);
            ObjectNode flatNode = objectMapper.createObjectNode();

            flattenRecursive(root, "", flatNode);

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(flatNode);
        } catch (Exception e) {
            return "Error parsing JSON: " + e.getMessage();
        }
    }

    private void flattenRecursive(JsonNode node, String prefix, ObjectNode flatNode) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String newPrefix = prefix.isEmpty() ? field.getKey() : prefix + "." + field.getKey();
                flattenRecursive(field.getValue(), newPrefix, flatNode);
            }
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                String newPrefix = prefix + "[" + i + "]";
                flattenRecursive(node.get(i), newPrefix, flatNode);
            }
        } else {
            flatNode.set(prefix, node);
        }
    }
}