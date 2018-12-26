package com.example.phakneath.ckccassignment.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.phakneath.ckccassignment.Model.LostFound;
import com.example.phakneath.ckccassignment.Model.Notification;
import com.example.phakneath.ckccassignment.Model.SaveLostFound;
import com.example.phakneath.ckccassignment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class FounderDialog extends DialogFragment implements View.OnClickListener{

    private EditText location, contact, remark;
    private Button cancel, submit;
    private View view;
    String tlocation, tcontact, tremark;
    SaveLostFound saveLostFound;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String uID, status;
    LostFound lostFound;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = LayoutInflater.from(getActivity()).inflate(R.layout.foundlost_dialog, null);
        AlertDialog dialog =builder.setView(view).setCancelable(false).create();

        initView();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cancel.setOnClickListener(this::onClick);
        submit.setOnClickListener(this::onClick);
        return dialog;
    }

    private void initView() {
        location = view.findViewById(R.id.item);
        contact = view.findViewById(R.id.contact);
        remark = view.findViewById(R.id.Remark);

        cancel = view.findViewById(R.id.cancel);
        submit = view.findViewById(R.id.submit);
    }

    public void setData(SaveLostFound saveLostFound)
    {
        this.saveLostFound = saveLostFound;
    }
    public void setLostFound(LostFound lostFound){
        this.lostFound = lostFound;
    }
    @Override
    public void onClick(View v) {
        if(v == cancel)
        {
            dismiss();
        }
        else if(v == submit)
        {
            if(!TextUtils.isEmpty(location.getText().toString().trim()) && !TextUtils.isEmpty(contact.getText().toString().trim()) && !TextUtils.isEmpty(remark.getText().toString().trim()))
            {
                submitData();
                dismiss();
                Toast.makeText(getContext(), "Submit Successful", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Please fill all above information", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void submitData()
    {
        tlocation = location.getText().toString();
        tcontact = contact.getText().toString();
        tremark = remark.getText().toString();
        status = "new";
        String id = "N" + uID + System.currentTimeMillis();
        Notification notification = new Notification(id,tlocation,tcontact,tremark,saveLostFound.getId(),saveLostFound.getMyOwnerID(),uID, status, System.currentTimeMillis());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Posting").child("individual").child(saveLostFound.getMyOwnerID()).child("notification").child("receive").child(id).setValue(notification);
        mDatabase.child("Posting").child("individual").child(uID).child("notification").child("send").child(id).setValue(notification);

        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        fdb.getReference("user/"+lostFound.getMyOwner()+"/playerId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String playerID = dataSnapshot.getValue(String.class);
                try {
                    OneSignal.postNotification(new JSONObject("{'title':'Item Found','contents': {'en':'The item in the post \""+lostFound.getItem()+"\" has been found'}, 'include_player_ids': ['" + playerID+ "']}"), null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
