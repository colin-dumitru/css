package edu.css.db;


import edu.css.json.JsonParser;

import java.beans.Introspector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static edu.css.json.JsonParser.*;

/**
 * Catalin Dumitru
 * Universitatea Alexandru Ioan Cuza
 */
public class JsonDBImpl implements JsonDB {
    private final File metaFile;
    private final File dataFile;
    private final Map<String, EntityMeta> entities = new HashMap<>();

    private boolean closed = true;
    private ObjectValue dbObject;

    private JsonDBImpl(File metaFile, File dataFile) {
        this.metaFile = metaFile;
        this.dataFile = dataFile;
    }

    private JsonDBImpl performValidation() {
        checkIfMetaExist();
        checkIfDataExists();
        checkIfKeyFieldExists();

        return this;
    }

    private void checkIfKeyFieldExists() {
        for (EntityMeta entityMeta : entities.values()) {
            if (entityMeta.getKeyColumn() == null) {
                throw new DBParseException("Entity does not have a key column: " + entityMeta.getName());
            }
            if (!entityMeta.getKeyColumn().getParser().getType().equals(Integer.class)) {
                throw new DBParseException("Key column can only be of type Integer: " + entityMeta.getName());
            }
        }
    }

    private JsonDBImpl loadMetadata() {
        try {
            Scanner scanner = new Scanner(metaFile);
            scanner.useDelimiter("\\Z");
            ObjectValue jsonObject = new JsonParser().parse(scanner.next());
            loadMetadata(jsonObject);
        } catch (FileNotFoundException e) {
            throw new DBParseException("Error loading metadata", e);
        }
        return this;
    }

    private void loadMetadata(ObjectValue jsonObject) {
        ArrayValue entitiesArray = jsonObject.property("entities");

        for (JsonValue jsonValue : entitiesArray.properties()) {
            EntityMeta entityMeta = createEntityMeta((ObjectValue) jsonValue);
            entities.put(entityMeta.getName(), entityMeta);
        }
    }

    private EntityMeta createEntityMeta(ObjectValue entityObject) {
        String name = entityObject.<StringValue>property("name").value();
        ArrayValue columns = entityObject.property("columns");
        Map<String, ColumnMeta> columnsMeta = createColumnsMeta(columns);
        List<String> orderedColumns = createOrderedColumns(columns);

        return new EntityMeta(name, columnsMeta, orderedColumns);
    }

    private List<String> createOrderedColumns(ArrayValue columns) {
        List<String> columnsMeta = newArrayList();

        for (JsonValue jsonValue : columns.properties()) {
            columnsMeta.add(((ObjectValue) jsonValue).<StringValue>property("name").value());
        }
        return columnsMeta;
    }

    private Map<String, ColumnMeta> createColumnsMeta(ArrayValue columns) {
        Map<String, ColumnMeta> columnsMeta = new HashMap<>();

        for (JsonValue jsonValue : columns.properties()) {
            ColumnMeta columnMeta = createColumnMeta((ObjectValue) jsonValue);
            columnsMeta.put(columnMeta.getName(), columnMeta);
        }
        return columnsMeta;
    }

    private ColumnMeta createColumnMeta(ObjectValue jsonObject) {
        String name = jsonObject.<StringValue>property("name").value();
        String type = jsonObject.<StringValue>property("type").value();
        boolean isKey = jsonObject.has("id") && jsonObject.<BooleanValue>property("id").value();

        return new ColumnMeta(name, type, isKey, ColumnParserBuilder.fromType(type));
    }

