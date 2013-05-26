package edu.css.json;

import com.google.common.base.Joiner;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static edu.css.json.JsonParser.ObjectValue;
import static java.util.Arrays.asList;

/**
 * Catalin Dumitru
 * Date: 4/27/13
 * Time: 3:11 PM
 */
public class JsonParser {
    private List<Builder> builderChain = newArrayList();
    private ChainBuilder chainBuilder = null;

    public JsonParser() {
        builderChain.add(new NullBuilder());
        builderChain.add(new ObjectBuilder());
        builderChain.add(new ArrayBuilder());
        builderChain.add(new DoubleBuilder());
        builderChain.add(new IntBuilder());
        builderChain.add(new StringBuilder());
        builderChain.add(new BooleanBuilder());

        chainBuilder = new ChainBuilder(builderChain);
    }

    public <T extends JsonValue> T parse(String input) {
        assert input != null : "input content cannot be null";
        return parseImpl(input);
    }

    private <T extends JsonValue> T parseImpl(String input) {
        return (T) chainParse(input);
    }

    private JsonValue chainParse(String input) {
        if (input.trim().isEmpty()) {
            throw new JsonParseException("Unexpected end of file");
        }
        return chainBuilder.build(input.trim()).value;
    }


    public static abstract class JsonValue {
        @Override
        public abstract String toString();
    }

    public static class NullValue extends JsonValue {

        @Override
        public String toString() {
            return "null";
        }
    }

    public static class IntValue extends JsonValue {
        private Integer value;

        public IntValue(Integer value) {
            this.value = value;
        }

        public Integer value() {
            return value;
        }

        @Override
        public String toString() {
            if (value == null) {
                return new NullValue().toString();
            }
            return value.toString();
        }
    }

    public static class StringValue extends JsonValue {
        private String value;

        public StringValue(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public String toString() {
            if (value == null) {
                return new NullValue().toString();
            }
            return String.format("\"%s\"", value);
        }
    }

    public static class BooleanValue extends JsonValue {
        private Boolean value;

        public BooleanValue(Boolean value) {
            this.value = value;
        }

        public Boolean value() {
            return value;
        }

        @Override
        public String toString() {
            if (value == null) {
                return new NullValue().toString();
            }
            return value.toString();
        }
    }

    public static class DoubleValue extends JsonValue {
        private Double value;

        public DoubleValue(Double value) {
            this.value = value;
        }

        public Double value() {
            return value;
        }

        @Override
        public String toString() {
            if (value == null) {
                return new NullValue().toString();
            }
            return value.toString();
        }
    }

    public static class ObjectValue extends JsonValue {
        private Map<String, JsonValue> properties = newLinkedHashMap();

        public ObjectValue() {
        }

        public ObjectValue(Map<String, JsonValue> properties) {
            assert properties != null : "properties map cannot be null";
            this.properties.putAll(properties);
        }

        public ObjectValue(Map.Entry<String, JsonValue>... properties) {
            assert properties != null : "properties map cannot be null";
            for (Map.Entry<String, JsonValue> property : properties) {
                this.properties.put(property.getKey(), property.getValue());
            }
        }

        public Map<String, JsonValue> properties() {
            return properties;
        }

        public <T extends JsonValue> T property(String key) {
            assert key != null : "property name cannot be null";
            return (T) this.properties.get(key);
        }

        public boolean has(String key) {
            assert key != null : "property name cannot be null";
            return properties.containsKey(key);
        }

        public void set(String key, JsonValue value) {
            assert key != null : "property name cannot be null";
            properties.put(key, value);
        }

        @Override
        public String toString() {
            List<String> formatted = newArrayList();

            for (Map.Entry<String, JsonValue> nameToValue : properties.entrySet()) {
                formatted.add(String.format("\"%s\":%s", nameToValue.getKey(), nameToValue.getValue().toString()));
            }
            return String.format("{%s}", Joiner.on(',').join(formatted));
        }
    }

    public static class ArrayValue extends JsonValue {
        private List<JsonValue> properties = newArrayList();

        public ArrayValue(List<JsonValue> properties) {
            assert properties != null : "properties list cannot be null";
            this.properties.addAll(properties);
        }

        public ArrayValue(JsonValue... properties) {
            assert properties != null : "properties list cannot be null";
            this.properties.addAll(asList(properties));
        }

        public List<JsonValue> properties() {
            return properties;
        }

        public <T extends JsonValue> T get(int index) {
            return (T) properties().get(index);
        }

        public void push(JsonValue value) {
            properties().add(value);
        }

        @Override
        public String toString() {
            List<String> formatted = newArrayList();

            for (JsonValue value : properties) {
                formatted.add(value.toString());
            }
            return String.format("[%s]", Joiner.on(',').join(formatted));
        }
    }
}

class ChainBuilder {
    private List<Builder> builderChain = newArrayList();

    ChainBuilder(List<Builder> builderChain) {
        assert builderChain != null : "builder list cannot be null";
        this.builderChain = builderChain;
    }

    public BuildResult build(String input) {
        assert input != null : "Input cannot be null";

        for (Builder builder : builderChain) {
            if (builder.matches(input)) {
                return builder.build(input, this);
            }
        }
        throw new JsonParseException("Cannot find property type");
    }
}

interface Builder {
    boolean matches(String input);

    BuildResult build(String input, ChainBuilder chainBuilder);
}

class ObjectBuilder implements Builder {

    @Override
    public boolean matches(String input) {
        return input.charAt(0) == '{';
    }

