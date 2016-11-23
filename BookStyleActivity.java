package com.ideationdesignservices.txtbook;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import com.flurry.android.FlurryAgent;
import com.ideationdesignservices.txtbook.pdf.TxtBookPdfSettings;
import com.itextpdf.text.pdf.BaseField;
import com.itextpdf.text.pdf.PdfFormField;
import java.util.Timer;
import java.util.TimerTask;

public class BookStyleActivity extends Activity {
    private static final String googleDocsUrl = "http://docs.google.com/viewer?url=";
    private Animation alphaAnimation;
    private ShareActionProvider mShareActionProvider;
    private Timer magnifyAnimationTimer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_style);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
                        Intent intent2 = new Intent(BookStyleActivity.this, OptionsActivity.class);
                        intent2.addFlags(PdfFormField.FF_RICHTEXT);
                        BookStyleActivity.this.startActivity(intent2);
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

    public void chooseConversationStyle(View view) {
        stopMagnifyBlinking();
        ImageView conversationImageView = (ImageView) findViewById(R.id.styleConversationImageView);
        if (((CheckBox) view).isChecked()) {
            ((CheckBox) findViewById(R.id.storyStyleCheckBox)).setChecked(false);
            ImageView storybookImageView = (ImageView) findViewById(R.id.styleStorybookImageView);
            if (storybookImageView.getAlpha() > 0.0f) {
                storybookImageView.animate().alpha(0.0f);
            }
            conversationImageView.animate().alpha(BaseField.BORDER_WIDTH_THIN);
            setConversationTextAlpha(BaseField.BORDER_WIDTH_THIN);
            setStorybookTextAlpha(0.5f);
            startMagnifyBlinking();
            return;
        }
        conversationImageView.animate().alpha(0.0f);
        setStorybookTextAlpha(BaseField.BORDER_WIDTH_THIN);
    }

    public void chooseStoryStyle(View view) {
        stopMagnifyBlinking();
        ImageView storybookImageView = (ImageView) findViewById(R.id.styleStorybookImageView);
        if (((CheckBox) view).isChecked()) {
            ((CheckBox) findViewById(R.id.conversationStyleCheckBox)).setChecked(false);
            ImageView conversationImageView = (ImageView) findViewById(R.id.styleConversationImageView);
            if (conversationImageView.getAlpha() > 0.0f) {
                conversationImageView.animate().alpha(0.0f);
            }
            storybookImageView.animate().alpha(BaseField.BORDER_WIDTH_THIN);
            setStorybookTextAlpha(BaseField.BORDER_WIDTH_THIN);
            setConversationTextAlpha(0.5f);
            startMagnifyBlinking();
            return;
        }
        storybookImageView.animate().alpha(0.0f);
        setConversationTextAlpha(BaseField.BORDER_WIDTH_THIN);
    }

    private void setConversationTextAlpha(float alpha) {
        TextView conversationTextView1 = (TextView) findViewById(R.id.conversationStylePt1TextView);
        TextView conversationTextView2 = (TextView) findViewById(R.id.conversationStylePt2TextView);
        TextView conversationTextView3 = (TextView) findViewById(R.id.conversationStylePt3TextView);
        ((TextView) findViewById(R.id.conversationStyleTextView)).animate().alpha(alpha);
        conversationTextView1.animate().alpha(alpha);
        conversationTextView2.animate().alpha(alpha);
        conversationTextView3.animate().alpha(alpha);
    }

    private void setStorybookTextAlpha(float alpha) {
        TextView storyTextView1 = (TextView) findViewById(R.id.storyStylePt1TextView);
        TextView storyTextView2 = (TextView) findViewById(R.id.storyStylePt2TextView);
        TextView storyTextView3 = (TextView) findViewById(R.id.storyStylePt3TextView);
        ((TextView) findViewById(R.id.storyStyleTextView)).animate().alpha(alpha);
        storyTextView1.animate().alpha(alpha);
        storyTextView2.animate().alpha(alpha);
        storyTextView3.animate().alpha(alpha);
    }

    private void startMagnifyBlinking() {
        ImageView magnifyImageView = (ImageView) findViewById(R.id.styleMagnifyImageView);
        if (this.alphaAnimation != null) {
            this.alphaAnimation.cancel();
            this.alphaAnimation = null;
        }
        this.alphaAnimation = new AlphaAnimation(0.0f, BaseField.BORDER_WIDTH_THIN);
        this.alphaAnimation.setDuration(2000);
        this.alphaAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation arg0) {
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationEnd(Animation arg0) {
                if (BookStyleActivity.this.magnifyAnimationTimer != null) {
                    BookStyleActivity.this.magnifyAnimationTimer.cancel();
                    BookStyleActivity.this.magnifyAnimationTimer = null;
                }
                BookStyleActivity.this.magnifyAnimationTimer = new Timer();
                BookStyleActivity.this.magnifyAnimationTimer.schedule(new TimerTask() {
                    public void run() {
                        BookStyleActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (BookStyleActivity.this.alphaAnimation != null) {
                                    BookStyleActivity.this.alphaAnimation.cancel();
                                    BookStyleActivity.this.alphaAnimation.reset();
                                }
                                ((ImageView) BookStyleActivity.this.findViewById(R.id.styleMagnifyImageView)).startAnimation(BookStyleActivity.this.alphaAnimation);
                            }
                        });
                    }
                }, 6000);
            }
        });
        magnifyImageView.setAlpha(BaseField.BORDER_WIDTH_THIN);
        magnifyImageView.startAnimation(this.alphaAnimation);
    }

    private void stopMagnifyBlinking() {
        if (this.alphaAnimation != null) {
            this.alphaAnimation.cancel();
            this.alphaAnimation = null;
        }
        if (this.magnifyAnimationTimer != null) {
            this.magnifyAnimationTimer.cancel();
            this.magnifyAnimationTimer = null;
        }
        ((ImageView) findViewById(R.id.styleMagnifyImageView)).setAlpha(0.0f);
    }

    public void showCurrentStylePdf(View view) {
        String pdfStyleStr;
        CheckBox storybookCheckbox = (CheckBox) findViewById(R.id.storyStyleCheckBox);
        if (((CheckBox) findViewById(R.id.conversationStyleCheckBox)).isChecked()) {
            pdfStyleStr = "conversation";
        } else if (storybookCheckbox.isChecked()) {
            pdfStyleStr = "storybook";
        } else {
            return;
        }
        String pdfFilename = "txtbook_" + pdfStyleStr + "_droid.pdf";
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.parse("http://docs.google.com/viewer?url=http://www.txt-book.com/" + pdfFilename), "text/html");
        startActivity(intent);
    }

    public void goCreate(View view) {
        CheckBox conversationCheckbox = (CheckBox) findViewById(R.id.conversationStyleCheckBox);
        CheckBox storyCheckbox = (CheckBox) findViewById(R.id.storyStyleCheckBox);
        if (conversationCheckbox.isChecked() || storyCheckbox.isChecked()) {
            int bookStyle = conversationCheckbox.isChecked() ? 1 : 2;
            Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            editor.putInt(TxtBookPdfSettings.BOOK_STYLE, bookStyle);
            editor.apply();
            startActivity(new Intent(this, FinalizeActivity.class));
            return;
        }
        new Builder(this).setTitle("Style Required").setMessage("You must select a style").setNeutralButton("Close", null).show();
    }
}
