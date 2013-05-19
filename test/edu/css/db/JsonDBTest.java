package edu.css.db;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ContiguousSet;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.google.common.collect.DiscreteDomain.integers;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Range.closed;
import static java.io.File.separator;
import static junit.framework.Assert.*;

/**
 * Catalin Dumitru
 * Date: 5/18/13
 * Time: 6:18 PM
 */
public class JsonDBTest {

    private Random random;

    @Before
    public void setUp() throws Exception {
        random = new Random(System.currentTimeMillis());
    }

    @Test(expected = DBParseException.class)
    public void testLoadDbWithMissingMeta() throws Exception {
        JsonDBImpl.fromFile("db\\test\\non_existent");
    }

    @Test(expected = DBParseException.class)
    public void testMissingKeyField() throws Exception {
        insertMetadataPrecondition("{" +
                "    \"entities\": [" +
                "        {" +
                "            \"name\": \"testEntity\"," +
                "            \"columns\": [" +
                "                {" +
                "                    \"name\": \"idField\"," +
                "                    \"type\": \"int\"" +
                "                }" +
                "            ]" +
                "        }" +
                "    ]" +
                "}");

        JsonDBImpl.fromFile("db\\test\\testentity");
    }

    @Test(expected = DBParseException.class)
    public void testInsertWithoutBegin() throws Exception {
        insertValidTestEntityMetadata();
        insertEmptyTestEntityData();

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        TestEntity testEntity = new TestEntity(null, null, null, null);

        db.save(testEntity);
        db.end(true);
    }

    @Test(expected = DBParseException.class)
    public void testInsertToManyColumns() throws Exception {
        insertMetadataPrecondition("{\n" +
                "    \"entities\": [" +
                "        {" +
                "            \"name\": \"testEntity\"," +
                "            \"columns\": [" +
                "                {" +
                "                    \"name\": \"idField\"," +
                "                    \"type\": \"int\"," +
                "                    \"id\": true" +
                "                }," +
                "                {" +
                "                    \"name\": \"integerField\"," +
                "                    \"type\": \"int\"" +
                "                }" +
                "            ]" +
                "        }" +
                "    ]" +
                "}");

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        TestEntity testEntity = new TestEntity(10, 123.456, "abc", true);

        db.begin();
        db.save(testEntity);
        db.end(true);
    }

    @Test(expected = DBParseException.class)
    public void testMissingManyColumns() throws Exception {
        insertMetadataPrecondition("{\n" +
                "    \"entities\": [" +
                "        {" +
                "            \"name\": \"testEntity\"," +
                "            \"columns\": [" +
                "                {" +
                "                    \"name\": \"idField\"," +
                "                    \"type\": \"int\"," +
                "                    \"id\": true" +
                "                }," +
                "                {" +
                "                    \"name\": \"integerField\"," +
                "                    \"type\": \"int\"" +
                "                }," +
                "                {" +
                "                    \"name\": \"doubleField\"," +
                "                    \"type\": \"decimal\"" +
                "                }," +
                "                {" +
                "                    \"name\": \"stringField\"," +
                "                    \"type\": \"string\"" +
                "                }," +
                "                {" +
                "                    \"name\": \"booleanField\"," +
                "                    \"type\": \"boolean\"" +
                "                }," +
                "                {" +
                "                    \"name\": \"additionalBooleanField\"," +
                "                    \"type\": \"boolean\"" +
                "                }" +
                "            ]" +
                "        }" +
                "    ]" +
                "}");

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        TestEntity testEntity = new TestEntity(10, 123.456, "abc", true);

        db.begin();
        db.save(testEntity);
        db.end(true);
    }

