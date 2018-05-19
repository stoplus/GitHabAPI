package com.example.den.githabapi.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.example.den.githabapi.R;
import com.example.den.githabapi.RepoActivity;
import com.example.den.githabapi.entity.Organization;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterGitHubRepo extends RecyclerView.Adapter<AdapterGitHubRepo.ViewHolder> {
    private LayoutInflater inflater;
    private List<Organization> list;
    private Context context;

    public AdapterGitHubRepo(Context context, List<Organization> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = new ArrayList<>(list);
        this.context = context;
    }//AdapterForAdmin

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
        View view = inflater.inflate(R.layout.item_organizations, parent, false);
        return new ViewHolder(view);
    } // onCreateViewHolder

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.textView)
        TextView name;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.idAddress)
        TextView address;
        @BindView(R.id.idBlog)
        TextView blog;

        private ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }//ViewHolder
    }//class ViewHolder

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        int position1 = holder.getAdapterPosition ();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RepoActivity.class);
                String name = list.get(position).getLogin();
                int publicRepos = list.get(position).getPublicRepos();
                intent.putExtra("nameList", name);
                intent.putExtra("publicRepos", publicRepos);
                context.startActivity(intent);
            }
        });
        String nameItem;
        if (list.get(position).getName() == null) {
            nameItem = list.get(position).getLogin();
            nameItem = nameItem.substring(0, 1).toUpperCase() + nameItem.substring(1);//Первая буква заглавная
            holder.name.setText(nameItem);
        } else {
            nameItem = list.get(position).getName();
            nameItem = nameItem.substring(0, 1).toUpperCase() + nameItem.substring(1);//Первая буква заглавная
            holder.name.setText(nameItem);
        }

        holder.address.setText(list.get(position).getLocation());
        String blogItem = list.get(position).getBlog();
            blogItem = blogItem.replace("https://", "").replace("www.", "").replace("http://", "");
        if (blogItem != null && blogItem.length() > 0 && blogItem.charAt(blogItem.length() - 1) == '/') {
            blogItem = blogItem.substring(0, blogItem.length() - 1);
        }
        holder.blog.setText(blogItem);
        Glide.with(context)
                .load(list.get(position).getAvatarUrl())
                .override(80, 80)
                .centerCrop()
                .error(R.mipmap.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
    }//onBindViewHolder
}//class AdapterGitHubRepo