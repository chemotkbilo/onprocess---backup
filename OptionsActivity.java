package com.ideationdesignservices.txtbook;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.flurry.android.FlurryAgent;
import com.flurry.org.apache.avro.file.DataFileConstants;
import com.ideationdesignservices.txtbook.pdf.TxtBookPdfSettings;
import com.ideationdesignservices.txtbook.sms.SmsMmsMessageThread;
import com.ideationdesignservices.txtbook.text.CountTextWatcher;
import com.ideationdesignservices.txtbook.util.ContactUtilities;
import com.ideationdesignservices.txtbook.widget.DateDialogFragment;
import com.ideationdesignservices.txtbook.widget.DateDialogFragmentListener;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OptionsActivity extends Activity {
    private SmsMmsMessageThread chosenThread;
    private List<String> conversationChoices;
    private Calendar endDate;
    private Boolean endDateChosen;
    private DateDialogFragment frag;
    private ShareActionProvider mShareActionProvider;
    private long maximumEndDate;
    private long maximumStartDate;
    private List<SmsMmsMessageThread> messageThreads;
    private long minimumEndDate;
    private long minimumStartDate;
    private CountTextWatcher myNameTextWatcher;
    private Calendar startDate;
    private Boolean startDateChosen;
    private CountTextWatcher theirNameTextWatcher;
    private String userDefaultName;

    public static java.lang.String getMmsAddress(android.content.Context r17, long r18) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x008f in list [B:12:0x008c]
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:173)
*/
        /*
        r2 = 4;
        r4 = new java.lang.String[r2];
        r2 = 0;
        r3 = "address";
        r4[r2] = r3;
        r2 = 1;
        r3 = "contact_id";
        r4[r2] = r3;
        r2 = 2;
        r3 = "charset";
        r4[r2] = r3;
        r2 = 3;
        r3 = "type";
        r4[r2] = r3;
        r5 = 0;
        r2 = "content://mms";
        r2 = android.net.Uri.parse(r2);
        r12 = r2.buildUpon();
        r2 = java.lang.String.valueOf(r18);
        r2 = r12.appendPath(r2);
        r3 = "addr";
        r2.appendPath(r3);
        r2 = r17.getContentResolver();
        r3 = r12.build();
        r6 = 0;
        r7 = 0;
        r14 = r2.query(r3, r4, r5, r6, r7);
        if (r14 == 0) goto L_0x009c;
    L_0x003f:
        r2 = r14.moveToFirst();	 Catch:{ all -> 0x0090 }
        if (r2 == 0) goto L_0x0097;	 Catch:{ all -> 0x0090 }
    L_0x0045:
        r2 = 0;	 Catch:{ all -> 0x0090 }
        r15 = r14.getString(r2);	 Catch:{ all -> 0x0090 }
        r2 = "insert-address-token";	 Catch:{ all -> 0x0090 }
        r2 = r15.contentEquals(r2);	 Catch:{ all -> 0x0090 }
        if (r2 == 0) goto L_0x008a;	 Catch:{ all -> 0x0090 }
    L_0x0052:
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0090 }
        r3 = "content://mms/";	 Catch:{ all -> 0x0090 }
        r2.<init>(r3);	 Catch:{ all -> 0x0090 }
        r0 = r18;	 Catch:{ all -> 0x0090 }
        r2 = r2.append(r0);	 Catch:{ all -> 0x0090 }
        r3 = "/addr";	 Catch:{ all -> 0x0090 }
        r2 = r2.append(r3);	 Catch:{ all -> 0x0090 }
        r2 = r2.toString();	 Catch:{ all -> 0x0090 }
        r7 = android.net.Uri.parse(r2);	 Catch:{ all -> 0x0090 }
        r6 = r17.getContentResolver();	 Catch:{ all -> 0x0090 }
        r8 = 0;	 Catch:{ all -> 0x0090 }
        r9 = "type=151";	 Catch:{ all -> 0x0090 }
        r10 = 0;	 Catch:{ all -> 0x0090 }
        r11 = 0;	 Catch:{ all -> 0x0090 }
        r13 = r6.query(r7, r8, r9, r10, r11);	 Catch:{ all -> 0x0090 }
        r2 = r13.moveToNext();	 Catch:{ all -> 0x0090 }
        if (r2 == 0) goto L_0x008a;	 Catch:{ all -> 0x0090 }
    L_0x0080:
        r2 = "address";	 Catch:{ all -> 0x0090 }
        r2 = r13.getColumnIndex(r2);	 Catch:{ all -> 0x0090 }
        r15 = r13.getString(r2);	 Catch:{ all -> 0x0090 }
    L_0x008a:
        if (r14 == 0) goto L_0x008f;
    L_0x008c:
        r14.close();
    L_0x008f:
        return r15;
    L_0x0090:
        r2 = move-exception;
        if (r14 == 0) goto L_0x0096;
    L_0x0093:
        r14.close();
    L_0x0096:
        throw r2;
    L_0x0097:
        if (r14 == 0) goto L_0x009c;
    L_0x0099:
        r14.close();
    L_0x009c:
        r2 = 17039374; // 0x104000e float:2.424461E-38 double:8.4185693E-317;
        r0 = r17;
        r15 = r0.getString(r2);
        goto L_0x008f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ideationdesignservices.txtbook.OptionsActivity.getMmsAddress(android.content.Context, long):java.lang.String");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ((LinearLayout) findViewById(R.id.scrollLayout)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (OptionsActivity.this.chosenThread == null) {
                    OptionsActivity.this.showNoConversationAlert();
                }
            }
        });
        ((TextView) findViewById(R.id.useNewNamesDesc)).setText(Html.fromHtml(getString(R.string.options_use_nicknames_desc)));
        this.startDateChosen = Boolean.valueOf(false);
        this.endDateChosen = Boolean.valueOf(false);
        this.minimumStartDate = -1;
        this.maximumStartDate = -1;
        this.minimumEndDate = -1;
        this.maximumEndDate = -1;
        this.userDefaultName = ContactUtilities.getPhoneOwnerName(this);
        prepareConversationSpinner();
        prepareDateSpinners();
        prepareNameEditors();
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
                new Builder(this).setIcon(17301543).setTitle(R.string.start_over_title).setMessage(R.string.start_over_confirmation).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent2 = new Intent(OptionsActivity.this, OptionsActivity.class);
                        intent2.addFlags(PdfFormField.FF_RICHTEXT);
                        OptionsActivity.this.startActivity(intent2);
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

    public void prepareConversationSpinner() {
        loadThreads();
        ArrayAdapter<String> adapter = new ArrayAdapter(this, 17367048, this.conversationChoices);
        adapter.setDropDownViewResource(17367049);
        Spinner spinner = (Spinner) findViewById(R.id.conversationSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                EditText backCoverNoteTextField = (EditText) OptionsActivity.this.findViewById(R.id.theirNameTextField);
                CheckBox allDatesCheckBox;
                CheckBox usePhotosCheckBox;
                CheckBox useDateStampCheckBox;
                CheckBox useNewNamesCheckBox;
                if (position <= 0 || position >= OptionsActivity.this.messageThreads.size()) {
                    OptionsActivity.this.chosenThread = null;
                    OptionsActivity.this.clearDates();
                    allDatesCheckBox = (CheckBox) OptionsActivity.this.findViewById(R.id.allDatesCheckBox);
                    allDatesCheckBox.setChecked(false);
                    allDatesCheckBox.setEnabled(false);
                    allDatesCheckBox.setClickable(false);
                    usePhotosCheckBox = (CheckBox) OptionsActivity.this.findViewById(R.id.usePhotosCheckBox);
                    usePhotosCheckBox.setEnabled(false);
                    usePhotosCheckBox.setClickable(false);
                    useDateStampCheckBox = (CheckBox) OptionsActivity.this.findViewById(R.id.useDateStampCheckBox);
                    useDateStampCheckBox.setEnabled(false);
                    useDateStampCheckBox.setClickable(false);
                    useNewNamesCheckBox = (CheckBox) OptionsActivity.this.findViewById(R.id.useNewNamesCheckBox);
                    useNewNamesCheckBox.setEnabled(false);
                    useNewNamesCheckBox.setClickable(false);
                    backCoverNoteTextField.setText(OptionsActivity.this.getString(R.string.options_their_name_hint));
                } else {
                    OptionsActivity.this.chosenThread = (SmsMmsMessageThread) OptionsActivity.this.messageThreads.get(position);
                    Spinner dateRangeStartSpinner = (Spinner) OptionsActivity.this.findViewById(R.id.dateRangeStartSpinner);
                    dateRangeStartSpinner.setEnabled(true);
                    dateRangeStartSpinner.setClickable(true);
                    Spinner dateRangeEndsSpinner = (Spinner) OptionsActivity.this.findViewById(R.id.dateRangeEndsSpinner);
                    dateRangeEndsSpinner.setEnabled(true);
                    dateRangeEndsSpinner.setClickable(true);
                    usePhotosCheckBox = (CheckBox) OptionsActivity.this.findViewById(R.id.usePhotosCheckBox);
                    usePhotosCheckBox.setEnabled(true);
                    usePhotosCheckBox.setClickable(true);
                    useDateStampCheckBox = (CheckBox) OptionsActivity.this.findViewById(R.id.useDateStampCheckBox);
                    useDateStampCheckBox.setEnabled(true);
                    useDateStampCheckBox.setClickable(true);
                    useNewNamesCheckBox = (CheckBox) OptionsActivity.this.findViewById(R.id.useNewNamesCheckBox);
                    useNewNamesCheckBox.setEnabled(true);
                    useNewNamesCheckBox.setClickable(true);
                    allDatesCheckBox = (CheckBox) OptionsActivity.this.findViewById(R.id.allDatesCheckBox);
                    allDatesCheckBox.setEnabled(true);
                    allDatesCheckBox.setChecked(false);
                    allDatesCheckBox.setClickable(true);
                    backCoverNoteTextField.setText(OptionsActivity.this.chosenThread.otherName);
                }
                OptionsActivity.this.updateDateRangeBounds();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                OptionsActivity.this.chosenThread = null;
            }
        });
    }

    private void loadThreads() {
        this.messageThreads = new ArrayList();
        this.conversationChoices = new ArrayList();
        this.conversationChoices.add(0, getString(R.string.cancel));
        this.messageThreads.add(null);
        String[] projection = new String[]{"*"};
        Cursor cur = getContentResolver().query(Uri.parse("content://mms-sms/conversations?simple=true"), projection, null, null, "date DESC");
        if (cur.moveToFirst()) {
            do {
                Long threadId = Long.valueOf(cur.getLong(cur.getColumnIndex("_id")));
                String snippet = cur.getString(cur.getColumnIndex("snippet"));
                String[] detailProjection = new String[]{"_id", "ct_t", "address"};
                Cursor cur2 = getContentResolver().query(Uri.parse("content://mms-sms/conversations/" + threadId), detailProjection, null, null, null);
                if (cur2.moveToFirst()) {
                    String displayText;
                    SmsMmsMessageThread thread = new SmsMmsMessageThread();
                    thread.threadId = threadId;
                    thread.otherName = ContactUtilities.findNameByAddress(this, cur2.getString(cur2.getColumnIndex("address")));
                    this.messageThreads.add(thread);
                    if (thread.otherName == null || thread.otherName.length() <= 0) {
                        if (snippet == null || snippet.length() == 0 || DataFileConstants.NULL_CODEC.equals(snippet)) {
                            snippet = "(No Text)";
                        }
                        displayText = snippet;
                    } else {
                        displayText = thread.otherName;
                    }
                    this.conversationChoices.add(displayText);
                }
            } while (cur.moveToNext());
        }
        cur.close();
    }

    public void prepareDateSpinners() {
        this.startDate = Calendar.getInstance();
        this.endDate = Calendar.getInstance();
        Spinner dateRangeStartSpinner = (Spinner) findViewById(R.id.dateRangeStartSpinner);
        OnTouchListener Spinner_OnTouchStartDate = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    OptionsActivity.this.chooseStartDate(v);
                }
                return true;
            }
        };
        OnKeyListener Spinner_OnKeyStartDate = new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 23) {
                    return false;
                }
                OptionsActivity.this.chooseStartDate(v);
                return true;
            }
        };
        Spinner dateRangeEndsSpinner = (Spinner) findViewById(R.id.dateRangeEndsSpinner);
        dateRangeStartSpinner.setOnTouchListener(Spinner_OnTouchStartDate);
        dateRangeStartSpinner.setOnKeyListener(Spinner_OnKeyStartDate);
        OnTouchListener Spinner_OnTouchEndDate = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    OptionsActivity.this.chooseEndDate(v);
                }
                return true;
            }
        };
        OnKeyListener Spinner_OnKeyEndDate = new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode != 23) {
                    return false;
                }
                OptionsActivity.this.chooseEndDate(v);
                return true;
            }
        };
        dateRangeEndsSpinner.setOnTouchListener(Spinner_OnTouchEndDate);
        dateRangeEndsSpinner.setOnKeyListener(Spinner_OnKeyEndDate);
        for (Spinner spinner : new Spinner[]{dateRangeStartSpinner, dateRangeEndsSpinner}) {
            ArrayAdapter<String> adapter = new ArrayAdapter(this, 17367048, new String[]{spinner.getPrompt().toString()});
            adapter.setDropDownViewResource(17367049);
            spinner.setAdapter(adapter);
        }
        if (this.chosenThread == null) {
            dateRangeStartSpinner.setEnabled(false);
            dateRangeEndsSpinner.setEnabled(false);
            dateRangeStartSpinner.setClickable(false);
            dateRangeEndsSpinner.setClickable(false);
        }
    }

    public void chooseStartDate(View view) {
        Calendar calendar;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DateDialogFragmentListener anonymousClass8 = new DateDialogFragmentListener() {
            public void updateChangedDate(int year, int month, int day) {
                ((Spinner) OptionsActivity.this.findViewById(R.id.dateRangeStartSpinner)).setPrompt(new StringBuilder(String.valueOf(String.valueOf(month + 1))).append("-").append(String.valueOf(day)).append("-").append(String.valueOf(year)).toString());
                OptionsActivity.this.startDate.set(year, month, day, 0, 0, 0);
                SimpleDateFormat dateFormat = new SimpleDateFormat("E. MMM d, yyyy", Locale.US);
                ((TextView) OptionsActivity.this.findViewById(R.id.startDateTextView)).setText(dateFormat.format(OptionsActivity.this.startDate.getTime()));
                ((CheckBox) OptionsActivity.this.findViewById(R.id.allDatesCheckBox)).setChecked(false);
                OptionsActivity.this.startDateChosen = Boolean.valueOf(true);
                OptionsActivity.this.minimumEndDate = OptionsActivity.this.startDate.getTimeInMillis();
                if (OptionsActivity.this.endDate.getTimeInMillis() < OptionsActivity.this.minimumEndDate) {
                    OptionsActivity.this.endDate.setTimeInMillis(OptionsActivity.this.minimumEndDate);
                    ((TextView) OptionsActivity.this.findViewById(R.id.endDateTextView)).setText(dateFormat.format(OptionsActivity.this.endDate.getTime()));
                    OptionsActivity.this.endDateChosen = Boolean.valueOf(true);
                }
            }
        };
        if (this.startDateChosen.booleanValue()) {
            calendar = this.startDate;
        } else {
            calendar = null;
        }
        this.frag = DateDialogFragment.newInstance(this, anonymousClass8, calendar, this.minimumStartDate, this.maximumStartDate, "Set Start Date");
        this.frag.show(ft, "DateDialogFragment");
    }

    public void chooseEndDate(View view) {
        Calendar calendar;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DateDialogFragmentListener anonymousClass9 = new DateDialogFragmentListener() {
            public void updateChangedDate(int year, int month, int day) {
                ((Spinner) OptionsActivity.this.findViewById(R.id.dateRangeEndsSpinner)).setPrompt(new StringBuilder(String.valueOf(String.valueOf(month + 1))).append("-").append(String.valueOf(day)).append("-").append(String.valueOf(year)).toString());
                OptionsActivity.this.endDate.set(year, month, day, 23, 59, 59);
                ((TextView) OptionsActivity.this.findViewById(R.id.endDateTextView)).setText(new SimpleDateFormat("E. MMM d, yyyy", Locale.US).format(OptionsActivity.this.endDate.getTime()));
                ((CheckBox) OptionsActivity.this.findViewById(R.id.allDatesCheckBox)).setChecked(false);
                OptionsActivity.this.endDateChosen = Boolean.valueOf(true);
            }
        };
        if (this.endDateChosen.booleanValue()) {
            calendar = this.endDate;
        } else {
            calendar = null;
        }
        this.frag = DateDialogFragment.newInstance(this, anonymousClass9, calendar, this.minimumEndDate, this.maximumEndDate, "Set End Date");
        this.frag.show(ft, "DateDialogFragment");
    }

    public void onDateRangeClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.allDatesCheckBox /*2131296302*/:
                if (checked) {
                    allDates();
                    return;
                } else {
                    clearDates();
                    return;
                }
            default:
                return;
        }
    }

    public void allDates() {
        Builder alert = new Builder(this);
        alert.setCancelable(false);
        alert.setTitle("Lifetime");
        alert.setMessage("TIP: For long text-message threads with photos we suggest choosing shorter date ranges and creating several Volumes. Three months of texts can be 200 pages long.");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                TextView startDateTextView = (TextView) OptionsActivity.this.findViewById(R.id.startDateTextView);
                TextView endDateTextView = (TextView) OptionsActivity.this.findViewById(R.id.endDateTextView);
                if (OptionsActivity.this.chosenThread != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("E. MMM d, yyyy", Locale.US);
                    OptionsActivity.this.startDate.setTimeInMillis(OptionsActivity.this.minimumStartDate);
                    startDateTextView.setText(dateFormat.format(OptionsActivity.this.startDate.getTime()));
                    OptionsActivity.this.startDateChosen = Boolean.valueOf(true);
                    OptionsActivity.this.endDate.setTimeInMillis(OptionsActivity.this.maximumEndDate);
                    endDateTextView.setText(dateFormat.format(OptionsActivity.this.endDate.getTime()));
                    OptionsActivity.this.endDateChosen = Boolean.valueOf(true);
                    return;
                }
                startDateTextView.setText(PdfObject.NOTHING);
                endDateTextView.setText(PdfObject.NOTHING);
                OptionsActivity.this.startDateChosen = Boolean.valueOf(false);
                OptionsActivity.this.endDateChosen = Boolean.valueOf(false);
            }
        });
        alert.show();
    }

    public void clearDates() {
        TextView endDateTextView = (TextView) findViewById(R.id.endDateTextView);
        ((TextView) findViewById(R.id.startDateTextView)).setText(PdfObject.NOTHING);
        endDateTextView.setText(PdfObject.NOTHING);
        this.startDateChosen = Boolean.valueOf(false);
        this.endDateChosen = Boolean.valueOf(false);
        if (this.chosenThread == null) {
            Spinner dateRangeStartSpinner = (Spinner) findViewById(R.id.dateRangeStartSpinner);
            dateRangeStartSpinner.setEnabled(false);
            dateRangeStartSpinner.setClickable(false);
            Spinner dateRangeEndsSpinner = (Spinner) findViewById(R.id.dateRangeEndsSpinner);
            dateRangeEndsSpinner.setEnabled(false);
            dateRangeEndsSpinner.setClickable(false);
        }
    }

    public void onUseNamesClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        EditText myName = (EditText) findViewById(R.id.myNameTextField);
        myName.setEnabled(checked);
        myName.setFocusable(checked);
        myName.setFocusableInTouchMode(checked);
        EditText theirName = (EditText) findViewById(R.id.theirNameTextField);
        theirName.setEnabled(checked);
        theirName.setFocusable(checked);
        theirName.setFocusableInTouchMode(checked);
    }

    public void updateDateRangeBounds() {
        this.minimumStartDate = -1;
        this.minimumEndDate = -1;
        this.maximumStartDate = -1;
        this.maximumEndDate = -1;
        if (this.chosenThread != null) {
            Cursor cur = getContentResolver().query(Uri.parse("content://mms-sms/conversations/" + this.chosenThread.threadId), new String[]{"normalized_date", "ct_t"}, null, null, "normalized_date");
            if (cur.moveToLast()) {
                this.maximumStartDate = cur.getLong(cur.getColumnIndex("normalized_date"));
                this.maximumEndDate = this.maximumStartDate;
                this.minimumStartDate = this.maximumStartDate;
                this.minimumEndDate = this.maximumStartDate;
            }
            if (cur.moveToFirst()) {
                this.minimumStartDate = cur.getLong(cur.getColumnIndex("normalized_date"));
                this.minimumEndDate = this.minimumStartDate;
            }
            cur.close();
            SimpleDateFormat dateFormat = new SimpleDateFormat("E. MMM d, yyyy", Locale.US);
            this.startDate.setTimeInMillis(this.minimumStartDate);
            ((TextView) findViewById(R.id.startDateTextView)).setText(dateFormat.format(this.startDate.getTime()));
            this.startDateChosen = Boolean.valueOf(true);
            TextView endDateTextView = (TextView) findViewById(R.id.endDateTextView);
            if (((CheckBox) findViewById(R.id.allDatesCheckBox)).isChecked()) {
                this.endDate.setTimeInMillis(this.maximumEndDate);
                endDateTextView.setText(dateFormat.format(this.endDate.getTime()));
                this.endDateChosen = Boolean.valueOf(true);
                return;
            }
            endDateTextView.setText(PdfObject.NOTHING);
            this.endDateChosen = Boolean.valueOf(false);
        }
    }

    public void prepareNameEditors() {
        this.myNameTextWatcher = new CountTextWatcher();
        this.myNameTextWatcher.countView = (TextView) findViewById(R.id.myNameCountTextView);
        this.myNameTextWatcher.maxLength = getResources().getInteger(R.integer.maxNameLength);
        EditText myNameTextField = (EditText) findViewById(R.id.myNameTextField);
        myNameTextField.addTextChangedListener(this.myNameTextWatcher);
        myNameTextField.setText(this.userDefaultName);
        myNameTextField.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    OptionsActivity.this.myNameTextWatcher.countView.setVisibility(0);
                    OptionsActivity.this.myNameTextWatcher.countView.setLayoutParams(new LayoutParams(-2, -2));
                    return;
                }
                OptionsActivity.this.myNameTextWatcher.countView.setVisibility(4);
                OptionsActivity.this.myNameTextWatcher.countView.setLayoutParams(new LayoutParams(0, -2));
                if (((EditText) v).getText().length() == 0) {
                    ((EditText) v).setText(OptionsActivity.this.userDefaultName);
                }
            }
        });
        this.theirNameTextWatcher = new CountTextWatcher();
        this.theirNameTextWatcher.countView = (TextView) findViewById(R.id.theirNameCountTextView);
        this.theirNameTextWatcher.maxLength = getResources().getInteger(R.integer.maxNameLength);
        EditText theirNameTextField = (EditText) findViewById(R.id.theirNameTextField);
        theirNameTextField.addTextChangedListener(this.theirNameTextWatcher);
        theirNameTextField.setText(getString(R.string.options_their_name_hint));
        theirNameTextField.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    OptionsActivity.this.theirNameTextWatcher.countView.setVisibility(0);
                    OptionsActivity.this.theirNameTextWatcher.countView.setLayoutParams(new LayoutParams(-2, -2));
                    return;
                }
                OptionsActivity.this.theirNameTextWatcher.countView.setVisibility(4);
                OptionsActivity.this.theirNameTextWatcher.countView.setLayoutParams(new LayoutParams(0, -2));
                if (((EditText) v).getText().length() != 0) {
                    return;
                }
                if (OptionsActivity.this.chosenThread == null) {
                    ((EditText) v).setText(OptionsActivity.this.getString(R.string.options_their_name_hint));
                } else {
                    ((EditText) v).setText(OptionsActivity.this.chosenThread.otherName);
                }
            }
        });
        theirNameTextField.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 6) {
                    return false;
                }
                ((InputMethodManager) OptionsActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(v.getWindowToken(), 0);
                v.clearFocus();
                return true;
            }
        });
    }

    public void showNoConversationAlert() {
        new Builder(this).setTitle("Conversation Required").setMessage("Please select a conversation first...").setNeutralButton("Close", null).show();
    }

    public void importTexts(View view) {
        if (this.chosenThread == null) {
            new Builder(this).setTitle("Conversation Required").setMessage("You must select a conversation first").setNeutralButton("Close", null).show();
        } else if (this.startDateChosen.booleanValue() && this.endDateChosen.booleanValue()) {
            String theirName;
            Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putLong(TxtBookPdfSettings.CHOSEN_THREAD_ID, this.chosenThread.threadId.longValue());
            editor.putLong(TxtBookPdfSettings.START_DATE_LIMIT_MILLIS, this.startDate.getTimeInMillis());
            editor.putLong(TxtBookPdfSettings.END_DATE_LIMIT_MILLIS, this.endDate.getTimeInMillis());
            if (((CheckBox) findViewById(R.id.usePhotosCheckBox)).isChecked()) {
                editor.putBoolean(TxtBookPdfSettings.USE_PHOTOS, true);
            } else {
                editor.putBoolean(TxtBookPdfSettings.USE_PHOTOS, false);
            }
            if (((CheckBox) findViewById(R.id.useDateStampCheckBox)).isChecked()) {
                editor.putBoolean(TxtBookPdfSettings.USE_TIMESTAMPS, true);
            } else {
                editor.putBoolean(TxtBookPdfSettings.USE_TIMESTAMPS, false);
            }
            String myName = "I";
            if (((CheckBox) findViewById(R.id.useNewNamesCheckBox)).isChecked()) {
                myName = ((EditText) findViewById(R.id.myNameTextField)).getText().toString();
                theirName = ((EditText) findViewById(R.id.theirNameTextField)).getText().toString();
                if (myName == null || theirName == null || myName.length() == 0 || theirName.length() == 0) {
                    new Builder(this).setTitle("Names Required").setMessage("'Use New Names' was selected, but both names were not provided.").setNeutralButton("Close", null).show();
                    return;
                }
            }
            theirName = this.chosenThread.otherName;
            myName = this.userDefaultName;
            editor.putString(TxtBookPdfSettings.MY_NAME, myName);
            editor.putString(TxtBookPdfSettings.THEIR_NAME, theirName);
            editor.apply();
            startActivity(new Intent(this, ConversationActivity.class));
        } else {
            new Builder(this).setTitle("Date Required").setMessage("Please select a date range.").setNeutralButton("Close", null).show();
        }
    }
}
