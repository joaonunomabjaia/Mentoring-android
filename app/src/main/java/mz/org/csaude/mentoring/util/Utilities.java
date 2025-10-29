package mz.org.csaude.mentoring.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.listner.dialog.IDialogListener;
import mz.org.csaude.mentoring.listner.dialog.IListbleDialogListener;

public class Utilities {

    private static Utilities instance;

    private static MessageDigest digester;

    final static int REQUEST_CODE_ASK_PERMISSIONS = 111;

    public static String centralServerUrl;

    private Utilities() {
    }

    public static Utilities getInstance() {
        if (instance == null) {
            instance = new Utilities();
        }
        return instance;
    }

    public static String getCentralServerUrl() {
        return centralServerUrl;
    }

    static {
        try {
            digester = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String MD5Crypt(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String to encript cannot be null or zero length");
        }

        digester.update(str.getBytes());
        byte[] hash = digester.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            if ((0xff & hash[i]) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
            } else {
                hexString.append(Integer.toHexString(0xFF & hash[i]));
            }
        }
        return hexString.toString();
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static boolean listHasElements(List list) {
        if (list == null) return false;
        return !(list.size() <= 0 || list.isEmpty());
    }

    /**
     * Common AppCompat Alert Dialog to be used in the Application everywhere
     *
     * @param mContext, Context of where to display
     */
    public static AlertDialog displayAlertDialog(final Context mContext, final String alertMessage, IDialogListener listener) {
        return genericDisplayAlertDialog(mContext, alertMessage, listener);
    }

    /**
     * Common AppCompat Alert Dialog to be used in the Application everywhere
     *
     * @param mContext, Context of where to display
     */
    public static AlertDialog displayAlertDialog(final Context mContext, final String alertMessage) {
        return genericDisplayAlertDialog(mContext, alertMessage, null);
    }

    /**
     * Common AppCompat Alert Dialog to be used in the Application everywhere
     *
     * @param mContext, Context of where to display
     */
    private static AlertDialog genericDisplayAlertDialog(final Context mContext, final String alertMessage, IDialogListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setMessage(alertMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (listener != null) listener.doOnConfirmed();
                        dialog.dismiss();
                    }

                });

