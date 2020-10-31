package com.example.drmednotifier;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.example.drmednotifier.data.User;
import com.example.drmednotifier.data.UserDao;
import com.example.drmednotifier.data.UserDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Setting_Page extends AppCompatActivity {
    int[] image = new int[]{R.drawable.ic_baseline_list_24,R.drawable.ic_dose};
    String[] headline = new String[]{"Medicine List","Today's Dose"};
    String[] bottomline = new String[]{"Display Your Medicine List","View Today's Medicine Dose  "};
    SwitchCompat switchCompat;
    String current_avatar;
    String avatar_T;
    String default_avatar;
    int  count;

    private UserDatabase userDatabase;
    private UserDao userDao;
    private List<User> usersLiveData;
    String name,g,age_gender;
    int gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting__page);

        setUserProperties();

        /*spinner set up*/
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        List<String> s_list = new ArrayList<>();
        s_list.add("Default Music");
        s_list.add("Light Music");
        s_list.add("Pop Music");
        s_list.add("Rock Hard");

        ArrayAdapter<String>adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,s_list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#D81B60"));
                ((TextView) parent.getChildAt(0)).setTextSize(15);
                String text = parent.getItemAtPosition(position).toString();
                if(text=="Default Music"){

                }
                else{
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Toolbar toolbar =  findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        /*switch */
        switchCompat = (SwitchCompat)  findViewById(R.id.Switch);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(), "Switch is ON", Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(), "Switch is OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });



        /*Hash map for list view*/
        ListView listView= findViewById(R.id.listview);
        List<HashMap<String,String>> list =new ArrayList<>();
        for(int i = 0; i<2;i++){

            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("image",Integer.toString(image[i]));
            hashMap.put("headline",headline[i]);
            hashMap.put("bottomline",bottomline[i]);

            list.add(hashMap);

        }

        /*bar menu set up*/
        String[] from = {"image","headline","bottomline"};
        int[] to ={R.id.home_avatar,R.id.headline,R.id.bottomline};
        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(),list,R.layout.listview_case,from,to);
        listView.setAdapter(simpleAdapter);

        /*list view click*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long l) {
                if(position==0){
                    Intent intent = new Intent(Setting_Page.this, Medicine_List.class);
                    startActivity(intent);
                }

                if(position==1){
                    Intent intent = new Intent(Setting_Page.this, Dose_Page.class);
                    startActivity(intent);
                }
            }
        });
    }

    /*checkbox*/
    public void CheckboxClickCheck(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_top:
                if (checked){
                    CheckBox x = view.findViewById(R.id.checkbox_top);
                    x.setText("Checked Thank u!");
                    ;
                    // Put some meat on the sandwich
                }
                if (!checked ){
                    CheckBox x = view.findViewById(R.id.checkbox_top);
                    x.setText("Not checked!! Loser");
                    ;
                    // Put some meat on the sandwich
                }
                break;
        }
    }
    /*transfer to New_User-Profile when click layout bar*/
    public void click_name_tag(View view) {
        Intent i = new Intent(this,New_User_Profile.class);
        startActivityForResult(i, 2);
    }

    public void avatar_click(View view) {
        Intent i = new Intent(this, Popup_Window.class);
        startActivityForResult(i, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == RESULT_OK) {
                setUserProperties();
            }
        }
    }

    private void setUserProperties () {
        userDatabase = Room.databaseBuilder(getApplicationContext(), UserDatabase.class, "user_database").allowMainThreadQueries().build();
        userDao = userDatabase.userDao();
        usersLiveData = userDao.getUser();

        ImageView z = findViewById(R.id.home_avatar);

        if (!usersLiveData.isEmpty()) {
            User user = usersLiveData.get(0);

            if (user.getFirstName().length() != 0) {
                name = user.getFirstName() + " " + user.getLastName();
                gender = user.getGender();
                if (gender == 0) {
                    g = "Male";
                } else if (gender == 1) {
                    g = "Female";
                } else if (gender == 2) {
                    g = "Others";
                }

                if (gender == -1) age_gender = user.getAge() + " years old";
                else age_gender = user.getAge() + " years old, " + g;

                ((TextView) findViewById(R.id.headline_name)).setText(name);
                ((TextView) findViewById(R.id.bottomline_age)).setText(age_gender);
            }

            default_avatar = user.getAvatar();
            if (default_avatar.equals("a1")) {
                z.setImageResource(R.drawable.a1);
            }
            if (default_avatar.equals("a2")) {
                z.setImageResource(R.drawable.a2);
            }
        }
    }
}