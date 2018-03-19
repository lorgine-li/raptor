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
    private static final Map<String, Property> DEFAULT_TYPE_PROPERTY;

    static {
        DEFAULT_TYPE_PROPERTY = new HashMap<>();
        DEFAULT_TYPE_PROPERTY.put("google.protobuf.Timestamp", new DateTimeProperty());
        DEFAULT_TYPE_PROPERTY.put("google.protobuf.StringValue", new StringProperty());
        DEFAULT_TYPE_PROPERTY.put("google.protobuf.Int32Value", new IntegerProperty());
        DEFAULT_TYPE_PROPERTY.put("google.protobuf.Int64Value", new LongProperty());
        DEFAULT_TYPE_PROPERTY.put("google.protobuf.FloatValue", new FloatProperty());
        DEFAULT_TYPE_PROPERTY.put("google.protobuf.DoubleValue", new DoubleProperty());
        DEFAULT_TYPE_PROPERTY.put("google.protobuf.BoolValue", new BooleanProperty());
        DEFAULT_TYPE_PROPERTY.put("google.protobuf.Struct", new ObjectProperty());
        DEFAULT_TYPE_PROPERTY.put("google.protobuf.Value", new ObjectProperty());
        DEFAULT_TYPE_PROPERTY.put("google.protobuf.ListValue", new ObjectProperty());
        DEFAULT_TYPE_PROPERTY.put("google.protobuf.Duration", new StringProperty());

    }


    private static Property formatProperty(FieldType fieldType, String typeDefPrefix, String basePackage) {
        Property property = DEFAULT_TYPE_PROPERTY.get(fieldType.getFQPN());

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
                default:
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
                || "google.protobuf.ListValue".equals(fieldType.getFQPN())) {
            property = new ArrayProperty(property);
        }

        return property;
    }

    public static Property formatTypeSwagger2(FieldType fieldType, String basePackage) {
        return formatProperty(fieldType, "definitions", basePackage);
    }

}
