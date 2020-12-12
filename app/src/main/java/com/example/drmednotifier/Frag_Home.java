package com.example.drmednotifier;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drmednotifier.data.Medication;
import com.example.drmednotifier.data.User;
import com.example.drmednotifier.data.UserDao;
import com.example.drmednotifier.data.UserDatabase;
import com.example.drmednotifier.medicationslist.MedActivitiesListViewModel;
import com.example.drmednotifier.medicationslist.MedicationRecyclerViewAdapter;
import com.example.drmednotifier.medicationslist.MedicationsListViewModel;

import java.util.List;

public class Frag_Home extends Fragment {
    private MedicationRecyclerViewAdapter medicationRecyclerViewAdapter;

    public Frag_Home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MedicationsListViewModel medicationsListViewModel = ViewModelProviders.of(this).get(MedicationsListViewModel.class);
        MedActivitiesListViewModel medActivitiesListViewModel = ViewModelProviders.of(this).get(MedActivitiesListViewModel.class);

        medicationRecyclerViewAdapter = new MedicationRecyclerViewAdapter(getContext(), medicationsListViewModel, medActivitiesListViewModel);

        medicationsListViewModel.getMedicationsLiveData().observe(this, new Observer<List<Medication>>() {
            @Override
            public void onChanged(List<Medication> medications) {
                if (medications != null) {
                    medicationRecyclerViewAdapter.setMedications(medications);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag__home, container, false);

        RecyclerView medicationsRecyclerView = view.findViewById(R.id.home_listmed_recylerView);
        medicationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        medicationsRecyclerView.setAdapter(medicationRecyclerViewAdapter);

/*
        // TODO: medication list item swipe feature
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition(); //get position which is swipe

                if (swipeDir == ItemTouchHelper.LEFT) {    //if swipe left

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); //alert for confirm to delete
                    builder.setMessage("Are you sure to delete?");    //set message

                    builder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() { //when click on DELETE
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            medicationRecyclerViewAdapter.notifyItemRemoved(position);    //item removed from recylcerview
                            Medication medication = medicationRecyclerViewAdapter.getMedByPos(position);
                            medication.deschedule(getContext());
                            medicationsListViewModel.deleteById(medication.getMedId());
                            medActivitiesListViewModel.deleteByMedId(medication.getMedId());
                            return;
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {  //not removing items if cancel is done
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            medicationRecyclerViewAdapter.notifyDataSetChanged();
                            return;
                        }
                    }).show();  //show alert dialog
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(medicationsRecyclerView);
 */

        getUserInfo(view);

        // Get the date info from the Calendar
        CalendarView theCalendarView = view.findViewById(R.id.calendarView);
        theCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Intent intent = new Intent(getActivity(), Dose_Page.class);
                intent.putExtra("YEAR", year);
                intent.putExtra("MONTH", month);
                intent.putExtra("DAY", dayOfMonth);
                startActivity(intent);
            }
        });

        return view;
    }

    private void getUserInfo(View view) {
        UserDatabase userDatabase = UserDatabase.getDatabase(getContext());
        UserDao userDao = userDatabase.userDao();
        List<User> usersLiveData = userDao.getUser();

        if (!usersLiveData.isEmpty()) {
            String fullName = "";
            String age = "";
            User user = usersLiveData.get(0);

            if (user.getFirstName().length() != 0) {
                fullName = user.getFirstName() + " " + user.getLastName();
                age = String.format("%d", user.getAge()) + " years old";

                ((TextView) view.findViewById(R.id.txtViewUserName)).setText(fullName);
                ((TextView) view.findViewById(R.id.txtViewUserAge)).setText(age);
            }

            ((TextView) view.findViewById(R.id.txtViewUserName)).setText(fullName);

            ImageView z = view.findViewById(R.id.home_avatar);
            String avatar = user.getAvatar();
            if (avatar.equals("a1")) {
                z.setImageResource(R.drawable.a1);
            }
            if (avatar.equals("a2")) {
                z.setImageResource(R.drawable.a2);
            }
        }
    }
}