package com.sabbir.walton.mmitest.TestActivities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private List<DataModel> dataModelList;
    private Context context;

    public DataAdapter(Context context, List<DataModel> dataModelList) {
        this.context = context;
        this.dataModelList = dataModelList;
    }


    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.datalayout, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        DataModel dataModel = dataModelList.get(position);
        holder.locationName.setText(dataModel.getName());
        holder.locationAddress.setText(dataModel.getAddress());
        holder.locationContact.setText(dataModel.getContact());

        /*holder.locationContact.setOnLongClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Cantact Number", dataModel.getContact());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context,"Contacct number copied to clipboard", Toast.LENGTH_SHORT).show();
            return true;
        });*/

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + dataModel.getContact()));
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    public void filterList(List<DataModel> filteredList) {
        dataModelList = filteredList;
        notifyDataSetChanged();
    }

    static class DataViewHolder extends RecyclerView.ViewHolder {

        TextView locationName, locationAddress, locationContact;


        DataViewHolder(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.dataName);
            locationAddress = itemView.findViewById(R.id.dataAddress);
            locationContact = itemView.findViewById(R.id.dataContact);

        }
    }
}
