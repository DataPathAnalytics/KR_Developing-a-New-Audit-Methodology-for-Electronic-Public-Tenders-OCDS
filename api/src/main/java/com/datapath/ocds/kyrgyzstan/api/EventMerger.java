package com.datapath.ocds.kyrgyzstan.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Iterator;

import static java.util.Objects.isNull;

public class EventMerger {

    private static final String ID = "id";
    private ObjectMapper objectMapper;

    public EventMerger(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String process(String rawEvent, String rawRelease) {
        try {
            JsonNode event = objectMapper.readTree(rawEvent);
            JsonNode release = isNull(rawRelease) ? objectMapper.createObjectNode() : objectMapper.readTree(rawRelease);

            JsonNode result = merge(event, release);
            return objectMapper.writeValueAsString(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addElemToArray(ArrayNode array, JsonNode elem) {
        boolean existInArray = false;
        for (JsonNode arrayElem : array) {
            JsonNode arrayElemId = arrayElem.get(ID);
            JsonNode elemId = elem.get(ID);
            if (arrayElemId.equals(elemId)) {
                existInArray = true;
            }
        }
        if (!existInArray) {
            array.add(elem);
        }
    }

    private JsonNode merge(JsonNode event, JsonNode release) {
        if (event.isContainerNode() && release.isContainerNode()) {
            if (event.isArray()) {
                return mergeArrays(event, release);
            } else {
                ObjectNode node = objectMapper.createObjectNode();
                Iterator<String> fieldNames = event.fieldNames();

                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    JsonNode eventNode = event.get(fieldName);
                    if (release.has(fieldName)) {
                        node.set(fieldName, merge(eventNode, release.get(fieldName)));
                    } else {
                        node.set(fieldName, eventNode);
                    }
                }

                Iterator<String> releaseFieldNames = release.fieldNames();
                while (releaseFieldNames.hasNext()) {
                    String fieldName = releaseFieldNames.next();
                    if (!node.hasNonNull(fieldName)) {
                        node.set(fieldName, release.get(fieldName));
                    }
                }
                return node;
            }
        } else {
            return event;
        }
    }

    private JsonNode mergeArrays(JsonNode event, JsonNode release) {
        ArrayNode node = objectMapper.createArrayNode();
        for (JsonNode eventElem : event) {
            boolean foundEqual = false;
            for (JsonNode releaseElem : release) {
                if (eventElem.hasNonNull(ID) && releaseElem.hasNonNull(ID)) {
                    JsonNode eventElemId = eventElem.get(ID);
                    JsonNode releaseElemId = releaseElem.get(ID);
                    if (eventElemId.equals(releaseElemId)) {
                        addElemToArray(node, merge(eventElem, releaseElem));
                        foundEqual = true;
                        break;
                    }
                } else if (eventElem.equals(releaseElem)) {
                    foundEqual = true;
                }
            }
            if (!foundEqual) {
                node.add(eventElem);
            }
        }

        for (JsonNode releaseElem : release) {
            boolean found = false;
            for (JsonNode resultNode : node) {
                if (resultNode.hasNonNull(ID) && releaseElem.hasNonNull(ID)) {
                    JsonNode resultElemId = resultNode.get(ID);
                    JsonNode releaseElemId = releaseElem.get(ID);
                    if (resultElemId.equals(releaseElemId)) {
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                node.add(releaseElem);
            }
        }

        return node;
    }

}
