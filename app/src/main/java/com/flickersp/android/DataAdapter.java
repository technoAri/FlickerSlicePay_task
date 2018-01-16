package com.flickersp.android;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rapidd08 on 1/13/2018.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<ImageModel> images;
    private Context context;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private Activity activity;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private List<ImageModel> imageListFiltered = new ArrayList<>();


    public DataAdapter(Context context, ArrayList<ImageModel> images, RecyclerView recyclerView) {
        this.images = images;
        this.context = context;
        imageListFiltered = images;
        imageListFiltered.addAll(images);

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {

        viewHolder.tv_img_name.setText(imageListFiltered.get(i).getImageName());
        Picasso.with(context).load(imageListFiltered.get(i).getImageUrl()).resize(240, 240).into(viewHolder.img);
    }

    @Override
    public int getItemCount() {
        return imageListFiltered.size();
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    imageListFiltered = images;
                } else {

                    ArrayList<ImageModel> filteredList = new ArrayList<>();
                    for (ImageModel imageModel : images) {

                        if (imageModel.getImageName().toLowerCase().contains(charString.toLowerCase())) {

                            filteredList.add(imageModel);
                        }
                    }

                    imageListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = imageListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                imageListFiltered = (ArrayList<ImageModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_img_name;
        private ImageView img;
        public ViewHolder(View view) {
            super(view);

            tv_img_name = (TextView)view.findViewById(R.id.tv_img_name);
            img = (ImageView) view.findViewById(R.id.img);
        }
    }
}