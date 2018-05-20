/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.softmed.uzazi_salama.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import com.softmed.uzazi_salama.Repository.ClientFollowupPersonObject;
import com.softmed.uzazi_salama.Repository.FacilityObject;
import com.softmed.uzazi_salama.Repository.ReferralServiceObject;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.Indicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Class containing some static utility methods.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    private Utils() {
    }

    ;


    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    public static ClientFollowupPersonObject convertToClientFollowupPersonObject(CommonPersonObject commonPersonObject) {
        String details = commonPersonObject.getColumnmaps().get("details");
        Log.d(TAG, "details string commonPersonObject = " + convertStandardJSONString(details));
        try {
            long visitDate = 0,dob=0,referralDate=0;
            try{
                visitDate = Long.parseLong(commonPersonObject.getColumnmaps().get("visit_date"));

            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                dob = Long.parseLong(commonPersonObject.getColumnmaps().get("date_of_birth"));

            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                referralDate = Long.parseLong(commonPersonObject.getColumnmaps().get("referral_date"));

            }catch (Exception e){
                e.printStackTrace();
            }

            return new ClientFollowupPersonObject(
                    commonPersonObject.getColumnmaps().get("id"),
                    commonPersonObject.getColumnmaps().get("relationalid"),
                    commonPersonObject.getColumnmaps().get("first_name"),
                    commonPersonObject.getColumnmaps().get("middle_name"),
                    commonPersonObject.getColumnmaps().get("surname"),
                    commonPersonObject.getColumnmaps().get("community_based_hiv_service"),
                    commonPersonObject.getColumnmaps().get("ctc_number"),
                    commonPersonObject.getColumnmaps().get("is_valid"),
                    commonPersonObject.getColumnmaps().get("referral_reason"),
                    commonPersonObject.getColumnmaps().get("gender"),
                    commonPersonObject.getColumnmaps().get("phone_number"),
                    commonPersonObject.getColumnmaps().get("care_taker_name"),
                    commonPersonObject.getColumnmaps().get("care_taker_name_phone_number"),
                    commonPersonObject.getColumnmaps().get("care_taker_relationship"),
                    commonPersonObject.getColumnmaps().get("facility_id"),
                    commonPersonObject.getColumnmaps().get("referral_service_id"),
                    commonPersonObject.getColumnmaps().get("referral_status"),visitDate,dob,referralDate,
                    commonPersonObject.getColumnmaps().get("village"),
                    commonPersonObject.getColumnmaps().get("details")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public static FacilityObject convertToFacilityObject(CommonPersonObject commonPersonObject) {
        try {
            return new FacilityObject(
                    commonPersonObject.getColumnmaps().get("id"),
                    commonPersonObject.getColumnmaps().get("name")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    } public static Indicator convertToIndicatorObject(CommonPersonObject commonPersonObject) {
        String details = commonPersonObject.getColumnmaps().get("indicatorName");
        try {
            return new Indicator(
                    commonPersonObject.getColumnmaps().get("id"),
                    commonPersonObject.getColumnmaps().get("referralIndicatorId"),
                    commonPersonObject.getColumnmaps().get("indicatorName"),
                    commonPersonObject.getColumnmaps().get("isActive")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static ReferralServiceObject convertToServiceObject(CommonPersonObject commonPersonObject) {
        try {
            return new ReferralServiceObject(
                    commonPersonObject.getColumnmaps().get("id"),
                    commonPersonObject.getColumnmaps().get("name"),
                    commonPersonObject.getColumnmaps().get("category")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public static List<ClientFollowupPersonObject> convertToClientFollowupPersonObjectList(List<CommonPersonObject> commonPersonObjectsList) {
        List<ClientFollowupPersonObject> clientFollowupPersonObjects = new ArrayList<>();
        for (CommonPersonObject common : commonPersonObjectsList) {
            clientFollowupPersonObjects.add(convertToClientFollowupPersonObject(common));

        }

        return clientFollowupPersonObjects;
    }


    public static List<FacilityObject> convertToFacilityObjectList(List<CommonPersonObject> commonPersonObjectsList) {
        List<FacilityObject> facilityObjects = new ArrayList<>();
        for (CommonPersonObject common : commonPersonObjectsList) {
            facilityObjects.add(convertToFacilityObject(common));
        }


        return facilityObjects;
    }
    public static List<Indicator> convertToIndicatorList(List<CommonPersonObject> commonPersonObjectsList) {
        List<Indicator> indicatorObjects = new ArrayList<>();
        for (CommonPersonObject common : commonPersonObjectsList) {
            indicatorObjects.add(convertToIndicatorObject(common));
        }


        return indicatorObjects;
    }
    public static List<ReferralServiceObject> convertToServiceObjectList(List<CommonPersonObject> commonPersonObjectsList) {
        List<ReferralServiceObject> facilityObjects = new ArrayList<>();
        for (CommonPersonObject common : commonPersonObjectsList) {
            facilityObjects.add(convertToServiceObject(common));
        }


        return facilityObjects;
    }
    public static String convertStandardJSONString(String data_json) {

        data_json = data_json.replaceAll("\\\\r\\\\n", "");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        data_json = data_json.replace("\\", "");
        if(data_json.contains("indicator_ids")){
            int start = data_json.indexOf("[");
            int end = data_json.indexOf("]");
            String indicator = data_json.substring(start,end);
            String indicator1 = data_json.substring(start,end);
            Log.d(TAG,"indicator b4 "+indicator);
            indicator1 = indicator1.replace('\"','*');
            indicator1 = indicator1.replace("*","(+");
            String indicator2 = indicator1.replace('+','"');
            indicator2 = indicator2.replace('(','\\');
            Log.d(TAG,"indicator after "+indicator2);
            data_json = data_json.replace(indicator,indicator2);
        }
        return data_json;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String generateRandomUUIDString(){
        return UUID.randomUUID().toString();
    }

    public static void sendRegistrationAlert(String phoneNumber){
        Log.d(TAG,"sending registration alerts to rapid pro server");
        JSONObject object = new JSONObject();
        try {
            object.put("urns",new JSONArray().put("tel:"+phoneNumber));
            object.put("flow","c342cb9e-67a6-4a11-b063-1ac04fbbc26f");
            Log.d(TAG,"payload = "+object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        URL url = null;
        try {
            url = new URL("http://139.162.151.34:8080/api/v2/flow_starts.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Token 7dae598d311c91fb3907687fdf6ed900fecb1a05");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestMethod("POST");

            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(object.toString().getBytes("UTF-8"));
            os.flush();
            os.close();


            int responseCode = conn.getResponseCode();
            Log.d(TAG,"POST Response Code :: " + responseCode);

            // read the response
            InputStream in  = conn.getInputStream();

            String result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            JSONObject jsonObject = new JSONObject(result);

            Log.d(TAG,"response is "+jsonObject.toString());

            in.close();
            os.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static long calculateEDDFromLNMP(long lnmp) {
        long day = 24 * 60 * 60 * 1000;
        return (day * 281) + lnmp;
    }

}
