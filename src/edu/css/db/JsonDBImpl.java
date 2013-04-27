package edu.css.db;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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

/**
 * Catalin Dumitru
 * Universitatea Alexandru Ioan Cuza
 */
public class JsonDBImpl implements JsonDB {
    private final File metaFile;
    private final File dataFile;
    private final Map<String, EntityMeta> entities = new HashMap<>();

    private boolean closed = true;
    private JSONObject dbObject;

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
            JSONObject jsonObject = new JSONObject(scanner.next());
            loadMetadata(jsonObject);
        } catch (FileNotFoundException | JSONException e) {
            throw new DBParseException("Error loading metadata", e);
        }
        return this;
    }

    private void loadMetadata(JSONObject jsonObject) throws JSONException {
        JSONArray entitiesArray = jsonObject.getJSONArray("entities");

        for (int i = 0; i < entitiesArray.length(); i++) {
            JSONObject entityObject = entitiesArray.getJSONObject(i);
            EntityMeta entityMeta = createEntityMeta(entityObject);
            entities.put(entityMeta.getName(), entityMeta);
        }
    }

    private EntityMeta createEntityMeta(JSONObject entityObject) throws JSONException {
        String name = entityObject.getString("name");
        JSONArray columns = entityObject.getJSONArray("columns");
        Map<String, ColumnMeta> columnsMeta = createColumnsMeta(columns);
        List<String> orderedColumns = createOrderedColumns(columns);

        return new EntityMeta(name, columnsMeta, orderedColumns);
    }

    private List<String> createOrderedColumns(JSONArray columns) throws JSONException {
        List<String> columnsMeta = newArrayList();

        for (int i = 0; i < columns.length(); i++) {
            columnsMeta.add(columns.getJSONObject(i).getString("name"));
        }
        return columnsMeta;
    }

    private Map<String, ColumnMeta> createColumnsMeta(JSONArray columns) throws JSONException {
        Map<String, ColumnMeta> columnsMeta = new HashMap<>();

        for (int i = 0; i < columns.length(); i++) {
            ColumnMeta columnMeta = createColumnMeta(columns.getJSONObject(i));
            columnsMeta.put(columnMeta.getName(), columnMeta);
        }
        return columnsMeta;
    }

    private ColumnMeta createColumnMeta(JSONObject jsonObject) throws JSONException {
        String name = jsonObject.getString("name");
        String type = jsonObject.getString("type");
        boolean isKey = jsonObject.has("id") && jsonObject.getBoolean("id");

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
            this.dbObject = new JSONObject(scanner.next());
        } catch (FileNotFoundException | JSONException e) {
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
        try {
            return loadAll(dbObject, clazz);
        } catch (JSONException e) {
            throw new DBParseException("Error loading metadata", e);
        }
    }

    private <T> List<T> loadAll(JSONObject jsonObject, Class<T> clazz) throws JSONException {
        if (!jsonObject.has("data")) {
            return Collections.emptyList();
        }
        JSONObject dataObject = jsonObject.getJSONObject("data");
        return loadAllFromData(dataObject, clazz);
    }

    private <T> List<T> loadAllFromData(JSONObject dataObject, Class<T> clazz) throws JSONException {
        String entityName = Introspector.decapitalize(clazz.getSimpleName());
        if (!dataObject.has(entityName)) {
            return Collections.emptyList();
        }
        JSONArray entityArray = dataObject.getJSONArray(entityName);
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

    private <T> List<T> loadAllFromArray(JSONArray entityArray, EntityMeta entityMeta, Constructor<T> constructor) throws JSONException {
        List<T> rows = newArrayList();

        for (int i = 0; i < entityArray.length(); i++) {
            T row = loadRow(entityArray.getJSONArray(i), entityMeta, constructor);
            rows.add(row);
        }
        return rows;
    }

    private <T> T loadRow(JSONArray array, EntityMeta entityMeta, Constructor<T> constructor) throws JSONException {
        T row = createNewInstance(constructor);

        for (int i = 0; i < array.length(); i++) {
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

    private Object getColumnData(String columnName, Object obj, EntityMeta entityMeta) {
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
        JSONArray dataArray = serialize(entity);
        save(dataArray, entity);
    }

    private <T> void save(JSONArray dataArray, T entity) {
        try {
            JSONObject dataObject = dbObject.getJSONObject("data");
            JSONArray entityArray = dataObject.getJSONArray(Introspector.decapitalize(entity.getClass().getSimpleName()));
            saveOrUpdateEntity(dataArray, entityArray, entity);
        } catch (JSONException e) {
            throw new DBParseException("Error updating entity", e);
        }
    }

    private <T> void saveOrUpdateEntity(JSONArray dataArray, JSONArray entityArray, T entity) {
        tryToRemoveEntity(entityArray, entity);
        entityArray.put(dataArray);
    }

    private <T> void tryToRemoveEntity(JSONArray dataArray, T entity) {
        try {
            removeEntity(dataArray, entity);
        } catch (JSONException e) {
            throw new DBParseException("Error removing entity", e);
        }
    }

    private <T> void removeEntity(JSONArray dataArray, T entity) throws JSONException {
        JSONArray entityArray = findArrayForEntity(dataArray, entity);
        if (entityArray != null) {
            dataArray.remove(entityArray);
        }
    }

    private <T> JSONArray findArrayForEntity(JSONArray dataArray, T entity) throws JSONException {
        for (int i = 0; i < dataArray.length(); i++) {
            if (arrayForEntity(dataArray.getJSONArray(i), entity)) {
                return dataArray.getJSONArray(i);
            }
        }
        return null;
    }

    private <T> boolean arrayForEntity(JSONArray jsonArray, T entity) throws JSONException {
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
        try {
            JSONObject seqObject = dbObject.getJSONObject("seq");
            return getNextSequence(seqObject, entityName);
        } catch (JSONException e) {
            throw new DBParseException("Error creating sequence", e);
        }
    }

    private int getNextSequence(JSONObject seqObject, String entityName) throws JSONException {
        int nextSeq = seqObject.getInt(entityName);
        nextSeq++;
        seqObject.put(entityName, nextSeq);
        return nextSeq;
    }

    private <T> JSONArray serialize(T entity) {
        JSONArray dataArray = new JSONArray();
        EntityMeta meta = entities.get(Introspector.decapitalize(entity.getClass().getSimpleName()));
        for (Object obj : serialize(entity, meta)) {
            dataArray.put(obj);
        }
        return dataArray;
    }

    private <T> List<Object> serialize(T entity, EntityMeta meta) {
        List<Object> objectValues = newArrayList();

        for (String column : meta.getOrderedColumns()) {
            objectValues.add(getEntityValue(entity, column, meta.getColumns().get(column)));
        }
        return objectValues;
    }

    private <T> Object getEntityValue(T entity, String column, ColumnMeta columnMeta) {
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
        try {
            JSONObject dataObject = dbObject.getJSONObject("data");
            JSONArray entityArray = dataObject.getJSONArray(Introspector.decapitalize(entity.getClass().getSimpleName()));
            removeEntity(entityArray, entity);
        } catch (JSONException e) {
            throw new DBParseException("Error updating entity", e);
        }
    }

    public static JsonDB fromFile(String file) {
        int beginIndex = file.lastIndexOf(File.separator);
        String dbName = file.substring(beginIndex == -1 ? 0 : beginIndex);
        File metaFile = new File(file + File.separator + dbName + ".meta.json");
        File dataFile = new File(file + File.separator + dbName + ".json");

        return new JsonDBImpl(metaFile, dataFile).performValidation().loadMetadata();
    }
}

class JsonDBCreator
{
    public void createDb(String path, Class clazz)
    {
        String dbName = Introspector.decapitalize(clazz.getSimpleName());

        File metaFile = new File(path + File.separator + dbName + ".meta.json");
        if(!metaFile.exists())
        {
            EntityMeta entityMeta = createEntityMeta(clazz);
            writeToFile(path, entityMeta);
        }
    }

    private EntityMeta createEntityMeta(Class clazz)
    {
        //TODO
        return null;
    }

    private void writeToFile(String path, EntityMeta entityMeta)
    {
        //TODO
    }
}

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
    Object toJson(Object obj);

    Object fromJson(Object data);

    Class getType();
}

class ColumnParserBuilder {

    public static final ColumnParser INT_PARSER = new ColumnParser() {
        @Override
        public Object toJson(Object obj) {
            return obj;
        }

        @Override
        public Object fromJson(Object data) {
            return data;
        }

        @Override
        public Class getType() {
            return Integer.class;
        }
    };
    public static final ColumnParser STRING_PARSER = new ColumnParser() {
        @Override
        public Object toJson(Object obj) {
            return obj;
        }

        @Override
        public Object fromJson(Object data) {
            return data;
        }

        @Override
        public Class getType() {
            return String.class;
        }
    };
    public static final ColumnParser BOOLEAN_PARSER = new ColumnParser() {
        @Override
        public Object toJson(Object obj) {
            return obj;
        }

        @Override
        public Object fromJson(Object data) {
            return data;
        }

        @Override
        public Class getType() {
            return Boolean.class;
        }
    };
    public static final ColumnParser DECIMAL_PARSER = new ColumnParser() {
        @Override
        public Object toJson(Object obj) {
            return obj;
        }

        @Override
        public Object fromJson(Object data) {
            if (data instanceof Integer) {
                return ((Integer) data).doubleValue();
            }
            return data;
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


