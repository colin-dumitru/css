package edu.css.json;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;

import static edu.css.json.JsonParser.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Catalin Dumitru
 * Date: 5/18/13
 * Time: 5:01 PM
 */
public class PrimitiveTest {
    private JsonParser jsonParser;

    @Before
    public void setUp() throws Exception {
        jsonParser = new JsonParser();
    }

    @Test
    public void testParseNull() throws Exception {
        JsonValue value = jsonParser.parse("null");

        assertTrue(value instanceof NullValue);
    }

    @Test
    public void testParseInt() throws Exception {
        JsonValue value = jsonParser.parse("1");

        assertTrue(value instanceof IntValue);
        assertEquals((long) ((IntValue) value).value(), 1);
    }


    @Test
    public void testParseDouble() throws Exception {
        JsonValue value = jsonParser.parse("9.76");

        assertTrue(value instanceof DoubleValue);
        assertEquals(((DoubleValue) value).value(), 9.76, 0.01);
    }

    @Test
    public void testParseBoolean() throws Exception {
        JsonValue value = jsonParser.parse("true");

        assertTrue(value instanceof BooleanValue);
        assertTrue(((BooleanValue) value).value());
    }

    @Test
    public void testParseString() throws Exception {
        JsonValue value = jsonParser.parse("\"Hello\"");

        assertTrue(value instanceof StringValue);
        assertEquals(((StringValue) value).value(), "Hello");
    }

    @Test
    public void testParseEmptyArray() throws Exception {
        JsonValue value = jsonParser.parse("[]");

        assertTrue(value instanceof ArrayValue);
        assertTrue(((ArrayValue) value).properties().isEmpty());
    }


    @Test
    public void testParseEmptyObject() throws Exception {
        JsonValue value = jsonParser.parse("{}");

        assertTrue(value instanceof ObjectValue);
        assertTrue(((ObjectValue) value).properties().isEmpty());
    }

    @Test(expected = JsonParseException.class)
    public void testMalformedString() throws Exception {
        jsonParser.parse("{a: \"abc\"");
    }

    @Test(expected = JsonParseException.class)
    public void testEmptyString() throws Exception {
        jsonParser.parse("");
    }

    @Test
    public void testComplexStructure1() throws Exception {
        JsonValue value = jsonParser.parse("" +
                "[" +
                "  {" +
                "    \"prop1\": \"abc\"," +
                "    \"prop2\": 1," +
                "    \"prop3\": 3.12," +
                "    \"prop4\": true" +
                "  }," +
                "[[null]]," +
                "\"def\"" +
                "]");

        assertTrue(value instanceof ArrayValue);
        assertEquals(((ArrayValue) value).properties().size(), 3);

        JsonValue objValue = ((ArrayValue) value).properties().get(0);
        assertTrue(objValue instanceof ObjectValue);
        assertEquals(((ObjectValue) objValue).properties().size(), 4);

        assertTrue(((ObjectValue) objValue).property("prop1") instanceof StringValue);
        assertEquals(((StringValue) ((ObjectValue) objValue).property("prop1")).value(), "abc");

        assertTrue(((ObjectValue) objValue).property("prop2") instanceof IntValue);
        assertEquals((long) ((IntValue) ((ObjectValue) objValue).property("prop2")).value(), 1);

        assertTrue(((ObjectValue) objValue).property("prop3") instanceof DoubleValue);
        assertEquals(((DoubleValue) ((ObjectValue) objValue).property("prop3")).value(), 3.12, 0.01);

        assertTrue(((ObjectValue) objValue).property("prop4") instanceof BooleanValue);
        assertEquals(((BooleanValue) ((ObjectValue) objValue).property("prop4")).value(), true);

        JsonValue arrayValue = ((ArrayValue) value).properties().get(1);
        assertTrue(arrayValue instanceof ArrayValue);
        assertEquals(((ArrayValue) arrayValue).properties().size(), 1);

        JsonValue nestedArrayValue = ((ArrayValue) arrayValue).properties().get(0);
        assertTrue(nestedArrayValue instanceof ArrayValue);
        assertEquals(((ArrayValue) nestedArrayValue).properties().size(), 1);

        assertTrue(((ArrayValue) nestedArrayValue).properties().get(0) instanceof NullValue);

        assertTrue(((ArrayValue) value).properties().get(2) instanceof StringValue);
        assertEquals(((StringValue) ((ArrayValue) value).properties().get(2)).value(), "def");
    }

