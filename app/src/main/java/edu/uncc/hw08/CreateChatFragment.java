package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateChatFragment extends Fragment {

    FirebaseAuth mAuth;
    ArrayList<User> Users = new ArrayList<>();
    UsersListAdapter adapter;
    String message = "";
    User selectedUser = new User();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateChatFragment newInstance(String param1, String param2) {
        CreateChatFragment fragment = new CreateChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        getActivity().setTitle("New Chat");

        TextView selectedName = view.findViewById(R.id.textViewSelectedUser);
        EditText messageEntry = view.findViewById(R.id.editTextMessage);

        ListView listView = view.findViewById(R.id.listView);
        adapter = new UsersListAdapter(getActivity(), R.layout.users_row_item, Users);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUser = Users.get(i);
                Log.d("demo", "onItemClick: "+Users.get(i));

                selectedName.setText(Users.get(i).getName());
            }
        });

        view.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration.remove();
                mlisten.goBack();
            }
        });

        view.findViewById(R.id.buttonSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = messageEntry.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(getActivity(), "Please enter a starting message.", Toast.LENGTH_SHORT).show();
                } else if (selectedUser == null) {
                    Toast.makeText(getActivity(), "Please select a user to chat with.", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth = FirebaseAuth.getInstance();
                    ArrayList<String> userIds = new ArrayList<>();
                    userIds.add(selectedUser.getUserId());
                    userIds.add(mAuth.getCurrentUser().getUid());

                    ArrayList<String> userNames = new ArrayList<>();
                    userNames.add(selectedUser.getName());
                    userNames.add(mAuth.getCurrentUser().getDisplayName());

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    HashMap<String, Object> chatEntry = new HashMap<>();
                    chatEntry.put("userIds", userIds);
                    chatEntry.put("userNames", userNames);

                    DocumentReference docRef = db.collection("Chats").document();
                    String docId = docRef.getId();
                    chatEntry.put("DocId", docId);
                    chatEntry.put("recentDate", FieldValue.serverTimestamp());
                    chatEntry.put("recentChat", message);

                    docRef.set(chatEntry).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DocumentReference docRef = db.collection("Chats").document(docId).collection("Chats").document();
                                HashMap<String, Object> chatMessage = new HashMap<>();
                                chatMessage.put("message", message);
                                chatMessage.put("date", FieldValue.serverTimestamp());
                                chatMessage.put("name", mAuth.getCurrentUser().getDisplayName());
                                chatMessage.put("userId", mAuth.getCurrentUser().getUid());

                                docRef.set(chatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("demo", "onComplete: Chat Added");
                                            registration.remove();
                                            mlisten.goBack();
                                        } else {
                                            Log.d("demo", "onComplete: Chat was not add");
                                        }
                                    }
                                });
                            } else {
                                Log.d("demo", "onComplete: Not successful"+task.getException().getMessage());
                            }
                        }
                    });

                }
            }
        });

        getUserList();

    }

    mListener mlisten;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mlisten = (mListener) context;
    }

    public interface mListener{
        void goBack();
    }

    ListenerRegistration registration;

    public void getUserList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();

            registration = db.collection("UsersHW8").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.d("demo", "onEvent: " + error);
                    } else {
                        Users.clear();
                        for (QueryDocumentSnapshot document : value) {
                            User user = new User(
                                    document.getString("name"),
                                    document.getString("email"),
                                    document.getString("userId"),
                                    document.getBoolean("isOnline")
                            );

                            if ((!(user.getUserId().equals(mAuth.getCurrentUser().getUid()))) && FirebaseAuth.getInstance().getCurrentUser() != null) {
                                Users.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
    }
}