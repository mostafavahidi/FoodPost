package com.mostafavahidi.foodpost.data;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mostafavahidi.foodpost.R;

import java.util.List;

/**
 * Created by Mostafa on 7/3/2017.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> mFoods;
    private Context mContext;

    public FoodAdapter(Context context, List<Food> foods) {
        mContext = context;
        mFoods = foods;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null, false));
    }

    public List<Food> getFoods() {
        return mFoods;
    }

    public void setFoods(List<Food> foods) {
        this.mFoods = foods;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Food food = mFoods.get(position);

        //Setting up the foodImageView content
        holder.foodImageView.setImageBitmap(food.getPhoto());
        holder.foodImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.foodImageView.setPadding(10,10,10,10);

        //Setting up the nameTextView content
        holder.nameTextView.setText(food.getFoodDesc());
        holder.nameTextView.setTypeface(null, Typeface.BOLD);

        //Setting up the foodDescTextView content
        holder.foodDescTextView.setText(food.getText());

        //Setting up the emailTextView content
        holder.emailTextView.setText(food.getEmail());

        //Setting up the firstFilter content
        Tag firstTag = food.getTags().get(0);
        holder.firstFilter.setText(firstTag.getText());
        holder.firstFilter.setGravity(Gravity.CENTER);

        //Setting up the secondFilter content
        Tag secondTag = food.getTags().get(1);
        holder.secondFilter.setText(secondTag.getText());
        holder.secondFilter.setGravity(Gravity.CENTER);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(1000);
        drawable.setColor(firstTag.getColor());
        holder.firstFilter.setBackgroundDrawable(drawable);
        GradientDrawable drawable1 = new GradientDrawable();
        drawable1.setCornerRadius(1000);
        drawable1.setColor(secondTag.getColor());
        holder.secondFilter.setBackgroundDrawable(drawable1);
    }

    private int getColor(int color) {
        return ContextCompat.getColor(mContext, color);
    }

    @Override
    public int getItemCount() {
        return mFoods.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView foodImageView;
        TextView nameTextView;
        TextView foodDescTextView;
        TextView emailTextView;
        TextView firstFilter;
        TextView secondFilter;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            foodDescTextView = (TextView) itemView.findViewById(R.id.foodDescTextView);
            firstFilter = (TextView) itemView.findViewById(R.id.filter_first);
            secondFilter = (TextView) itemView.findViewById(R.id.filter_second);
            foodImageView = (ImageView) itemView.findViewById(R.id.foodImageView);
            emailTextView = (TextView) itemView.findViewById(R.id.emailTextView);
        }
    }

}
