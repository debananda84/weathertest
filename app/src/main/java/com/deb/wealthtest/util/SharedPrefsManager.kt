package com.deb.wealthtest.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.ColorSpace.Model
import android.util.Log
import com.google.gson.GsonBuilder
import java.lang.reflect.Type


/**
 * Singleton Class for accessing SharedPreferences,
 * should be initialized once in the beginning by any application component using static
 * method initialize(applicationContext)
 */
class SharedPrefsManager private constructor(appContext: Context) {
    private val prefs: SharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    val editor: SharedPreferences.Editor
        get() = prefs.edit()

    /**
     * Clears all data in SharedPreferences
     */
    fun clearPrefs() {
        val editor = prefs.edit()
        editor.clear()
        editor.commit()
    }

    fun removeKey(key: String) {
        prefs.edit().remove(key).commit()
    }

    fun containsKey(key: String): Boolean {
        return prefs.contains(key)
    }

    @JvmOverloads
    fun getString(key: String, defValue: String? = null): String? {
        return prefs.getString(key, defValue)
    }

    fun setString(key: String, value: String) {
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    @JvmOverloads
    fun getInt(key: String, defValue: Int = 0): Int {
        return prefs.getInt(key, defValue)
    }

    fun setInt(key: String, value: Int) {
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    @JvmOverloads
    fun getLong(key: String, defValue: Long = 0L): Long {
        return prefs.getLong(key, defValue)
    }

    fun setLong(key: String, value: Long) {
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    @JvmOverloads
    fun getBoolean(key: String, defValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defValue)
    }

    fun setBoolean(key: String, value: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    @JvmOverloads
    fun getFloat(key: String, defValue: Float = 0f): Boolean {
        return getFloat(key, defValue)
    }

    fun setFloat(key: String, value: Float?) {
        val editor = prefs.edit()
        editor.putFloat(key, value!!)
        editor.apply()
    }




    /**
     * Saves object into the Preferences.
     *
     * @param `object` Object of model class (of type [T]) to save
     * @param key Key with which Shared preferences to
     **/
    fun <T> put(`object`: T, key: String) {
        //Convert object to JSON String.
        val jsonString = GsonBuilder().create().toJson(`object`)
        //Save that String in SharedPreferences
        setString(key, jsonString)
    }

    /**
     * Used to retrieve object from the Preferences.
     *
     * @param key Shared Preference key with which object was saved.
     **/
    inline fun <reified T> get(key: String): T? {
        //We read JSON String which was saved.
        val value = getString(key, null)
        //JSON String was found which means object can be read.
        //We convert this JSON String to model object. Parameter "c" (of
        //type Class < T >" is used to cast.
        return GsonBuilder().create().fromJson(value, T::class.java)
    }


    fun registerPrefsListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterPrefsListener(

        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }


    companion object {

        private val TAG = SharedPrefsManager::class.java.name
        private var uniqueInstance: SharedPrefsManager? = null
        const val PREF_NAME = "test"

        /**
         * Throws IllegalStateException if this class is not initialized
         *
         * @return unique SharedPrefsManager instance
         */
        val instance: SharedPrefsManager
            get() {
                checkNotNull(uniqueInstance) { "SharedPrefsManager is not initialized, call initialize(applicationContext) " + "static method first" }
                return uniqueInstance as SharedPrefsManager
            }

        /**
         * Initialize this class using application Context,
         * should be called once in the beginning by any application Component
         *
         * @param appContext application context
         */
        fun initialize(appContext: Context?) {
            if (appContext == null) {
                throw NullPointerException("Provided application context is null")
            }
            if (uniqueInstance == null) {
                synchronized(SharedPrefsManager::class.java) {
                    if (uniqueInstance == null) {
                        uniqueInstance = SharedPrefsManager(appContext!!)
                    }
                }
            }
        }

    }
}