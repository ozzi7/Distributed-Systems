package ch.ethz.inf.vs.android.blumers.lamport;

import java.util.ArrayList;
import java.util.Hashtable;


import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Chat extends Activity {

	public ArrayList<Message> messageList;
	public MessageListAdapter adapter;
	public String username;
	
	public Button sendButton;
	public EditText messageEditText;
	public ListView messageListView;
	
	public int myIndex;
	public Hashtable<String, String> clientNamehashtable = new Hashtable<String,String>();
	
	public Connection connection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		username = this.getIntent().getStringExtra("name");
		
		messageList = new ArrayList<Chat.Message>();

		sendButton = (Button)findViewById(R.id.sendButton);
		messageEditText = (EditText)findViewById(R.id.messageEditText);
		messageListView = (ListView)findViewById(R.id.messageListView);
		
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String textToSend = messageEditText.getEditableText().toString();
				messageEditText.getEditableText().clear();
				connection.textMsgOutputBuffer.addMsg(textToSend);
			}
		});
		
		adapter = new MessageListAdapter(this, messageList);
		messageListView.setAdapter(adapter);
		connection = new Connection(this);
	}
	
	@Override
	public void onBackPressed() {
	    connection.stop();
	    finish();
	    return;
	}  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	// writes message to screen
	public void showMessage(String name, String msg)
	{
		final Message m = new Message(name, msg);
		messageListView.post(new Runnable() {
			
			@Override
			public void run() {
				
				messageList.add(m);
				adapter.notifyDataSetChanged();
				messageListView.setSelection(adapter.getCount() - 1);
			}
		});
	}
	
	class Message
	{
		public Message(String name, String msg)
		{
			this.name = name;
			this.msg = msg;
		}
		
		public String name;
		public String msg;
	}
	
	class MessageListAdapter extends ArrayAdapter<Message>
	{
		private final Context context;
		private final ArrayList<Message> values;
		
		public MessageListAdapter(Context context, ArrayList<Message> values) {
		    super(context, R.layout.message_layout, values);
		    this.context = context;
		    this.values = values;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.message_layout, parent, false);
			TextView nameText = (TextView) rowView.findViewById(R.id.nameText);
			nameText.setText(values.get(position).name);
			TextView msgText = (TextView) rowView.findViewById(R.id.msgText);
			msgText.setText(values.get(position).msg);
			return rowView;
		}
	}
}
