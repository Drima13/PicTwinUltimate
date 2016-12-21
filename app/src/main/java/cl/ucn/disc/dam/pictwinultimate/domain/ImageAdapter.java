package cl.ucn.disc.dam.pictwinultimate.domain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import butterknife.BindView;
import cl.ucn.disc.dam.pictwinultimate.R;
import cl.ucn.disc.dam.pictwinultimate.activities.FotoActivity;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;


public class ImageAdapter extends BaseAdapter{
    private Activity activity;
    private ArrayList<Twin> twins;
    /*
    @BindView(R.id.imageView1)
    ImageView img1;

    @BindView(R.id.imageView2)
    ImageView img2;
    */
    public ImageAdapter(Activity activity, ArrayList<Twin> twins){

        this.activity = activity;
        this.twins = twins;
    }

    @Override
    public int getCount() {
        return twins.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;

        if(convertView == null){
            LayoutInflater inf = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inf.inflate(R.layout.screen_list, null);
        }


        final Twin twin = twins.get(position);
        ImageView img1 = (ImageView)row.findViewById(R.id.imageView1);
        ImageView img2 = (ImageView)row.findViewById(R.id.imageView2);

        String[] path1 = twin.getLocal().getUrl().split("file://");
        String[] path2 = twin.getRemote().getUrl().split("file://");

        File file1 = new File(path1[1]);
        Uri pic1= Uri.fromFile(file1);

        File file2 = new File(path2[1]);
        Uri pic2= Uri.fromFile(file2);

        Picasso.with(this.activity)
                .load(pic1)
                .resize(300,300)
                .centerCrop()
                .into(img1);

        Picasso.with(this.activity)
                .load(pic2)
                .resize(300,300)
                .centerCrop()
                .into(img2);
/*
        img1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                FotoActivity fotoActivity = new FotoActivity();
                Intent intent = new Intent();
                intent.putExtra("url",twin.getLocal().getUrl());
                fotoActivity.startActivity(intent);

            }
        });
        */
        return row;
    }
}
