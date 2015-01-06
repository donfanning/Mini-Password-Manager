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

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

public class ServiceListAdapter extends BaseExpandableListAdapter {

    private List<Records> records;
    private Context context;
    private Typeface typeFace;


    public ServiceListAdapter(List<Records> records, Context context, Typeface typeFace) {
        this.records = records;
        this.context = context;
        this.typeFace = typeFace;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return records.get(groupPosition).getRecordData().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return records.get(groupPosition).getRecordData().get(childPosition).hashCode();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.service_record, parent, false);
        }

        TextView username = (TextView) v.findViewById(R.id.username);
        TextView notes = (TextView) v.findViewById(R.id.notes);

        RecordData data = records.get(groupPosition).getRecordData().get(childPosition);

        username.setText(data.getUsername());
        notes.setText(data.getNotes());
        final String password = data.getPassword();

        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Password");
                builder.setMessage(password).setPositiveButton("Close", null).show();

            }
        });
        return v;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return records.get(groupPosition).getRecordData().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return records.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return records.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return records.get(groupPosition).hashCode();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.service_head, parent, false);
        }
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView url = (TextView) v.findViewById(R.id.url);

        Records current_record = records.get(groupPosition);

        title.setTypeface(typeFace);
        title.setText(current_record.getTitle());
        url.setText(current_record.getUrl());

        return v;
    }
    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}