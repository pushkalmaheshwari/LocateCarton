package localhost.locatecarton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Global {
    public static String wmServer;
    public static String LocationBarcode;
    public static String CartonBarcode;

    public static void ShowScreen(int errorString, Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle("Error")
                .setMessage(errorString);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static void ShowScreen(int errorString, Context context, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title)
                .setMessage(errorString);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

