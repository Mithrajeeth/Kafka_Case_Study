package com.example.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import java.util.ArrayList;
import java.util.List;

public class RecordMapService {

    public GenericRecord map(JsonNode node, Schema schema) {
        GenericRecord record = new GenericData.Record(schema);

        for (Schema.Field field : schema.getFields()) {
            String name = field.name();
            JsonNode value = (node != null) ? node.get(name) : null;

            if (value == null || value.isNull()) {
                System.out.println("MISSING/NULL FIELD: '" + name + "'"
                        + (node != null && node.has("id") ? " in record id=" + node.get("id") : ""));
                record.put(name, null);
                continue;
            }

            record.put(name, convert(value, field.schema()));
        }
        return record;
    }

    private Object convert(JsonNode value, Schema fieldSchema) {
        if (value == null || value.isNull()) return null;

        switch (fieldSchema.getType()) {

            case RECORD:
                return map(value, fieldSchema);

            case ARRAY:
                List<Object> items = new ArrayList<>();
                Schema itemSchema = fieldSchema.getElementType();
                for (JsonNode item : value) {
                    items.add(itemSchema.getType() == Schema.Type.RECORD
                            ? map(item, itemSchema)
                            : item.asText());
                }
                return items;

            case LONG:
                return value.asLong();

            case DOUBLE:
                return value.asDouble();

            case BOOLEAN:
                return value.asBoolean();

            case UNION:
                // nullable field -> union is [null, actualType] or [null, actualType1, actualType2...]
                for (Schema unionType : fieldSchema.getTypes()) {
                    if (unionType.getType() != Schema.Type.NULL) {
                        return convert(value, unionType);
                    }
                }
                return null;

            case STRING:
            default:
                return value.asText();
        }
    }
}