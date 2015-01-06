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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.widget.Toast;


public class SetService {

    private Context context;
    private DBHelper dbHandler;
    private Crypter crypter;
    private SQLiteDatabase database;

    public SetService(Context context, DBHelper dbHandler, Crypter crypter) {
        this.context = context;
        this.dbHandler = dbHandler;
        this.crypter = crypter;
    }

    public boolean addService(String[] toAdd) {
        long success = -1;
        if(toAdd[1]==null)
            toAdd[1]="";
        if(toAdd[4]==null)
            toAdd[4]="";
        try {
            ContentValues pushData = new ContentValues();
            long id = System.currentTimeMillis()/1000;
            pushData.put("ID",id);
            pushData.put("TITLE", crypter.encrypt(toAdd[0]));
            pushData.put("URL", crypter.encrypt(toAdd[1]));
            pushData.put("USERNAME", crypter.encrypt(toAdd[2]));
            pushData.put("PASSWORD", crypter.encrypt(toAdd[3]));
            pushData.put("NOTES", crypter.encrypt(toAdd[4]));
            database = dbHandler.openDB(true);
            try {
                success = database.insertOrThrow("RECORDS", null, pushData);
            } catch (SQLiteException e) {
                Toast.makeText(context, "Error writing record" , Toast.LENGTH_SHORT).show();
            }
            dbHandler.closeDB();
        } catch (Exception e) {
            Toast.makeText(context, "Encryption error" , Toast.LENGTH_SHORT).show();
        }
        return success != -1;
    }

    public boolean updateService(int id,String[] toUpdate) {
        long success = -1;
        if(toUpdate[1]==null)
            toUpdate[1]="";
        if(toUpdate[4]==null)
            toUpdate[4]="";
        try {
            ContentValues pushData = new ContentValues();
            pushData.put("TITLE", crypter.encrypt(toUpdate[0]));
            pushData.put("URL", crypter.encrypt(toUpdate[1]));
            pushData.put("USERNAME", crypter.encrypt(toUpdate[2]));
            pushData.put("PASSWORD", crypter.encrypt(toUpdate[3]));
            pushData.put("NOTES", crypter.encrypt(toUpdate[4]));
            database = dbHandler.openDB(true);
            try {
                success = database.update("RECORDS", pushData, "ID=" + id, null);
            } catch (SQLiteException e) {

            }
            dbHandler.closeDB();
        } catch (Exception e) {
            Toast.makeText(context, "Encryption Error" , Toast.LENGTH_SHORT).show();
        }
        return success == 1;
    }

    public String passwordGenerator(boolean lower,boolean upper,boolean numbers,boolean symbols,String passwordLength) {
        String password = "";
        String chars = "";
        if(lower)
            chars = chars + "abcdefghijklmnopqrstuvwxyz";
        if(upper)
            chars = chars + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if(numbers)
            chars = chars + "0123456789";
        if(symbols)
            chars = chars + "<>?\\;:/*-+.#$%^&£!";
        int length = Integer.parseInt(passwordLength);
        for(int i=0;i<length;i++) {
            int rand = (int) (Math.random() * chars.length());
            password = password + chars.charAt(rand);
        }
        return password;
    }

    public boolean isEmpty(String text) {
        return text.length() == 0;
    }
}
