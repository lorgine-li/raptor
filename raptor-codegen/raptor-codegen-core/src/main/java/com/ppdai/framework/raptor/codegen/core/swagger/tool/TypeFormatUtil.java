package com.ppdai.framework.raptor.codegen.core.swagger.tool;

import com.ppdai.framework.raptor.codegen.core.constant.ProtobufConstant;
import com.ppdai.framework.raptor.codegen.core.swagger.exception.SwaggerGenException;
import com.ppdai.framework.raptor.codegen.core.swagger.type.FieldType;
import com.ppdai.framework.raptor.codegen.core.utils.CommonUtils;
import io.swagger.models.properties.*;

import java.util.HashMap;
import java.util.Map;

import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;

/**
 * Created by zhangyicong on 18-3-1.
 */
public class TypeFormatUtil {
    private static final Map<String, Property> wktProperties;
    private static final Map<String, TypeFormat> wktSchemas;


    static {
        wktProperties = new HashMap<>();
        wktProperties.put("google.protobuf.Timestamp", new DateTimeProperty());
        wktProperties.put("google.protobuf.StringValue", new StringProperty());
        wktProperties.put("google.protobuf.Int32Value", new IntegerProperty());
        wktProperties.put("google.protobuf.Int64Value", new LongProperty());
        wktProperties.put("google.protobuf.FloatValue", new FloatProperty());
        wktProperties.put("google.protobuf.DoubleValue", new DoubleProperty());
        wktProperties.put("google.protobuf.BoolValue", new BooleanProperty());
        wktProperties.put("google.protobuf.Struct", new ObjectProperty());
        wktProperties.put("google.protobuf.Value", new ObjectProperty());
        wktProperties.put("google.protobuf.ListValue", new ObjectProperty());
        wktProperties.put("google.protobuf.Duration", new StringProperty());


        wktSchemas = new HashMap<>();
        wktSchemas.put("google.protobuf.Timestamp", new TypeFormat("string", "date-time", null, null));
        wktSchemas.put("google.protobuf.StringValue", new TypeFormat("string", "", null, null));
        wktSchemas.put("google.protobuf.Int32Value", new TypeFormat("integer", "int32", null, null));
        wktSchemas.put("google.protobuf.Int64Value", new TypeFormat("integer", "int64", null, null));
        wktSchemas.put("google.protobuf.FloatValue", new TypeFormat("number", "float", null, null));
        wktSchemas.put("google.protobuf.DoubleValue", new TypeFormat("number", "double", null, null));
        wktSchemas.put("google.protobuf.BoolValue", new TypeFormat("boolean", "boolean", null, null));
        wktSchemas.put("google.protobuf.Struct", new TypeFormat("object", null, null, true));
        wktSchemas.put("google.protobuf.Value", new TypeFormat("object", null, null, true));
        wktSchemas.put("google.protobuf.ListValue", new TypeFormat("object", null, null, true));
        wktSchemas.put("google.protobuf.Duration", new TypeFormat("string", "", null, null));
    }


    private static Property formatProperty(FieldType fieldType, String typeDefPrefix, String basePackage) {
        Property property = wktProperties.get(fieldType.getFQPN());

        if (!fieldType.getFQPN().startsWith("google.protobuf") && property == null) {
//            property = new AbstractProperty() {};

            switch (fieldType.getType()) {
                case TYPE_BYTES:
                    property = new ByteArrayProperty();
                    break;
                case TYPE_INT32:
                case TYPE_SINT32:
                case TYPE_SFIXED32:
                    property = new IntegerProperty();
                    break;
                case TYPE_UINT32:
                case TYPE_FIXED32:
                case TYPE_INT64:
                case TYPE_SINT64:
                case TYPE_SFIXED64:
                    property = new LongProperty();
                    break;
                case TYPE_UINT64:
                case TYPE_FIXED64:
                    property = new StringProperty("uint64");
                    break;
                case TYPE_FLOAT:
                    property = new FloatProperty();
                    break;
                case TYPE_DOUBLE:
                    property = new DoubleProperty();
                    break;
                case TYPE_BOOL:
                    property = new BooleanProperty();
                    break;
                case TYPE_STRING:
                    property = new StringProperty();
                    break;
                case TYPE_ENUM:
                case TYPE_MESSAGE:
                case TYPE_GROUP:
                    property = new RefProperty();
                    if (CommonUtils.getPackageNameFromFQPN(fieldType.getFQPN()).equals(basePackage)) {
                        ((RefProperty) property)
                                .set$ref("#/" + typeDefPrefix + "/" + fieldType.getClassName() + ProtobufConstant.PACKAGE_SEPARATOR + fieldType.getTypeName());
                    } else {
                        ((RefProperty) property).set$ref("#/" + typeDefPrefix + "/" + fieldType.getFQCN());
                    }
                    break;
            }
        }

        if (property == null) {
            throw new SwaggerGenException("field name: " + fieldType.getName()
                    + ", type: " + fieldType.getFQPN()
                    + " in message: " + fieldType.getMessage()
                    + " is unsupported");
        }

        if (fieldType.getLabel().equals(LABEL_REPEATED)
                || fieldType.getFQPN().equals("google.protobuf.ListValue")) {
            property = new ArrayProperty(property);
        }

        return property;
    }

    public static Property formatTypeSwagger2(FieldType fieldType, String basePackage) {
        return formatProperty(fieldType, "definitions", basePackage);
    }

}
