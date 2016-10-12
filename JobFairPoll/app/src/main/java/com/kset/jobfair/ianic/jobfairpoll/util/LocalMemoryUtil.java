package com.kset.jobfair.ianic.jobfairpoll.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.kset.jobfair.ianic.jobfairpoll.R;
import com.kset.jobfair.ianic.jobfairpoll.objects.Question;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class which offers methods which are used for storing the data locally; on the phone
 * memory.
 *
 * @author Ivan Anić
 * @version 1.0
 */
public class LocalMemoryUtil {


    public static boolean QUESTIONS_FETCHED = false;
    public static HashMap<Integer, Question> questionStorage;

    public final static String LOCAL_FILE_PATH = "storage/emulated/0/tmp/question0.ser";
    public final static String LOCAL_DIR_PATH = "storage/emulated/0/tmp/";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void writeToMemory(HashMap<Integer, Integer> map, Context context) {
        verifyStoragePermissions((Activity) context);

        HashMap<Integer, HashMap> tempMap = (HashMap<Integer, HashMap>) readFromMemory(context);
        tempMap.put(tempMap.size(), map);

        try {
            FileOutputStream fileOut =
                    new FileOutputStream(LOCAL_FILE_PATH);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(tempMap);
            out.close();
            fileOut.close();
            // check
            // System.out.printf("Serialized data is saved in " + LOCAL_FILE_PATH);
        } catch (IOException i) {
            MessagesUtil.createDialogExportFinished(
                    context, R.string.file_error_alert, R.string.file_error);
        }
    }

    public static Map readFromMemory(Context context) {
        verifyStoragePermissions((Activity) context);
        HashMap<Integer, HashMap> map = new HashMap<>();

        try {
            File file = new File(LOCAL_FILE_PATH);
            if (!file.exists())
                new File(LOCAL_DIR_PATH).mkdirs();
            file.createNewFile();

            FileInputStream fileIn = new FileInputStream(LOCAL_FILE_PATH);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            map = (HashMap<Integer, HashMap>) in.readObject();
            in.close();
            fileIn.close();
        } catch (EOFException ignored) {
        } catch (IOException | ClassNotFoundException c) {
            // check
            // System.out.println("File not found!");
            MessagesUtil.createDialogExportFinished(
                    context, R.string.file_error_alert, R.string.file_error);
        }

        /* data check
        for (HashMap hashMap : map.values())
            Log.d("Map:  ", hashMap.toString());
        */
        return map;
    }

    /*
        Generates the string which will be sent to the server, based on the given data.
     */
    public static String getPostDataString(HashMap<Integer, Integer> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<Integer, Integer> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(String.valueOf("answers[" + entry.getKey() + "]"), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
        }

        return result.toString();
    }

    /*
        Stores the previously saved data to a new file.
     */
    public static void flushFileBuffer(Context context) {
        verifyStoragePermissions((Activity) context);

        File currDir = new File(LOCAL_DIR_PATH);
        File[] fileArray = currDir.listFiles();
        Pattern p = Pattern.compile("\\p{N}+");
        Matcher m;
        String lastFileName = fileArray[fileArray.length - 1].getName();

        int lastIndex = 0;
        try {
            m = p.matcher(lastFileName);
            if (m.find()) {
                lastIndex = Integer.parseInt(lastFileName.substring(m.start(), m.end()));
            }
        } catch (NumberFormatException x) {
            MessagesUtil.createDialogExportFinished(context,
                    R.string.file_error_alert, R.string.file_error);
        }

        p = Pattern.compile("\\p{L}+");
        m = p.matcher(lastFileName);
        String newFileName = "";
        if (m.find()) {
            newFileName = lastFileName.substring(m.start(), m.end())
                    + String.valueOf(lastIndex + 1) + ".ser";
        }

        boolean bool = new File(LOCAL_FILE_PATH).renameTo(new File("//" + LOCAL_DIR_PATH + newFileName));

        if (!bool) MessagesUtil.createDialogExportFinished(context,
                R.string.file_error_alert, R.string.file_error);
    }
}