    @Override
    public BuildResult build(String input, ChainBuilder chainBuilder) {
        assert matches(input) : "Cannot build value from invalid input";

        input = input.substring(1).trim();
        ObjectValue value = new ObjectValue();

        while (input.length() > 0 && input.charAt(0) != '}') {
            if (input.charAt(0) == ',') {
                input = input.substring(1).trim();
            } else {
                input = parseProperty(input, value, chainBuilder);
            }
        }

        return new BuildResult(input.substring(1).trim(), value);
    }

    private String parseProperty(String input, ObjectValue value, ChainBuilder chainBuilder) {
        String propertyName = parseProperty(input);
        input = input.substring(propertyName.length() + 2).trim();

        if (input.charAt(0) != ':') {
            throw new JsonParseException("Invalid property format");
        }
        BuildResult propertyValue = chainBuilder.build(input.substring(1).trim());
        value.properties().put(propertyName, propertyValue.value);
        return propertyValue.remaining;
    }

    private String parseProperty(String input) {
        Pattern pattern = Pattern.compile("^(\"[^\"]+\")");
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            throw new JsonParseException("Invalid object format");
        }
        String encodedProperty = matcher.group(1);
        return encodedProperty.substring(1, encodedProperty.length() - 1);
    }
}

class ArrayBuilder implements Builder {

    @Override
    public boolean matches(String input) {
        return input.charAt(0) == '[';
    }

    @Override
    public BuildResult build(String input, ChainBuilder chainBuilder) {
        assert matches(input) : "Cannot build value from invalid input";

        input = input.substring(1).trim();
        JsonParser.ArrayValue value = new JsonParser.ArrayValue();

        while (input.length() > 0 && input.charAt(0) != ']') {
            if (input.charAt(0) == ',') {
                input = input.substring(1).trim();
            } else {
                input = parseObject(input, value, chainBuilder);
            }
        }

        return new BuildResult(input.substring(1).trim(), value);
    }

    private String parseObject(String input, JsonParser.ArrayValue value, ChainBuilder chainBuilder) {
        BuildResult result = chainBuilder.build(input);
        value.properties().add(result.value);
        return result.remaining;
    }
}

class IntBuilder implements Builder {

    @Override
    public boolean matches(String input) {
        Pattern pattern = Pattern.compile("^(-?\\d+\\b)");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    @Override
    public BuildResult build(String input, ChainBuilder chainBuilder) {
        Pattern pattern = Pattern.compile("^(-?\\d+\\b)");
        Matcher matcher = pattern.matcher(input);

        boolean elementFound = matcher.find();
        assert elementFound : "Cannot build value from invalid input";

        String encodedNumber = matcher.group(1);
        return new BuildResult(input.substring(encodedNumber.length()).trim(),
                new JsonParser.IntValue(Integer.parseInt(encodedNumber)));
    }
}

class StringBuilder implements Builder {

    @Override
    public boolean matches(String input) {
        Pattern pattern = Pattern.compile("^(\"[^\"]*\")");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    @Override
    public BuildResult build(String input, ChainBuilder chainBuilder) {
        Pattern pattern = Pattern.compile("^(\"[^\"]*\")");
        Matcher matcher = pattern.matcher(input);

        boolean elementFound = matcher.find();
        assert elementFound : "Cannot build value from invalid input";

        String encodedString = matcher.group(1);
        return new BuildResult(input.substring(encodedString.length()).trim(),
                new JsonParser.StringValue(encodedString.substring(1, encodedString.length() - 1)));
    }
}

class BooleanBuilder implements Builder {

    @Override
    public boolean matches(String input) {
        Pattern pattern = Pattern.compile("^((true)|(false))");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    @Override
    public BuildResult build(String input, ChainBuilder chainBuilder) {
        Pattern pattern = Pattern.compile("^((true)|(false))");
        Matcher matcher = pattern.matcher(input);

        boolean elementFound = matcher.find();
        assert elementFound : "Cannot build value from invalid input";

        String encodedBoolean = matcher.group(1);
        return new BuildResult(input.substring(encodedBoolean.length()).trim(),
                new JsonParser.BooleanValue(Boolean.valueOf(encodedBoolean)));
    }
}

class DoubleBuilder implements Builder {

    @Override
    public boolean matches(String input) {
        Pattern pattern = Pattern.compile("^(-?\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    @Override
    public BuildResult build(String input, ChainBuilder chainBuilder) {
        Pattern pattern = Pattern.compile("^(-?\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(input);

        boolean elementFound = matcher.find();
        assert elementFound : "Cannot build value from invalid input";

        String encodedBoolean = matcher.group(1);
        return new BuildResult(input.substring(encodedBoolean.length()).trim(),
                new JsonParser.DoubleValue(Double.valueOf(encodedBoolean)));
    }
}

class NullBuilder implements Builder {

    @Override
    public boolean matches(String input) {
        Pattern pattern = Pattern.compile("^(null)");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    @Override
    public BuildResult build(String input, ChainBuilder chainBuilder) {
        Pattern pattern = Pattern.compile("^(null)");
        Matcher matcher = pattern.matcher(input);

        boolean elementFound = matcher.find();
        assert elementFound : "Cannot build value from invalid input";

        String encodedBoolean = matcher.group(1);
        return new BuildResult(input.substring(encodedBoolean.length()).trim(), new JsonParser.NullValue());
    }
}

class BuildResult {
    public String remaining;
    public JsonParser.JsonValue value;

    BuildResult(String remaining, JsonParser.JsonValue value) {
        this.remaining = remaining;
        this.value = value;
    }
}



