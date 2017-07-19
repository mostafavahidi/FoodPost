package com.mostafavahidi.foodpost;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mostafavahidi.foodpost.data.Food;
import com.mostafavahidi.foodpost.data.FoodAdapter;
import com.mostafavahidi.foodpost.data.Tag;
import com.squareup.picasso.Picasso;
import com.yalantis.filter.adapter.FilterAdapter;
import com.yalantis.filter.animator.FiltersListItemAnimator;
import com.yalantis.filter.listener.FilterListener;
import com.yalantis.filter.widget.FilterItem;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mostafa on 7/3/2017.
 */

public class SearchActivity extends AppCompatActivity implements FilterListener<Tag> {

    private RecyclerView mRecyclerView;

    private Bitmap test = null;

    private int[] mColors;
    private String[] mTitles;
    private static List<Food> mAllFoods = new ArrayList<Food>();
    private List<Tag> mAllTags;
    private com.yalantis.filter.widget.Filter<Tag> mFilter;
    private FoodAdapter mAdapter;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private String myUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImagePipelineConfig config = ImagePipelineConfig
                .newBuilder(this)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, config);

        //Setting up the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.post_toolbar_searchActivity);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //Setting up server variables.
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getCurrentUser().getUid();

        mRecyclerView = (RecyclerView) findViewById(R.id.list);

