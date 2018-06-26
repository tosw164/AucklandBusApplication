package com.tosw164.busapp.dataclasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tosw164.busapp.R;

import java.util.List;

/**
 * Created by TheoOswandi on 27/06/2018.
 */

public class BusrowAdapter extends ArrayAdapter<RealtimeBusInformationRow> {


        private List<RealtimeBusInformationRow> rows;
        Context context;

        public BusrowAdapter(Context context, List<RealtimeBusInformationRow> data) {
            super(context, R.layout.bus_row, data);
            this.rows = data;
            this.context=context;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View listItem = convertView;

            //Based on comment_row.xml defined in layout resources
            if (listItem == null){
                listItem = LayoutInflater.from(this.context).inflate(R.layout.bus_row, parent, false);
            }
            RealtimeBusInformationRow row = rows.get(position);

            TextView row_shortname = (TextView) listItem.findViewById(R.id.busrow_shortname);
            row_shortname.setText(row.short_name);

            TextView row_destdisplay = (TextView) listItem.findViewById(R.id.busrow_destdisplay);
            row_destdisplay.setText(row.destination_display);

            TextView row_schetime = (TextView) listItem.findViewById(R.id.busrow_scheduledtime);
            row_schetime.setText(row.scheduled_time);

            TextView row_expectedtime = (TextView) listItem.findViewById(R.id.busrow_expectedtime);
            row_expectedtime.setText(row.expected_time);

            return listItem;
        }
}
