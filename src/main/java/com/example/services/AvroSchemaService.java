package com.example.services;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

import com.fasterxml.jackson.databind.JsonNode;

public class AvroSchemaService {

    public Schema inferSchemaFromSamples(List<JsonNode> samples, String name, String namespace) {

        // field name -> one example JsonNode value (first non-null one we see)
        Map<String, JsonNode> fieldExamples = new LinkedHashMap<>();
        Set<String> fieldsInEvery = null;

        for (JsonNode sample : samples) {
            Set<String> currentFields = new HashSet<>();

            Iterator<Map.Entry<String, JsonNode>> it = sample.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                currentFields.add(entry.getKey());
                fieldExamples.putIfAbsent(entry.getKey(), entry.getValue());
            }

            fieldsInEvery = (fieldsInEvery == null)
                    ? currentFields
                    : intersect(fieldsInEvery, currentFields);
        }

        SchemaBuilder.FieldAssembler<Schema> fields =
                SchemaBuilder.record(name).namespace(namespace).fields();

        for (Map.Entry<String, JsonNode> entry : fieldExamples.entrySet()) {
            String key = entry.getKey();
            JsonNode value = entry.getValue();
            boolean optional = fieldsInEvery == null || !fieldsInEvery.contains(key);
            addField(fields, key, value, optional, namespace);
        }

        return fields.endRecord();
    }

    private void addField(SchemaBuilder.FieldAssembler<Schema> fields, String key, JsonNode value,
                           boolean optional, String namespace) {

        switch (value.getNodeType()) {

            case STRING -> {
                if (optional) fields.name(key).type().nullable().stringType().noDefault();
                else fields.name(key).type().stringType().noDefault();
            }

            case NUMBER -> {
                boolean isLong = value.isIntegralNumber();
                if (optional) {
                    if (isLong) fields.name(key).type().nullable().longType().noDefault();
                    else fields.name(key).type().nullable().doubleType().noDefault();
                } else {
                    if (isLong) fields.name(key).type().longType().noDefault();
                    else fields.name(key).type().doubleType().noDefault();
                }
            }

            case BOOLEAN -> {
                if (optional) fields.name(key).type().nullable().booleanType().noDefault();
                else fields.name(key).type().booleanType().noDefault();
            }

            case NULL -> fields.name(key).type().nullable().stringType().noDefault();

            case OBJECT -> {
                Schema nested = inferSchemaFromSamples(
                        Collections.singletonList(value), capitalize(key) + "Record", namespace);
                if (optional) fields.name(key).type().unionOf().nullType().and().type(nested).endUnion().nullDefault();
                else fields.name(key).type(nested).noDefault();
            }

            case ARRAY -> {
                if (value.isEmpty()) {
                    fields.name(key).type().array().items().stringType().noDefault();
                } else if (value.get(0).isObject()) {
                    Schema itemSchema = inferSchemaFromSamples(
                            Collections.singletonList(value.get(0)), capitalize(key) + "Item", namespace);
                    fields.name(key).type().array().items(itemSchema).noDefault();
                } else {
                    fields.name(key).type().array().items().stringType().noDefault();
                }
            }

            default -> fields.name(key).type().nullable().stringType().noDefault();
        }
    }

    private Set<String> intersect(Set<String> a, Set<String> b) {
        Set<String> result = new HashSet<>(a);
        result.retainAll(b);
        return result;
    }

    private String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}