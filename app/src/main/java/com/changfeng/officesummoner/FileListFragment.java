package com.changfeng.officesummoner;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by changfeng on 2015/5/24.
 */
public class FileListFragment extends Fragment{

    private static final String TAG = "FileListFragment";

    public static final String RECENT_DATABASE = "recent.db";

    private static final String TABLE_RECENT = "recent";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_PATH = "path";

    public static final String CREATE_RECENT = "create table " + TABLE_RECENT + "(" +
            COLUMN_ID + " integer primary key autoincrement," +
            COLUMN_TITLE + " text," +
            COLUMN_PATH +" text)";

    private static final int RECENT = 0;
    private static final int PDF = 1;
    private static final int WORD = 2;
    private static final int EXCEL = 3;
    private static final int TEXT = 4;

    private DatabaseHelper databaseHelper;

    List<FileInfo> fileInfoList;
    List<String> filenameList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_list, container, false);

        int position = FragmentPagerItem.getPosition(getArguments());

//        Log.d(TAG, "onCreate " + position);

        databaseHelper = new DatabaseHelper(getActivity(), RECENT_DATABASE, null, 1);

        fileInfoList = getFileInfoList(position);


        filenameList = new ArrayList<>();
        for (FileInfo f : fileInfoList) {
            filenameList.add(f.getName());
        }



        ListView listView = (ListView) v.findViewById(R.id.list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, filenameList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (!databaseHelper.isExist(fileInfoList.get(i).getPath())) {
                    databaseHelper.insertFileInfo(fileInfoList.get(i));
                }
                openFile(fileInfoList.get(i).getPath());
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return true;
            }
        });
        return v;
    }

    private List<FileInfo> getFileInfoList(int fileType) {
        List<FileInfo> list = new ArrayList<>();


        if (fileType == RECENT) {
            list = databaseHelper.loadFileInfos();
        } else {

            Uri uri = MediaStore.Files.getContentUri("external");

             String[] columns = new String[]{
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.DATA
            };

            String selection;


            if (fileType == PDF) {
                selection = "(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='application/pdf')";
            } else if (fileType == WORD) {
                selection = "(" + MediaStore.Files.FileColumns.DATA+ " like '%.doc%')";
            } else if (fileType == EXCEL) {
                selection = "(" + MediaStore.Files.FileColumns.DATA + " like '%.xls%')";
//            } else if (fileType == PPT) {
//                selection = "(" + MediaStore.Files.FileColumns.DATA + " like '%.ppt%')";
            } else if (fileType == TEXT) {
                selection = "(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='text/plain')";
            } else {
                selection = "(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='application/pdf')";
            }

            ContentResolver cr = getActivity().getContentResolver();
            Cursor c = cr.query(uri, columns, selection, null, null, null);

            if (c.moveToFirst()) {
                int titleIndex = c.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
                int dataIndex = c.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                fileInfoList = new ArrayList<>();
                do {
                    FileInfo fi = new FileInfo();
                    fi.setName(c.getString(titleIndex));
                    fi.setPath(c.getString(dataIndex));
                    if (isTargetFile(fi.getPath(), fileType)) {
                        list.add(fi);
                    }

                } while (c.moveToNext());
            }
            c.close();
        }

        return list;
    }

    private boolean isTargetFile(String path, int type) {
//        Log.d(TAG, "isTargetFile " + getFileSuffix(path) + " " + type);
        if (type == PDF) {
            return getFileSuffix(path).equals("pdf") || getFileSuffix(path).equals("PDF");
        } else if (type == WORD) {
            return getFileSuffix(path).equals("doc") || getFileSuffix(path).equals("docx");
        } else if (type == EXCEL) {
            return getFileSuffix(path).equals("xls") || getFileSuffix(path).equals("xlsx");
//        } else if (type == PPT) {
//            return getFileSuffix(path).equals("ppt") || getFileSuffix(path).equals("pptx");
        } else if (type == TEXT) {
            return getFileSuffix(path).equals("txt");
        } else {
            return false;
        }
    }

    static private String getFileSuffix(String path) {
        path = path.trim();
        int lastDotIndex = path.lastIndexOf('.');

        if (lastDotIndex >= 0 && lastDotIndex < path.length()) {
            return path.substring(lastDotIndex + 1, path.length());
        } else {
            return "";
        }
    }

    private void openFile(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type;
        String suffix = getFileSuffix(path);
        if (suffix.equals("pdf")) {
            type = "application/pdf";
        } else if (suffix.equals("doc") ){
//            type = "application/vnd.ms-word";
            type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (suffix.equals("docx")) {
            type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (suffix.equals("xls")) {
//            type = "application/vnd.ms-excel";
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (suffix.equals("xlsx")) {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (suffix.equals("ppt")) {
            type = "application/vnd.ms-powerpoint";
        } else if (suffix.equals("pptx")) {
            type = " application/vnd.openxmlformats-officedocument.presentationml.presentation";
        } else if (suffix.equals("txt")) {
            type = "text/plain";
        } else {
            type = "";
        }
        intent.setDataAndType(Uri.fromFile(new File(path)), type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
    }




}
