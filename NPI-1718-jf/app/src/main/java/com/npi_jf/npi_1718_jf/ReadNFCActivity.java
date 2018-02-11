package com.npi_jf.npi_1718_jf;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;

public class ReadNFCActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

        private NfcAdapter mNfcAdapter;
        private TextView tv;
        private EditText et;
        private PendingIntent mPendingIntent;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_read_nfc);

                tv = (TextView) findViewById(R.id.textView_show);
                et = (EditText) findViewById(R.id.editText_send);

                NfcManager nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
                mNfcAdapter = nfcManager.getDefaultAdapter();

                mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        }

        private NdefMessage getTestMessage() {

                byte[] mimeBytes = "".getBytes(Charset.forName("US-ASCII"));
                byte[] id = new byte[] {1, 3, 3, 7};
                byte[] payload = et.getText().toString().getBytes(Charset.forName("US-ASCII"));
                return new NdefMessage(new NdefRecord[] {new NdefRecord(NdefRecord.TNF_MIME_MEDIA, null, null, payload)});
        }


        @Override
        protected void onResume() {

                super.onResume();

                mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
                mNfcAdapter.setNdefPushMessageCallback(this, this);

        }

        // sending message
        @Override
        public NdefMessage createNdefMessage(NfcEvent event) {
                return getTestMessage();
        }


        private NdefMessage[] getNdefMessages(Intent intent) {
                Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (rawMessages != null) {

                        NdefMessage[] messages = new NdefMessage[rawMessages.length];

                        for (int i = 0; i < messages.length; i++) {
                                messages[i] = (NdefMessage) rawMessages[i];
                        }
                        return messages;

                } else {
                        return null;
                }
        }

        static String displayByteArray(byte[] bytes) {

                String res="";
                StringBuilder builder = new StringBuilder().append("[");

                for (int i = 0; i < bytes.length; i++) {
                        res+=(char)bytes[i];
                }

                return res;
        }

        // displaying message
        @Override
        protected void onNewIntent(Intent intent) {

                super.onNewIntent(intent);

                NdefMessage[] messages = getNdefMessages(intent);
                tv.setText(displayByteArray(messages[0].toByteArray()));
                Toast.makeText(this, displayByteArray(messages[0].toByteArray()), Toast.LENGTH_LONG).show();

        }

}