    private void checkIfDataExists() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                throw new DBParseException("Cannot create data file", e);
            }
        }
    }

    private void checkIfMetaExist() {
        if (!metaFile.exists()) {
            throw new DBParseException("Metadata file does not exist");
        }
    }

    @Override
    public void begin() {
        this.closed = false;
        try {
            Scanner scanner = new Scanner(dataFile);
            scanner.useDelimiter("\\Z");
            this.dbObject = new JsonParser().parse(scanner.next());
        } catch (FileNotFoundException e) {
            throw new DBParseException("Error loading metadata", e);
        }
    }

    @Override
    public void end(boolean saveChanges) {
        this.closed = true;
        if (saveChanges) {
            saveChanges();
        }
    }

    private void saveChanges() {
        String stringData = dbObject.toString();
        try {
            FileWriter fileWriter = new FileWriter(dataFile);
            fileWriter.write(stringData);
            fileWriter.close();
        } catch (IOException e) {
            throw new DBParseException("Error saving to file", e);
        }
    }

    @Override
    public <T> List<T> getAll(Class<T> clazz) {
        checkIfOpened();
        checkIfMetadata(clazz);
        return getAllChecked(clazz);
    }

    private <T> List<T> getAllChecked(Class<T> clazz) {
        return loadAll(dbObject, clazz);
    }

    private <T> List<T> loadAll(ObjectValue jsonObject, Class<T> clazz) {
        if (!jsonObject.has("data")) {
            return Collections.emptyList();
        }
        ObjectValue dataObject = jsonObject.property("data");
        return loadAllFromData(dataObject, clazz);
    }

    private <T> List<T> loadAllFromData(ObjectValue dataObject, Class<T> clazz) {
        String entityName = Introspector.decapitalize(clazz.getSimpleName());
        if (!dataObject.has(entityName)) {
            return Collections.emptyList();
        }
        ArrayValue entityArray = dataObject.property(entityName);
        Constructor<T> constructor = getConstructor(clazz);
        return loadAllFromArray(entityArray, entities.get(entityName), constructor);
    }

    private <T> Constructor<T> getConstructor(Class<T> clazz) {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new DBParseException("Error getting class constructor", e);
        }
    }

    private <T> List<T> loadAllFromArray(ArrayValue entityArray, EntityMeta entityMeta, Constructor<T> constructor) {
        List<T> rows = newArrayList();

        for (JsonValue jsonValue : entityArray.properties()) {
            T row = loadRow((ArrayValue) jsonValue, entityMeta, constructor);
            rows.add(row);
        }
        return rows;
    }

    private <T> T loadRow(ArrayValue array, EntityMeta entityMeta, Constructor<T> constructor) {
        T row = createNewInstance(constructor);

        for (int i = 0; i < array.properties().size(); i++) {
            String columnName = entityMeta.getOrderedColumns().get(i);
            Object columnData = getColumnData(columnName, array.get(i), entityMeta);
            setColumnData(row, columnData, columnName);
        }

        return row;
    }

    private <T> void setColumnData(T row, Object columnData, String columnName) {
        Field dataField = getField(columnName, row.getClass());
        dataField.setAccessible(true);
        setFieldValue(row, columnData, dataField);
    }

    private <T> void setFieldValue(T obj, Object value, Field field) {
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new DBParseException(e);
        }
    }

    private <T> Field getField(String columnName, Class<T> clazz) {
        try {
            return clazz.getDeclaredField(columnName);
        } catch (NoSuchFieldException e) {
            throw new DBParseException("Error getting entity field", e);
        }
    }

    private Object getColumnData(String columnName, JsonValue obj, EntityMeta entityMeta) {
        return entityMeta.getColumns().get(columnName).getParser().fromJson(obj);
    }

    private <T> T createNewInstance(Constructor<T> constructor) {
        try {
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DBParseException("Error creating entity", e);
        }
    }

    private <T> void checkIfMetadata(Class<T> clazz) {
        checkEntityAnnotation(clazz);
        EntityMeta metadata = checkMetadataPresent(clazz);

        checkFieldMetadata(clazz, metadata);
    }

    private <T> void checkFieldMetadata(Class<T> clazz, EntityMeta metadata) {
        int matchedFields = 0;

        for (Field field : clazz.getDeclaredFields()) {
            checkFieldMetadata(metadata, field);
            matchedFields++;
        }

        if (metadata.getColumns().size() != matchedFields) {
            throw new DBParseException("Number of declared fields and metadata fields do not match");
        }
    }

    private void checkFieldMetadata(EntityMeta metadata, Field field) {
        if (field.getAnnotation(Column.class) != null) {
            checkMetadataPresent(metadata, field);
            ColumnMeta fieldMeta = checkColumnMetadata(metadata, field);
            checkIdMetadata(field, fieldMeta);
        }
    }

    private void checkIdMetadata(Field field, ColumnMeta fieldMeta) {
        if (fieldMeta.isKey() && field.getAnnotation(Id.class) == null) {
            throw new DBParseException("Field is not key field: " + field.getName());
        }
    }

    private ColumnMeta checkColumnMetadata(EntityMeta metadata, Field field) {
        ColumnMeta fieldMeta = metadata.getColumns().get(field.getName());
        if (!fieldMeta.getParser().getType().equals(field.getType())) {
            throw new DBParseException("Mismatched field type: " + field.getName());
        }
        return fieldMeta;
    }

    private void checkMetadataPresent(EntityMeta metadata, Field field) {
        if (!metadata.getColumns().containsKey(field.getName())) {
            throw new DBParseException("Metadata for field was not found: " + field.getName());
        }
    }

    private <T> EntityMeta checkMetadataPresent(Class<T> clazz) {
        EntityMeta metadata = entities.get(Introspector.decapitalize(clazz.getSimpleName().toLowerCase()));
        if (metadata == null) {
            throw new DBParseException("No metadata was found for entity");
        }
        return metadata;
    }

    private <T> void checkEntityAnnotation(Class<T> clazz) {
        Entity entityAnnotation = clazz.getAnnotation(Entity.class);
        if (entityAnnotation == null) {
            throw new DBParseException("Object is not an entity");
        }
    }

    private void checkIfOpened() {
        if (closed) {
            throw new DBParseException("Database must be opened first");
        }
    }

    @Override
    public <T> T find(Object id, Class<T> clazz) {
        List<T> loadedEntities = getAll(clazz);
        for (T loadedEntity : loadedEntities) {
            if (matches(loadedEntity, id, clazz)) {
                return loadedEntity;
            }
        }
        return null;
    }

    private <T> boolean matches(T loadedEntity, Object id, Class<T> clazz) {
        return getIdValue(loadedEntity, clazz).equals(id);
    }

    private <T> Object getIdValue(T loadedEntity, Class<T> clazz) {
        EntityMeta entityMeta = entities.get(Introspector.decapitalize(clazz.getSimpleName()));
        Field keyField = getField(entityMeta.getKeyColumn().getName(), clazz);
        keyField.setAccessible(true);
        return getFieldValue(loadedEntity, keyField);
    }

    private <T> Object getFieldValue(T loadedEntity, Field keyField) {
        try {
            keyField.setAccessible(true);
            return keyField.get(loadedEntity);
        } catch (IllegalAccessException e) {
            throw new DBParseException("Error getting key field value", e);
        }
    }

    @Override
    public <T> void save(T entity) {
        checkIfOpened();
        checkIfMetadata(entity.getClass());

        updateKeyField(entity);
        ArrayValue dataArray = serialize(entity);
        save(dataArray, entity);
    }

    private <T> void save(ArrayValue dataArray, T entity) {
        ObjectValue dataObject = dbObject.property("data");
        ArrayValue entityArray = dataObject.property(Introspector.decapitalize(entity.getClass().getSimpleName()));
        saveOrUpdateEntity(dataArray, entityArray, entity);
    }

    private <T> void saveOrUpdateEntity(ArrayValue dataArray, ArrayValue entityArray, T entity) {
        tryToRemoveEntity(entityArray, entity);
        entityArray.push(dataArray);
    }

    private <T> void tryToRemoveEntity(ArrayValue dataArray, T entity) {
        removeEntity(dataArray, entity);
    }

    private <T> void removeEntity(ArrayValue dataArray, T entity) {
        ArrayValue entityArray = findArrayForEntity(dataArray, entity);
        if (entityArray != null) {
            dataArray.properties().remove(entityArray);
        }
    }

    private <T> ArrayValue findArrayForEntity(ArrayValue dataArray, T entity) {
        for (JsonValue jsonValue : dataArray.properties()) {
            if (arrayForEntity((ArrayValue) jsonValue, entity)) {
                return (ArrayValue) jsonValue;
            }
        }
        return null;
    }

    private <T> boolean arrayForEntity(ArrayValue jsonArray, T entity) {
        String entityName = Introspector.decapitalize(entity.getClass().getSimpleName());
        Object row = loadRow(jsonArray, entities.get(entityName), getConstructor(entity.getClass()));
        Object rowIdValue = getFieldValue(row, getField(entities.get(entityName).getKeyColumn().getName(), entity.getClass()));
        Object idValue = getFieldValue(entity, getField(entities.get(entityName).getKeyColumn().getName(), entity.getClass()));
        return rowIdValue.equals(idValue);
    }

    private <T> void updateKeyField(T entity) {
        EntityMeta meta = entities.get(Introspector.decapitalize(entity.getClass().getSimpleName()));
        Field keyField = getField(meta.getKeyColumn().getName(), entity.getClass());
        keyField.setAccessible(true);
        updateKeyField(keyField, entity);
    }

    private <T> void updateKeyField(Field keyField, T entity) {
        if (getFieldValue(entity, keyField) == null) {
            Integer id = getNextSequence(Introspector.decapitalize(entity.getClass().getSimpleName()));
            setFieldValue(entity, id, keyField);
        }
    }

    private Integer getNextSequence(String entityName) {
        ObjectValue seqObject = dbObject.property("seq");
        return getNextSequence(seqObject, entityName);
    }

    private int getNextSequence(ObjectValue seqObject, String entityName) {
        int nextSeq = seqObject.<IntValue>property(entityName).value();
        nextSeq++;
        seqObject.set(entityName, new IntValue(nextSeq));
        return nextSeq;
    }

    private <T> ArrayValue serialize(T entity) {
        ArrayValue dataArray = new ArrayValue();
        EntityMeta meta = entities.get(Introspector.decapitalize(entity.getClass().getSimpleName()));
        for (JsonValue obj : serialize(entity, meta)) {
            dataArray.push(obj);
        }
        return dataArray;
    }

    private <T> List<JsonValue> serialize(T entity, EntityMeta meta) {
        List<JsonValue> objectValues = newArrayList();

        for (String column : meta.getOrderedColumns()) {
            objectValues.add(getEntityValue(entity, column, meta.getColumns().get(column)));
        }
        return objectValues;
    }

    private <T> JsonValue getEntityValue(T entity, String column, ColumnMeta columnMeta) {
        Field columnField = getField(column, entity.getClass());
        columnField.setAccessible(true);
        return columnMeta.getParser().toJson(getFieldValue(entity, columnField));
    }

    @Override
    public <T> void delete(T entity) {
        checkIfOpened();
        checkIfMetadata(entity.getClass());

        tryToDelete(entity);
    }

    private <T> void tryToDelete(T entity) {
        ObjectValue dataObject = dbObject.property("data");
        ArrayValue entityArray = dataObject.property(Introspector.decapitalize(entity.getClass().getSimpleName()));
        removeEntity(entityArray, entity);
    }

    public static JsonDB fromFile(String file) {
        int beginIndex = file.lastIndexOf(File.separator);
        String dbName = file.substring(beginIndex == -1 ? 0 : beginIndex);
        File metaFile = new File(file + File.separator + dbName + ".meta.json");
        File dataFile = new File(file + File.separator + dbName + ".json");

        return new JsonDBImpl(metaFile, dataFile).performValidation().loadMetadata();
    }
}

