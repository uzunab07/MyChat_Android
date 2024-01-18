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
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyChatsFragment extends Fragment {
    private FirebaseAuth mAuth;
    MyChatAdapter adapter;
    ArrayList<Chat> chats = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyChatsFragment newInstance(String param1, String param2) {
        MyChatsFragment fragment = new MyChatsFragment();
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
        return inflater.inflate(R.layout.fragment_my_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        getActivity().setTitle("My Chats");

        ListView listView = view.findViewById(R.id.listView);
        adapter = new MyChatAdapter(getContext(), R.layout.my_chats_list_item, chats);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Chat chat = chats.get(i);
                mlisten.goToChat(chat);
            }
        });

        view.findViewById(R.id.buttonNewChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlisten.goToAddChat();
            }
        });

        view.findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSignOut();
            }
        });

        getChats();
        getUserStatus();
    }

    mListener mlisten;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mlisten = (mListener) context;
    }

    public interface mListener{
        void goToAddChat();
        void logOut();
        void goToChat(Chat chat);
    }


    public void setSignOut(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null) {
            db.collection("UsersHW8").document(mAuth.getCurrentUser().getUid()).update("isOnline", false).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("demo", "onComplete: Is Offline now!");
                    mAuth.signOut();
                    //FirebaseAuth.getInstance().signOut();
                    mlisten.logOut();
                }
            });
        }
    }

    public void getUserStatus(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null) {
            db.collection("UsersHW8").document(mAuth.getCurrentUser().getUid()).update("isOnline", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("demo", "onComplete: Now online!");
                }
            });
        }
    }

    public void getChats(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();

        if(User != null) {

            db.collection("Chats").orderBy("recentDate", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    chats.clear();

                    for (QueryDocumentSnapshot document : value) {
                        String documentID = document.getString("DocId");
                        ArrayList<String> recipients = (ArrayList<String>) document.get("userNames");
                        recipients.remove(mAuth.getCurrentUser().getDisplayName());
                        ArrayList<String> recepIDs = (ArrayList<String>) document.get("userIds");

                        Chat chat = new Chat(
                                recipients.get(0),
                                document.getString("recentChat"),
                                document.getTimestamp("recentDate")
                        );

                        chat.setDocId(document.getId());

                        if ((recepIDs.contains(mAuth.getCurrentUser().getUid()))) {
                            chats.add(chat);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });

        }
    }
}