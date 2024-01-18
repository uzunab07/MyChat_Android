package edu.uncc.hw08;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uncc.hw08.databinding.ChatListItemBinding;
import edu.uncc.hw08.databinding.FragmentChatBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private Chat mParam1;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(Chat param1) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Chat) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    FragmentChatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Chat - "+mParam1.getName());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatMessgeAdapter();
        binding.recyclerView.setAdapter(adapter);

        getChatMessages();

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.editTextMessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(getActivity(), "You need to type a message!", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    HashMap<String, Object> newMessage = new HashMap<>();
                    newMessage.put("date", FieldValue.serverTimestamp());
                    newMessage.put("message", message);
                    newMessage.put("name", mAuth.getCurrentUser().getDisplayName());
                    newMessage.put("userId", mAuth.getCurrentUser().getUid());

                    db.collection("Chats").document(mParam1.getDocId()).collection("Chats").add(newMessage).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            binding.editTextMessage.setText("");
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Chats").document(mParam1.getDocId()).update("recentDate", FieldValue.serverTimestamp());
                            db.collection("Chats").document(mParam1.getDocId()).update("recentChat", message);
                        }
                    });
                }
            }
        });

        binding.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration.remove();
                mlisten.goBack();
            }
        });

        binding.buttonDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Chats").document(mParam1.getDocId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            registration.remove();
                            mlisten.goBack();
                        } else {
                            Toast.makeText(getActivity(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }


    ChatMessgeAdapter adapter;
    ArrayList<ChatMessage> chatMessages = new ArrayList<>();

    class ChatMessgeAdapter extends RecyclerView.Adapter<ChatMessgeAdapter.ChatMessageViewHolder> {

        @NonNull
        @Override
        public ChatMessgeAdapter.ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ChatListItemBinding binding = ChatListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ChatMessgeAdapter.ChatMessageViewHolder(binding);
            //return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ChatMessgeAdapter.ChatMessageViewHolder holder, int position) {
            ChatMessage chatMessage = chatMessages.get(position);
            holder.setupUi(chatMessage);
        }

        @Override
        public int getItemCount() {
            return chatMessages.size();
        }

        class ChatMessageViewHolder extends RecyclerView.ViewHolder {
            ChatListItemBinding mBinding;

            ChatMessage mChatMessage;
            public ChatMessageViewHolder(@NonNull ChatListItemBinding binding){
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUi(ChatMessage chatMessage){
                mChatMessage = chatMessage;

                mBinding.textViewMsgBy.setText(mChatMessage.getName());
                mBinding.textViewMsgText.setText(mChatMessage.getMessage());
                if(mChatMessage.getTimeStamp() != null) {
                    mBinding.textViewMsgOn.setText(mChatMessage.getTimeStamp().toDate().toString());
                }

                if(mChatMessage.getUserId().equals(mAuth.getCurrentUser().getUid())){
                    mBinding.imageViewDelete.setVisibility(View.VISIBLE);
                } else {
                    mBinding.imageViewDelete.setVisibility(View.GONE);
                }

                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Are you sure you want to delete this message?").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("Chats").document(mParam1.getDocId()).collection("Chats").document(mChatMessage.getMessageId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("demo", "onComplete: DELETED");
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        /*
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Chats").document(mParam1.getDocId()).collection("Chats").document(mChatMessage.getMessageId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("demo", "onComplete: DELETED");
                                adapter.notifyDataSetChanged();
                            }
                        });

                         */
                    }
                });
            }
        }
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

    public void getChatMessages(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        registration = db.collection("Chats").document(mParam1.getDocId()).collection("Chats").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    chatMessages.clear();
                    for (QueryDocumentSnapshot document : value) {
                        ChatMessage chatMessage = new ChatMessage(
                                document.getTimestamp("date"),
                                document.getString("message"),
                                document.getString("name"),
                                document.getString("userId")
                        );

                        chatMessage.setMessageId(document.getId());

                        chatMessages.add(chatMessage);
                    }
                    //Notify adapter
                    adapter.notifyDataSetChanged();
                }
            });
    }
}