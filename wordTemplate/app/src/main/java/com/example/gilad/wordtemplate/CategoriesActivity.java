package com.example.gilad.wordtemplate;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class CategoriesActivity extends AppCompatActivity {


    ImageButton cat1;
    ImageButton cat2;
    ImageButton cat3;
    ImageButton cat4;
    ImageButton cat5;
    ImageButton cat6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.cat_toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO -> turn into list and iterate

        cat1 = (ImageButton) findViewById(R.id.imageViewCat1);
        cat2 = (ImageButton) findViewById(R.id.imageViewCat2);
        cat3 = (ImageButton) findViewById(R.id.imageViewCat3);
        cat4 = (ImageButton) findViewById(R.id.imageViewCat4);
        cat5 = (ImageButton) findViewById(R.id.imageViewCat5);
        cat6 = (ImageButton) findViewById(R.id.imageViewCat6);


        //TODO load up categories and mark them as "selected"

        cat1.setOnClickListener(new CatBtnLis(1));
        cat2.setOnClickListener(new CatBtnLis(2));
        cat3.setOnClickListener(new CatBtnLis(3));
        cat4.setOnClickListener(new CatBtnLis(4));
        cat5.setOnClickListener(new CatBtnLis(5));
        cat6.setOnClickListener(new CatBtnLis(6));

        for(int i = 1; i <= 6 ; i++){
            int id = getResources().getIdentifier("imageViewCat" + i,
                    "id", getPackageName());
            ImageButton btn = (ImageButton) findViewById(id);
            btn.setBackground(new ColorDrawable(getColor(R.color.catGray)));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ok) {
            //TODO go back to main screen
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CatBtnLis implements View.OnClickListener {

        int myNum;

        private CatBtnLis(int num) {
            this.myNum = num;
        }

        @Override
        public void onClick(View v) {
            //TODO change users categories
            ColorDrawable btColor = (ColorDrawable) v.getBackground();
            int colorId = btColor.getColor();
            if (colorId == getColor(R.color.catGray))
                v.setBackground(new ColorDrawable(getColor(R.color.colorPrimary)));
            else {
                v.setBackground(new ColorDrawable(getColor(R.color.catGray)));
            }
        }
    }

}
