package com.ideationdesignservices.txtbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.flurry.android.FlurryAgent;
import com.ideationdesignservices.txtbook.pdf.TxtBookPdf;
import com.ideationdesignservices.txtbook.pdf.TxtBookPdfSettings;
import com.ideationdesignservices.txtbook.text.CountTextWatcher;
import com.ideationdesignservices.txtbook.util.ImageUtilities;
import com.ideationdesignservices.util_vending.IabHelper;
import com.ideationdesignservices.util_vending.IabHelper.OnConsumeFinishedListener;
import com.ideationdesignservices.util_vending.IabHelper.OnIabPurchaseFinishedListener;
import com.ideationdesignservices.util_vending.IabHelper.OnIabSetupFinishedListener;
import com.ideationdesignservices.util_vending.IabHelper.QueryInventoryFinishedListener;
import com.ideationdesignservices.util_vending.IabResult;
import com.ideationdesignservices.util_vending.Inventory;
import com.ideationdesignservices.util_vending.Purchase;
import com.ideationdesignservices.util_vending.SkuDetails;
import com.itextpdf.text.pdf.BaseField;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinalizeActivity extends Activity implements OnConsumeFinishedListener, OnIabPurchaseFinishedListener, OnIabSetupFinishedListener, QueryInventoryFinishedListener {
    private static final String BASE_64_PUBLIC_KEY_PT_1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg+TlA6OQQnyAapeCW+Hx3VjT/RvGDB8NzX/vAWf4oA/Q9h5EhGt/WuTWAAUyiTKXKCvyA4HULF+DYDvnJ7muSnaMZm8Gguct4S4foEabejlPfqyMyWj2pgSZVfFja5HKEJLSmdbmexpm6yH+yFD/F/Yw";
    private static final String BASE_64_PUBLIC_KEY_PT_2 = "QHT+nGzx5YDgJ2WeXyz5HKpTwE00JrRe1omwnL+8GIhheS4SoQZnp8SIHGB32b6OtTg3ygBn3LAdAdv/Lv4KcNYEuZHxrScn+g3Mye3xriYObAnb3SiJx2ATmHQKjPkqEqGM7fR+scoBBxEAbXmBMpkJ6+w/+a9kPijcEaNKSQtMSdtvsfoaY4rpEvTfdQIDAQAB";
    private static final int REQUEST_CODE_CAPTURE_IMAGE_ACTIVITY = 5555;
    private static final int REQUEST_CODE_SEND_PDF_EMAIL_ACTIVITY = 6666;
    private static final String SINGLE_TXTBOOK = "single_txtbook";
    private static final String UNLIMITED_TXTBOOK = "unlimited_txtbook";
    private CountTextWatcher backCoverNoteTextWatcher;
    private Bitmap coverPhoto;
    private String emailAddress;
    private CountTextWatcher frontCoverTitleTextWatcher;
    private Boolean isCoverPhotoChosen;
    private Boolean isIABSetup = Boolean.valueOf(false);
    private Boolean isIABSetupFailed = Boolean.valueOf(false);
    private Boolean isPDFCreated = Boolean.valueOf(false);
    private Boolean isPDFPaidFor = Boolean.valueOf(false);
    private Boolean isPublishWaitingForIAB = Boolean.valueOf(false);
    IabHelper mHelper;
    private TxtBookPdf mPdf;
    private ShareActionProvider mShareActionProvider;
    private ProgressDialog pdfProgressDialog;
    private AlertDialog txtbookReadyAlert;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setupIAB();
        this.isCoverPhotoChosen = Boolean.valueOf(false);
        ((TextView) findViewById(R.id.tapToAddPhotoDesc)).setTextColor(Color.parseColor("#999999"));
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mPdf != null) {
            File file = new File(Environment.getExternalStorageDirectory(), this.mPdf.filename);
            if (!this.isPDFPaidFor.booleanValue() && file.exists()) {
                file.delete();
            }
            if (this.txtbookReadyAlert != null && this.txtbookReadyAlert.isShowing()) {
                this.txtbookReadyAlert.dismiss();
            }
        }
        if (this.mHelper != null) {
            this.mHelper.dispose();
        }
        this.mHelper = null;
    }

    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Txtbook.FLURRY_KEY);
    }

    protected void onStop() {
        super.onStop();
        if (this.mPdf != null) {
            File file = new File(Environment.getExternalStorageDirectory(), this.mPdf.filename);
            if (!this.isPDFPaidFor.booleanValue() && file.exists()) {
                file.delete();
            }
            if (this.txtbookReadyAlert != null && this.txtbookReadyAlert.isShowing()) {
                this.txtbookReadyAlert.dismiss();
            }
        }
        FlurryAgent.onEndSession(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && event.getRepeatCount() == 0 && this.mPdf != null) {
            File file = new File(Environment.getExternalStorageDirectory(), this.mPdf.filename);
            if (!this.isPDFPaidFor.booleanValue() && file.exists()) {
                file.delete();
            }
            if (this.txtbookReadyAlert != null && this.txtbookReadyAlert.isShowing()) {
                this.txtbookReadyAlert.dismiss();
            }
        }
        return super.onKeyDown(keyCode, event);
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
        this.frontCoverTitleTextWatcher = new CountTextWatcher();
        this.frontCoverTitleTextWatcher.countView = (TextView) findViewById(R.id.titleCountTextView);
        this.frontCoverTitleTextWatcher.maxLength = getResources().getInteger(R.integer.maxFrontCoverTitleLength);
        EditText bookCoverTitleTextField = (EditText) findViewById(R.id.bookCoverTitleTextField);
        bookCoverTitleTextField.addTextChangedListener(this.frontCoverTitleTextWatcher);
        bookCoverTitleTextField.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    FinalizeActivity.this.frontCoverTitleTextWatcher.countView.setVisibility(0);
                    FinalizeActivity.this.frontCoverTitleTextWatcher.countView.setLayoutParams(new LayoutParams(-2, -2));
                    return;
                }
                FinalizeActivity.this.frontCoverTitleTextWatcher.countView.setVisibility(4);
                FinalizeActivity.this.frontCoverTitleTextWatcher.countView.setLayoutParams(new LayoutParams(0, -2));
            }
        });
        this.backCoverNoteTextWatcher = new CountTextWatcher();
        this.backCoverNoteTextWatcher.countView = (TextView) findViewById(R.id.noteCountTextView);
        this.backCoverNoteTextWatcher.maxLength = getResources().getInteger(R.integer.maxBackCoverNoteLength);
        EditText backCoverNoteTextField = (EditText) findViewById(R.id.backCoverNoteTextField);
        backCoverNoteTextField.addTextChangedListener(this.backCoverNoteTextWatcher);
        backCoverNoteTextField.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    FinalizeActivity.this.backCoverNoteTextWatcher.countView.setVisibility(0);
                    FinalizeActivity.this.backCoverNoteTextWatcher.countView.setLayoutParams(new LayoutParams(-2, -2));
                    return;
                }
                FinalizeActivity.this.backCoverNoteTextWatcher.countView.setVisibility(4);
                FinalizeActivity.this.backCoverNoteTextWatcher.countView.setLayoutParams(new LayoutParams(0, -2));
            }
        });
        backCoverNoteTextField.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != 6) {
                    return false;
                }
                ((InputMethodManager) FinalizeActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(v.getWindowToken(), 0);
                v.clearFocus();
                return true;
            }
        });
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
                        Intent intent2 = new Intent(FinalizeActivity.this, OptionsActivity.class);
                        intent2.addFlags(PdfFormField.FF_RICHTEXT);
                        FinalizeActivity.this.startActivity(intent2);
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

    public void onAddCoverClicked(View view) {
        EditText bookCoverTitleTextField = (EditText) findViewById(R.id.bookCoverTitleTextField);
        EditText backCoverNoteTextField = (EditText) findViewById(R.id.backCoverNoteTextField);
        if (((CheckBox) view).isChecked()) {
            bookCoverTitleTextField.setEnabled(true);
            backCoverNoteTextField.setEnabled(true);
            return;
        }
        bookCoverTitleTextField.setEnabled(false);
        backCoverNoteTextField.setEnabled(false);
    }

    public void onAddPhotoClicked(View view) {
        Button tapToAddPhotoButton = (Button) findViewById(R.id.tapToAddPhotoButton);
        ImageView tapToAddPhotoImage = (ImageView) findViewById(R.id.tapToAddPhotoImage);
        TextView tapToAddPhotoDesc = (TextView) findViewById(R.id.tapToAddPhotoDesc);
        if (((CheckBox) view).isChecked()) {
            tapToAddPhotoButton.setEnabled(true);
            tapToAddPhotoButton.setAlpha(BaseField.BORDER_WIDTH_THIN);
            tapToAddPhotoImage.setAlpha(BaseField.BORDER_WIDTH_THIN);
            tapToAddPhotoDesc.setTextColor(Color.parseColor("#333333"));
            return;
        }
        tapToAddPhotoButton.setEnabled(false);
        tapToAddPhotoButton.setAlpha(0.5f);
        tapToAddPhotoImage.setAlpha(0.5f);
        tapToAddPhotoDesc.setTextColor(Color.parseColor("#999999"));
    }

    public void addPhoto(View view) {
        List<Intent> cameraIntents = new ArrayList();
        Intent captureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        for (ResolveInfo res : getPackageManager().queryIntentActivities(captureIntent, 0)) {
            String packageName = res.activityInfo.packageName;
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra("return-data", true);
            intent.setAction("android.media.action.IMAGE_CAPTURE");
            cameraIntents.add(intent);
        }
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction("android.intent.action.GET_CONTENT");
        Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
        chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) cameraIntents.toArray(new Parcelable[0]));
        startActivityForResult(chooserIntent, REQUEST_CODE_CAPTURE_IMAGE_ACTIVITY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == REQUEST_CODE_CAPTURE_IMAGE_ACTIVITY) {
            if (resultCode == -1) {
                Uri selectedImageUri = data == null ? null : data.getData();
                this.coverPhoto = null;
                Button tapToAddPhotoButton = (Button) findViewById(R.id.tapToAddPhotoButton);
                ImageView tapToAddPhotoImage = (ImageView) findViewById(R.id.tapToAddPhotoImage);
                int maxWidth = tapToAddPhotoImage.getWidth();
                float scale = getResources().getDisplayMetrics().density;
                if (selectedImageUri == null) {
                    this.coverPhoto = (Bitmap) data.getExtras().get("data");
                    this.isCoverPhotoChosen = Boolean.valueOf(true);
                } else {
                    try {
                        this.coverPhoto = ImageUtilities.getCorrectlyOrientedImage(this, selectedImageUri);
                        this.isCoverPhotoChosen = Boolean.valueOf(true);
                    } catch (OutOfMemoryError e) {
                        this.isCoverPhotoChosen = Boolean.valueOf(false);
                    } catch (Exception e2) {
                        this.isCoverPhotoChosen = Boolean.valueOf(false);
                    }
                }
                if (this.isCoverPhotoChosen.booleanValue()) {
                    int width = (int) (((float) this.coverPhoto.getWidth()) * (135.0f / ((float) this.coverPhoto.getHeight())));
                    if (width > maxWidth) {
                        width = maxWidth;
                    }
                    tapToAddPhotoButton.setLayoutParams(new RelativeLayout.LayoutParams((int) ((((float) width) * scale) + 0.5f), (int) ((135.0f * scale) + 0.5f)));
                    tapToAddPhotoImage.setImageBitmap(this.coverPhoto);
                    ((TextView) findViewById(R.id.tapToAddPhotoDesc)).setVisibility(4);
                    return;
                }
                tapToAddPhotoButton.setLayoutParams(new RelativeLayout.LayoutParams(((RelativeLayout) findViewById(R.id.addPhotoLayout)).getWidth(), (int) ((135.0f * scale) + 0.5f)));
            }
        } else if (requestCode == REQUEST_CODE_SEND_PDF_EMAIL_ACTIVITY) {
            new Builder(this).setTitle(R.string.before_you_go_title).setMessage(getString(R.string.before_you_go_msg)).setNegativeButton("Back to Email", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    FinalizeActivity.this.sendPdfTo(FinalizeActivity.this.emailAddress);
                }
            }).setPositiveButton("Finished", null).show();
        }
    }

    public void send(View view) {
        try {
            CheckBox addCoverCheckBox = (CheckBox) findViewById(R.id.addCoverCheckBox);
            if (addCoverCheckBox.isChecked()) {
                TextView backCoverNoteTextField = (TextView) findViewById(R.id.backCoverNoteTextField);
                if (((TextView) findViewById(R.id.bookCoverTitleTextField)).getText().length() == 0 && backCoverNoteTextField.getText().length() == 0) {
                    new Builder(this).setTitle("Cover Details Required").setMessage("'Add Cover' was selected, but neither a title nor a note were provided.").setNeutralButton("Close", null).show();
                    return;
                }
            }
            CheckBox addCoverPhotoCheckBox = (CheckBox) findViewById(R.id.addCoverPhotoCheckBox);
            if (!addCoverPhotoCheckBox.isChecked() || this.isCoverPhotoChosen.booleanValue()) {
                this.emailAddress = ((EditText) findViewById(R.id.enterEmailTextField)).getText().toString();
                if (this.emailAddress.length() == 0) {
                    new Builder(this).setTitle("Email Address Required").setMessage("An email address must be provided.").setNeutralButton("Close", null).show();
                    return;
                }
                this.pdfProgressDialog = ProgressDialog.show(this, getString(R.string.please_wait), getString(R.string.creating_your_txtbook));
                this.pdfProgressDialog.setIndeterminate(true);
                String bookCoverTitle = ((EditText) findViewById(R.id.bookCoverTitleTextField)).getText().toString();
                String backCoverText = ((EditText) findViewById(R.id.backCoverNoteTextField)).getText().toString();
                this.mPdf = new TxtBookPdf();
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                TxtBookPdfSettings pdfSettings = new TxtBookPdfSettings();
                pdfSettings.threadId = Long.valueOf(settings.getLong(TxtBookPdfSettings.CHOSEN_THREAD_ID, 0));
                pdfSettings.startDateMillis = Long.valueOf(settings.getLong(TxtBookPdfSettings.START_DATE_LIMIT_MILLIS, 0));
                pdfSettings.endDateMillis = Long.valueOf(settings.getLong(TxtBookPdfSettings.END_DATE_LIMIT_MILLIS, 0));
                pdfSettings.useTimestamps = Boolean.valueOf(settings.getBoolean(TxtBookPdfSettings.USE_TIMESTAMPS, false));
                pdfSettings.myName = settings.getString(TxtBookPdfSettings.MY_NAME, PdfObject.NOTHING);
                pdfSettings.theirName = settings.getString(TxtBookPdfSettings.THEIR_NAME, PdfObject.NOTHING);
                pdfSettings.selectedRowsList = settings.getString(TxtBookPdfSettings.SELECTED_ROWS_LIST, PdfObject.NOTHING);
                pdfSettings.bookStyle = settings.getInt(TxtBookPdfSettings.BOOK_STYLE, 1);
                boolean z = addCoverPhotoCheckBox.isChecked() && this.isCoverPhotoChosen.booleanValue();
                pdfSettings.addFrontCoverImage = Boolean.valueOf(z);
                z = (addCoverCheckBox.isChecked() && bookCoverTitle.length() > 0) || pdfSettings.addFrontCoverImage.booleanValue();
                pdfSettings.addFrontCover = Boolean.valueOf(z);
                z = addCoverCheckBox.isChecked() && backCoverText.length() > 0;
                pdfSettings.addBackCover = Boolean.valueOf(z);
                pdfSettings.bookCoverTitle = bookCoverTitle;
                pdfSettings.backCoverNote = backCoverText;
                pdfSettings.coverPhoto = this.coverPhoto;
                pdfSettings.compressionLevel = 9;
                this.mPdf.settings = pdfSettings;
                Thread thread1 = new Thread() {
                    public void run() {
                        try {
                            FinalizeActivity.this.isPDFCreated = Boolean.valueOf(false);
                            FinalizeActivity.this.mPdf.createPDF(FinalizeActivity.this);
                            FinalizeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    FinalizeActivity.this.didCreatePDF();
                                }
                            });
                        } catch (OutOfMemoryError e) {
                            FinalizeActivity.this.handleCreatePDFException(null);
                        } catch (Exception e2) {
                            FinalizeActivity.this.handleCreatePDFException(e2);
                        }
                    }
                };
                thread1.setPriority(10);
                thread1.start();
                return;
            }
            new Builder(this).setTitle("Cover Photo Required").setMessage("'Add Cover Photo' was selected, but no photo was provided.").setNeutralButton("Close", null).show();
        } catch (Exception e) {
            handleCreatePDFException(e);
        }
    }

    private void handleCreatePDFException(Exception e) {
        if (e != null) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            public void run() {
                FinalizeActivity.this.pdfProgressDialog.dismiss();
                new Builder(FinalizeActivity.this).setTitle(FinalizeActivity.this.getString(R.string.txtbook_too_large_title)).setMessage(FinalizeActivity.this.getString(R.string.txtbook_too_large_msg)).setNeutralButton("Close", null).show();
            }
        });
    }

    private void didCreatePDF() {
        this.isPDFPaidFor = Boolean.valueOf(false);
        this.isPDFCreated = Boolean.valueOf(true);
        if (this.isIABSetup.booleanValue()) {
            checkIABOrOfferPurchase();
        } else if (this.isIABSetupFailed.booleanValue()) {
            setupIAB();
        } else {
            this.isPublishWaitingForIAB = Boolean.valueOf(true);
        }
    }

    public void setupIAB() {
        this.mHelper = new IabHelper(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg+TlA6OQQnyAapeCW+Hx3VjT/RvGDB8NzX/vAWf4oA/Q9h5EhGt/WuTWAAUyiTKXKCvyA4HULF+DYDvnJ7muSnaMZm8Gguct4S4foEabejlPfqyMyWj2pgSZVfFja5HKEJLSmdbmexpm6yH+yFD/F/YwQHT+nGzx5YDgJ2WeXyz5HKpTwE00JrRe1omwnL+8GIhheS4SoQZnp8SIHGB32b6OtTg3ygBn3LAdAdv/Lv4KcNYEuZHxrScn+g3Mye3xriYObAnb3SiJx2ATmHQKjPkqEqGM7fR+scoBBxEAbXmBMpkJ6+w/+a9kPijcEaNKSQtMSdtvsfoaY4rpEvTfdQIDAQAB");
        this.mHelper.startSetup(this);
    }

    public void alertIABConnectionFailure(String message) {
        if (this.pdfProgressDialog != null) {
            this.pdfProgressDialog.dismiss();
        }
        String response = "Sorry, there was a payment error. Nothing has been charged to your account. Please try purchasing your txt-book again.";
        if (message != null && message.length() > 0) {
            response = new StringBuilder(String.valueOf(response)).append("\n\nMessage: '").append(message).append("'").toString();
        }
        new Builder(this).setTitle("Payment Error").setMessage(response).setNeutralButton("OK", null).show();
    }

    public void checkIABOrOfferPurchase() {
        List<String> additionalSkuList = new ArrayList();
        additionalSkuList.add(SINGLE_TXTBOOK);
        additionalSkuList.add(UNLIMITED_TXTBOOK);
        this.mHelper.queryInventoryAsync(true, additionalSkuList, this);
    }

    public Boolean sendPdfTo(String emailAddress) {
        this.isPDFPaidFor = Boolean.valueOf(true);
        this.pdfProgressDialog.dismiss();
        File file = new File(Environment.getExternalStorageDirectory(), this.mPdf.filename);
        if (!file.exists() || !file.canRead()) {
            return Boolean.valueOf(false);
        }
        Uri uri = Uri.parse("file://" + file);
        Intent emailIntent = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:"));
        emailIntent.putExtra("android.intent.extra.EMAIL", new String[]{emailAddress});
        emailIntent.putExtra("android.intent.extra.SUBJECT", getString(R.string.txtbook_is_here));
        emailIntent.putExtra("android.intent.extra.TEXT", Html.fromHtml(getString(R.string.txtbook_html_email)));
        emailIntent.putExtra("android.intent.extra.STREAM", uri);
        Map pdfParams = new HashMap();
        pdfParams.put("PDF Recipient", emailAddress);
        FlurryAgent.logEvent("PDF_SENT", pdfParams);
        startActivityForResult(Intent.createChooser(emailIntent, "Send PDF to..."), REQUEST_CODE_SEND_PDF_EMAIL_ACTIVITY);
        return Boolean.valueOf(true);
    }

    public void onIabSetupFinished(IabResult result) {
        if (result.isSuccess()) {
            this.isIABSetup = Boolean.valueOf(true);
            if (this.isPublishWaitingForIAB.booleanValue()) {
                checkIABOrOfferPurchase();
                return;
            }
            return;
        }
        this.isIABSetupFailed = Boolean.valueOf(true);
        if (this.isPDFCreated.booleanValue()) {
            alertIABConnectionFailure(IabHelper.getResponseDesc(result.getResponse()));
        }
    }

    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        if (result.isFailure()) {
            alertIABConnectionFailure(IabHelper.getResponseDesc(result.getResponse()));
        } else if (Boolean.valueOf(inventory.hasPurchase(UNLIMITED_TXTBOOK)).booleanValue()) {
            sendPdfTo(this.emailAddress);
        } else if (Boolean.valueOf(inventory.hasPurchase(SINGLE_TXTBOOK)).booleanValue()) {
            this.mHelper.consumeAsync(inventory.getPurchase(SINGLE_TXTBOOK), (OnConsumeFinishedListener) this);
        } else {
            SkuDetails singleSkuDetails = inventory.getSkuDetails(SINGLE_TXTBOOK);
            SkuDetails unlimitedSkuDetails = inventory.getSkuDetails(UNLIMITED_TXTBOOK);
            if (singleSkuDetails == null || unlimitedSkuDetails == null) {
                alertIABConnectionFailure(PdfObject.NOTHING);
                return;
            }
            String singlePrice = singleSkuDetails.getPrice();
            String unlimitedPrice = unlimitedSkuDetails.getPrice();
            this.pdfProgressDialog.dismiss();
            Builder alert = new Builder(this);
            alert.setCancelable(false);
            alert.setTitle("Your txt-book book is ready!");
            alert.setMessage("You can purchase this one txt-book for " + singlePrice + " or purchase UNLIMITED txt-books for " + unlimitedPrice + ". The unlimited package is most popular because you may wish to edit the book, try new styles and be able to create all the books you would like for future occasions, gifts, keepsakes or records.");
            alert.setNeutralButton("Single" + ", " + singlePrice, new OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    FinalizeActivity.this.mHelper.launchPurchaseFlow(FinalizeActivity.this, FinalizeActivity.SINGLE_TXTBOOK, 10001, FinalizeActivity.this, "f2JMYZmzozkkDvaPVayTHH4CoNEqoRJCheZA");
                }
            });
            alert.setPositiveButton("UNLIMITED" + ", " + unlimitedPrice, new OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    FinalizeActivity.this.mHelper.launchPurchaseFlow(FinalizeActivity.this, FinalizeActivity.UNLIMITED_TXTBOOK, 10001, FinalizeActivity.this, "f2JMYZmzozkkDvaPVayTHH4CoNEqoRJCheZA");
                }
            });
            alert.setNegativeButton("Cancel", new OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (FinalizeActivity.this.mPdf != null) {
                        File file = new File(Environment.getExternalStorageDirectory(), FinalizeActivity.this.mPdf.filename);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
            });
            this.txtbookReadyAlert = alert.create();
            this.txtbookReadyAlert.show();
        }
    }

    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        if (result.isFailure()) {
            File file = new File(Environment.getExternalStorageDirectory(), this.mPdf.filename);
            if (!this.isPDFPaidFor.booleanValue() && file.exists()) {
                file.delete();
            }
            alertIABConnectionFailure(IabHelper.getResponseDesc(result.getResponse()));
        } else if (purchase.getSku().equals(SINGLE_TXTBOOK)) {
            this.mHelper.consumeAsync(purchase, (OnConsumeFinishedListener) this);
        } else if (purchase.getSku().equals(UNLIMITED_TXTBOOK)) {
            sendPdfTo(this.emailAddress);
        }
    }

    public void onConsumeFinished(Purchase purchase, IabResult result) {
        if (result.isSuccess()) {
            sendPdfTo(this.emailAddress);
        } else {
            alertIABConnectionFailure(IabHelper.getResponseDesc(result.getResponse()));
        }
    }
}
