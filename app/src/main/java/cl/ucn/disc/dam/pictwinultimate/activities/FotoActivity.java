package cl.ucn.disc.dam.pictwinultimate.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import cl.ucn.disc.dam.pictwinultimate.R;
import cl.ucn.disc.dam.pictwinultimate.domain.Twin;

/**
 * Created by david on 12/21/2016.
 */

public class FotoActivity extends Activity {

    Twin twin;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.tvLike)
    TextView tvLike;

    @BindView(R.id.tvDislike)
    TextView tvDislike;

    @BindView(R.id.tvLatitud)
    TextView tvLatitud;

    @BindView(R.id.tvLongitud)
    TextView tvLongitud;

    @BindView(R.id.bLike)
    TextView bLike;

    @BindView(R.id.bDislike)
    TextView bDislike;

    @BindView(R.id.bWarning)
    TextView bWarning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.foto_screen);

        ButterKnife.bind(this);

       // String urlImg = getIntent().getExtras().getString("url");

        String[] path1 = getIntent().getExtras().getString("url").split("file://");

        File file1 = new File(path1[1]);
        Uri pic1= Uri.fromFile(file1);

        Picasso.with(getBaseContext())
                .load(pic1)
                .centerCrop()
                .into(imageView);
    }
}
