package com.example.asus.test_snaappy.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.asus.test_snaappy.FullImageActivity;
import com.example.asus.test_snaappy.R;
import com.example.asus.test_snaappy.URLConnHelper.ImageLoader;
import com.example.asus.test_snaappy.models.Image;

import java.util.List;


public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private List<Image> list;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    private int orientation;

    public GridViewAdapter(List<Image> list, Context context){
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader =  ImageLoader.getInstance();
        orientation = context.getResources().getConfiguration().orientation;
        this.context =context;

    }
    @Override
    public int getCount() {
        return list.size();
    }
    public Image getImage(int position){
        return (Image) getItem(position);
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = inflater.inflate(R.layout.model_image, parent, false);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), FullImageActivity.class);
                intent.putExtra("url",list.get(position).getImageUrl());
                context.startActivity(intent);
            }
        });

        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //Для разных ориентаций, разные размеры
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
        imageView.setLayoutParams(new LinearLayout.LayoutParams(parent.getWidth()/3, parent.getHeight()/3, Gravity.CENTER));
        imageLoader.getImage(getImage(position).getImageUrl(),imageView, parent.getHeight()/3,parent.getWidth()/3);
        }

        else{
            imageView.setLayoutParams(new LinearLayout.LayoutParams(parent.getWidth()/2, parent.getHeight()/2, Gravity.CENTER));
            imageLoader.getImage(getImage(position).getImageUrl(),imageView, parent.getHeight(),parent.getWidth()/2);
        }
        return view;
    }
}
