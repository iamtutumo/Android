package com.winbeelabs.schools.helper;

import org.json.JSONObject;

public class helper_string
{
    /**
     * Returns string value from json if not null, without throwing exception
     * @param json
     * @param key
     * @return
     */
    public static String optString(JSONObject json, String key)
    {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key))
            return null;
        else
            return json.optString(key, null);
    }

    /**
     * Returns int value from json if not null, without throwing exception
     * @param json
     * @param key
     * @return
     */
    public static Integer optInt(JSONObject json, String key)
    {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key))
            return 0;
        else
            return json.optInt(key, 0);
    }
}