//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        mRecyclerView.setAdapter(mAdapter = new FoodAdapter(this, mAllFoods = getFoods()));
//        mRecyclerView.setItemAnimator(new FiltersListItemAnimator());

        firebaseDatabase.getReference(
                "foods")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Food> foodRecords = new ArrayList<Food>();
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Food toAdd = ds.getValue(Food.class);
                            Log.i("image Bitmap: ", "YESSSSSSS");
                            Bitmap imageBitmap = null;

                            if (ds.getValue(Food.class).getImageUrl() != null){
                                try {
                                    imageBitmap = decodeFromFirebaseBase64(ds.getValue(Food.class).getImageUrl());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            toAdd.setPhoto(imageBitmap);

                            foodRecords.add(toAdd);
                        }
                        mAllFoods = foodRecords;
                        mAdapter = new FoodAdapter(
                                SearchActivity.this, mAllFoods);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));
                        mRecyclerView.setAdapter(mAdapter = new FoodAdapter(SearchActivity.this, foodRecords));
                        mRecyclerView.setItemAnimator(new FiltersListItemAnimator());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mColors = getResources().getIntArray(R.array.colors);
        mTitles = getResources().getStringArray(R.array.food_titles);

        mFilter = (com.yalantis.filter.widget.Filter<Tag>) findViewById(R.id.filter);
        mFilter.setAdapter(new Adapter(getTags()));
        mFilter.setListener(this);

        //the text to show when there's no selected items
        mFilter.setNoSelectedItemText(getString(R.string.str_all_selected));
        mFilter.build();
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
            byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    private void calculateDiff(final List<Food> oldList, final List<Food> newList) {
        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }
        }).dispatchUpdatesTo(mAdapter);
    }

    private List<Tag> getTags() {
        List<Tag> tags = new ArrayList<>();

        for (int i = 0; i < mTitles.length; ++i) {
            tags.add(new Tag(mTitles[i], mColors[i]));
        }

        return tags;
    }

    @Override
    public void onNothingSelected() {
        if (mRecyclerView != null) {
            firebaseDatabase.getReference(
                    "foods")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<Food> foodRecords = new ArrayList<Food>();
                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                Food toAdd = ds.getValue(Food.class);
                                Log.i("image Bitmap: ", "YESSSSSSS");
                                Bitmap imageBitmap = null;
                                if (ds.getValue(Food.class).getImageUrl() != null){
                                    try {
                                        imageBitmap = decodeFromFirebaseBase64(ds.getValue(Food.class).getImageUrl());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                toAdd.setPhoto(imageBitmap);
                                foodRecords.add(toAdd);
                            }
                            mAdapter.setFoods(foodRecords);
                            mAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

    private List<Food> getFoods() {
        List<Food> toReturn = new ArrayList<Food>();

        for (Food food: mAllFoods){
            toReturn.add(food);
        }

        return toReturn;
//        return new ArrayList<Food>() {{
//            add(new Food("Carol Bell", "Graphic Designer",
//                    "http://kingofwallpapers.com/girl/girl-011.jpg", test,
//                    new ArrayList<Tag>() {{
//                add(new Tag(mTitles[2], mColors[2]));
//                add(new Tag(mTitles[4], mColors[4]));
//            }}));
//            add(new Food("Mostafa Vahidi", "Graphic Designer",
//                    "http://kingofwallpapers.com/girl/girl-011.jpg", test,
//                    new ArrayList<Tag>() {{
//                        add(new Tag(mTitles[2], mColors[2]));
//                        add(new Tag(mTitles[4], mColors[4]));
//                    }}));
//            add(new Food("Yu Sun", "Graphic Designer",
//                    "http://kingofwallpapers.com/girl/girl-011.jpg", test,
//                    new ArrayList<Tag>() {{
//                        add(new Tag(mTitles[2], mColors[2]));
//                        add(new Tag(mTitles[4], mColors[4]));
//                    }}));
//            add(new Food("Andrei Voicu", "Graphic Designer",
//                    "http://kingofwallpapers.com/girl/girl-011.jpg", test,
//                    new ArrayList<Tag>() {{
//                        add(new Tag(mTitles[2], mColors[2]));
//                        add(new Tag(mTitles[4], mColors[4]));
//                    }}));
//            add(new Food("James Blake", "Graphic Designer",
//                    "http://kingofwallpapers.com/girl/girl-011.jpg", test,
//                    new ArrayList<Tag>() {{
//                        add(new Tag(mTitles[2], mColors[2]));
//                        add(new Tag(mTitles[4], mColors[4]));
//                    }}));
//        }};
    }

    private List<Food> findByTags(List<Tag> tags) {
        List<Food> foods = new ArrayList<>();

        for (Food food : mAllFoods) {
            for (Tag tag : tags) {
                if (food.hasTag(tag.getText()) && !foods.contains(food)) {
                    foods.add(food);
                }
            }
        }

        return foods;
    }

    @Override
    public void onFiltersSelected(@NotNull ArrayList<Tag> filters) {
        List<Food> newFoods = findByTags(filters);
        List<Food> oldFoods = mAdapter.getFoods();
        mAdapter.setFoods(newFoods);
        calculateDiff(oldFoods, newFoods);
    }

    @Override
    public void onFilterSelected(Tag item) {
        if (item.getText().equals(mTitles[0])) {
            mFilter.deselectAll();
            mFilter.collapse();
        }
    }

    @Override
    public void onFilterDeselected(Tag item) {

    }

    class Adapter extends FilterAdapter<Tag> {

        Adapter(@NotNull List<? extends Tag> items) {
            super(items);
        }

        @NotNull
        @Override
        public FilterItem createView(int position, Tag item) {
            FilterItem filterItem = new FilterItem(SearchActivity.this);

            filterItem.setStrokeColor(mColors[0]);
            filterItem.setTextColor(mColors[0]);
            filterItem.setCornerRadius(10);
            filterItem.setCheckedTextColor(ContextCompat.getColor(SearchActivity.this, android.R.color.white));
            filterItem.setColor(ContextCompat.getColor(SearchActivity.this, android.R.color.white));
            filterItem.setCheckedColor(mColors[position]);
            filterItem.setText(item.getText());
            filterItem.deselect();

            return filterItem;
        }
    }

    //Setting up the menu methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                // User chose the "Settings" item, show the app settings UI...
                Intent Intent = new Intent(this, HomeActivity.class);
                this.startActivity(Intent);
                break;
            case R.id.action_settings:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                break;
            case R.id.action_delete:
                firebaseDatabase.getReference("foods/" + firebaseAuth.getCurrentUser().getUid()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(SearchActivity.this,"Your Food Post has been deleted!", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        return true;
    }
}
