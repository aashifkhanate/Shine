package com.manan.dev.shineymca.AdminZone;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.manan.dev.shineymca.Adapters.AutocompleteCoordinatorAdapter;
import com.manan.dev.shineymca.Models.Coordinator;
import com.manan.dev.shineymca.Models.Round;
import com.manan.dev.shineymca.Models.FAQ;
import com.manan.dev.shineymca.R;
import com.manan.dev.shineymca.Utility.Methods;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.util.Collections.sort;

public class AddRoundActivity extends AppCompatActivity {

    ImageView mPoster, mEditPoster, mAddFaq;
    EditText mName, mDescription, mDate, mTime, mVenue, mSpecialNotes;
    AutoCompleteTextView mCoordinators;
    LinearLayout mCoordinatorView, mFaqView;
    Button mSubmit;
    DatabaseReference mCoordinatorDatabaseReference;
    ChildEventListener mCoordinatorChildEventListener;
    ArrayList<Coordinator> mCoordinatorsAll, mSelectedCorrdinators;
    AutocompleteCoordinatorAdapter coordinatorAdapter;
    final private int REQ_ID_POSTER = 101;
    Round mCurrRound;
    Uri mPosterUri;
    long date, time, roundNumber;
    ArrayList<EditText> mFaqQuestion, mFaqAnswer;
    ArrayList<FAQ> mFaqs;
    String mClubName;
    DatabaseReference mDatabaseRef;
    StorageReference firebaseStorage;
    FirebaseAuth mAuth;
    String clubName;
    ProgressDialog mProgressDialog;
    private DatabaseReference mRoundCount;
    private DatabaseReference mRoundReference;
    private ChildEventListener mRoundListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_round);
        initializeVariables();
        initForEventUpload();
        addOnClickListeners();
        setUpAutoCompleteTextView();

        roundNumber = getIntent().getLongExtra("roundNumber", 0);
        setTitle("Round "+roundNumber);
        addDatabaseListeners();
    }


    private void initForEventUpload() {
        mAuth = FirebaseAuth.getInstance();
        clubName = mAuth.getCurrentUser().getDisplayName();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Clubs").child(clubName).child("Rounds");
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        Log.d("ASIF", clubName);
        mRoundCount = FirebaseDatabase.getInstance().getReference().child("Clubs").child(clubName).child("roundCount");
        mRoundReference = FirebaseDatabase.getInstance().getReference().child("Clubs").child(clubName).child("Rounds");
    }

    private void initializeVariables() {
        roundNumber = 0;
        mPoster = (ImageView) findViewById(R.id.iv_main_poster);
        mEditPoster = (ImageView) findViewById(R.id.iv_edit);
        mAddFaq = (ImageView) findViewById(R.id.iv_add_faq);
        mName = (EditText) findViewById(R.id.et_event_name);
        mDescription = (EditText) findViewById(R.id.et_description);
        mDate = (EditText) findViewById(R.id.et_date);
        mTime = (EditText) findViewById(R.id.et_time);
        mVenue = (EditText) findViewById(R.id.et_venue);
        mSpecialNotes = (EditText) findViewById(R.id.et_special_notes);

        mCoordinators = (AutoCompleteTextView) findViewById(R.id.et_coordinators);

        mCoordinatorView = (LinearLayout) findViewById(R.id.ll_coordinators);
        mFaqView = (LinearLayout) findViewById(R.id.ll_faq);

        mSubmit = (Button) findViewById(R.id.bt_submit);

        mClubName = Methods.getEmailSharedPref(AddRoundActivity.this);
        mFaqs = new ArrayList<FAQ>();
        mCurrRound = new Round();
        mFaqQuestion = new ArrayList<>();
        mFaqAnswer = new ArrayList<>();
        mCoordinatorsAll = new ArrayList<>();
        mSelectedCorrdinators = new ArrayList<>();
        mCoordinatorDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Coordinators");
    }


    private void addOnClickListeners() {
        //Select poster
        mEditPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Poster"), REQ_ID_POSTER);
            }
        });

        //select date
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                java.util.Calendar mcurrentDate = java.util.Calendar.getInstance();
                final int mYear = mcurrentDate.get(java.util.Calendar.YEAR);
                final int mMonth = mcurrentDate.get(java.util.Calendar.MONTH);
                final int mDay = mcurrentDate.get(java.util.Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(AddRoundActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        java.util.Calendar myCalendar = java.util.Calendar.getInstance();
                        myCalendar.setTimeInMillis(0);
                        myCalendar.set(java.util.Calendar.YEAR, selectedyear);
                        myCalendar.set(java.util.Calendar.MONTH, selectedmonth);
                        myCalendar.set(java.util.Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yy"; //Change as you need
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

                        mDate.setText(sdf.format(myCalendar.getTime()));

                        date = (myCalendar.getTimeInMillis());
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.getDatePicker().setCalendarViewShown(true);
                mDatePicker.show();
            }
        });

        //select time
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final java.util.Calendar mcurrentDate = java.util.Calendar.getInstance();
                final int mHour = mcurrentDate.get(java.util.Calendar.HOUR_OF_DAY);
                final int mMinute = mcurrentDate.get(java.util.Calendar.MINUTE);

                TimePickerDialog mTimePicker = new TimePickerDialog(AddRoundActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String displayTime = String.format(Locale.ENGLISH, "%02d:%02d", hourOfDay, minute);

                        mTime.setText(displayTime);
                        time = 1000L * (hourOfDay * 60 * 60 + minute * 60) - TimeZone.getDefault().getRawOffset();
                    }
                }, mHour, mMinute, true);

                mTimePicker.setTitle("Select date");
                mTimePicker.show();
            }
        });

        //add faq
        mAddFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFaqList();
            }
        });

        //click to add event to db
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateFaqList();
                boolean checker = checkDetails();
                if (checker) {
                    updatePostersBasedOnDownloadURLs();


                } else {
                    Toast.makeText(AddRoundActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void populateFaqList() {
        mFaqs.clear();
        for (int i = 0; i < mFaqQuestion.size(); i++) {
            FAQ faq = new FAQ();
            faq.setQuestion(mFaqQuestion.get(i).getText().toString());
            faq.setAnswer(mFaqAnswer.get(i).getText().toString());
            mFaqs.add(faq);
        }
    }

    //upload new event
    private void uploadEvent() {
        mCurrRound = new Round(mPosterUri.toString(), mName.getText().toString(), mDescription.getText().toString(),
                mVenue.getText().toString(), mSpecialNotes.getText().toString(), mClubName, date,
                time, roundNumber, mSelectedCorrdinators, mFaqs);
        mDatabaseRef.child(String.valueOf(roundNumber)).setValue(mCurrRound).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mRoundCount.setValue(roundNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgressDialog.dismiss();
                                Toast.makeText(AddRoundActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AddRoundActivity.this, "Asif failed u", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(AddRoundActivity.this, "Failure", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    //download url of poster
    private void updatePostersBasedOnDownloadURLs() {
        mProgressDialog = new ProgressDialog(AddRoundActivity.this);
        mProgressDialog.setTitle("Uploading Round");
        mProgressDialog.setMessage("Please Wait");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        Bitmap bmp = null;
        String imgName = mPosterUri.getLastPathSegment();
        try {

            bmp = MediaStore.Images.Media.getBitmap(AddRoundActivity.this.getContentResolver(), mPosterUri);
            bmp = Bitmap.createScaledBitmap(bmp, 500, (int) ((float) bmp.getHeight() / bmp.getWidth() * 500), true);
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, boas);


            imgName = imgName.replace('.', '@');
            int lastIndex = imgName.lastIndexOf('@');

            String imgExtension = imgName.substring(lastIndex + 1);

            String imageName = clubName + "." + imgExtension;

            firebaseStorage = FirebaseStorage.getInstance().getReference().child("event_posters").child(imageName);

            UploadTask uploadTask = firebaseStorage.putBytes(boas.toByteArray());

            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return firebaseStorage.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        mPosterUri = task.getResult();
                        uploadEvent();
                    }
                }
            });
        } catch (Exception e) {
            Log.d("yatin", e.getMessage());
            mProgressDialog.dismiss();
        }
    }

    private boolean checkDetails() {

        if (!isNetworkAvailable()) {
            Toast.makeText(AddRoundActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mName.getText().toString().equals("")) {
            mName.setError("Enter a Name");
            return false;
        }
        if (mDescription.getText().toString().equals("")) {
            mDescription.setError("Enter a Description");
            return false;
        }
        if (mDate.getText().toString().equals("")) {
            mDate.setError("Enter a Date");
            return false;
        }
        if (mTime.getText().toString().equals("")) {
            mTime.setError("Enter a Time");
            return false;
        }
        if (mVenue.getText().toString().equals("")) {
            mVenue.setError("Enter a Venue");
            return false;
        }
        if (mSpecialNotes.getText().toString().equals("")) {
            mSpecialNotes.setError("Enter Special Notes");
            return false;
        }
        if (mSelectedCorrdinators.size() <= 0) {
            mCoordinators.setError("Enter Coordinates");
            return false;
        }
        if (mPosterUri == null) {
            Toast.makeText(AddRoundActivity.this, "Add a poster first", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (int i = 0; i < mFaqQuestion.size(); i++) {
            if ((mFaqQuestion.get(i).getText().toString().equals("")  ||  mFaqAnswer.get(i).getText().toString().equals(""))) {

                Toast.makeText(this, "faq cant empty", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (mFaqs.size() == 0) {
            Toast.makeText(AddRoundActivity.this, "FAQs cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    private void addFaqList() {
        @SuppressLint("InflateParams") LinearLayout view = (LinearLayout) LayoutInflater.from(AddRoundActivity.this).inflate(R.layout.layout_faq, null, false);
        EditText mQuestion = (EditText) view.findViewById(R.id.et_faq_question);
        EditText mAnswer = (EditText) view.findViewById(R.id.et_faq_answer);



        mFaqQuestion.add(mQuestion);
        mFaqAnswer.add(mAnswer);
        mFaqView.addView(view);

        //mFaqs.add(new FAQ(mQuestion.getText().toString(),mAnswer.getText().toString()));

    }

    //adding listeners to Autocomplete text view for adding coordinators.
    private void setUpAutoCompleteTextView() {

        coordinatorAdapter = new AutocompleteCoordinatorAdapter(AddRoundActivity.this,
                R.layout.coordinator_item_view,
                R.id.coordinator_item_name,
                mCoordinatorsAll);

        mCoordinators.setAdapter(coordinatorAdapter);
        mCoordinators.setThreshold(1);
        mCoordinators.setInputType(InputType.TYPE_CLASS_TEXT);

        mCoordinators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCoordinators.showDropDown();
            }
        });

        mCoordinators.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Coordinator mCoordinator = mCoordinatorsAll.get(i);
                mCoordinators.setText("", false);

                for (int j = 0; j < mSelectedCorrdinators.size(); j++) {
                    if (mCoordinator.getCoordPhone().equals(mSelectedCorrdinators.get(j).getCoordPhone())) {
                        return;
                    }
                }

                mSelectedCorrdinators.add(mCoordinator);
                addCoordinatorToLayout(mCoordinator);
            }
        });
    }


    private void addCoordinatorToLayout(final Coordinator mCoordinator) {
        @SuppressLint("InflateParams") final LinearLayout view = (LinearLayout) LayoutInflater.from(AddRoundActivity.this).inflate(R.layout.layout_coordinators, null, false);
        ((TextView) view.findViewById(R.id.tvUserName)).setText(mCoordinator.getCoordName());
        ((TextView) view.findViewById(R.id.tvUserId)).setText(mCoordinator.getCoordPhone());

        view.findViewById(R.id.removeCoordinator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                mCoordinatorView.removeView(view);

                for (int k = 0; k < mSelectedCorrdinators.size(); k++) {
                    if (mCoordinator.getCoordPhone().equals(mSelectedCorrdinators.get(k).getCoordPhone())) {
                        mSelectedCorrdinators.remove(k);
                        break;
                    }
                }

            }
        });

        mCoordinatorView.addView(view);
    }

    @Override
    protected void onPause() {
        super.onPause();

        removeDatabaseListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    // remove listener when activity is paused.
    private void removeDatabaseListeners() {
        if (mCoordinatorChildEventListener != null) {
            mCoordinatorDatabaseReference.removeEventListener(mCoordinatorChildEventListener);
            mCoordinatorChildEventListener = null;
        }

        if(mRoundListener != null){
            mRoundReference.removeEventListener(mRoundListener);
            mRoundListener = null;
        }
    }

    // add listener on databse when activity is resumed.
    private void addDatabaseListeners() {
        if (mCoordinatorChildEventListener == null) {
            mCoordinatorChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    try {
                        if (dataSnapshot.getKey().equals(Methods.getEmailSharedPref(getApplicationContext()))) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d("yatin", snapshot.getValue().toString());
                                Coordinator coordinator = snapshot.getValue(Coordinator.class);
                                coordinator.setCoordId(snapshot.getKey());
                                mCoordinatorsAll.add(coordinator);
                            }
                        }
                        updateList();
                    } catch (Exception e) {
                        Log.d("yd", e.getMessage());
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    try {
                        if (dataSnapshot.getKey().equals(Methods.getEmailSharedPref(getApplicationContext()))) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d("yatin", snapshot.getValue().toString());
                                Coordinator coordinator = snapshot.getValue(Coordinator.class);
                                coordinator.setCoordId(snapshot.getKey());
                                mCoordinatorsAll.add(coordinator);
                            }
                        }
                        updateList();
                    } catch (Exception e) {
                        Log.d("yd", e.getMessage());
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot.getKey().equals(Methods.getEmailSharedPref(getApplicationContext()))) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d("removed", snapshot.getValue().toString());
                                Coordinator coordinator = snapshot.getValue(Coordinator.class);
                                coordinator.setCoordId(snapshot.getKey());
                                mCoordinatorsAll.remove(coordinator);
                            }
                        }
                        updateList();
                    } catch (Exception e) {
                        Log.d("yd", e.getMessage());
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mCoordinatorDatabaseReference.addChildEventListener(mCoordinatorChildEventListener);
        }

        if(mRoundListener == null){
            mRoundListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    try {
                        if(dataSnapshot.getKey().equals(String.valueOf(roundNumber))){
                            Round curr = dataSnapshot.getValue(Round.class);
                            displayData(curr);
                        }
                    } catch (Exception e){
                        e.getMessage();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    try {
                        if(dataSnapshot.getKey().equals(String.valueOf(roundNumber))){
                            Round curr = dataSnapshot.getValue(Round.class);
                            displayData(curr);
                        }
                    } catch (Exception e){
                        e.getMessage();
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mRoundReference.addChildEventListener(mRoundListener);
        }
    }

    private void displayData(Round curr) {

        Picasso.get().load(curr.getPoster()).into(mPoster);

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(curr.getPoster());
        try {
            final File localFile = File.createTempFile("image", "jpg");
            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    mPosterUri = Uri.fromFile(localFile);
                }
            });
        } catch (Exception e){
            Log.d("yatin", e.getMessage());
        }

        mName.setText(curr.getName());
        mDescription.setText(curr.getDescription());
        mVenue.setText(curr.getVenue());
        mSpecialNotes.setText(curr.getSpecialNotes());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(curr.getDate());

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        String formattedDate = sdf.format(cal.getTime());
        mDate.setText(formattedDate);

        cal.setTimeInMillis(curr.getTime());
        SimpleDateFormat sdf1 = new SimpleDateFormat("kk:mm", Locale.US);
        String formattedTime = sdf1.format(cal.getTime());
        mTime.setText(formattedTime);

        mSelectedCorrdinators = curr.getCoordinators();
        mCoordinatorView.removeAllViews();
        mFaqView.removeAllViews();


        for(int i = 0; i < curr.getCoordinators().size(); i++){
            final Coordinator mCoordinator = curr.getCoordinators().get(i);
            @SuppressLint("InflateParams") final LinearLayout view = (LinearLayout) LayoutInflater.from(AddRoundActivity.this).inflate(R.layout.layout_coordinators, null, false);
            ((TextView) view.findViewById(R.id.tvUserName)).setText(mCoordinator.getCoordName());
            ((TextView) view.findViewById(R.id.tvUserId)).setText(mCoordinator.getCoordPhone());

            view.findViewById(R.id.removeCoordinator).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    mCoordinatorView.removeView(view);

                    for (int k = 0; k < mSelectedCorrdinators.size(); k++) {
                        if (mCoordinator.getCoordPhone().equals(mSelectedCorrdinators.get(k).getCoordPhone())) {
                            mSelectedCorrdinators.remove(k);
                            break;
                        }
                    }

                }
            });

            mCoordinatorView.addView(view);
        }

        for(int i = 0; i < curr.getFaq().size(); i++){
            @SuppressLint("InflateParams") LinearLayout view = (LinearLayout) LayoutInflater.from(AddRoundActivity.this).inflate(R.layout.layout_faq, null, false);
            EditText mQuestion = (EditText) view.findViewById(R.id.et_faq_question);
            EditText mAnswer = (EditText) view.findViewById(R.id.et_faq_answer);

            mQuestion.setText(curr.getFaq().get(i).getQuestion());
            mAnswer.setText(curr.getFaq().get(i).getAnswer());

            mFaqQuestion.add(mQuestion);
            mFaqAnswer.add(mAnswer);
            mFaqView.addView(view);
        }


    }

    private void updateList() {
        sort(mCoordinatorsAll);
        coordinatorAdapter = new AutocompleteCoordinatorAdapter(
                AddRoundActivity.this,
                R.layout.coordinator_item_view,
                R.id.coordinator_item_name,
                mCoordinatorsAll
        );
        mCoordinators.setAdapter(coordinatorAdapter);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQ_ID_POSTER:
                if (data != null && data.getData() != null) {
                    mPosterUri = data.getData();
                    try {
                        Bitmap bitmap;
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mPosterUri);
                        float finalWidth = min(100, bitmap.getWidth());
                        bitmap = Bitmap.createScaledBitmap(bitmap, (int) finalWidth, (int) (finalWidth / bitmap.getWidth() * bitmap.getHeight()),
                                true);
                        mPoster.setImageBitmap(bitmap);
                        mPoster.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }
}
