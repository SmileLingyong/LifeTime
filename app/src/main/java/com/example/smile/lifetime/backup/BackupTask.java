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
        //获得正在使用的数据库路径，默认路径是 /data/data/(包名)/database/数据库名（注意这里数据库名不用加后缀）
        File dbFile = mContext.getDatabasePath("/data/data/com.example.smile.lifetime/databases/notes");
        Log.d("Tag_StorageDirectory", Environment.getExternalStorageDirectory().getAbsolutePath()); //获取SD卡路径

        // 新建一个 AAA 目录
        File exportDir = new File(Environment.getExternalStorageDirectory(), "Life_Time_Backup");
        // 如果该目录不存在，则创建该目录
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }
        // 在 AAA 目录下创建 notes.db文件
        File backup = new File(exportDir, dbFile.getName());

        String command = params[0];
        // 根据传入的 要求执行 备份 或 恢复
        if (command.equals(COMMAND_BACKUP)) {
            try {
                backup.createNewFile();
                fileCopy(dbFile, backup);
                return Log.d("Tag_backup", "ok");
            } catch (Exception e) {
                e.printStackTrace();
                return Log.d("Tag_backup", "fail");
            }
        } else if (command.equals(COMMAND_RESTORE)) {
            try {
                fileCopy(backup, dbFile);
                return Log.d("Tag_restore", "success");

            } catch (Exception e) {
                e.printStackTrace();
                return Log.d("Tag_restore", "fail");
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