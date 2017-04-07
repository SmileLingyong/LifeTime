package com.example.smile.lifetime.backup;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


/**
 * Created by lly54 on 2017/4/6.
 */

public class BackupTask extends AsyncTask<String, Void, Integer> {

    private static final String COMMAND_BACKUP = "backupDatabase";
    public static final String COMMAND_RESTORE = "restoreDatabase";
    private Context mContext;

    public BackupTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected Integer doInBackground(String... params) {
        //获得正在使用的数据库路径，默认路径是 /data/data/(包名)/database/*.db
        File dbFile = mContext.getDatabasePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/data/com.example.smile.lifetime/databases/notes.db");
        Log.d("Tag", Environment.getExternalStorageDirectory().getAbsolutePath());

        File exportDir = new File(Environment.getExternalStorageDirectory(), "data");

        if (!exportDir.exists()) {
            exportDir.mkdir();
        }

        File backup = new File(exportDir, dbFile.getName());
        Log.d("TagbackupName", dbFile.getName());   //要备份的文件名

        String command = params[0];
        Log.d("Tagcommand", command);
        if (command.equals(COMMAND_BACKUP)) {
            try {
                backup.createNewFile();
                fileCopy(dbFile, backup);
                return Log.d("Tagbackup", "ok");
            } catch (Exception e) {
                e.printStackTrace();
                return Log.d("Tagbackup", "fail");
            }
        } else if (command.equals(COMMAND_RESTORE)) {
            try {
                fileCopy(backup, dbFile);
                return Log.d("Tagrestore", "success");

            } catch (Exception e) {
                e.printStackTrace();
                return Log.d("Tagrestore", "fail");
            }
        } else {
            return null;
        }
    }

    private void fileCopy(File dbFile, File backup) throws IOException {
        FileChannel inChannel = new FileInputStream(dbFile).getChannel();
        FileChannel outChannel = new FileOutputStream(backup).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
}