    private void insertMetadataPrecondition(String precondition) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Joiner.on(separator).join(
                    new String[]{"db", "test", "testentity", "testentity.meta.json"})));

            writer.write(precondition);
            writer.flush();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Cannot write to metadata file");
        }
    }

    private void insertDataPrecondition(String precondition) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Joiner.on(separator).join(
                    new String[]{"db", "test", "testentity", "testentity.json"})));

            writer.write(precondition);
            writer.flush();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Cannot write to data file");
        }
    }

    @Test
    public void testInsertNullValues() throws Exception {
        insertValidTestEntityMetadata();
        insertEmptyTestEntityData();

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        TestEntity testEntity = new TestEntity(null, null, null, null);

        db.begin();
        db.save(testEntity);
        db.end(true);

        db.begin();
        TestEntity storedEntity = db.find(testEntity.getIdField(), TestEntity.class);
        db.end(false);

        assertEntitiesEqual(testEntity, storedEntity);
    }

    private void insertEmptyTestEntityData() {
        insertDataPrecondition("{\"data\": {" +
                "    \"testEntity\": [" +
                "    ]" +
                "}, \"seq\": {" +
                "    \"testEntity\": 1" +
                "}}");
    }

    private void insertValidTestEntityMetadata() {
        insertMetadataPrecondition("{\n" +
                "    \"entities\": [" +
                "        {" +
                "            \"name\": \"testEntity\"," +
                "            \"columns\": [" +
                "                {" +
                "                    \"name\": \"idField\"," +
                "                    \"type\": \"int\"," +
                "                    \"id\": true" +
                "                }," +
                "                {" +
                "                    \"name\": \"integerField\"," +
                "                    \"type\": \"int\"" +
                "                }," +
                "                {" +
                "                    \"name\": \"doubleField\"," +
                "                    \"type\": \"decimal\"" +
                "                }," +
                "                {" +
                "                    \"name\": \"stringField\"," +
                "                    \"type\": \"string\"" +
                "                }," +
                "                {" +
                "                    \"name\": \"booleanField\"," +
                "                    \"type\": \"boolean\"" +
                "                }" +
                "            ]" +
                "        }" +
                "    ]" +
                "}");
    }

    private void assertEntitiesEqual(TestEntity testEntity, TestEntity storedEntity) {
        assertNotNull(storedEntity);
        assertEquals(storedEntity.getBooleanField(), testEntity.getBooleanField());
        assertEquals(storedEntity.getIntegerField(), testEntity.getIntegerField());
        assertEquals(storedEntity.getStringField(), testEntity.getStringField());
        if (storedEntity.getDoubleField() == null && testEntity.getDoubleField() == null) {
            return;
        }
        assertEquals(storedEntity.getDoubleField(), testEntity.getDoubleField(), 0.001);
    }

    @Test
    public void testInsertValidData() throws Exception {
        insertValidTestEntityMetadata();
        insertEmptyTestEntityData();

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        TestEntity testEntity = new TestEntity(10, 123.456, "abc", true);

        db.begin();
        db.save(testEntity);
        db.end(true);

        db.begin();
        TestEntity storedEntity = db.find(testEntity.getIdField(), TestEntity.class);
        db.end(false);

        assertEntitiesEqual(testEntity, storedEntity);
    }

    @Test
    public void testInsertWithoutSave() throws Exception {
        insertValidTestEntityMetadata();
        insertEmptyTestEntityData();

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        TestEntity testEntity = new TestEntity(10, 123.456, "abc", true);

        db.begin();
        db.save(testEntity);
        db.end(false);

        db.begin();
        TestEntity storedEntity = db.find(testEntity.getIdField(), TestEntity.class);
        db.end(false);

        assertNull(storedEntity);
    }

    @Test
    public void testInsertRandomData() throws Exception {
        insertValidTestEntityMetadata();
        insertEmptyTestEntityData();

        List<TestEntity> testEntities = createRandomEntities(100);

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        db.begin();
        for (TestEntity testEntity : testEntities) {
            db.save(testEntity);
        }
        db.end(true);

        db.begin();
        for (TestEntity testEntity : testEntities) {
            TestEntity storedEntity = db.find(testEntity.getIdField(), TestEntity.class);
            assertEntitiesEqual(testEntity, storedEntity);
        }
        db.end(false);
    }

    private List<TestEntity> createRandomEntities(int count) {
        return from(ContiguousSet.create(closed(1, count), integers())).transform(new Function<Integer, TestEntity>() {
            @Nullable
            @Override
            public TestEntity apply(@Nullable Integer input) {
                return randomEntity();
            }
        }).toList();
    }

    private TestEntity randomEntity() {
        return new TestEntity(random.nextInt(), random.nextDouble(), Integer.toString(random.nextInt()), random.nextBoolean());
    }

    @Test
    public void testFindNonExistentEntity() throws Exception {
        insertValidTestEntityMetadata();
        insertEmptyTestEntityData();

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        db.begin();
        TestEntity storedEntity = db.find(1, TestEntity.class);
        db.end(false);

        assertNull(storedEntity);
    }

    @Test
    public void testFindExistingEntity() throws Exception {
        insertValidTestEntityMetadata();
        insertSingleEntityPrecondition();

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        TestEntity testEntity = new TestEntity(10, 123.456, "abc", true);

        db.begin();
        TestEntity storedEntity = db.find(2, TestEntity.class);
        db.end(false);

        assertEntitiesEqual(testEntity, storedEntity);
    }

    @Test
    public void testFindAllEntities() throws Exception {
        insertValidTestEntityMetadata();
        insertDataPrecondition("" +
                "{\"data\": {\n" +
                "    \"testEntity\": [\n" +
                "        [2, 10, 123.456, \"abc\", true],\n" +
                "        [3, 11, 223.456, \"def\", false],\n" +
                "        [4, 12, 323.456, \"ghi\", true]\n" +
                "    ]\n" +
                "}, \"seq\": {\n" +
                "    \"testEntity\": 2\n" +
                "}}");

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        db.begin();
        List<TestEntity> storedEntities = db.getAll(TestEntity.class);
        db.end(false);

        assertEquals(storedEntities.size(), 3);
        assertEntitiesEqual(new TestEntity(10, 123.456, "abc", true), storedEntities.get(0));
        assertEntitiesEqual(new TestEntity(11, 223.456, "def", false), storedEntities.get(1));
        assertEntitiesEqual(new TestEntity(12, 323.456, "ghi", true), storedEntities.get(2));
    }

    @Test
    public void testFindWithIntegerAsDecimal() throws Exception {
        insertValidTestEntityMetadata();
        insertDataPrecondition("" +
                "{\"data\": {\n" +
                "    \"testEntity\": [\n" +
                "        [2, 10, 123, \"abc\", true]\n" +
                "    ]\n" +
                "}, \"seq\": {\n" +
                "    \"testEntity\": 2\n" +
                "}}");

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        TestEntity testEntity = new TestEntity(10, 123d, "abc", true);

        db.begin();
        TestEntity storedEntity = db.find(2, TestEntity.class);
        db.end(false);

        assertEntitiesEqual(testEntity, storedEntity);

    }

    @Test
    public void testUpdateEntity() throws Exception {
        insertValidTestEntityMetadata();
        insertSingleEntityPrecondition();

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        db.begin();
        TestEntity testEntity = db.find(2, TestEntity.class);
        testEntity.setIntegerField(20);
        testEntity.setDoubleField(456.123);
        testEntity.setStringField("def");
        testEntity.setBooleanField(false);
        db.save(testEntity);
        db.end(true);

        db.begin();
        List<TestEntity> storedEntities = db.getAll(TestEntity.class);
        db.end(false);

        assertEquals(storedEntities.size(), 1);
        assertEntitiesEqual(testEntity, storedEntities.get(0));
    }

    @Test
    public void testUpdateEntityWithNullValues() throws Exception {
        insertValidTestEntityMetadata();
        insertSingleEntityPrecondition();

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        db.begin();
        TestEntity testEntity = db.find(2, TestEntity.class);
        testEntity.setIntegerField(null);
        testEntity.setDoubleField(null);
        testEntity.setStringField(null);
        testEntity.setBooleanField(null);
        db.save(testEntity);
        db.end(true);

        db.begin();
        List<TestEntity> storedEntities = db.getAll(TestEntity.class);
        db.end(false);

        assertEquals(storedEntities.size(), 1);
        assertEntitiesEqual(testEntity, storedEntities.get(0));
    }

    private void insertSingleEntityPrecondition() {
        insertDataPrecondition("" +
                "{\"data\": {\n" +
                "    \"testEntity\": [\n" +
                "        [2, 10, 123.456, \"abc\", true]\n" +
                "    ]\n" +
                "}, \"seq\": {\n" +
                "    \"testEntity\": 2\n" +
                "}}");
    }

    @Test
    public void testDelete() throws Exception {
        insertValidTestEntityMetadata();
        insertSingleEntityPrecondition();

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        db.begin();
        TestEntity testEntity = db.find(2, TestEntity.class);
        db.delete(testEntity);
        db.end(true);

        db.begin();
        List<TestEntity> storedEntities = db.getAll(TestEntity.class);
        db.end(false);

        assertEquals(storedEntities.size(), 0);
    }


    @Test
    public void testDeleteNotPersistedEntity() throws Exception {
        insertValidTestEntityMetadata();
        insertSingleEntityPrecondition();

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        db.begin();
        db.delete(new TestEntity());
        db.end(true);

        db.begin();
        List<TestEntity> storedEntities = db.getAll(TestEntity.class);
        db.end(false);

        assertEquals(storedEntities.size(), 1);
    }


    @Test
    public void testDeleteNullEntity() throws Exception {
        insertValidTestEntityMetadata();
        insertSingleEntityPrecondition();

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        db.begin();
        db.delete(null);
        db.end(true);

        db.begin();
        List<TestEntity> storedEntities = db.getAll(TestEntity.class);
        db.end(false);

        assertEquals(storedEntities.size(), 1);
    }

    @Test
    public void testDeleteWithoutSave() throws Exception {
        insertValidTestEntityMetadata();
        insertSingleEntityPrecondition();

        JsonDB db = JsonDBImpl.fromFile("db\\test\\testentity");

        db.begin();
        TestEntity testEntity = db.find(2, TestEntity.class);
        db.delete(testEntity);
        db.end(false);

        db.begin();
        List<TestEntity> storedEntities = db.getAll(TestEntity.class);
        db.end(false);

        assertEquals(storedEntities.size(), 1);
    }


}
