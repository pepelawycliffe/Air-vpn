package debugger;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Helper {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void printLog(String logMessage)
    {
        Log.d("HelperLogger", logMessage);
    }
}
