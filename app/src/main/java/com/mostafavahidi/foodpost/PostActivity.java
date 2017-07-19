package com.mostafavahidi.foodpost;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mostafavahidi.foodpost.data.Food;
import com.mostafavahidi.foodpost.data.Tag;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;

import static com.mostafavahidi.foodpost.R.id.map;

public class PostActivity extends AppCompatActivity implements VerticalStepperForm{

    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private VerticalStepperFormLayout verticalStepperForm;
    private static Tag tagOne;
    private static Tag tagTwo;
    private EditText foodDescVar;
    private static String foodDesc;
    private ImageView imageView;
    private EditText dropOffVar;
    private static Bitmap bitmapToLoad;
    private static String dropOffAdr;
    private static LatLng latLngVar;
    private static String mCurrentPhotoPath;
    private static File imageFile;
    private static String myUid;
    private static int numChecked; //Number of checkboxes checked.
    private static FirebaseAuth firebaseAuth;
    private static Food newFoodPost;
    private static int[] mColors;
    private static String[] mTitles;
    private static File photoFile;
    private static String imageUrl;
    private static double lat;
    private static double lon;
    private EditText email;
    private static String emailVar;
    private EditText name;
    private static String nameVar;

    //UI Elements
    private CheckBox allCategoriesCheckBox;
    private CheckBox sandwichCheckBox;
    private CheckBox snackCheckBox;
    private CheckBox beverageCheckBox;
    private CheckBox fruitCheckBox;
    private CheckBox chickenCheckBox;
    private CheckBox seaFoodCheckBox;
    private CheckBox dessertCheckBox;
    private CheckBox vegetarianCheckBox;
    private CheckBox nutsCheckBox;
    private CheckBox beefCheckBox;
    private CheckBox grainCheckBox;
    private CheckBox otherCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //Creating the new foodPost
        newFoodPost = new Food();

