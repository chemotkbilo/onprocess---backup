package com.ideationdesignservices.txtbook;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import com.flurry.android.FlurryAgent;
import com.itextpdf.text.pdf.PdfFormField;

public class LegalActivity extends Activity {
    private ShareActionProvider mShareActionProvider;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) findViewById(R.id.legalText)).setText(Html.fromHtml(getString(R.string.legal_text)));
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
        getMenuInflater().inflate(R.menu.legal_menu, menu);
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
                        Intent intent2 = new Intent(LegalActivity.this, OptionsActivity.class);
                        intent2.addFlags(PdfFormField.FF_RICHTEXT);
                        LegalActivity.this.startActivity(intent2);
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
}
