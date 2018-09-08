package bd.ac.pstu.rezwan12cse.bounduley12.MyAppWithFirebase;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import bd.ac.pstu.rezwan12cse.bounduley12.Adaptar.RecyclerAdapter;
import bd.ac.pstu.rezwan12cse.bounduley12.Model.FireModel;
import bd.ac.pstu.rezwan12cse.bounduley12.R;

public class Friends_RecycleView extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myref;
    List<FireModel> list = new ArrayList<FireModel>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;


    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private boolean appBarExpanded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends__recycle_view);

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Friends_RecycleView.this));

        appBarLayout = findViewById(R.id.appbar);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        collapsingToolbarLayout = findViewById(R.id.cToolbar);
        collapsingToolbarLayout.setTitle("CSE 12 batch, PSTU");

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.header);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(R.color.primary_500);
                collapsingToolbarLayout.setContentScrimColor(vibrantColor);
                collapsingToolbarLayout.setStatusBarScrimColor(R.color.black_trans80);
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(Friends_RecycleView.class.getSimpleName(), "onOffsetChanged: verticalOffset: " + verticalOffset);
                if (Math.abs(verticalOffset) > 200){
                    appBarExpanded = false;
                    invalidateOptionsMenu();
                }else {
                    appBarExpanded = true;
                    invalidateOptionsMenu();
                }
            }
        });



        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




/*

        recyclerView = findViewById(R.id.recycle);
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list, Friends_RecycleView.this);
        RecyclerView.LayoutManager recyce = new GridLayoutManager(Friends_RecycleView.this, 2);
        //RecyclerView.LayoutManager recyce = new LinearLayoutManager(Friends_RecycleView.this);
        recyclerView.setLayoutManager(recyce);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);
*/


        database = FirebaseDatabase.getInstance();
        myref = database.getReference("AuthUsers");
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    FireModel value = s.getValue(FireModel.class);

                    list.add(value);
                }
                adapter = new RecyclerAdapter(Friends_RecycleView.this , list);
                RecyclerView.LayoutManager recyce = new GridLayoutManager(Friends_RecycleView.this, 2);
                recyclerView.setLayoutManager(recyce);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
