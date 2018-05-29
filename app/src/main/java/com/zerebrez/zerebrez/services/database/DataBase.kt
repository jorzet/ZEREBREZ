/*
 * Copyright [2018] [Jorge Zepeda Tinoco]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zerebrez.zerebrez.services.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.zerebrez.zerebrez.models.entities.ModuleEntry
import com.zerebrez.zerebrez.models.entities.QuestionEntry

/**
 * Created by Jorge Zepeda Tinoco on 24/04/18.
 * jorzet.94@gmail.com
 */

class DataBase (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_MODULE_TABLE =
            "CREATE TABLE ${ModuleEntry.TABLE_NAME} (" +
                    "${ModuleEntry.COLUMN_MODULE_ID} INTEGER PRIMARY KEY," +
                    "${ModuleEntry.COLUMN_MODULE_NAME} TEXT)"

    private val SQL_CREATE_QUESTION_TABLE =
            "CREATE TABLE ${QuestionEntry.TABLE_NAME} (" +
                    "${QuestionEntry.COLUMN_QUESTION_ID} INTEGER PRIMARY KEY," +
                    "${QuestionEntry.COLUMN_MODULE_ID} INTEGER," +
                    "${QuestionEntry.COLUMN_SUBJECT} TEXT," +
                    "${QuestionEntry.COLUMN_TEXT} TEXT," +
                    "${QuestionEntry.COLUMN_OPTION_ONE} TEXT," +
                    "${QuestionEntry.COLUMN_OPTION_TWO} TEXT," +
                    "${QuestionEntry.COLUMN_OPTION_THREE} TEXT," +
                    "${QuestionEntry.COLUMN_OPTION_FOUR} TEXT," +
                    "${QuestionEntry.COLUMN_ANSWER} TEXT," +
                    "${QuestionEntry.COLUMN_YEAR} TEXT," +
                    "${QuestionEntry.COLUMN_TYPE} TEXT," +
                    " FOREIGN KEY (${QuestionEntry.COLUMN_MODULE_ID}) REFERENCES ${ModuleEntry.TABLE_NAME}(${ModuleEntry.COLUMN_MODULE_ID}));"

    private val SQL_DELETE_MODULE_TABLE = "DROP TABLE IF EXISTS ${ModuleEntry.TABLE_NAME}"
    private val SQL_DELETE_QUESTION_TABLE = "DROP TABLE IF EXISTS ${QuestionEntry.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_MODULE_TABLE)
        db.execSQL(SQL_CREATE_QUESTION_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_MODULE_TABLE)
        db.execSQL(SQL_DELETE_QUESTION_TABLE)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "zerebrez.db"
    }

}
