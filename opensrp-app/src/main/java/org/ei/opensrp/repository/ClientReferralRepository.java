package org.ei.opensrp.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.ClientReferral;

import java.util.ArrayList;
import java.util.List;

import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.apache.commons.lang3.StringUtils.repeat;

public class ClientReferralRepository extends DrishtiRepository {
    private static final String CHILD_SQL = "CREATE TABLE ClientReferral(" +
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
            "pmtct_status VARCHAR, " +
            "height_below_average VARCHAR, " +
            "level_of_education VARCHAR, " +
            "spouse_name VARCHAR, " +
            "gravida VARCHAR, " +
            "para VARCHAR, " +
            "edd VARCHAR, " +
            "lmnp_date VARCHAR, " +
            "referral_date VARCHAR, " +
            "facility_id VARCHAR, " +
            "referral_reason VARCHAR, " +
            "referral_status VARCHAR, " +
            "other_notes VARCHAR, " +
            "referral_feedback VARCHAR, " +
            "service_provider_uiid VARCHAR, " +
            "is_valid VARCHAR)";


    public static final String CLIENT_REFERRAL = "client_referral";
    public static final String ID_COLUMN = "id";
    public static final String Relational_ID = "relationalid";
    public static final String FIRST_NAME = "first_name";
    public static final String MIDDLE_NAME = "middle_name";
    public static final String SURNAME = "surname";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String WARD = "ward";
    public static final String VILLAGE = "village";
    public static final String MAP_CUE = "map_cue";
    public static final String DATE_OF_BIRTH = "date_of_birth";
    public static final String PMTCT_STATUS = "pmtct_status";
    public static final String HEIGHT_BELOW_AVERAGE = "height_below_average";
    public static final String LEVEL_OF_EDUCATION = "level_of_education";
    public static final String SPOUSE_NAME = "spouse_name";
    public static final String GRAVIDA = "gravida";
    public static final String PARA = "para";
    public static final String EDD = "edd";
    public static final String LNMP_DATE = "lmnp_date";
    public static final String REFERRAL_DATE = "referral_date";
    public static final String FACILITY_ID = "facility_id";
    public static final String REFERRAL_REASON = "referral_reason";
    public static final String REFERRAL_STATUS = "referral_status";
    public static final String OTHER_NOTES = "other_notes";
    public static final String REFERRAL_FEEDBACK = "referral_feedback";
    public static final String SERVICE_PROVIDER_UUID = "service_provider_uiid";
    public static final String IS_VALID = "is_valid";
    public static final String[] CLIENT_REFERRAL_TABLE_COLUMNS = {
            ID_COLUMN,
            Relational_ID,
            FIRST_NAME,
            MIDDLE_NAME,
            SURNAME,
            PHONE_NUMBER,
            WARD,
            VILLAGE,
            MAP_CUE,
            GRAVIDA,
            PARA,
            DATE_OF_BIRTH,
            LNMP_DATE,
            EDD,
            PMTCT_STATUS,
            HEIGHT_BELOW_AVERAGE,
            SPOUSE_NAME,
            LEVEL_OF_EDUCATION,
            REFERRAL_DATE,
            FACILITY_ID,
            REFERRAL_REASON,
            REFERRAL_FEEDBACK,
            OTHER_NOTES,
            REFERRAL_STATUS,
            SERVICE_PROVIDER_UUID,
            IS_VALID};

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(CHILD_SQL);
    }

    public void add(ClientReferral clientReferral) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.insert(CLIENT_REFERRAL, null, createValuesFor(clientReferral));
    }

    public void update(ClientReferral clientReferral) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.update(CLIENT_REFERRAL, createValuesFor(clientReferral), ID_COLUMN + " = ?", new String[]{String.valueOf(clientReferral.getId())});
    }

    public List<ClientReferral> all() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(CLIENT_REFERRAL, CLIENT_REFERRAL_TABLE_COLUMNS, null, null, null, null, null);
        return readAll(cursor);
    }

    public ClientReferral find(String caseId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(CLIENT_REFERRAL, CLIENT_REFERRAL_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId}, null, null, null, null);
        List<ClientReferral> children = readAll(cursor);

        if (children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }

    public List<ClientReferral> findClientReferralByCaseIds(String... caseIds) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery(String.format("SELECT * FROM %s WHERE %s IN (%s)", CLIENT_REFERRAL, ID_COLUMN, insertPlaceholdersForInClause(caseIds.length)), caseIds);
        return readAll(cursor);
    }
    public ClientReferral findByServiceStatus(String name) {

        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(CLIENT_REFERRAL, CLIENT_REFERRAL_TABLE_COLUMNS, REFERRAL_STATUS + " = ?", new String[]{name}, null, null, null, null);
        List<ClientReferral> children = readAll(cursor);

        if (children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }
    public void updateStatus(String caseId, String name ){
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REFERRAL_STATUS, name);
        database.update(CLIENT_REFERRAL, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public List<ClientReferral> findByServiceCaseId(String caseId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(CLIENT_REFERRAL, CLIENT_REFERRAL_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId}, null, null, null, null);
        return readAll(cursor);
    }

    public long count() {
        return longForQuery(masterRepository.getReadableDatabase(), "SELECT COUNT(1) FROM " + CLIENT_REFERRAL, new String[0]);
    }

    public long unsuccesfulcount() {
        return longForQuery(masterRepository.getReadableDatabase(), "SELECT COUNT(1) FROM " + CLIENT_REFERRAL
                        + " WHERE " + REFERRAL_STATUS + " = ? ",
                new String[]{"0"});
    }
    public long succesfulcount() {
        return longForQuery(masterRepository.getReadableDatabase(), "SELECT COUNT(1) FROM " + CLIENT_REFERRAL
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


    public ContentValues createValuesFor(ClientReferral clientReferral) {

        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, clientReferral.getId());
        values.put(Relational_ID, clientReferral.getRelationalid());
        values.put(FIRST_NAME, clientReferral.getFirst_name());
        values.put(MIDDLE_NAME, clientReferral.getMiddle_name());
        values.put(SURNAME, clientReferral.getSurname());
        values.put(PHONE_NUMBER, clientReferral.getPhone_number());
        values.put(WARD, clientReferral.getWard());
        values.put(VILLAGE, clientReferral.getVillage());
        values.put(MAP_CUE, clientReferral.getMap_cue());
        values.put(GRAVIDA, clientReferral.getGravida());
        values.put(PARA, clientReferral.getPara());
        values.put(DATE_OF_BIRTH, clientReferral.getDate_of_birth());
        values.put(LNMP_DATE, clientReferral.getLmnp_date());
        values.put(EDD, clientReferral.getEdd());
        values.put(PMTCT_STATUS, clientReferral.getPmtct_status());
        values.put(HEIGHT_BELOW_AVERAGE, clientReferral.isHeight_below_average());
        values.put(SPOUSE_NAME, clientReferral.getSpouse_name());
        values.put(LEVEL_OF_EDUCATION, clientReferral.getLevel_of_education());
        values.put(REFERRAL_DATE, clientReferral.getReferral_date());
        values.put(FACILITY_ID, clientReferral.getFacility_id());
        values.put(REFERRAL_REASON, clientReferral.getReferral_reason());
        values.put(REFERRAL_FEEDBACK, clientReferral.getReferral_feedback());
        values.put(OTHER_NOTES, clientReferral.getOther_notes());
        values.put(REFERRAL_STATUS, clientReferral.getReferral_status());
        values.put(SERVICE_PROVIDER_UUID, clientReferral.getService_provider_uiid());
        values.put(IS_VALID, clientReferral.is_valid());

        return values;
    }

    public ContentValues createValuesUpdateValues(ClientReferral clientReferral) {

        ContentValues values = new ContentValues();
        values.put(REFERRAL_STATUS, clientReferral.getReferral_status());
        values.put(REFERRAL_FEEDBACK, clientReferral.getReferral_status());
        values.put(OTHER_NOTES, clientReferral.getReferral_status());
        //TODO finish up updating other values


        return values;
    }


    private List<ClientReferral> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<ClientReferral> referralServicesListDataModel = new ArrayList<ClientReferral>();
        while (!cursor.isAfterLast()) {

            ClientReferral clientReferral = new ClientReferral();
            clientReferral.setId(cursor.getInt(cursor.getColumnIndex(ID_COLUMN)));
            clientReferral.setRelationalid(cursor.getInt(cursor.getColumnIndex(Relational_ID)));
            clientReferral.setFirst_name(cursor.getString(cursor.getColumnIndex(FIRST_NAME)));
            clientReferral.setMiddle_name(cursor.getString(cursor.getColumnIndex(MIDDLE_NAME)));
            clientReferral.setSurname(cursor.getString(cursor.getColumnIndex(SURNAME)));
            clientReferral.setPhone_number(cursor.getString(cursor.getColumnIndex(PHONE_NUMBER)));
            clientReferral.setWard(cursor.getString(cursor.getColumnIndex(WARD)));
            clientReferral.setVillage(cursor.getString(cursor.getColumnIndex(VILLAGE)));
            clientReferral.setMap_cue(cursor.getString(cursor.getColumnIndex(MAP_CUE)));
            clientReferral.setGravida(cursor.getInt(cursor.getColumnIndex(GRAVIDA)));
            clientReferral.setPara(cursor.getInt(cursor.getColumnIndex(PARA)));
            clientReferral.setDate_of_birth(cursor.getLong(cursor.getColumnIndex(DATE_OF_BIRTH)));
            clientReferral.setLmnp_date(cursor.getLong(cursor.getColumnIndex(LNMP_DATE)));
            clientReferral.setEdd(cursor.getLong(cursor.getColumnIndex(EDD)));
            clientReferral.setPmtct_status(cursor.getInt(cursor.getColumnIndex(PMTCT_STATUS)));
            clientReferral.setHeight_below_average(cursor.getInt(cursor.getColumnIndex(HEIGHT_BELOW_AVERAGE))==1);
            clientReferral.setSpouse_name(cursor.getString(cursor.getColumnIndex(SPOUSE_NAME)));
            clientReferral.setLevel_of_education(cursor.getInt(cursor.getColumnIndex(LEVEL_OF_EDUCATION)));
            clientReferral.setReferral_date(cursor.getLong(cursor.getColumnIndex(REFERRAL_DATE)));
            clientReferral.setFacility_id(cursor.getString(cursor.getColumnIndex(FACILITY_ID)));
            clientReferral.setReferral_reason(cursor.getString(cursor.getColumnIndex(REFERRAL_REASON)));
            clientReferral.setReferral_feedback(cursor.getString(cursor.getColumnIndex(REFERRAL_FEEDBACK)));
            clientReferral.setOther_notes(cursor.getString(cursor.getColumnIndex(OTHER_NOTES)));
            clientReferral.setReferral_status(cursor.getInt(cursor.getColumnIndex(REFERRAL_STATUS)));
            clientReferral.setService_provider_uiid(cursor.getString(cursor.getColumnIndex(SERVICE_PROVIDER_UUID)));
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
        database.delete(CLIENT_REFERRAL, ID_COLUMN + "= ?", new String[]{childId});
    }
}
