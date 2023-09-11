package com.example.iskeduler;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    Context context;
    ArrayList<Note> arrayList;
    OnItemClickListener onItemClickListener;
    private Collection<Note> dataList;
    NoteAdapter adapter;

    public NoteAdapter(Context context, ArrayList<Note> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public NoteAdapter(List<String> dataList) {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Title.setText(arrayList.get(position).getTitle());
        holder.Content.setText(arrayList.get(position).getContent());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arrayList.get(position)));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Title, Content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.list_item_Title);
            Content = itemView.findViewById(R.id.list_item_Content);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



    public interface OnItemClickListener {
        void onClick(Note note);
    }

    private void filter(String query) {
        List<Note> filteredList = new ArrayList<>();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(dataList);
        } else {
            String searchPattern = query.toLowerCase().trim();
            for (Note item : dataList) {
                if (item.getTitle().toLowerCase().contains(searchPattern) ||
                        item.getContent().toLowerCase().contains(searchPattern)) {
                    filteredList.add(item);
                }
            }
        }
        updateData(filteredList); // Update the existing adapter's data
    }

    public void updateData(List<Note> newData) {
        dataList.clear();
        dataList.addAll(newData);
        notifyDataSetChanged();
    }

}