        //Setting up the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.post_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // Finding the view
        verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);

        //Setting up database variables


        //Setting up the title and color of food tag arrrays.
        mColors = getResources().getIntArray(R.array.colors);
        mTitles = getResources().getStringArray(R.array.food_titles);


        //Setting up the posting form for the users.
        String[] mySteps = {"Name", "Food Description", "Food Tags","Email", "Drop-Off Location", "Picture"};
        //String[] mySteps = getResources().getStringArray(R.array.food_titles);
        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryLight);



        // Setting up and initializing the form
        VerticalStepperFormLayout.Builder.newInstance(verticalStepperForm, mySteps, (VerticalStepperForm) this, this)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true) // It is true by default, so in this case this line is not necessary
                .init();



        allCategoriesCheckBox = (CheckBox) findViewById(R.id.allCategories);
        sandwichCheckBox = (CheckBox) findViewById(R.id.sandwich);
        snackCheckBox = (CheckBox) findViewById(R.id.snack);
        beverageCheckBox= (CheckBox) findViewById(R.id.beverage);
        fruitCheckBox= (CheckBox) findViewById(R.id.fruit);
        chickenCheckBox = (CheckBox) findViewById(R.id.chicken);
        seaFoodCheckBox = (CheckBox) findViewById(R.id.seaFood);
        dessertCheckBox = (CheckBox) findViewById(R.id.dessert);
        vegetarianCheckBox = (CheckBox) findViewById(R.id.vegetarian);
        nutsCheckBox = (CheckBox) findViewById(R.id.nuts);
        beefCheckBox = (CheckBox) findViewById(R.id.beef);
        grainCheckBox = (CheckBox) findViewById(R.id.grain);
        otherCheckBox = (CheckBox) findViewById(R.id.other);

        numChecked = 0;


        //SETTING ALL THE ONCHECKEDLISTENERS
        allCategoriesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    allCategoriesCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[0],mColors[0]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[0],mColors[0]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        allCategoriesCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                        allCategoriesCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        sandwichCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    sandwichCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[1],mColors[1]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[1],mColors[1]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        sandwichCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                        sandwichCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        snackCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    snackCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[2],mColors[2]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[2],mColors[2]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        snackCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                        snackCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        beverageCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    beverageCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[3],mColors[3]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[3],mColors[3]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        beverageCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                       beverageCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        fruitCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    fruitCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[4],mColors[4]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[4],mColors[4]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        fruitCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                        fruitCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        chickenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    chickenCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[5],mColors[5]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[5],mColors[5]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        chickenCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                        chickenCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        seaFoodCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    seaFoodCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[6],mColors[6]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[6],mColors[6]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        seaFoodCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                        seaFoodCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        dessertCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    dessertCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[7],mColors[7]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[7],mColors[7]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        dessertCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                        dessertCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        vegetarianCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    vegetarianCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[8],mColors[8]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[8],mColors[8]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        vegetarianCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                        vegetarianCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        nutsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    nutsCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[9],mColors[9]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[9],mColors[9]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        nutsCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                        nutsCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        beefCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    beefCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[10],mColors[10]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[10],mColors[10]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        beefCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                        beefCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        grainCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    grainCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[11],mColors[11]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[11],mColors[11]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        grainCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                       grainCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        otherCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && numChecked >= 2) {
                    otherCheckBox.setChecked(false);
                } else {
                    if (isChecked) {
                        if (tagOne == null){
                            tagOne = new Tag(mTitles[12],mColors[12]);
                            Log.i("tagOne added: ", tagOne.getText());
                        } else {
                            tagTwo = new Tag(mTitles[12],mColors[12]);
                            Log.i("tagTwo added: ", tagTwo.getText());
                        }
                        otherCheckBox.setChecked(true);
                        numChecked++;
                    } else {
                       otherCheckBox.setChecked(false);
                        numChecked--;
                    }
                }
            }
        });

        //CREATING DROP OFF LOCATION STEP
        dropOffVar.setHint("Input the address of desired dropoff location.");
        dropOffVar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAddress(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                dropOffAdr = s.toString();
            }
        });
        dropOffVar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(checkAddress(v.getText().toString())) {
                    dropOffAdr = v.getText().toString();
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        //SETTING FOOD DESCRIPTION VARIABLE
        foodDescVar.setSingleLine(true);
        foodDescVar.setHint("i.e. Food Description");
        foodDescVar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFood(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                foodDesc = s.toString();
            }
        });
        foodDescVar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(checkFood(v.getText().toString())) {
                    foodDesc = v.getText().toString();
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        //SETTING EMAIL VARIABLE
        email.setSingleLine(true);
        email.setHint("i.e. Convenient Email");
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                emailVar = s.toString();
            }
        });
        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(checkEmail(v.getText().toString())) {
                    emailVar = v.getText().toString();
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        //SETTING NAME VARIABLE
        name.setSingleLine(true);
        name.setHint("i.e. Your name.");
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                nameVar = s.toString();
            }
        });
        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(checkName(v.getText().toString())) {
                    nameVar = v.getText().toString();
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });




        //CREATING PICTURE STEP LISTENER.
        imageView = (ImageView) findViewById(R.id.postPictureButton);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(PostActivity.this.getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == PostActivity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            bitmapToLoad = imageBitmap;
            imageView.setImageBitmap(bitmapToLoad);
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        imageUrl = imageEncoded;
    }

    @Override
    public View createStepContentView(int stepNumber) {
        View view = null;
        switch (stepNumber) {
            case 0:
                view = createNameStep();
                break;
            case 1:
                view = createFoodTypeStep();
                break;
            case 2:
                view = createFoodTags();
                break;
            case 3:
                view = createEmailStep();
                break;
            case 4:
                view = createDropoffStep();
                break;
            case 5:
                view = createPictureStep();
                break;
        }
        return view;
    }

    private View createDropoffStep() {
        dropOffVar = new EditText(this);


        return dropOffVar;
    }

    private View createFoodTypeStep() {
        foodDescVar = new EditText(this);

        return foodDescVar;
    }

    private View createEmailStep(){
        email = new EditText(this);

        return email;
    }

    private View createNameStep(){
        name = new EditText(this);

        return name;
    }

    private View createPictureStep(){
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout pictureStepContent =
                (LinearLayout) inflater.inflate(R.layout.activity_post_picture, null, false);

        imageView = (ImageView) pictureStepContent.findViewById(R.id.postPictureButton);
        if(bitmapToLoad == null){

        } else {
            imageView.setImageBitmap(bitmapToLoad);
        }

        return pictureStepContent;
    }

    private View createFoodTags(){
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout tagStepContent =
                (LinearLayout) inflater.inflate(R.layout.activity_post_foodtags, null, false);

        return tagStepContent;
    }

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case 0:
                checkName(name.getText().toString());
                break;
            case 1:
                checkFood(foodDescVar.getText().toString());
                break;
            case 2:
                checkTags(tagOne, tagTwo);
                break;
            case 3:
                checkEmail(email.getText().toString());
                break;
            case 4:
                checkAddress(dropOffVar.getText().toString());
                break;
            case 5:
                checkPicture();
                break;
            default:
                verticalStepperForm.setActiveStepAsCompleted();
                break;
        }
    }


    private boolean checkFood(String input){
        if (input.length() > 10 && input.length() <= 200) {
            verticalStepperForm.setActiveStepAsCompleted();
            return true;
        } else {
            // This error message is optional (use null if you don't want to display an error message)
            String errorMessage = "The food description must have more than 10 and less than 200 characters.";
            verticalStepperForm.setActiveStepAsUncompleted(errorMessage);
            return false;
        }
    }

    private boolean checkTags(Tag tagOne, Tag tagTwo){
        verticalStepperForm.setActiveStepAsCompleted();
        return true;
    }

    private boolean checkPicture (){
        //For now always return true, later check if picture is inappropriate.
        verticalStepperForm.setActiveStepAsCompleted();
        return true;
    }


    private boolean checkAddress(String strAddress) {
        verticalStepperForm.setActiveStepAsCompleted();
        return true;
    }

    private boolean checkEmail(String email){
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        if (m.matches()){
            verticalStepperForm.setActiveStepAsCompleted();
            return true;
        } else {
            String toPrint = "Please input a valid email.";
            verticalStepperForm.setActiveStepAsUncompleted(toPrint);
            return false;
        }
    }

    private boolean checkName(String name){
        if (name.length() > 3 && name.length() < 20) {
            verticalStepperForm.setActiveStepAsCompleted();
            return true;
        } else {
            String toPrint = "Your name should have more than 3 and less than 20 characters.";
            verticalStepperForm.setActiveStepAsUncompleted(toPrint);
            return false;
        }
    }

    @Override
    public void sendData() {

        //SENDING DATA ONTO FIREBASE LIVE DATABASE
        FirebaseAuth firebaseAuth4 = FirebaseAuth.getInstance();
        myUid = firebaseAuth4.getCurrentUser().getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference referenceDatabase = firebaseDatabase.getReference();

        //SETTING THE FOOD DESCRIPTION ONTO NEWFOODINSTANCE
        newFoodPost.setFoodDesc(foodDesc);

        //SETTING THE EMAIL ONTO NEWFOODINSTANCE
        newFoodPost.setEmail(emailVar);

        //SETTING THE NAME ONTO NEWFOODINSTANCE
        newFoodPost.setName(nameVar);

        //SETTING FOOD TAG DATA ONTO NEWFOODINSTANCE
        List<Tag> tagsToAdd = new ArrayList<Tag>();
        tagsToAdd.add(tagOne);
        tagsToAdd.add(tagTwo);
        newFoodPost.setTags(tagsToAdd);

        //SETTING THE ADDRESS OF THE NEWFOODINSTANCE
        newFoodPost.setText(dropOffAdr);

        //SETTING THE PICTURE URL TO NEWFOODPOST.
        newFoodPost.setImageUrl(imageUrl);

        /**
         * Add the ability for the same user to add multiple food posts under the same account later on.
         */
        referenceDatabase.child("foods").child(myUid).setValue(newFoodPost);


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
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference("foods/" + firebaseAuth.getCurrentUser().getUid()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(PostActivity.this,"Your Food Post has been deleted!", Toast.LENGTH_SHORT).show();
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
