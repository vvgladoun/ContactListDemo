package gladun.vladimir.contactlistdemo.utilities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton for volley
 *
 * @author vvgladoun@gmail.com
 */
public class VolleyHelper {
    private static VolleyHelper mInstance;
    private RequestQueue mRequestQueue;
    private Context mContext;

    private VolleyHelper(Context context){
        this.mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyHelper getInstance(Context context){
        if(mInstance == null){
            mInstance = new VolleyHelper(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
