package org.ei.opensrp.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.ClientFollowup;
import org.ei.opensrp.domain.ClientReferral;

import java.util.ArrayList;
import java.util.List;

import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.apache.commons.lang3.StringUtils.repeat;

public class ClientFollowupRepository extends DrishtiRepository {
    private static final String CHILD_SQL = "CREATE TABLE anc_followup_client(" +
            "id VARCHAR PRIMARY KEY, " +
            "relationalid VARCHAR, " +
            "first_name VARCHAR, " +
            "middle_name VARCHAR, " +
            "surname VARCHAR, " +
            "phone_number VARCHAR, " +
            "ward VARCHAR, " +
            "village VARCHAR, " +
            "map_cue VARCHAR, " +
            "date_of_birth VARCHAR, " +
            "spouse_name VARCHAR, " +
            "edd VARCHAR, " +
            "referral_date VARCHAR, " +
            "facility_id VARCHAR, " +
            "referral_reason VARCHAR, " +
            "referral_status VARCHAR, " +
            "comment VARCHAR, " +
            "facility_id VARCHAR, " +
            "referral_feedback VARCHAR, " +
            "service_provider_uuid VARCHAR, " +
            "details VARCHAR, " +
            "referral_uuid VARCHAR, " +
            "service_provider_uuid VARCHAR, " +
            "is_valid VARCHAR)";

