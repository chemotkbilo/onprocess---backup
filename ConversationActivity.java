package com.ideationdesignservices.txtbook;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import com.flurry.android.FlurryAgent;
import com.ideationdesignservices.txtbook.pdf.TxtBookPdfSettings;
import com.ideationdesignservices.txtbook.sms.MessageListAdapter;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfObject;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;

public class ConversationActivity extends Activity {
    private MessageListAdapter adapter;
    private ShareActionProvider mShareActionProvider;
    public ArrayList<Integer> selectedRows;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int[] to = new int[0];
        int[] layouts = new int[]{R.layout.message_layout_me, R.layout.message_layout_them};
        String selection = "normalized_date >= " + settings.getLong(TxtBookPdfSettings.START_DATE_LIMIT_MILLIS, 0) + " AND normalized_date <= " + settings.getLong(TxtBookPdfSettings.END_DATE_LIMIT_MILLIS, 0);
        String[] columns = new String[]{"_id", "ct_t", "normalized_date"};
        this.adapter = new MessageListAdapter(this, layouts, getContentResolver().query(Uri.parse("content://mms-sms/conversations/" + settings.getLong(TxtBookPdfSettings.CHOSEN_THREAD_ID, 0)), columns, selection, null, "normalized_date"), columns, to, 0);
        this.adapter.usePhotos = Boolean.valueOf(settings.getBoolean(TxtBookPdfSettings.USE_PHOTOS, false));
        this.adapter.useTimestamps = Boolean.valueOf(settings.getBoolean(TxtBookPdfSettings.USE_TIMESTAMPS, false));
        String myName = settings.getString(TxtBookPdfSettings.MY_NAME, PdfObject.NOTHING);
        String theirName = settings.getString(TxtBookPdfSettings.THEIR_NAME, PdfObject.NOTHING);
        this.adapter.myName = myName;
        this.adapter.theirName = theirName;
        this.selectedRows = new ArrayList();
        selectAll();
        LayoutInflater inflater = LayoutInflater.from(this);
        ViewGroup list = (ListView) findViewById(R.id.smslist);
        list.addFooterView(inflater.inflate(R.layout.message_footer, list, false));
        View headerView = inflater.inflate(R.layout.message_header, list, false);
        ((TextView) headerView.findViewById(R.id.startTextView)).setText(getString(R.string.start));
        list.addHeaderView(headerView);
        list.setAdapter(this.adapter);
        boolean showMemoryWarning = false;
        if (this.selectedRows.size() > 5000) {
            showMemoryWarning = true;
        } else if (this.adapter.usePhotos.booleanValue()) {
            int numImages = 0;
            Iterator it = this.selectedRows.iterator();
            while (it.hasNext()) {
                if (this.adapter.rowHasImage(((Integer) it.next()).intValue())) {
                    numImages++;
                }
            }
            if (numImages > 50) {
                showMemoryWarning = true;
            }
        }
        if (showMemoryWarning) {
            Builder alert = new Builder(this);
            alert.setCancelable(false);
            alert.setTitle("Large Selection");
            alert.setMessage("Wow! This would make an impressive txt-book, but it may be too big to e-mail. This many texts can be hundreds of pages long and photos can make the txt-book file too big to e-mail. You may want to deselect the messages and photos you do not want to include or pick a shorter date range.");
            alert.setPositiveButton("OK", new OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
        }
    }

    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Txtbook.FLURRY_KEY);
    }

    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_share).getActionProvider();
        Intent shareIntent = new Intent();
        shareIntent.setAction("android.intent.action.SEND");
        shareIntent.setType("text/plain");
        shareIntent.putExtra("android.intent.extra.SUBJECT", getString(R.string.share_subject));
        shareIntent.putExtra("android.intent.extra.TEXT", getString(R.string.share_text));
        this.mShareActionProvider.setShareIntent(shareIntent);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                finish();
                return true;
            case R.id.menu_settings /*2131296329*/:
                Intent intent4 = new Intent(this, LegalActivity.class);
                intent4.addFlags(PdfFormField.FF_RICHTEXT);
                startActivity(intent4);
                return true;
            case R.id.menu_new /*2131296330*/:
                new Builder(this).setIcon(17301543).setTitle(R.string.start_over_title).setMessage(R.string.start_over_confirmation).setPositiveButton(R.string.yes, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent2 = new Intent(ConversationActivity.this, OptionsActivity.class);
                        intent2.addFlags(PdfFormField.FF_RICHTEXT);
                        ConversationActivity.this.startActivity(intent2);
                    }
                }).setNegativeButton(R.string.cancel, null).show();
                return true;
            case R.id.menu_email /*2131296331*/:
                Intent emailIntent = new Intent("android.intent.action.SEND");
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra("android.intent.extra.EMAIL", new String[]{"support@txt-book.com"});
                emailIntent.putExtra("android.intent.extra.SUBJECT", "I have something to say...");
                startActivity(Intent.createChooser(emailIntent, "Contact Us:"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void selectOrClearAll(View view) {
        if (this.selectedRows.size() == 0) {
            selectAll();
        } else {
            clearAll();
        }
    }

    public void clearAll() {
        this.selectedRows = new ArrayList();
        this.adapter.notifyDataSetChanged();
        updateSelectOrClearAllButton();
    }

    public void selectAll() {
        this.selectedRows = new ArrayList();
        int numRows = this.adapter.getCursor().getCount();
        int i = 0;
        while (i < numRows) {
            if (this.adapter.usePhotos.booleanValue() || !this.adapter.rowHasImage(i)) {
                this.selectedRows.add(Integer.valueOf(i));
            }
            i++;
        }
        this.adapter.notifyDataSetChanged();
        updateSelectOrClearAllButton();
    }

    public void updateSelectOrClearAllButton() {
        Button selectOrClearAllButton = (Button) findViewById(R.id.selectOrClearAllButton);
        if (this.selectedRows.size() == 0) {
            selectOrClearAllButton.setText(getString(R.string.conversation_select_all));
        } else {
            selectOrClearAllButton.setText(getString(R.string.conversation_clear_all));
        }
    }

    public void goBookStyle(View view) {
        if (this.selectedRows.size() == 0) {
            new Builder(this).setTitle("Nothing Selected").setMessage("Please select at least one item.").setNeutralButton("Close", null).show();
            return;
        }
        Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        JSONArray arr = new JSONArray();
        Iterator<Integer> itr = this.selectedRows.iterator();
        while (itr.hasNext()) {
            arr.put((Integer) itr.next());
        }
        editor.putString(TxtBookPdfSettings.SELECTED_ROWS_LIST, arr.toString());
        editor.apply();
        startActivity(new Intent(this, BookStyleActivity.class));
    }
}