        return builder.create();
    }

    private static AlertDialog displayConfirmationDialog(final Context mContext, final String dialogMesg, String positive, String negative, int position, BaseModel baseModel, IListbleDialogListener listener) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                // set message, title, and icon
                .setTitle(mContext.getResources().getString(R.string.app_name))
                .setMessage(dialogMesg)

                .setPositiveButton(positive, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (baseModel != null) {
                            listener.remove(baseModel);
                        } else {
                            try {
                                listener.remove(position);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.dismiss();
                    }

                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myQuittingDialogBox;
    }

    public static AlertDialog displayConfirmationDialog(final Context mContext, final String dialogMesg, String positive, String negative, IDialogListener listener) {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                // set message, title, and icon
                .setTitle(mContext.getResources().getString(R.string.app_name))
                .setMessage(dialogMesg)

                .setPositiveButton(positive, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        listener.doOnConfirmed();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listener.doOnDeny();
                        dialog.dismiss();

                    }
                })
                .create();

        return myQuittingDialogBox;
    }

    public static AlertDialog displayDeleteConfirmationDialogFromList(final Context mContext, final String dialogMesg, int position, IListbleDialogListener listener) {
        return displayConfirmationDialog(mContext, dialogMesg, mContext.getString(R.string.remove), mContext.getString(R.string.cancel), position, null, listener);
    }

    public static AlertDialog displayDeleteConfirmationDialog(final Context mContext, final String dialogMesg, BaseModel baseModel, IListbleDialogListener listener) {
        return displayConfirmationDialog(mContext, dialogMesg, mContext.getString(R.string.remove), mContext.getString(R.string.cancel), 0, baseModel, listener);
    }

    @SafeVarargs
    public static <T> List<T> parseToList(T... obj) {
        if (obj == null || obj.length == 0) return null;

        List<T> list = new ArrayList<T>();

        for (T o : obj) list.add(o);

        return list;
    }

    public static <T extends Object, S extends Object> List<S> parseList(List<T> list, Class<S> classe) {
        if (list == null) return null;

        List<S> parsedList = new ArrayList<S>();

        for (T t : list) {
            parsedList.add((S) t);
        }

        return parsedList;
    }

    /*@RequiresApi(api = Build.VERSION_CODES.N)
    public static DatePickerDialog showDateDialog(Context context, DatePickerDialog.OnDateSetListener dateSetListener){

        int mYear, mMonth, mDay;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context);
        dateSetListener.onDateSet();

    }*/

    public static boolean stringHasValue(String string) {
        return string != null && !string.isEmpty() && string.trim().length() > 0;
    }

    public static boolean objectNotNull(Object obj) {
        return obj != null;
    }

    public static String parseIntToString(int toParse) {
        return String.valueOf(toParse);
    }

    public static String parseDoubleToString(double toParse) {
        return String.valueOf(toParse);
    }

    public static String parseLongToString(long toParse) {
        return String.valueOf(toParse);
    }

    public static UUID getNewUUID() {
        return UUID.randomUUID();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static String garantirXCaracterOnNumber(long number, int x) {
        String formatedNumber = "";
        int numberOfCharacterToIncrise = 0;

        formatedNumber = number + "";

        numberOfCharacterToIncrise = x - formatedNumber.length();

        for (int i = 0; i < numberOfCharacterToIncrise; i++) formatedNumber = "0" + formatedNumber;

        return formatedNumber;
    }

    public static String concatStrings(String currentString, String toConcant, String scapeStr) {
        if (!stringHasValue(currentString)) return toConcant;

        if (!stringHasValue(toConcant)) return currentString;

        return currentString + scapeStr + toConcant;
    }

    public static boolean isStringIn(String value, String... inValues) {
        if (inValues == null || value == null) return false;

        for (String str : inValues) {
            if (value.equals(str)) return true;
        }

        return false;
    }

    public static boolean listHasElements(ArrayList<?> list) {
        return list != null && !list.isEmpty() && list.size() > 0;
    }

    public static <T extends BaseModel> T findOnArray(List<T> list, T toFind) {
        for (T o : list) {
            if (o.equals(toFind)) return o;
        }
        return null;
    }

    public static void expand(View view) {
        Animation animation = expandAction(view);
        view.startAnimation(animation);
    }

    private static Animation expandAction(final View view) {

        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int actualheight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) (actualheight * interpolatedTime);
                view.requestLayout();
            }
        };

        animation.setDuration((long) (actualheight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(animation);

        return animation;


    }

    public static void collapse(final View view) {

        final int actualHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = actualHeight - (int) (actualHeight * interpolatedTime);
                    view.requestLayout();

                }
            }
        };

        animation.setDuration((long) (actualHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(animation);
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    /*public static NotificationCompat.Builder showNotification(String title, String contntText, Context context, String channelId){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(contntText);
        return mBuilder;
    }*/


    public static void previewPdfFiles(final Context mContext, File pdfFile) {
        PackageManager packageManager = mContext.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        //       if (list.size() > 0) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Uri uri = Uri.fromFile(pdfFile);

        Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".FileProvider", pdfFile);
        intent.setDataAndType(uri, "application/pdf");

        mContext.startActivity(intent);
    }


    public static boolean checkPermissionsToViewPdf(Activity activity) {
        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                showMessageOKCancel(activity, "You need to allow access to Storage to store pdf reports ",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return false;
            }
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return false;
        }
        return false;
    }


    public static void showMessageOKCancel(Activity activity, String message, DialogInterface.OnClickListener okListener) {
        new android.app.AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public static void issueNotification(NotificationManagerCompat notificationManagerCompat, Context context, String contentText, String channel, boolean progressStatus, int notificationId) {

        Notification builder = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Tutoria")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setContentText(contentText)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setProgress(0, 0, progressStatus)
                .build();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(notificationId, builder);
    }

    public static boolean isWorkScheduled(String tag, WorkManager instance) {
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                if (state.equals(WorkInfo.State.RUNNING) || state.equals(WorkInfo.State.ENQUEUED)) {
                    return true;
                }
            }
            return false;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static final String LOG_TAG = "CryptUtils";

    /**
     * Generates a PBKDF2WithHmacSHA256 hash of password and salt and returns it as a Base64-encoded string.
     * @param password the password to encrypt
     * @param salt random string that should be used to salt the password
     * @return Base64-encoded string of hash
     */
    public static String encryptPassword(String password, String salt) {
        final String algorithm = "PBKDF2WithHmacSHA256";
        final int iterations = 10000;
        final int keyLength = 256;

        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error while encrypting password with " + algorithm, e);
            return null;
        }
    }

    /**
     * Generates a random salt.
     * @return Base64-encoded string of random salt
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[32];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static boolean isWorkRunning(String tag, WorkManager instance) {
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                if (state.equals(WorkInfo.State.RUNNING)) {
                    return true;
                }
            }
            return false;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void validateTextField(Context context, TextView view){
        if (view.getText().toString().isEmpty()) {
            view.setError(context.getString(R.string.required_field));
            view.requestFocus();
        }
    }


    public static boolean validadePhoneNumber(Context context, TextView view){
        Pattern pattern = Pattern.compile("^\\+\\d{12}$");
        Matcher matcher = pattern.matcher(view.getText().toString());

        if (!matcher.find()) {
            view.setError(context.getString(R.string.phone_number_invalid));
            view.requestFocus();
            return true;
        }

        return false;
    }
    public static boolean validadeEmail(Context context, TextView view){
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(view.getText().toString());

        if (!matcher.find()) {
            view.setError(context.getString(R.string.email_invalid));
            view.requestFocus();
            return true;
        }

        return false;
    }

    public static <T extends Object, S extends Object> List<S> parse(List<T> list, Class<S> classe)  {
        if (list == null) return null;
        List<S> parsedList = new ArrayList<S>();

        for (T t : list){
            try {
                parsedList.add(classe.getDeclaredConstructor(t.getClass()).newInstance(t));
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return parsedList;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Dialog displayCustomConfirmationDialog(final BaseActivity mContext, final String dialogMesg, String positive, String negative, IDialogListener listener) {
        Dialog dialog = new Dialog(mContext);
        LayoutInflater inflater = mContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_popup, null);
        dialog.setContentView(dialogView);

        TextView msg = dialogView.findViewById((R.id.alertMessage));
        EditText endTime = dialogView.findViewById(R.id.endTime);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        endTime.setText(DateUtilities.formatToHHMI(DateUtilities.getCurrentDate()));
        msg.setText(dialogMesg);

        endTime.setOnClickListener(view -> {
            showTimePickerDialog(endTime, mContext);
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.doOnDeny();
                dialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String endTimeValue = endTime.getText().toString();
                listener.doOnConfirmed(endTimeValue);
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public static Dialog showLoadingDialog(final BaseActivity mContext, final String message) {
        if (mContext == null || mContext.isFinishing()) {
            return null; // Return early if the context is invalid
        }

        // Step 1: Create the dialog and inflate the layout
        Dialog dialog = new Dialog(mContext);
        LayoutInflater inflater = mContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_loading, null);
        dialog.setContentView(dialogView);

        // Step 2: Set up the message
        TextView loadingMessage = dialogView.findViewById(R.id.loadingMessage);
        if (message != null && !message.isEmpty()) {
            loadingMessage.setText(message);
        } else {
            loadingMessage.setText("Loading..."); // Default message
        }

        // Step 3: Make the dialog non-cancelable to prevent accidental dismiss
        dialog.setCancelable(false);

        // Step 4: Show the dialog
        dialog.show();

        return dialog;
    }

    private static void showTimePickerDialog(EditText viewTe, BaseActivity activity) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Show the time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Handle the time set event
                String time = hourOfDay + ":" + garantirXCaracterOnNumber(minute, 2);
                viewTe.setText(time);
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    public static Object extractElementByUuid(List list, String uuid) {
        for (Object object: list) {
            if(((BaseModel) object).getUuid().equalsIgnoreCase(uuid)) return object;
        }
        return null;
    }

    public static boolean isValidNumber(String value) {
        try {
            int number = Integer.parseInt(value);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static ColorStateList colorStateList(Context ctx, int colorOrRes) {
        try {
            // Se for um @ColorRes válido, isso funciona
            return AppCompatResources.getColorStateList(ctx, colorOrRes);
        } catch (Resources.NotFoundException e) {
            // Caso contrário, trate como @ColorInt (ex.: 0x33FFA000)
            return ColorStateList.valueOf(colorOrRes);
        }
    }

    // Se quiser manter versões explícitas:
    public static ColorStateList colorStateListRes(Context ctx, @ColorRes int res) {
        return AppCompatResources.getColorStateList(ctx, res);
    }
    public static ColorStateList colorStateListInt(@ColorInt int color) {
        return ColorStateList.valueOf(color);
    }
}
