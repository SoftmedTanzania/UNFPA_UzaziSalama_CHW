package com.softmed.uzazi_salama.Repository;

import android.content.ContentValues;

import static org.ei.opensrp.commonregistry.CommonRepository.DETAILS_COLUMN;
import static org.ei.opensrp.commonregistry.CommonRepository.ID_COLUMN;
import static org.ei.opensrp.commonregistry.CommonRepository.Relational_ID;

/**
 * Created by Kency on 14/09/2017.
 */

public class FollowUpRepository {

    public static final String Comment = "comment";
    public static final String Date = "follow_up_date";
    public static final String Reason = "follow_up_reason";
    public static final String IS_VALID = "is_valid";


    public ContentValues createValuesFor(FollowUpPersonObject followUp) {

        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, followUp.getId());
        values.put(Relational_ID, followUp.getRelationalId());
        values.put(Comment, followUp.getComment());
        values.put(Date, followUp.getFollow_up_date());
        values.put(Reason, followUp.getFollow_up_reason());
        values.put(IS_VALID, followUp.getIs_valid());
        values.put(DETAILS_COLUMN, followUp.getDetails());
        return values;
    }

}
