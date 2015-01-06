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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.haibison.android.lockpattern.LockPatternActivity;
import com.haibison.android.lockpattern.util.Settings;

public class Login extends ActionBarActivity {
    private String encrypted_pattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        encrypted_pattern = getPattern();
        if(encrypted_pattern == null) {
            Intent setupActivity = new Intent(Login.this, NewPattern.class);
            startActivity(setupActivity);
            finish();
        }
        else
            login_screen();
    }

    private void login_screen(){
        Settings.Display.setMaxRetries(Login.this, 1);
        Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null, Login.this, LockPatternActivity.class);
		intent.putExtra(LockPatternActivity.EXTRA_PATTERN, encrypted_pattern.toCharArray());
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2: {
				switch (resultCode) {
					case RESULT_OK:
						String pattern = new String(data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN));
						Intent secureActivity = new Intent(Login.this, ServiceList.class);
						Bundle bundle = new Bundle();
						bundle.putString("Pattern", pattern);
						secureActivity.putExtras(bundle);
						startActivity(secureActivity);
						finish();
						break;
					case RESULT_CANCELED:
						finish();
						break;
					case LockPatternActivity.RESULT_FAILED:
                        Toast.makeText(Login.this, "Incorrect pattern" , Toast.LENGTH_SHORT).show();
                        login_screen();
						break;
					case LockPatternActivity.RESULT_FORGOT_PATTERN:
						Toast.makeText(Login.this, "Sorry can't help" , Toast.LENGTH_SHORT).show();
                        login_screen();
						break;
				}
            }
        }
    }

    private String getPattern() {
        DBHelper dbHandler = new DBHelper(Login.this);
        SQLiteDatabase database = dbHandler.openDB(false);
        String pattern = "";
        try {
            Cursor x = database.query("APP_SETTINGS",new String[] {"PATTERN"},null,null,null,null,null);
            if(x.moveToFirst()) {
                pattern = x.getString(0);
            }
            else {
                pattern = null;
            }
            x.close();
        } catch (SQLiteException e) {
            Toast.makeText(Login.this, "Error getting pattern" , Toast.LENGTH_SHORT).show();
        }
        dbHandler.closeDB();
        return pattern;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}