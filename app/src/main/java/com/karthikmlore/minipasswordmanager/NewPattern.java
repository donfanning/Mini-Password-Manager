/**
 * Mini Password Manager
 *
 * Copyright (c) 2014-2015 Karthik M'lore
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.karthikmlore.minipasswordmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.haibison.android.lockpattern.LockPatternActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NewPattern extends ActionBarActivity {

    private Crypter old_crypter, new_crypter;
    private Context context;
    private DBHelper dbHandler;
    private SQLiteDatabase database;
    private String pattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = NewPattern.this;
        dbHandler = new DBHelper(context);
        try {
            pattern = getIntent().getExtras().getString("pattern");
        } catch(Exception e) {
            pattern = null;
        }
        Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null, context, LockPatternActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    String new_pattern = new String(data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN));
                    new_crypter = new Crypter(new_pattern);
                    ContentValues push_pattern = new ContentValues();
                    try {
                        push_pattern.put("PATTERN", new_crypter.encrypt(new_pattern));
                        database = dbHandler.openDB(true);
                        if(pattern == null) {
                            try {
                                if (database.insertOrThrow("APP_SETTINGS", null, push_pattern) != 1)
                                    Toast.makeText(context, "Error storing new pattern", Toast.LENGTH_SHORT).show();
                                else {
                                    Toast.makeText(context, "Pattern set", Toast.LENGTH_SHORT).show();
                                    move_to_login();
                                }

                            } catch (SQLiteException e) {
                                Toast.makeText(context, "Error writing to database", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            old_crypter = new Crypter(pattern);
                            try {
                                if(database.update("APP_SETTINGS", push_pattern, "PATTERN = '" + old_crypter.encrypt(pattern) + "'", null) == 1) {
                                    recrypt_records();
                                    Toast.makeText(context, "Pattern changed", Toast.LENGTH_SHORT).show();
                                    move_to_login();
                                }
                            } catch(Exception e) {
                                Toast.makeText(context, "Error setting new pattern", Toast.LENGTH_SHORT).show();
                            }

                        }
                        dbHandler.closeDB();
                    } catch (Exception e) {
                        Toast.makeText(context, "Encryption error" , Toast.LENGTH_SHORT).show();
                    }
                }
                else finish();
                break;
            }

        }
    }

    private void recrypt_records() {
        database = dbHandler.openDB(true);
        ArrayList<List<String>> records = new ArrayList<>();
        String adder[] = new String[6];
        int j;
        try {
            Cursor x = database.query("RECORDS",new String[] {"ID","TITLE","URL","USERNAME","PASSWORD","NOTES"},null,null,null,null,null);
            if(x.moveToFirst()) {
                do {
                    records.add(Arrays.asList(x.getString(0), x.getString(1), x.getString(2), x.getString(3), x.getString(4), x.getString(5)));
                } while(x.moveToNext());
                x.close();
                for (List<String> list : records) {
                    j = 0;
                    for (String i : list) {
                        adder[j] = i;
                        j++;
                    }
                    try {
                        ContentValues secUpd = new ContentValues();
                        secUpd.put("TITLE", new_crypter.encrypt(old_crypter.decrypt(adder[1])));
                        secUpd.put("URL", new_crypter.encrypt(old_crypter.decrypt(adder[2])));
                        secUpd.put("USERNAME", new_crypter.encrypt(old_crypter.decrypt(adder[3])));
                        secUpd.put("PASSWORD", new_crypter.encrypt(old_crypter.decrypt(adder[4])));
                        secUpd.put("NOTES", new_crypter.encrypt(old_crypter.decrypt(adder[5])));
                        try {
                            database.update("RECORDS", secUpd, "ID="+Integer.parseInt(adder[0]), null);
                        }catch(SQLiteException e) {
                            Toast.makeText(context, "Error Writing to Database" , Toast.LENGTH_SHORT).show();
                        }
                    } catch(Exception e) {
                        Toast.makeText(context, "Error re-encrypting Data" , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (SQLiteException e) {
            Toast.makeText(context, "Error reading database" , Toast.LENGTH_SHORT).show();
        }
        dbHandler.closeDB();
    }

    private void move_to_login() {
        Intent secureActivity = new Intent(context, Login.class);
        startActivity(secureActivity);
        finish();
    }
}