    public static final String TABLE_NAME = "anc_followup_client";
    public static final String ID_COLUMN = "id";
    public static final String Relational_ID = "relationalid";
    public static final String FIRST_NAME = "first_name";
    public static final String MIDDLE_NAME = "middle_name";
    public static final String SURNAME = "surname";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String DATE_OF_BIRTH = "date_of_birth";
    public static final String EDD = "edd";
    public static final String VILLAGE = "village";
    public static final String MAP_CUE = "map_cue";
    public static final String SPOUSE_NAME = "spouse_name";
    public static final String REFERRAL_DATE = "referral_date";
    public static final String FACILITY_ID = "facility_id";
    public static final String REFERRAL_REASON = "referral_reason";
    public static final String REFERRAL_STATUS = "referral_status";
    public static final String REFERRAL_UUID = "referral_uuid";
    public static final String COMMENT = "comment";
    public static final String SERVICE_PROVIDER_UUID = "service_provider_uuid";
    public static final String IS_VALID = "is_valid";
    public static final String[] CLIENT_REFERRAL_TABLE_COLUMNS = {
            ID_COLUMN,
            Relational_ID,
            FIRST_NAME,
            MIDDLE_NAME,
            SURNAME,
            PHONE_NUMBER,
            VILLAGE,
            MAP_CUE,
            DATE_OF_BIRTH,
            EDD,
            SPOUSE_NAME,
            REFERRAL_DATE,
            FACILITY_ID,
            REFERRAL_REASON,
            COMMENT,
            REFERRAL_STATUS,
            SERVICE_PROVIDER_UUID,
            REFERRAL_UUID,
            IS_VALID};

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(CHILD_SQL);
    }

    public void add(ClientFollowup clientReferral) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.insert(TABLE_NAME, null, createValuesFor(clientReferral));
    }

    public void update(ClientFollowup clientReferral) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.update(TABLE_NAME, createValuesFor(clientReferral), ID_COLUMN + " = ?", new String[]{String.valueOf(clientReferral.getId())});
    }
    public void customUpdate(ContentValues contentValues, String caseId) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        Log.d("customInsert", "tableName = " + TABLE_NAME);
        database.update(TABLE_NAME, contentValues, ID_COLUMN + " = ?", new String[]{caseId});

    }


    public List<ClientFollowup> all() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, CLIENT_REFERRAL_TABLE_COLUMNS, null, null, null, null, null);
        return readAll(cursor);
    }

    public ClientFollowup find(String caseId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, CLIENT_REFERRAL_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId}, null, null, null, null);
        List<ClientFollowup> children = readAll(cursor);

        if (children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }

    public List<ClientFollowup> findClientReferralByCaseIds(String... caseIds) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery(String.format("SELECT * FROM %s WHERE %s IN (%s)", TABLE_NAME, ID_COLUMN, insertPlaceholdersForInClause(caseIds.length)), caseIds);
        return readAll(cursor);
    }
    public ClientFollowup findByServiceStatus(String name) {

        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, CLIENT_REFERRAL_TABLE_COLUMNS, REFERRAL_STATUS + " = ?", new String[]{name}, null, null, null, null);
        List<ClientFollowup> children = readAll(cursor);

        if (children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }
    public void updateStatus(String caseId, String name ){
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REFERRAL_STATUS, name);
        database.update(TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public List<ClientFollowup> findByServiceCaseId(String caseId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, CLIENT_REFERRAL_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId}, null, null, null, null);
        return readAll(cursor);
    }

    public long count() {
        return longForQuery(masterRepository.getReadableDatabase(), "SELECT COUNT(1) FROM " + TABLE_NAME, new String[0]);
    }

    public long unsuccesfulcount() {
        return longForQuery(masterRepository.getReadableDatabase(), "SELECT COUNT(1) FROM " + TABLE_NAME
                        + " WHERE " + REFERRAL_STATUS + " = ? ",
                new String[]{"0"});
    }
    public long succesfulcount() {
        return longForQuery(masterRepository.getReadableDatabase(), "SELECT COUNT(1) FROM " + TABLE_NAME
                        + " WHERE " + REFERRAL_STATUS + " = ? ",
                new String[]{"1"});
    }
    private String tableColumnsForQuery(String tableName, String[] tableColumns) {
        return StringUtils.join(prepend(tableColumns, tableName), ", ");
    }

    private String[] prepend(String[] input, String tableName) {
        int length = input.length;
        String[] output = new String[length];
        for (int index = 0; index < length; index++) {
            output[index] = tableName + "." + input[index] + " as " + tableName + input[index];
        }
        return output;
    }


    public ContentValues createValuesFor(ClientFollowup clientFollowup) {

        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, clientFollowup.getId());
        values.put(Relational_ID, clientFollowup.getRelationalid());
        values.put(FIRST_NAME, clientFollowup.getFirst_name());
        values.put(MIDDLE_NAME, clientFollowup.getMiddle_name());
        values.put(SURNAME, clientFollowup.getSurname());
        values.put(PHONE_NUMBER, clientFollowup.getPhone_number());
        values.put(VILLAGE, clientFollowup.getVillage());
        values.put(MAP_CUE, clientFollowup.getMap_cue());
        values.put(DATE_OF_BIRTH, clientFollowup.getDate_of_birth());
        values.put(EDD, clientFollowup.getEdd());
        values.put(SPOUSE_NAME, clientFollowup.getSpouse_name());
        values.put(REFERRAL_DATE, clientFollowup.getReferral_date());
        values.put(FACILITY_ID, clientFollowup.getFacility_id());
        values.put(REFERRAL_REASON, clientFollowup.getReferral_reason());
        values.put(COMMENT, clientFollowup.getComment());
        values.put(REFERRAL_STATUS, clientFollowup.getReferral_status());
        values.put(SERVICE_PROVIDER_UUID, clientFollowup.getService_provider_uuid());
        values.put(IS_VALID, clientFollowup.is_valid());

        return values;
    }

    public ContentValues createValuesUpdateValues(ClientFollowup clientReferral) {

        ContentValues values = new ContentValues();
        values.put(REFERRAL_STATUS, clientReferral.getReferral_status());
        values.put(COMMENT, clientReferral.getReferral_status());
        //TODO finish up updating other values

        return values;
    }

    public List<ClientFollowup> RawCustomQueryForAdapter(String query) {
        Log.i(getClass().getName(), query);
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        return readAll(cursor);
    }

    private List<ClientFollowup> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<ClientFollowup> referralServicesListDataModel = new ArrayList<ClientFollowup>();
        while (!cursor.isAfterLast()) {

            ClientFollowup clientReferral = new ClientFollowup();
            clientReferral.setId(cursor.getString(cursor.getColumnIndex(ID_COLUMN)));
            clientReferral.setRelationalid(cursor.getString(cursor.getColumnIndex(Relational_ID)));
            clientReferral.setFirst_name(cursor.getString(cursor.getColumnIndex(FIRST_NAME)));
            clientReferral.setMiddle_name(cursor.getString(cursor.getColumnIndex(MIDDLE_NAME)));
            clientReferral.setSurname(cursor.getString(cursor.getColumnIndex(SURNAME)));
            clientReferral.setPhone_number(cursor.getString(cursor.getColumnIndex(PHONE_NUMBER)));
            clientReferral.setVillage(cursor.getString(cursor.getColumnIndex(VILLAGE)));
            clientReferral.setMap_cue(cursor.getString(cursor.getColumnIndex(MAP_CUE)));
            clientReferral.setDate_of_birth(cursor.getLong(cursor.getColumnIndex(DATE_OF_BIRTH)));
            clientReferral.setEdd(cursor.getLong(cursor.getColumnIndex(EDD)));
            clientReferral.setSpouse_name(cursor.getString(cursor.getColumnIndex(SPOUSE_NAME)));
            clientReferral.setReferral_date(cursor.getLong(cursor.getColumnIndex(REFERRAL_DATE)));
            clientReferral.setFacility_id(cursor.getString(cursor.getColumnIndex(FACILITY_ID)));
            clientReferral.setReferral_reason(cursor.getString(cursor.getColumnIndex(REFERRAL_REASON)));
            clientReferral.setComment(cursor.getString(cursor.getColumnIndex(COMMENT)));
            clientReferral.setReferral_status(cursor.getInt(cursor.getColumnIndex(REFERRAL_STATUS)));
            clientReferral.setReferral_uuid(cursor.getString(cursor.getColumnIndex(REFERRAL_UUID)));
            clientReferral.setService_provider_uuid(cursor.getString(cursor.getColumnIndex(SERVICE_PROVIDER_UUID)));
            clientReferral.setDetails(cursor.getString(cursor.getColumnIndex("details")));
            clientReferral.setIs_valid(cursor.getInt(cursor.getColumnIndex(IS_VALID))==1);

            referralServicesListDataModel.add(clientReferral);
            cursor.moveToNext();
        }
        cursor.close();
        return referralServicesListDataModel;
    }

    private String getColumnValueByAlias(Cursor cursor, String table, String column) {
        return cursor.getString(cursor.getColumnIndex(table + column));
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }


    public void delete(String childId) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.delete(TABLE_NAME, ID_COLUMN + "= ?", new String[]{childId});
    }
}
