package com.example.testapp_2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.testapp_2.db.Info;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SimpleViewHolder> {

    private ArrayList<Info> arrayList = new ArrayList<>(StaticParams.amount);

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.bind(arrayList.get(position));

        // Проверка что мы внизу recyclerview

        if (position == arrayList.size() - 1) {
            StaticParams.flag = true;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setItems(ArrayList<Info> infoArrayList) {
        arrayList.clear();
        arrayList.addAll(infoArrayList);
        notifyDataSetChanged();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView name, viewers, channels;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.textViewName);
            viewers = itemView.findViewById(R.id.textViewViewers);
            channels = itemView.findViewById(R.id.textViewChannels);
        }

        public void bind(Info object) {
            Picasso.get()
                    .load(object.getImage())
                    .into(imageView);

            name.setText("Игра: " + object.getName());
            viewers.setText("Зрители: " + object.getViewers());
            channels.setText("Каналы: " + object.getChannels());
        }
    }
}