    @Test
    public void testComplexStructure2() throws Exception {
        JsonValue value = jsonParser.parse("" +
                "{\n" +
                "   \"prop1\": {\n" +
                "      \"prop1\" : \"abc\",\n" +
                "      \"prop2\" : null\n" +
                "   },\n" +
                "   \"prop2\": [\n" +
                "      {\n" +
                "         \"prop1\": true,\n" +
                "         \"prop2\": -1\n" +
                "      }\n" +
                "   ],\n" +
                "   \"prop3\": 1.234\n" +
                "}");

        assertTrue(value instanceof ObjectValue);
        assertEquals(((ObjectValue) value).properties().size(), 3);

        JsonValue objectProperty = ((ObjectValue) value).property("prop1");
        assertTrue(objectProperty instanceof ObjectValue);

        assertEquals(((ObjectValue) objectProperty).properties().size(), 2);

        assertTrue(((ObjectValue) objectProperty).properties().get("prop1") instanceof StringValue);
        assertEquals(((StringValue) ((ObjectValue) objectProperty).properties().get("prop1")).value(), "abc");

        assertTrue(((ObjectValue) objectProperty).properties().get("prop2") instanceof NullValue);

        JsonValue arrayValue = ((ObjectValue) value).property("prop2");
        assertTrue(arrayValue instanceof ArrayValue);
        assertEquals(((ArrayValue) arrayValue).properties().size(), 1);

        JsonValue nestedObjectValue = ((ArrayValue) arrayValue).properties().get(0);
        assertTrue(nestedObjectValue instanceof ObjectValue);

        assertTrue(((ObjectValue) nestedObjectValue).property("prop1") instanceof BooleanValue);
        assertEquals(((BooleanValue) ((ObjectValue) nestedObjectValue).property("prop1")).value(), true);

        assertTrue(((ObjectValue) nestedObjectValue).property("prop2") instanceof IntValue);
        assertEquals((long) ((IntValue) ((ObjectValue) nestedObjectValue).property("prop2")).value(), -1);

        assertTrue(((ObjectValue) value).property("prop3") instanceof DoubleValue);
        assertEquals(((DoubleValue) ((ObjectValue) value).property("prop3")).value(), 1.234, 0.001);
    }

    @Test
    public void testIntValueToString() throws Exception {
        assertEquals(new IntValue(1).toString(), "1");
    }

    @Test
    public void testBooleanValueToString() throws Exception {
        assertEquals(new BooleanValue(false).toString(), "false");
    }

    @Test
    public void testDoubleValueToString() throws Exception {
        assertEquals(new DoubleValue(4.0).toString(), "4.0");
    }

    @Test
    public void testStringValueToString() throws Exception {
        assertEquals(new StringValue("abc").toString(), "\"abc\"");
    }

    @Test
    public void testNullValueToString() throws Exception {
        assertEquals(new NullValue().toString(), "null");
    }

    @Test
    public void testEmptyObjectToString() throws Exception {
        assertEquals(new ObjectValue().toString(), "{}");
    }

    @Test
    public void testEmptyArrayToString() throws Exception {
        assertEquals(new ArrayValue().toString(), "[]");
    }

    @Test
    public void testComplexObjectToString1() throws Exception {
        String stringValue = new ArrayValue(
                new ObjectValue(
                        Maps.<String, JsonValue>immutableEntry("prop1", new StringValue("abc")),
                        Maps.<String, JsonValue>immutableEntry("prop2", new IntValue(1)),
                        Maps.<String, JsonValue>immutableEntry("prop3", new DoubleValue(3.0)),
                        Maps.<String, JsonValue>immutableEntry("prop4", new BooleanValue(true))
                ),
                new ArrayValue(new ArrayValue(new NullValue())),
                new StringValue("def")
        ).toString();

        assertEquals(stringValue, "[{\"prop1\":\"abc\",\"prop2\":1,\"prop3\":3.0,\"prop4\":true},[[null]],\"def\"]");
    }

    @Test
    public void testComplexObjectToString2() throws Exception {
        String stringValue = new ObjectValue(
                Maps.<String, JsonValue>immutableEntry("prop1", new ObjectValue(
                        Maps.<String, JsonValue>immutableEntry("prop1", new StringValue("abc")),
                        Maps.<String, JsonValue>immutableEntry("prop2", new NullValue())
                )),
                Maps.<String, JsonValue>immutableEntry("prop2", new ArrayValue(
                        new ObjectValue(
                                Maps.<String, JsonValue>immutableEntry("prop1", new BooleanValue(true)),
                                Maps.<String, JsonValue>immutableEntry("prop2", new IntValue(-1))
                        )
                )),
                Maps.<String, JsonValue>immutableEntry("prop3", new DoubleValue(1.234))
        ).toString();

        assertEquals(stringValue, "{\"prop1\":{\"prop1\":\"abc\",\"prop2\":null},\"prop2\":[{\"prop1\":true,\"prop2\":-1}],\"prop3\":1.234}");
    }
}
