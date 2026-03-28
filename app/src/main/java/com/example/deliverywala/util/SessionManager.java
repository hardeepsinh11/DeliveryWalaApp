package com.example.deliverywala.util;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;

/**
 * Manages user session and preferences using SharedPreferences
 * Handles login state, user details, and various application preferences
 */
public class SessionManager {
    // Shared Preferences references
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editor_check_list;
    private SharedPreferences.Editor editor_total;
    private SharedPreferences.Editor editor_completed;
    private SharedPreferences.Editor editor_pending;
    private SharedPreferences pref_list;
    private SharedPreferences pref_check_list;
    private Context _context;
    private int PRIVATE_MODE = 0;

    // Preference file names
    private static final String PREF_NAME = "main_pref";
    private static final String TOTAL_PREF_NAME = "main_pref";
    private static final String COMPLETE_PREF_NAME = "com_pref";

    // Preference keys
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_UID = "u_id";
    public static final String KEY_PASS = "u_password";
    public static final String KEY_OBJSEC = "objSec";
    public static final String KEY_UT = "uType";
    public static final String KEY_LASTUPDATE = "last_update";
    public static final String KEY_DATA = "datas";
    public static final String KEY_EMAIL = "email";
    public static final String CHECKED_ITEM = "checked_item";
    public static final String KEY_TOTAL_PAPER = "tot_papers";
    public static final String KEY_PAPER_ID = "paper_id";
    public static final String KEY_COMPLETED_PAPER = "completed_papers";

    /**
     * Constructor - Initializes all SharedPreferences instances
     * @param context Application context used to access SharedPreferences
     */
    public SessionManager(Context context) {
        try {
            this._context = context;
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
            pref_check_list = _context.getSharedPreferences(COMPLETE_PREF_NAME, PRIVATE_MODE);
            pref_list = _context.getSharedPreferences(TOTAL_PREF_NAME, PRIVATE_MODE);
            editor_check_list = pref_check_list.edit();
            editor_total = pref_list.edit();
            editor_completed = pref_list.edit();
            editor_pending = pref_list.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a basic login session with user details
     * @param name User's display name
     * @param email User's email address
     * @param p_id Associated paper ID
     */
    public void createLoginSession(String name, String email, String p_id) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PAPER_ID, p_id);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    /**
     * Stores dashboard related data in preferences
     * @param lastUpdate Timestamp of last update
     * @param datas Serialized dashboard data
     */
    public void createDashData(String lastUpdate, String datas) {
        editor.putString(KEY_DATA, datas);
        editor.putString(KEY_LASTUPDATE, lastUpdate);
        editor.commit();
    }

    /**
     * Creates a complete login session with all user credentials
     * @param uid Unique user identifier
     * @param objsec Security token
     * @param ut User type
     * @param names User's full name
     * @param pass User's password (should be encrypted)
     */
    public void createLogin(String uid, String objsec, String ut, String names, String pass) {
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_OBJSEC, objsec);
        editor.putString(KEY_UT, ut);
        editor.putString(KEY_NAME, names);
        editor.putString(KEY_PASS, pass);
        editor.putBoolean(IS_LOGIN, true);
        editor.commit();
    }

    /**
     * Retrieves the stored paper ID
     * @return Paper ID as String, returns "0" if not found
     */
    public String getClickID() {
        return pref.getString(KEY_PAPER_ID, "0");
    }

    /**
     * Stores the total count of papers
     * @param tot_papers Total number of papers
     */
    public void createTotalpapers(int tot_papers) {
        editor_total.putInt(KEY_TOTAL_PAPER, tot_papers);
        editor_total.commit();
    }

    /**
     * Retrieves the total count of papers
     * @return Total paper count, returns 0 if not found
     */
    public int getTotalPaper() {
        return pref.getInt(KEY_TOTAL_PAPER, 0);
    }

    /**
     * Stores the count of completed papers
     * @param c_papers Number of completed papers
     */
    public void createCompletedpapers(int c_papers) {
        editor_total.putInt(KEY_COMPLETED_PAPER, c_papers);
        editor_total.commit();
    }

    /**
     * Retrieves the count of completed papers
     * @return Completed paper count, returns 0 if not found
     */
    public int getCompletedpapers() {
        return pref.getInt(KEY_COMPLETED_PAPER, 0);
    }

    /**
     * Stores the checked items list
     * @param checked_item Serialized list of checked items
     */
    public void createCheckSession(String checked_item) {
        editor_check_list.putString(CHECKED_ITEM, checked_item);
        editor_check_list.commit();
    }

    /**
     * Retrieves the list of checked items
     * @return Serialized checked items, returns empty string if not found
     */
    public String getCheckedList() {
        return pref_check_list.getString(CHECKED_ITEM, "");
    }

    /**
     * Updates the remaining items list
     * @param checked_item Serialized list of remaining items
     */
    public void remain_list(String checked_item) {
        editor_check_list.putString(CHECKED_ITEM, checked_item);
        editor_check_list.commit();
    }

    /**
     * Checks if user is logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean checkLogin() {
        return isLoggedIn();
    }

    /**
     * Retrieves all stored user details
     * @return HashMap containing user credentials and login state
     */
    public HashMap<String, String> getuserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_UID, pref.getString(KEY_UID, ""));
        user.put(KEY_UT, pref.getString(KEY_UT, ""));
        user.put(KEY_OBJSEC, pref.getString(KEY_OBJSEC, ""));
        user.put(KEY_NAME, pref.getString(KEY_NAME, ""));
        user.put(KEY_PASS, pref.getString(KEY_PASS, ""));
        user.put(IS_LOGIN, String.valueOf(pref.getBoolean(IS_LOGIN, false)));
        return user;
    }

    /**
     * Retrieves dashboard related data
     * @return HashMap containing dashboard data and last update timestamp
     */
    public HashMap<String, String> getDashDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_LASTUPDATE, pref.getString(KEY_LASTUPDATE, null));
        user.put(KEY_DATA, pref.getString(KEY_DATA, null));
        return user;
    }

    /**
     * Clears user session data (logout)
     * Resets all user credentials and sets login state to false
     */
    public void logoutUser() {
        editor.putBoolean(IS_LOGIN, false);
        editor.putString(KEY_UID, "");
        editor.putString(KEY_UT, "");
        editor.putString(KEY_OBJSEC, "");
        editor.putString(KEY_PASS, "");
        editor.commit();
    }

    /**
     * Checks current login state
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}