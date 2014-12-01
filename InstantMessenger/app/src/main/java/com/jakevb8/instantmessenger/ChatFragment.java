package com.jakevb8.instantmessenger;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jvanburen on 12/1/2014.
 */
public class ChatFragment extends Fragment {

    private ArrayAdapter<String> _adapter;
    private ListView _messageList;
    private static boolean _loadUnreadMessage;

    public static ChatFragment newInstance(boolean loadUnreadMessages) {
        _loadUnreadMessage = loadUnreadMessages;
        return new ChatFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, null);
        _messageList = (ListView) view.findViewById(R.id.direct_connect_message_list);

        _adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<String>());
        _messageList.setAdapter(_adapter);

        if(_loadUnreadMessage) {
            MessageDatabase messageDatabase = new MessageDatabase(getActivity());
            List<Message> messages = messageDatabase.getNewMessages();
            messageDatabase.close();
            for(Message message : messages) {
                addMessage(message.UserId + ": " + message.UserName + ": " + message.Message);
            }
        }
        return  view;
    }

    public void addMessage(String message) {
        _adapter.add(message);
    }
}
