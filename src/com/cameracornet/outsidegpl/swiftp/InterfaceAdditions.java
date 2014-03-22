package com.cameracornet.outsidegpl.swiftp;

import android.content.Intent;
import android.util.Log;

import java.io.File;

import be.ppareit.swiftp.FsApp;
import be.ppareit.swiftp.server.CmdAbstractStore;
import be.ppareit.swiftp.server.SessionThread;

/**
 * Created by adminsag on 3/22/14.
 */
public class InterfaceAdditions {
    public static boolean getFTPCommandDeleteDisabled()
    {
        return true;
    }


    public static void sendIntentToCameraCornet(File storeFile, SessionThread sessionThread, String errString)
    {
        // ZebraCamera should be informed with broadcast of intent, even if it is error

        try
        {
            Intent intent1 = new Intent();
            intent1.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent1.setAction("com.cameracornet.FTP_INCOMING");

            intent1.putExtra("filePath", storeFile.getPath());
            intent1.putExtra("FTPusername", sessionThread.getAccount().username);
            intent1.putExtra("senderInetAddress", sessionThread.getSocket().getInetAddress());
            String netIPaddressAsString = sessionThread.getSocket().getInetAddress().getHostAddress();
            intent1.putExtra("senderIPAddress", netIPaddressAsString);
            String networkMACaddress = OutsideCodeAdditions.getMacFromArpCache(netIPaddressAsString);
            intent1.putExtra("senderNetworkMAC", networkMACaddress);
            intent1.putExtra("fileSize", (long) 0);
            // Try to keep a time reference as close to session start time as possible.
            intent1.putExtra("timeSessionAcquired", sessionThread.notedTimeAcquired);
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

    public static boolean getFTPCommandMkdirDisabled() {
        return true;
    }
}
