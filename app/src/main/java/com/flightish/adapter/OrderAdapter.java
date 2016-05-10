package com.flightish.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flightish.R;
import com.flightish.model.MenuDTO;

import java.util.List;

public class OrderAdapter extends BaseAdapter {

    List<MenuDTO> orders;
    private static LayoutInflater inflater=null;
    private Context context;

    public OrderAdapter(Context context, int textViewResourceId, List<MenuDTO> orders) {
        this.context = context;
        this.orders = orders;

        this.inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public Object getItem(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return orders.size();
    }


    @Override
    public boolean hasStableIds() {
      return true;
    }
    public class Holder
    {
        TextView title;
        TextView price;
        TextView vendor;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.menu_list, null);
        holder.title=(TextView) rowView.findViewById(R.id.title);
        holder.price=(TextView) rowView.findViewById(R.id.price);
        holder.vendor=(TextView) rowView.findViewById(R.id.vendor);

        holder.vendor.setText(orders.get(position).getCommpany_name());
        holder.title.setText(orders.get(position).getItem_name());
        holder.price.setText("Rs." +orders.get(position).getPrice());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Toast.makeText(context, "Order Confirmed", Toast.LENGTH_LONG).show();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Confirm Order?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        return rowView;
    }

  }