//class JsonDBCreator
//{
//    public void createDb(String path, Class clazz)
//    {
//        String dbName = Introspector.decapitalize(clazz.getSimpleName());
//
//        File metaFile = new File(path + File.separator + dbName + ".meta.json");
//        if(!metaFile.exists())
//        {
//            EntityMeta entityMeta = createEntityMeta(clazz);
//            writeToFile(path, entityMeta);
//        }
//    }
//
//    private EntityMeta createEntityMeta(Class clazz)
//    {
//        //TODO
//        return null;
//    }
//
//    private void writeToFile(String path, EntityMeta entityMeta)
//    {
//        //TODO
//    }
//}

class EntityMeta {
    private String name;
    private Map<String, ColumnMeta> columns;
    private List<String> orderedColumns;

    EntityMeta(String name, Map<String, ColumnMeta> columns, List<String> orderedColumns) {
        this.name = name;
        this.columns = columns;
        this.orderedColumns = orderedColumns;
    }

    List<String> getOrderedColumns() {
        return orderedColumns;
    }

    String getName() {
        return name;
    }

    Map<String, ColumnMeta> getColumns() {
        return columns;
    }

    public ColumnMeta getKeyColumn() {
        for (ColumnMeta columnMeta : columns.values()) {
            if (columnMeta.isKey()) {
                return columnMeta;
            }
        }
        return null;
    }
}

