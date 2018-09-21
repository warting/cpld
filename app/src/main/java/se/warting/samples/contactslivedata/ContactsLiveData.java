package se.warting.samples.contactslivedata;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

import se.warting.samples.contactslivedata.dummy.DummyContent;

public class ContactsLiveData extends LiveData<ArrayList<DummyContent.DummyItem>> {

    private final ContentObserver contentObserver;

    private String[] mProjection = new String[]{
            ContactsContract.Profile._ID,
            ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
            ContactsContract.Profile.LOOKUP_KEY,
            ContactsContract.Profile.PHOTO_THUMBNAIL_URI
    };

    private Context context;

    ContactsLiveData(Context context) {
        this.context = context;
        this.contentObserver = new ContentObserver(new Handler());
    }

    @Override
    protected void onActive() {
        ProviderInfo providerInfo = context.getPackageManager().resolveContentProvider(ContactsContract.AUTHORITY, 0);
        if (providerInfo != null) {
            context.getContentResolver()
                    .registerContentObserver(ContactsContract.Profile.CONTENT_URI, true, contentObserver);

        }
        loadData();
    }


    private void loadData() {
        ArrayList<DummyContent.DummyItem> contacts = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ContactsContract.Profile.CONTENT_URI,
                    mProjection,
                    null,
                    null,
                    null);
            if (cursor == null) {
                return;
            }

            while (cursor.moveToNext()){
                contacts.add(new DummyContent.DummyItem("a", "b", "c"));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        setValue(contacts);
    }

    @Override
    protected void onInactive() {
        context.getContentResolver().unregisterContentObserver(contentObserver);
    }

    class ContentObserver extends android.database.ContentObserver {
        ContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            ContactsLiveData.this.loadData();
        }
    }


}
