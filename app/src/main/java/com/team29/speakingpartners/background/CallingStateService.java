package com.team29.speakingpartners.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team29.speakingpartners.activity.voicecall.IncomingSplashActivity;
import com.team29.speakingpartners.model.CallingRequestListModel;

import javax.annotation.Nullable;

public class CallingStateService extends Service {

    private static final String TAG = CallingStateService.class.getSimpleName();

    FirebaseFirestore db;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service Started");
        Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service Destroyed");
        Toast.makeText(getApplicationContext(), "Service Stop", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = FirebaseFirestore.getInstance();

        listenIncomingCall();

        return super.onStartCommand(intent, flags, startId);
    }

    void listenIncomingCall() {
        if (FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {
            db.collection("calling")
                    .whereEqualTo("to_email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .whereEqualTo("from_status", 1)
                    .whereEqualTo("to_status", 0)
                    .whereEqualTo("call_type", 1)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.e(TAG, "Listen Error");
//                                return;
                            }

                            for (DocumentChange change : snapshots.getDocumentChanges()) {

                                if(!change.getType().equals(DocumentChange.Type.MODIFIED)) {
                                    CallingRequestListModel model = change.getDocument().toObject(CallingRequestListModel.class).withId(change.getDocument().getId());
                                    /*if (model.getTo_status() == 0 && model.getCall_type() == 1) {

                                        //break;
                                    }*/

                                    Intent i = new Intent(CallingStateService.this, IncomingSplashActivity.class);
                                    i.putExtra("REQ_MODEL", model);
                                    i.putExtra("ID", model.id);
                                    startActivity(i);
                                    stopService(new Intent(CallingStateService.this,CallingStateService.class));

                                }
                            }
                        }
                    });
        }
    }
}
