package com.example.den.githabapi.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.den.githabapi.R;
import com.example.den.githabapi.entity.GitHubRepo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterRepo extends RecyclerView.Adapter<AdapterRepo.ViewHolder> {

    private LayoutInflater inflater;
    private List<GitHubRepo> list;
    private Context context;

    public AdapterRepo(Context context, List<GitHubRepo> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>(list);
        this.context = context;
    }//Adapter

    @Override
    public int getItemCount() {
        return list.size();
    }//getItemCount

    @Override
    public long getItemId(int position) {
        return position;
    }//getItemId

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_repo, parent, false);
        return new ViewHolder(view);
    } // onCreateViewHolder

    //внутрений класс ViewHolder для хранения элементов разметки
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textViewName)
        TextView textViewName;
        @BindView(R.id.textViewDescription)
        TextView textViewDescription;
        @BindView(R.id.lines)
        View lines;

        // в конструкторе получаем ссылки на элементы по id
        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }//ViewHolder
    }//class ViewHolder

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.textViewName.setText(list.get(position).getName());
        holder.textViewDescription.setText(list.get(position).getDescription());
    }//onBindViewHolder
}
