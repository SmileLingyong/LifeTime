package com.example.smile.lifetime.backup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by lly54 on 2017/4/6.
 */

public class BackupTask extends AsyncTask<String, Void, Integer> {

    private static final String COMMAND_BACKUP = "backupDatabase";
    public static final String COMMAND_RESTORE = "restoreDatabase";
    public static final String COMMAND_SAVE_PIC = "savePic";

    private Context mContext;
    private View view;

    public BackupTask(Context context) {
        this.mContext = context;
    }

    public BackupTask(Context context, View view, String picName) {
        this.mContext = context;
        this.view = view;
    }

    @Override
    protected Integer doInBackground(String... params) {
        //获得正在使用的数据库路径，默认路径是 /data/data/(包名)/database/数据库名（注意这里数据库名不用加后缀）
        File dbFile = mContext.getDatabasePath("/data/data/com.example.smile.lifetime/databases/notes");
        Log.d("Tag_StorageDirectory", Environment.getExternalStorageDirectory().getAbsolutePath()); //获取SD卡路径

        // 新建一个 Life_Time_Backup 目录用于备份数据库
        File exportDir = new File(Environment.getExternalStorageDirectory(), "Life_Time_Backup");
        // 如果该目录不存在，则创建该目录
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }
        // 在 Life_Time_Backup 目录下创建 notes.db文件
        File backup = new File(exportDir, dbFile.getName());
        Log.d("LLY_db", exportDir.getAbsolutePath() + "\\" + dbFile.getName());

        // 新建一个 Life_Time_Down_Pic 目录用于 存放图片
        File exportDirSavePic = new File(Environment.getExternalStorageDirectory(), "Life_Time_Down_Pic");
        // 如果该目录不存在，则创建该目录
        if (!exportDirSavePic.exists()) {
            exportDirSavePic.mkdir();
        }

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
        } else if (command.equals(COMMAND_SAVE_PIC)) {
            try {
                svaeBitmap(view, exportDirSavePic);
                return Log.d("Tag_downPic", "success");
            } catch (Exception e) {
                e.printStackTrace();
                return Log.d("Tag_downPic", "fail");
            }
        } else {
            return null;
        }
    }

    //复制文件
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

    //使用Bitmap保存图片 参考：http://blog.csdn.net/maplejaw_/article/details/51243056
    private void svaeBitmap(View view,File exportDirSavePic) {
        // 创建对应大小的bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        //存储
        FileOutputStream outStream = null;
        // 在 Life_Time_Backup 目录下创建 图片名.png 文件
        File file = new File(exportDirSavePic, getCharacterAndNumber() + ".png");
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bitmap.recycle();
                if(outStream!=null){
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCharacterAndNumber() {
        String rel="";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        return rel;
    }
}

