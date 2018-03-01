package com.ppdai.framework.raptor.codegen.core.swagger.tool;

import com.ppdai.framework.raptor.codegen.core.swagger.type.FieldType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyicong on 18-3-1.
 */
public class TypeFormatUtil {

    private static Map<String, Object> formatType(FieldType fieldType, String typeDefPrefix) {
        TypeFormat typeFormat = new TypeFormat();
        typeFormat.setType("");
        typeFormat.setFormat("UNKNOWN");

        Map<String, Object> typeSchema = new HashMap<>(2);

        switch (fieldType.getType()) {
            case TYPE_BYTES:
                typeFormat.setType("string");
                typeFormat.setFormat("byte");
                break;
            case TYPE_INT32:
            case TYPE_SINT32:
            case TYPE_SFIXED32:
                typeFormat.setType("integer");
                typeFormat.setFormat("int32");
                break;
            case TYPE_UINT32:
            case TYPE_FIXED32:
            case TYPE_INT64:
            case TYPE_SINT64:
            case TYPE_SFIXED64:
                typeFormat.setType("integer");
                typeFormat.setFormat("int64");
                break;
            case TYPE_UINT64:
            case TYPE_FIXED64:
                typeFormat.setType("string");
                typeFormat.setFormat("uint64");
                break;
            case TYPE_FLOAT:
                typeFormat.setType("number");
                typeFormat.setFormat("float");
                break;
            case TYPE_DOUBLE:
                typeFormat.setType("number");
                typeFormat.setFormat("double");
                break;
            case TYPE_BOOL:
                typeFormat.setType("boolean");
                typeFormat.setFormat("boolean");
                break;
            case TYPE_STRING:
                typeFormat.setType("string");
                typeFormat.setFormat("");
                break;
            case TYPE_ENUM:
            case TYPE_MESSAGE:
            case TYPE_GROUP:
                typeFormat.setRef("#/" + typeDefPrefix + "/" + fieldType.getTypeName());
                break;
        }

        if (typeFormat.getRef() == null) { // primitive type
            typeSchema.put("type", typeFormat.getType());
            typeSchema.put("format", typeFormat.getFormat());
        } else { // complex type
            typeSchema.put("$ref", typeFormat.getRef());
        }

        switch (fieldType.getLabel()) {
            case LABEL_REPEATED:
                Map<String, Object> subTypeSchema = new HashMap<>(typeSchema);
                typeSchema.clear();
                typeSchema.put("type", "array");
                typeSchema.put("items", subTypeSchema);
                break;
        }

        return typeSchema;
    }

    public static Map<String, Object> formatTypeSwagger2(FieldType fieldType) {
        return formatType(fieldType, "definitions");
    }

    public static Map<String, Object> formatTypeSwagger3(FieldType fieldType) {
        return formatType(fieldType, "components/schemas");
    }
}
