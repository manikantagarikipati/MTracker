package com.geekmk.mtracker.helper;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by manikanta.garikipati on 14/01/18.
 */

public class ExportImportDB {

  private void exportDB(Context context) {
    // TODO Auto-generated method stub

    try {
      File sd = Environment.getExternalStorageDirectory();
      File data = Environment.getDataDirectory();

      if (sd.canWrite()) {
        String currentDBPath = "//data//" + "PackageName"
            + "//databases//" + "DatabaseName";
        String backupDBPath = "/BackupFolder/DatabaseName";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);

        FileChannel src = new FileInputStream(currentDB).getChannel();
        FileChannel dst = new FileOutputStream(backupDB).getChannel();
        dst.transferFrom(src, 0, src.size());
        src.close();
        dst.close();
        AppUtils.showToast(context, "DB Export Completed");
      }
    } catch (Exception e) {
      AppUtils.showToast(context, "DB Export failed");

    }
  }

  //importing database
  private void importDB(Context context) {
    // TODO Auto-generated method stub

    try {
      File sd = Environment.getExternalStorageDirectory();
      File data = Environment.getDataDirectory();

      if (sd.canWrite()) {
        String currentDBPath = "//data//" + "PackageName"
            + "//databases//" + "DatabaseName";
        String backupDBPath = "/BackupFolder/DatabaseName";
        File backupDB = new File(data, currentDBPath);
        File currentDB = new File(sd, backupDBPath);

        FileChannel src = new FileInputStream(currentDB).getChannel();
        FileChannel dst = new FileOutputStream(backupDB).getChannel();
        dst.transferFrom(src, 0, src.size());
        src.close();
        dst.close();
        AppUtils.showToast(context, "DB Import Success");

      }
    } catch (Exception e) {
      AppUtils.showToast(context, "DB Import failed");

    }
  }


}