class ColumnMeta {
    private String name;
    private String type;
    private boolean key;
    private ColumnParser columnParser;

    ColumnMeta(String name, String type, boolean key, ColumnParser columnParser) {
        this.name = name;
        this.type = type;
        this.key = key;
        this.columnParser = columnParser;
    }

    String getName() {
        return name;
    }

    String getType() {
        return type;
    }

    boolean isKey() {
        return key;
    }

    ColumnParser getParser() {
        return columnParser;
    }
}

interface ColumnParser {
    JsonValue toJson(Object obj);

    Object fromJson(JsonValue data);

    Class getType();
}

class ColumnParserBuilder {

    public static final ColumnParser INT_PARSER = new ColumnParser() {
        @Override
        public JsonValue toJson(Object obj) {
            return new IntValue((Integer) obj);
        }

        @Override
        public Object fromJson(JsonValue data) {
            return ((IntValue) data).value();
        }

        @Override
        public Class getType() {
            return Integer.class;
        }
    };
    public static final ColumnParser STRING_PARSER = new ColumnParser() {
        @Override
        public JsonValue toJson(Object obj) {
            return new StringValue((String) obj);
        }

        @Override
        public Object fromJson(JsonValue data) {
            return ((StringValue) data).value();
        }

        @Override
        public Class getType() {
            return String.class;
        }
    };
    public static final ColumnParser BOOLEAN_PARSER = new ColumnParser() {
        @Override
        public JsonValue toJson(Object obj) {
            return new BooleanValue((Boolean) obj);
        }

        @Override
        public Object fromJson(JsonValue data) {
            return ((BooleanValue) data).value();
        }

        @Override
        public Class getType() {
            return Boolean.class;
        }
    };
    public static final ColumnParser DECIMAL_PARSER = new ColumnParser() {
        @Override
        public JsonValue toJson(Object obj) {
            return new DoubleValue((Double) obj);
        }

        @Override
        public Object fromJson(JsonValue data) {
            return ((DoubleValue) data).value();
        }

        @Override
        public Class getType() {
            return Double.class;
        }
    };

    public static ColumnParser fromType(String type) {
        if (type.equals("int")) {
            return INT_PARSER;
        }

        if (type.equals("string")) {
            return STRING_PARSER;
        }

        if (type.equals("boolean")) {
            return BOOLEAN_PARSER;
        }

        if (type.equals("decimal")) {
            return DECIMAL_PARSER;
        }

        throw new DBParseException("Unsupported column type: " + type);
    }
}


