package com.cameracornet.outsidegpl.swiftp;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import be.ppareit.swiftp.FsApp;
import be.ppareit.swiftp.FsService;
import be.ppareit.swiftp.FsSettings;
import be.ppareit.swiftp.server.CmdAbstractStore;
import be.ppareit.swiftp.server.SessionThread;

/**
 * Copyright 2013-2014 Stephen A. Gutknecht.
 * This class file, InterfaceAdditions.java is dual licensed.
 *    1) GPL licensed for compliance with the direct including in binary builds of application GPL SwiFTP
 *    2) commercial licensed for use by Stephen A. Gutknecht, CameraCornet.com, and other agreed parties as determined by author.
 */
public class InterfaceAdditions {
    public static boolean getFTPCommandDeleteDisabled()
    {
        return true;
    }
    public static boolean getFTPCommandMkdirDisabled() {
        // ToDo: make GUI preferences for this
        return false;
    }

    public static AtomicInteger incomingFileStartCount = new AtomicInteger();
    /*
    This method is called on the START of a FTP upload. Upload may take time, so nice to have early indicator.
    ToDo: could send Intent to generate packet client GUI activity of motion detection
     */
    public static boolean notifyIncomingFTPFileStart(final String inDetail) {
        final int outputIndex = incomingFileStartCount.incrementAndGet();
        if (FsSettings.shouldIncomingToast()) {
            FsService.mySelf.toastHandler.post(new Runnable() {
                public void run() {
                    String outMessage = FsService.mySelf.getString(R.string.file_incoming_toast) + " #" + outputIndex + " @" + inDetail;
                    Toast toast = Toast.makeText(FsService.mySelf, outMessage, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        }

        return true;
    }


    public static void sendIntentToCameraCornet(File storeFile, SessionThread sessionThread, String errString)
    {
        // CameraCornet should be informed with broadcast of intent, even if it is error

        try
        {
            Intent intent1 = new Intent();
            intent1.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent1.setAction("com.cameracornet.FTP_INCOMING");

            intent1.putExtra("filePath", storeFile.getPath());
            intent1.putExtra("FTPusername", sessionThread.getAccount().getUsername());
            intent1.putExtra("senderInetAddress", sessionThread.getSocket().getInetAddress());
            String netIPaddressAsString = sessionThread.getSocket().getInetAddress().getHostAddress();
            intent1.putExtra("senderIPAddress", netIPaddressAsString);
            String networkMACaddress = OutsideCodeAdditions.getMacFromArpCache(netIPaddressAsString);
            intent1.putExtra("senderNetworkMAC", networkMACaddress);
            intent1.putExtra("fileSize", storeFile.length());
            // ToDo: ideal that we checksum hash the file as soon as received to track that it is not corrupted on SDCard or otherwise. perhaps even as it is incoming we start hashing it to prevent duplicate I/O.
            intent1.putExtra("fileHash0", "");
            // Try to keep a time reference as close to session start time as possible.
            intent1.putExtra("timeSessionAcquired", sessionThread.getNotedTimeAcquired());
            if (errString == null)
            {
                intent1.putExtra("ready", true);
            }
            else
            {
                intent1.putExtra("ready", false);
                intent1.putExtra("errorString", errString);
            }

            FsApp.getAppContext().sendBroadcast(intent1);
        }
        catch (Exception e0)
        {
            Log.e("SwiFTP_CamCornet", "Exception. Failure to send com.cameracornet.FTP_INCOMING broadcast intent");
        }
    }
}
