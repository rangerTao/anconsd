/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ranger.bmaterials.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.ranger.bmaterials.tools.Pair;

//import com.duoku.gamesearch.tools.Pair;

/**
 * The download manager is a system service that handles long-running HTTP downloads. Clients may
 * request that a URI be downloaded to a particular destination file. The download manager will
 * conduct the download in the background, taking care of HTTP interactions and retrying downloads
 * after failures or across connectivity changes and system reboots.
 *
 * Instances of this class should be obtained through
 * {@link android.content.Context#getSystemService(String)} by passing
 * {@link android.content.Context#DOWNLOAD_SERVICE}.
 *
 * Apps that request downloads through this API should register a broadcast receiver for
 * {@link #ACTION_NOTIFICATION_CLICKED} to appropriately handle when the user clicks on a running
 * download in a notification or from the downloads UI.
 */
public class DownloadManager {
    /** log tag. */
    private static final String TAG = "DownloadManager";

    /**
     * An identifier for a particular download, unique across the system.  Clients use this ID to
     * make subsequent calls related to the download.
     */
    public static final String COLUMN_ID = BaseColumns._ID;

    /**
     * The client-supplied title for this download.  This will be displayed in system notifications.
     * Defaults to the empty string.
     */
    public static final String COLUMN_TITLE = "title";

    /**
     * The client-supplied description of this download.  This will be displayed in system
     * notifications.  Defaults to the empty string.
     */
    public static final String COLUMN_DESCRIPTION = "description";

    /**
     * URI to be downloaded.
     */
    public static final String COLUMN_URI = "uri";

    /**
     * Internet Media Type of the downloaded file.  If no value is provided upon creation, this will
     * initially be null and will be filled in based on the server's response once the download has
     * started.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc1590.txt">RFC 1590, defining Media Types</a>
     */
    public static final String COLUMN_MEDIA_TYPE = "media_type";

    /**
     * Total size of the download in bytes.  This will initially be -1 and will be filled in once
     * the download starts.
     */
    public static final String COLUMN_TOTAL_SIZE_BYTES = "total_size";

    /**
     * Uri where downloaded file will be stored.  If a destination is supplied by client, that URI
     * will be used here.  Otherwise, the value will initially be null and will be filled in with a
     * generated URI once the download has started.
     */
    public static final String COLUMN_LOCAL_URI = "local_uri";

    /**
     * Current status of the download, as one of the STATUS_* constants.
     */
    public static final String COLUMN_STATUS = "status";
    
    /**
     * 
     */
    public static final String COLUMN_ORIGINAL_STATUS = "original_status";

    /**
     * Provides more detail on the status of the download.  Its meaning depends on the value of
     * {@link #COLUMN_STATUS}.
     *
     * When {@link #COLUMN_STATUS} is {@link #STATUS_FAILED}, this indicates the type of error that
     * occurred.  If an HTTP error occurred, this will hold the HTTP status code as defined in RFC
     * 2616.  Otherwise, it will hold one of the ERROR_* constants.
     *
     * When {@link #COLUMN_STATUS} is {@link #STATUS_PAUSED}, this indicates why the download is
     * paused.  It will hold one of the PAUSED_* constants.
     *
     * If {@link #COLUMN_STATUS} is neither {@link #STATUS_FAILED} nor {@link #STATUS_PAUSED}, this
     * column's value is undefined.
     *
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html#sec6.1.1">RFC 2616
     * status codes</a>
     */
    public static final String COLUMN_REASON = "reason";

    /**
     * Number of bytes download so far.
     */
    public static final String COLUMN_BYTES_DOWNLOADED_SO_FAR = "bytes_so_far";

    /**
     * Timestamp when the download was last modified, in {@link System#currentTimeMillis
     * System.currentTimeMillis()} (wall clock time in UTC).
     */
    public static final String COLUMN_LAST_MODIFIED_TIMESTAMP = "last_modified_timestamp";

    /**
     * The URI to the corresponding entry in MediaProvider for this downloaded entry. It is
     * used to delete the entries from MediaProvider database when it is deleted from the
     * downloaded list.
     */
    public static final String COLUMN_MEDIAPROVIDER_URI = "mediaprovider_uri";

    /**
     * Value of {@link #COLUMN_STATUS} when the download is waiting to start.
     */
    public static final int STATUS_PENDING = 1 << 0;

    /**
     * Value of {@link #COLUMN_STATUS} when the download is currently running.
     */
    public static final int STATUS_RUNNING = 1 << 1;

    /**
     * Value of {@link #COLUMN_STATUS} when the download is waiting to retry or resume.
     */
    public static final int STATUS_PAUSED = 1 << 2;

    /**
     * Value of {@link #COLUMN_STATUS} when the download has successfully completed.
     */
    public static final int STATUS_SUCCESSFUL = 1 << 3; // SUPPRESS CHECKSTYLE

    /**
     * Value of {@link #COLUMN_STATUS} when the download has failed (and will not be retried).
     */
    public static final int STATUS_FAILED = 1 << 4; // SUPPRESS CHECKSTYLE


    /**
     * Value of COLUMN_ERROR_CODE when the download has completed with an error that doesn't fit
     * under any other error code.
     */
    public static final int ERROR_UNKNOWN = 1000;

    /**
     * Value of {@link #COLUMN_REASON} when a storage issue arises which doesn't fit under any
     * other error code. Use the more specific {@link #ERROR_INSUFFICIENT_SPACE} and
     * {@link #ERROR_DEVICE_NOT_FOUND} when appropriate.
     */
    public static final int ERROR_FILE_ERROR = 1001;

    /**
     * Value of {@link #COLUMN_REASON} when an HTTP code was received that download manager
     * can't handle.
     */
    public static final int ERROR_UNHANDLED_HTTP_CODE = 1002;

    /**
     * Value of {@link #COLUMN_REASON} when an error receiving or processing data occurred at
     * the HTTP level.
     */
    public static final int ERROR_HTTP_DATA_ERROR = 1004;

    /**
     * Value of {@link #COLUMN_REASON} when there were too many redirects.
     */
    public static final int ERROR_TOO_MANY_REDIRECTS = 1005;

    /**
     * Value of {@link #COLUMN_REASON} when there was insufficient storage space. Typically,
     * this is because the SD card is full.
     */
    public static final int ERROR_INSUFFICIENT_SPACE = 1006;

    /**
     * Value of {@link #COLUMN_REASON} when no external storage device was found. Typically,
     * this is because the SD card is not mounted.
     */
    public static final int ERROR_DEVICE_NOT_FOUND = 1007;

    /**
     * Value of {@link #COLUMN_REASON} when some possibly transient error occurred but we can't
     * resume the download.
     */
    public static final int ERROR_CANNOT_RESUME = 1008;

    /**
     * Value of {@link #COLUMN_REASON} when the requested destination file already exists (the
     * download manager will not overwrite an existing file).
     */
    public static final int ERROR_FILE_ALREADY_EXISTS = 1009;

    /**
     * Value of {@link #COLUMN_REASON} when the download is paused because some network error
     * occurred and the download manager is waiting before retrying the request.
     */
    public static final int PAUSED_WAITING_TO_RETRY = 1;

    /**
     * Value of {@link #COLUMN_REASON} when the download is waiting for network connectivity to
     * proceed.
     */
    public static final int PAUSED_WAITING_FOR_NETWORK = 2;

    /**
     * Value of {@link #COLUMN_REASON} when the download exceeds a size limit for downloads over
     * the mobile network and the download manager is waiting for a Wi-Fi connection to proceed.
     */
    public static final int PAUSED_QUEUED_FOR_WIFI = 3;
    
    /**
     * ԭDownloadManagerû������ֶ�
     * added by wangliang.
     */
    public static final int PAUSED_BY_APP  = 4;

    /**
     * Value of {@link #COLUMN_REASON} when the download is paused for some other reason.
     */
    public static final int PAUSED_UNKNOWN = 5;

    /**
     * Broadcast intent action sent by the download manager when a download completes.
     */
    public static final String ACTION_DOWNLOAD_START = Constants.PREFIX+".intent.action.DOWNLOAD_START";
    public static final String ACTION_DOWNLOAD_COMPLETE = Constants.PREFIX+".intent.action.DOWNLOAD_COMPLETE";
    public static final String ACTION_DOWNLOAD_CANCLE = Constants.PREFIX+".intent.action.DOWNLOAD_CANCLE";
    public static final String ACTION_DOWNLOAD_PAUSE = Constants.PREFIX+".intent.action.DOWNLOAD_PAUSE";
    public static final String ACTION_DOWNLOAD_PAUSE_BY_USER = Constants.PREFIX+".intent.action.DOWNLOAD_PAUSE_BY_USER";
    public static final String ACTION_DOWNLOAD_RUNNING= Constants.PREFIX+".intent.action.DOWNLOAD_RUNNING";

    /**
     * Broadcast intent action sent by the download manager when the user clicks on a running
     * download, either from a system notification or from the downloads UI.
     */
    public static final String ACTION_NOTIFICATION_CLICKED =
    		Constants.PREFIX+".intent.action.DOWNLOAD_NOTIFICATION_CLICKED";

    /**
     * Intent action to launch an activity to display all downloads.
     */
    public static final String ACTION_VIEW_DOWNLOADS = Constants.PREFIX+".intent.action.VIEW_DOWNLOADS";

    /**
     * Intent extra included with {@link #ACTION_DOWNLOAD_COMPLETE} intents, indicating the ID (as a
     * long) of the download that just completed.
     */
    public static final String EXTRA_DOWNLOAD_ID = "extra_download_id";

    public static final String COLUMN_EXTRA = "extra";

    /** this array must contain all public columns */
    private static final String[] COLUMNS = new String[] {
        COLUMN_ID,
        COLUMN_MEDIAPROVIDER_URI,
        COLUMN_TITLE,
        COLUMN_DESCRIPTION,
        COLUMN_URI,
        COLUMN_MEDIA_TYPE,
        COLUMN_TOTAL_SIZE_BYTES,
        COLUMN_LOCAL_URI,
        COLUMN_STATUS,
        COLUMN_REASON,
        COLUMN_BYTES_DOWNLOADED_SO_FAR,
        COLUMN_LAST_MODIFIED_TIMESTAMP,

        COLUMN_ORIGINAL_STATUS,
        COLUMN_EXTRA,
    };

    /** columns to request from DownloadProvider */
    private static final String[] UNDERLYING_COLUMNS = new String[] {
        Downloads.Impl._ID,
        Downloads.Impl.COLUMN_MEDIAPROVIDER_URI,
        Downloads.COLUMN_TITLE,
        Downloads.COLUMN_DESCRIPTION,
        Downloads.COLUMN_URI,
        Downloads.COLUMN_MIME_TYPE,
        Downloads.COLUMN_TOTAL_BYTES,
        Downloads.COLUMN_STATUS,
        Downloads.COLUMN_CURRENT_BYTES,
        Downloads.COLUMN_LAST_MODIFICATION,
        Downloads.COLUMN_DESTINATION,
        Downloads.COLUMN_APP_DATA,
        Downloads.Impl.COLUMN_FILE_NAME_HINT,
        Downloads.Impl.DATA,
    };
    /** LONG_COLUMNS */
    private static final Set<String> LONG_COLUMNS = new HashSet<String>(
            Arrays.asList(COLUMN_ID, COLUMN_TOTAL_SIZE_BYTES, COLUMN_STATUS, COLUMN_REASON,
                          COLUMN_BYTES_DOWNLOADED_SO_FAR, COLUMN_LAST_MODIFIED_TIMESTAMP,COLUMN_ORIGINAL_STATUS));

    /**
     * This class contains all the information necessary to request a new download. The URI is the
     * only required parameter.
     *
     * Note that the default download destination is a shared volume where the system might delete
     * your file if it needs to reclaim space for system use. If this is a problem, use a location
     * on external storage (see {@link #setDestinationUri(Uri)}.
     */
    public static class Request {
        /**
         * Bit flag for {@link #setAllowedNetworkTypes} corresponding to
         * {@link ConnectivityManager#TYPE_MOBILE}.
         */
        public static final int NETWORK_MOBILE = 1 << 0;

        /**
         * Bit flag for {@link #setAllowedNetworkTypes} corresponding to
         * {@link ConnectivityManager#TYPE_WIFI}.
         */
        public static final int NETWORK_WIFI = 1 << 1;
        /** URI. */
        private Uri mUri;
        /** mDestinationUri. */
        private Uri mDestinationUri;
        /** mRequestHeaders. */
        private List<Pair<String, String>> mRequestHeaders = new ArrayList<Pair<String, String>>();
        /** mTitle. */
        private CharSequence mTitle;
        /** mDescription. */
        private CharSequence mDescription;
        /** mShowNotification. */
        private boolean mShowNotification = true;
        /** mMimeType. */
        private String mMimeType;
        /** mRoamingAllowed. */
        private boolean mRoamingAllowed = true;
        /** mAllowedNetworkTypes  default to all network types allowed */
        private int mAllowedNetworkTypes = ~0;
        /** mIsVisibleInDownloadsUi. */
        private boolean mIsVisibleInDownloadsUi = true;

        /**
         * constructor.
         * @param uri the HTTP URI to download.
         */
        public Request(Uri uri) {
            if (uri == null) {
                throw new NullPointerException();
            }
            String scheme = uri.getScheme();
            if (scheme == null || !scheme.equals("http")) {
                throw new IllegalArgumentException("Can only download HTTP URIs: " + uri);
            }
            mUri = uri;
        }

        /**
         * Set the local destination for the downloaded file. Must be a file URI to a path on
         * external storage, and the calling application must have the WRITE_EXTERNAL_STORAGE
         * permission.
         *
         * By default, downloads are saved to a generated filename in the shared download cache and
         * may be deleted by the system at any time to reclaim space.
         * @param uri url.
         * @return this object
         */
        public Request setDestinationUri(Uri uri) {
            mDestinationUri = uri;
            return this;
        }
        

        /**
         * Set the local destination for the downloaded file to a path within the application's
         * external files directory (as returned by {@link Context#getExternalFilesDir(String)}.
         *
         * @param context the {@link Context} to use in determining the external files directory
         * @param dirType the directory type to pass to {@link Context#getExternalFilesDir(String)}
         * @param subPath the path within the external directory, including the destination filename
         * @return this object
         */
/*        public Request setDestinationInExternalFilesDir(Context context, String dirType,
                String subPath) {
            setDestinationFromBase(context.getExternalFilesDir(dirType), subPath);
            return this;
        }*/

        /**
         * Set the local destination for the downloaded file to a path within the public external
         * storage directory (as returned by
         * {@link Environment#getExternalStoragePublicDirectory(String)}.
         *
         * @param dirType the directory type to pass to
         *        {@link Environment#getExternalStoragePublicDirectory(String)}
         * @param subPath the path within the external directory, including the destination filename
         * @return this object
         */
        public Request setDestinationInExternalPublicDir(String dirType, String subPath) {
            File file = new File(Environment.getExternalStorageDirectory(), dirType);
            setDestinationFromBase(file, subPath);
            return this;
        }
        /**
         * set destination from the base file.
         * @param base base file.
         * @param subPath subpath.
         */
        private void setDestinationFromBase(File base, String subPath) {
            if (subPath == null) {
                throw new NullPointerException("subPath cannot be null");
            }
            mDestinationUri = Uri.withAppendedPath(Uri.fromFile(base), subPath);
        }

        /**
         * Add an HTTP header to be included with the download request.  The header will be added to
         * the end of the list.
         * @param header HTTP header name
         * @param value header value
         * @return this object
         * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2">HTTP/1.1
         *      Message Headers</a>
         */
        public Request addRequestHeader(String header, String value) {
            if (header == null) {
                throw new NullPointerException("header cannot be null");
            }
            if (header.contains(":")) {
                throw new IllegalArgumentException("header may not contain ':'");
            }
            if (value == null) {
                value = "";
            }
            mRequestHeaders.add(Pair.create(header, value));
            return this;
        }

        /**
         * Set the title of this download, to be displayed in notifications (if enabled).  If no
         * title is given, a default one will be assigned based on the download filename, once the
         * download starts.
         * @param title title
         * @return this object
         */
        public Request setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        /**
         * Set a description of this download, to be displayed in notifications (if enabled)
         * @param description description.
         * @return this object
         */
        public Request setDescription(CharSequence description) {
            mDescription = description;
            return this;
        }

        /**
         * Set the MIME content type of this download.  This will override the content type declared
         * in the server's response.
         * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7">HTTP/1.1
         *      Media Types</a>
         * @param mimeType mimeType
         * @return this object
         */
        public Request setMimeType(String mimeType) {
            mMimeType = mimeType;
            return this;
        }

        /**
         * Control whether a system notification is posted by the download manager while this
         * download is running. If enabled, the download manager posts notifications about downloads
         * through the system {@link android.app.NotificationManager}. By default, a notification is
         * shown.
         *
         * If set to false, this requires the permission
         * android.permission.DOWNLOAD_WITHOUT_NOTIFICATION.
         *
         * @param show whether the download manager should show a notification for this download.
         * @return this object
         */
        public Request setShowRunningNotification(boolean show) {
            mShowNotification = show;
            return this;
        }

        /**
         * Restrict the types of networks over which this download may proceed.  By default, all
         * network types are allowed.
         * @param flags any combination of the NETWORK_* bit flags.
         * @return this object
         */
        public Request setAllowedNetworkTypes(int flags) {
            mAllowedNetworkTypes = flags;
            return this;
        }

        /**
         * Set whether this download may proceed over a roaming connection.  By default, roaming is
         * allowed.
         * @param allowed whether to allow a roaming connection to be used
         * @return this object
         */
        public Request setAllowedOverRoaming(boolean allowed) {
            mRoamingAllowed = allowed;
            return this;
        }

        /**
         * Set whether this download should be displayed in the system's Downloads UI. True by
         * default.
         * @param isVisible whether to display this download in the Downloads UI
         * @return this object
         */
        public Request setVisibleInDownloadsUi(boolean isVisible) {
            mIsVisibleInDownloadsUi = isVisible;
            return this;
        }

        /**
         * ContentValues to be passed to DownloadProvider.insert()
         * @param packageName package name.
         * @return ContentValues to be passed to DownloadProvider.insert()
         */
        ContentValues toContentValues(String packageName) {
            ContentValues values = new ContentValues();
            assert mUri != null;
            values.put(Downloads.COLUMN_URI, mUri.toString());
            values.put(Downloads.Impl.COLUMN_IS_PUBLIC_API, true);
            values.put(Downloads.COLUMN_NOTIFICATION_PACKAGE, packageName);

            if (mDestinationUri != null) {
                values.put(Downloads.COLUMN_DESTINATION, Downloads.Impl.DESTINATION_FILE_URI);
                values.put(Downloads.COLUMN_FILE_NAME_HINT, mDestinationUri.toString());
            } else {
                values.put(Downloads.COLUMN_DESTINATION,
                           Downloads.DESTINATION_CACHE_PARTITION_PURGEABLE);
            }

            if (!mRequestHeaders.isEmpty()) {
                encodeHttpHeaders(values);
            }

            putIfNonNull(values, Downloads.COLUMN_TITLE, mTitle);
            putIfNonNull(values, Downloads.COLUMN_DESCRIPTION, mDescription);
            putIfNonNull(values, Downloads.COLUMN_MIME_TYPE, mMimeType);

            int visibility = Downloads.VISIBILITY_VISIBLE;
            if (!mShowNotification) {
                visibility = Downloads.VISIBILITY_HIDDEN;
            }
            values.put(Downloads.COLUMN_VISIBILITY, visibility);

            values.put(Downloads.Impl.COLUMN_ALLOWED_NETWORK_TYPES, mAllowedNetworkTypes);
            values.put(Downloads.Impl.COLUMN_ALLOW_ROAMING, mRoamingAllowed);
            values.put(Downloads.Impl.COLUMN_IS_VISIBLE_IN_DOWNLOADS_UI, mIsVisibleInDownloadsUi);

            return values;
        }
        /**
         * encodeHttpHeaders
         * @param values headers.
         */
        private void encodeHttpHeaders(ContentValues values) {
            int index = 0;
            for (Pair<String, String> header : mRequestHeaders) {
                String headerString = header.mFirst + ": " + header.mSecond;
                values.put(Downloads.Impl.RequestHeaders.INSERT_KEY_PREFIX + index, headerString);
                index++;
            }
        }
        /**
         * putIfNonNull 
         * @param contentValues ContentValues
         * @param key key
         * @param value value
         */
        private void putIfNonNull(ContentValues contentValues, String key, Object value) {
            if (value != null) {
                contentValues.put(key, value.toString());
            }
        }
    }

    /**
     * This class may be used to filter download manager queries.
     */
    public static class Query {
        /**
         * Constant for use with {@link #orderBy}
         * @hide
         */
        public static final int ORDER_ASCENDING = 1;

        /**
         * Constant for use with {@link #orderBy}
         * @hide
         */
        public static final int ORDER_DESCENDING = 2;
        
        /** ids. */
        private long[] mIds = null;
        /** status. */
        private Integer mStatusFlags = null;
        /** order by column. */
        private String mOrderByColumn = Downloads.COLUMN_LAST_MODIFICATION;
        /** order direction. */
        private int mOrderDirection = ORDER_DESCENDING;
        /** mOnlyIncludeVisibleInDownloadsUi */
        private boolean mOnlyIncludeVisibleInDownloadsUi = false;

        /**
         * Include only the downloads with the given IDs.
         * @param ids ids.
         * @return this object
         */
        public Query setFilterById(long... ids) {
            mIds = ids;
            return this;
        }
        private String extra ;
        public Query setFilterByExtra(String extra) {
        	this.extra = extra;
            return this;
        }

        /**
         * Include only downloads with status matching any the given status flags.
         * @param flags any combination of the STATUS_* bit flags
         * @return this object
         */
        public Query setFilterByStatus(int flags) {
            mStatusFlags = flags;
            return this;
        }

        /**
         * Controls whether this query includes downloads not visible in the system's Downloads UI.
         * @param value if true, this query will only include downloads that should be displayed in
         *            the system's Downloads UI; if false (the default), this query will include
         *            both visible and invisible downloads.
         * @return this object
         * @hide
         */
        public Query setOnlyIncludeVisibleInDownloadsUi(boolean value) {
            mOnlyIncludeVisibleInDownloadsUi = value;
            return this;
        }

        /**
         * Change the sort order of the returned Cursor.
         *
         * @param column one of the COLUMN_* constants; currently, only
         *         {@link #COLUMN_LAST_MODIFIED_TIMESTAMP} and {@link #COLUMN_TOTAL_SIZE_BYTES} are
         *         supported.
         * @param direction either {@link #ORDER_ASCENDING} or {@link #ORDER_DESCENDING}
         * @return this object
         * @hide
         */
        public Query orderBy(String column, int direction) {
            if (direction != ORDER_ASCENDING && direction != ORDER_DESCENDING  ) {
                throw new IllegalArgumentException("Invalid direction: " + direction);
            }

            if (column.equals(COLUMN_LAST_MODIFIED_TIMESTAMP)) {
                mOrderByColumn = Downloads.COLUMN_LAST_MODIFICATION;
            } else if (column.equals(COLUMN_TOTAL_SIZE_BYTES)) {
                mOrderByColumn = Downloads.COLUMN_TOTAL_BYTES;
            } else if (column.equals(COLUMN_ID)) {
                mOrderByColumn = Downloads.Impl._ID;
            } else {
                throw new IllegalArgumentException("Cannot order by " + column);
            }
            mOrderDirection = direction;
            return this;
        }

        /**
         * Run this query using the given ContentResolver.
         * @param resolver ContentResolver
         * @param projection the projection to pass to ContentResolver.query()
         * @param baseUri baseUri
         * @return the Cursor returned by ContentResolver.query()
         */
        Cursor runQuery(ContentResolver resolver, String[] projection, Uri baseUri) {
            Uri uri = baseUri;
            List<String> selectionParts = new ArrayList<String>();
            String[] selectionArgs = null;

            if (mIds != null) {
                selectionParts.add(getWhereClauseForIds(mIds));
                selectionArgs = getWhereArgsForIds(mIds);
            } if(extra != null){
          	  	selectionParts.add("( "+Downloads.COLUMN_APP_DATA+" =? )");
          	  	selectionArgs = new String[]{extra};
            }

            if (mStatusFlags != null) {
                List<String> parts = new ArrayList<String>();
                if ((mStatusFlags & STATUS_PENDING) != 0) {
                    parts.add(statusClause("=", Downloads.STATUS_PENDING));
                }
                if ((mStatusFlags & STATUS_RUNNING) != 0) {
                    parts.add(statusClause("=", Downloads.STATUS_RUNNING));
                }
                if ((mStatusFlags & STATUS_PAUSED) != 0) {
                    parts.add(statusClause("=", Downloads.Impl.STATUS_PAUSED_BY_APP));
                    parts.add(statusClause("=", Downloads.Impl.STATUS_WAITING_TO_RETRY));
                    parts.add(statusClause("=", Downloads.Impl.STATUS_WAITING_FOR_NETWORK));
                    parts.add(statusClause("=", Downloads.Impl.STATUS_QUEUED_FOR_WIFI));
                }
                if ((mStatusFlags & STATUS_SUCCESSFUL) != 0) {
                    parts.add(statusClause("=", Downloads.STATUS_SUCCESS));
                }
                if ((mStatusFlags & STATUS_FAILED) != 0) {
                    parts.add("(" + statusClause(">=", 400) + " AND " + statusClause("<", 600) + ")"); // SUPPRESS CHECKSTYLE
                }
                selectionParts.add(joinStrings(" OR ", parts));
            }

            if (mOnlyIncludeVisibleInDownloadsUi) {
                selectionParts.add(Downloads.Impl.COLUMN_IS_VISIBLE_IN_DOWNLOADS_UI + " != '0'");
            }
           
            // only return rows which are not marked 'deleted = 1'
            selectionParts.add(Downloads.Impl.COLUMN_DELETED + " != '1'");

            String selection = joinStrings(" AND ", selectionParts);
            
            String orderDirection = "ASC";
            if (mOrderDirection != ORDER_ASCENDING) {
                orderDirection = "DESC";
            }
            String orderBy = mOrderByColumn + " " + orderDirection;

            return resolver.query(uri, projection, selection, selectionArgs, orderBy);
        }
        
        /**
         * joinStrings.
         * 
         * @param joiner  joiner 
         * @param parts parts
         * @return joinedStrings
         */
        private String joinStrings(String joiner, Iterable<String> parts) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (String part : parts) {
                if (!first) {
                    builder.append(joiner);
                }
                builder.append(part);
                first = false;
            }
            return builder.toString();
        }
        /**
         * get status string .
         * @param operator operator
         * @param value value
         * @return result
         */
        private String statusClause(String operator, int value) {
            return Downloads.COLUMN_STATUS + operator + "'" + value + "'";
        }
    }
    
    /**
     * ContentResolver
     */
    private ContentResolver mResolver;
    /** package name. */
    private String mPackageName;
    /** mBaseUri see Downloads.Impl.CONTENT_URI */
    private Uri mBaseUri = Downloads.Impl.CONTENT_URI;

    /**
     * constructor.
     * @param resolver  ContentResolver
     * @param packageName packageName
     */
    private DownloadManager(Context context) {
    	
        mResolver = context.getContentResolver();
        mPackageName = context.getPackageName();
    }

    static DownloadManager INSTANCE ;
    public synchronized static DownloadManager getInstance(Context context){
    	if(INSTANCE == null){
    		INSTANCE = new DownloadManager(context);
    	}
    	return INSTANCE ;
    }
    /**
     * Makes this object access the download provider through /all_downloads URIs rather than
     * /my_downloads URIs, for clients that have permission to do so.
     * @param accessAllDownloads accessAllDownloads
     */
    public void setAccessAllDownloads(boolean accessAllDownloads) {
        if (accessAllDownloads) {
            mBaseUri = Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI;
        } else {
            mBaseUri = Downloads.Impl.CONTENT_URI;
        }
    }

    /**
     * Enqueue a new download.  The download will start automatically once the download manager is
     * ready to execute it and connectivity is available.
     *
     * @param request the parameters specifying this download
     * @return an ID for the download, unique across the system.  This ID is used to make future
     * calls related to this download.
     */
    public long enqueue(Request request) {
        ContentValues values = request.toContentValues(mPackageName);
        Uri downloadUri = mResolver.insert(Downloads.CONTENT_URI, values);
        long id = Long.parseLong(downloadUri.getLastPathSegment());
        return id;
    }
    

    /**
     * Marks the specified download as 'to be deleted'. This is done when a completed download
     * is to be removed but the row was stored without enough info to delete the corresponding
     * metadata from Mediaprovider database. Actual cleanup of this row is done in DownloadService.
     *
     * @param ids the IDs of the downloads to be marked 'deleted'
     * @return the number of downloads actually updated
     * @hide
     */
    public int markRowDeleted(long... ids) {
    	String whereClause = null ;
    	String[] whereArgs = null ;
        if (ids == null || ids.length == 0) {
            // called with nothing to remove!
            //throw new IllegalArgumentException("input param 'ids' can't be null");
        }else {
        	whereClause = getWhereClauseForIds(ids);
        	whereArgs = getWhereArgsForIds(ids) ;
        }
        ContentValues values = new ContentValues();
        values.put(Downloads.Impl.COLUMN_DELETED, 1);
        return mResolver.update(mBaseUri, values, whereClause,
        		whereArgs);
    }
    
    public int markRowDeleted(String extra) {
    	String whereClause = null ;
    	String[] whereArgs = null ;
    	whereClause = Downloads.COLUMN_APP_DATA +"=?" ;
    	whereArgs = new String[]{extra};
    	ContentValues values = new ContentValues();
    	values.put(Downloads.Impl.COLUMN_DELETED, 1);
    	return mResolver.update(mBaseUri, values, whereClause,
    			whereArgs);
    }

    /**
     * Cancel downloads and remove them from the download manager.  Each download will be stopped if
     * it was running, and it will no longer be accessible through the download manager.  If a file
     * was already downloaded to external storage, it will not be deleted.
     *
     * @param ids the IDs of the downloads to remove
     * @return the number of downloads actually removed
     */
    public int remove(long... ids) {
    	String whereClause = null ;
    	String[] whereArgs = null ;
        if (ids == null || ids.length == 0) {
            // called with nothing to remove!
            //throw new IllegalArgumentException("input param 'ids' can't be null");
        	Log.e(TAG, "ids is empty,so remove all the download!");
        }else {
        	whereClause = getWhereClauseForIds(ids);
        	whereArgs = getWhereArgsForIds(ids);
        }
        return mResolver.delete(mBaseUri, whereClause, whereArgs);
    }
    
    public int remove(String extra) {
    	String whereClause = null ;
    	String[] whereArgs = null ;
    	whereClause = Downloads.COLUMN_APP_DATA +"=?" ;
    	whereArgs = new String[]{extra};
        return mResolver.delete(mBaseUri, whereClause, whereArgs);
    }

    /**
     * Query the download manager about downloads that have been requested.
     * @param query parameters specifying filters for this query
     * @return a Cursor over the result set of downloads, with columns consisting of all the
     * COLUMN_* constants.
     */
    public Cursor query(Query query) {
        Cursor underlyingCursor = query.runQuery(mResolver, UNDERLYING_COLUMNS, mBaseUri);
        if (underlyingCursor == null) {
            return null;
        }
        return new CursorTranslator(underlyingCursor, mBaseUri);
    }
    
    /**
     * Open a downloaded file for reading.  The download must have completed.
     * @param id the ID of the download
     * @return a read-only {@link ParcelFileDescriptor}
     * @throws FileNotFoundException if the destination file does not already exist
     */
    public ParcelFileDescriptor openDownloadedFile(long id) throws FileNotFoundException {
        return mResolver.openFileDescriptor(getDownloadUri(id), "r");
    }

    /**
     * Resume the given downloads, which must be paused. This method will only
     * work when called from within the download manager's process.
     * 
     * @param ids
     *            the IDs of the downloads
     * @hide
     */
    public void resumeDownload(long... ids) {
    	String whereClause = null ;
    	String[] whereArgs = null ;
    	if(ids != null && ids.length > 0){
    		List<Long> validIds = new ArrayList<Long>(ids.length);
    		Cursor cursor = query(new Query().setFilterById(ids));
        	try {
        	    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
        		    .moveToNext()) {
	        		int status = cursor
	        			.getInt(cursor.getColumnIndex(COLUMN_STATUS));
	        		logStatus(status);
	        		long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
	        		if (status != STATUS_PENDING && status != STATUS_RUNNING) {
	        			validIds.add(id);
	        		}else {
	        			/*throw new IllegalArgumentException(
		        			    "Cannot only resume a paused download: "
		        				    + id);*/
	        			Log.e(TAG, "Cannot only resume a download:"+id);
	        		}
        	    }
        	} finally {
        	    cursor.close();
        	}
        	if(validIds.size() == 0){
        		Log.e(TAG, "No valid downloadID,cannot resume any download.");
        		return ;
        	}
        	whereClause = getWhereClauseForIds(ids);
    		whereArgs = getWhereArgsForIds(ids);
    	//�ָ�����
    	}else {
    		whereClause = Downloads.Impl.COLUMN_STATUS + " !=? AND "+ Downloads.Impl.COLUMN_STATUS+" !=? AND "+Downloads.Impl.COLUMN_STATUS+" !=?";
    		whereArgs = new String[] { String.valueOf(Downloads.Impl.STATUS_PENDING),
                    String.valueOf(Downloads.Impl.STATUS_RUNNING),
                    String.valueOf(Downloads.Impl.STATUS_SUCCESS)
            };
    	}
    	ContentValues values = new ContentValues();
    	//values.put(Downloads.Impl.COLUMN_VISIBILITY,Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    	values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_PENDING);
    	values.put(Downloads.COLUMN_CONTROL, Downloads.CONTROL_RUN);
    	mResolver.update(mBaseUri, values, whereClause,whereArgs);
        }
    
    
    public int updateDownload(long oldId,String newUrl,String saveDest,String newAppData) {
    	String whereClause = Downloads.Impl._ID +"=? AND "+Downloads.Impl.COLUMN_URI+"!=?";
    	String[] whereArgs = new String[]{String.valueOf(oldId),newUrl} ;
    	
    	ContentValues values = new ContentValues();
    	values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_PENDING);
    	values.put(Downloads.COLUMN_CONTROL, Downloads.CONTROL_RUN);
    	values.put(Downloads.Impl.COLUMN_URI, newUrl);
    	values.put(Downloads.COLUMN_FILE_NAME_HINT,
				Uri.fromFile(new File(saveDest)).toString());
    	values.put(Downloads.Impl.COLUMN_APP_DATA, newAppData);
    	return mResolver.update(mBaseUri, values, whereClause,whereArgs);
      }
    
    public int updateDownload(String oldUrl,String newUrl,String saveDest,String newAppData) {
    	String whereClause = Downloads.Impl.COLUMN_URI +"=?" ;
    	String[] whereArgs = new String[]{oldUrl} ;
    	ContentValues values = new ContentValues();
    	values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_PENDING);
    	values.put(Downloads.COLUMN_CONTROL, Downloads.CONTROL_RUN);
    	values.put(Downloads.Impl.COLUMN_URI, newUrl);
    	values.put(Downloads.COLUMN_FILE_NAME_HINT,
    			Uri.fromFile(new File(saveDest)).toString());
    	values.put(Downloads.Impl.COLUMN_APP_DATA, newAppData);
    	return mResolver.update(mBaseUri, values, whereClause,whereArgs);
    }
    
    /**
     * ���ȴ��������������Ϊ��ʼ״̬
     */
    public void resumeLastPausedDownload() {
    	
//    	String whereClause = null ;
//    	String[] whereArgs = null ;
//		//�ָ�����
//		whereClause = Downloads.Impl.COLUMN_STATUS + " IN (?,?)";
//		whereArgs = new String[] { String.valueOf(Downloads.Impl.STATUS_WAITING_FOR_NETWORK),
//								   String.valueOf(Downloads.Impl.STATUS_QUEUED_FOR_WIFI),
//    				 };
//    	ContentValues values = new ContentValues();
//    	//values.put(Downloads.Impl.COLUMN_VISIBILITY,Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//    	values.put(Downloads.COLUMN_STATUS, Downloads.STATUS_PENDING);
//    	values.put(Downloads.COLUMN_CONTROL, Downloads.CONTROL_RUN);
//    	mResolver.update(mBaseUri, values, whereClause,whereArgs);
    }
    
    /**
     * Restart the given downloads, which must have already completed
     * (successfully or not). This method will only work when called from within
     * the download manager's process.
     * 
     * @param ids
     *            the IDs of the downloads
     * @param context
     *            Context
     * @hide
     */
    public void restartDownload(Context context, long... ids) {
    	String whereClause = null ;
    	String[] whereArgs = null ;
    	if(ids != null && ids.length > 0){
    		List<Long> validIds = new ArrayList<Long>(ids.length);
    		Cursor cursor = query(new Query().setFilterById(ids));
            try {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    int status = cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS));
                    logStatus(status);
	        		long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                    if (status != STATUS_SUCCESSFUL && status != STATUS_FAILED && status != STATUS_PAUSED ) {
                    	//throw new IllegalArgumentException("Cannot restart incomplete download: "+ cursor.getLong(cursor .getColumnIndex(COLUMN_ID)));
                    	Log.e(TAG, "Cannot restart incomplete download::"+id);
                    }else {
                    	validIds.add(id);
                    }
                }
            } finally {
                cursor.close();
            }
            if(validIds.size() == 0){
        		Log.e(TAG, "No valid downloadID,cannot resume any download.");
        		return ;
        	}
        	whereClause = getWhereClauseForIds(ids);
    		whereArgs = getWhereArgsForIds(ids);
    	} else {
    		whereClause = Downloads.Impl.COLUMN_STATUS + " !=? OR "+ Downloads.Impl.COLUMN_STATUS+" !=?";
    		whereArgs = new String[] { String.valueOf(Downloads.Impl.STATUS_PENDING),
                    String.valueOf(Downloads.Impl.STATUS_RUNNING) };
    	}
        

        ContentValues values = new ContentValues();
        values.put(Downloads.Impl.COLUMN_CURRENT_BYTES, 0);
        values.put(Downloads.Impl.COLUMN_TOTAL_BYTES, -1);
        values.putNull(Downloads.Impl.DATA);

        //values.put(Downloads.Impl.COLUMN_VISIBILITY,Downloads.Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_PENDING);
        values.put(Downloads.Impl.COLUMN_CONTROL, Downloads.Impl.CONTROL_RUN);
        
        context.startService(new Intent(context, DownloadService.class));
        
        mResolver.update(mBaseUri, values, whereClause, whereArgs);
      
    }

    
    private void logStatus(int status){
    	switch (status) {
			case STATUS_PENDING:
				Log.i(TAG, "STATUS_PENDING");
				break;
			case STATUS_RUNNING:
				Log.i(TAG, "STATUS_RUNNING");
				break;
			case STATUS_PAUSED:
				Log.i(TAG, "STATUS_PAUSED");
				break;
			case STATUS_FAILED:
				Log.i(TAG, "STATUS_FAILED");
				break;
			case STATUS_SUCCESSFUL:
				Log.i(TAG, "STATUS_SUCCESSFUL");
				break;
			default:
				Log.i(TAG, "Unknown status!");
				break ;
		}
    	
    }
    
    
    /**
     * ��������
     * 
     * @param context
     * @param ids
     *            id����
     */
    public int updateDownload(Context context, long id,String appData) {
    	String whereClause = null ;
    	String[] whereArgs =null ;
    	long[] ids = new long[]{id};
        whereClause = getWhereClauseForIds(ids);
        whereArgs = getWhereArgsForIds(ids) ;
    	
    	ContentValues values = new ContentValues();
        values.put(Downloads.Impl.COLUMN_APP_DATA, appData);
        return  mResolver.update(mBaseUri, values, whereClause,whereArgs);
    }
    
    
    /**
     * ��ͣ����
     * 
     * @param context
     * @param ids
     *            id����
     */
    public void pauseDownload(Context context, long... ids) {
    	String whereClause = null ;
    	String[] whereArgs =null ;
    	//��ָͣ��������
    	if(ids != null && ids.length > 0){
    		List<Long> validIds = new ArrayList<Long>(ids.length);
        	Cursor cursor = query(new Query().setFilterById(ids));
        	try {
    			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
    					.moveToNext()) {
    				int status = cursor
    						.getInt(cursor.getColumnIndex(COLUMN_STATUS));
    				logStatus(status);
    				long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
    				if (status != STATUS_RUNNING && status != STATUS_PENDING) {
    					Log.e(TAG, "Can only pause a running/penging download:"+id);
    	    		    //throw new IllegalArgumentException("Can only pause a running download: "+ validId);
    				} else {
    					validIds.add(id);
    				}
    			}
        	} finally {
        	    cursor.close();
        	}
        	if(validIds.size() == 0){
        		Log.e(TAG, "No valid downloadID,cannot pause any download.");
        		return ;
        	}
        	long[] finalIds = new long[validIds.size()];
        	int length = finalIds.length;
        	for (int i = 0; i < length; i++) {
        		finalIds[i] = validIds.get(i);
    		}
        	whereClause = getWhereClauseForIds(finalIds);
        	whereArgs = getWhereArgsForIds(finalIds) ;
        //��ͣ���е����ڽ��л���pending״̬������
    	}else {
    		whereClause = Downloads.Impl.COLUMN_STATUS + " IN (?,?,?,? )";
    		whereArgs = new String[] { String.valueOf(Downloads.Impl.STATUS_PENDING),
                    String.valueOf(Downloads.Impl.STATUS_RUNNING) ,
                    String.valueOf(Downloads.Impl.STATUS_WAITING_FOR_NETWORK),
                    String.valueOf(Downloads.Impl.STATUS_QUEUED_FOR_WIFI)
                    };
    	}
    	
    	ContentValues values = new ContentValues();
        values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_PAUSED_BY_APP);
        values.put(Downloads.Impl.COLUMN_CONTROL, Downloads.Impl.CONTROL_PAUSED);
        //values.put(Downloads.COLUMN_NO_INTEGRITY, true);
        mResolver.update(mBaseUri, values, whereClause,whereArgs);
    }
    
    private int translateNetworkTypeToApiFlag(int networkType) {
        switch (networkType) {
            case ConnectivityManager.TYPE_MOBILE:
                return DownloadManager.Request.NETWORK_MOBILE;

            case ConnectivityManager.TYPE_WIFI:
                return DownloadManager.Request.NETWORK_WIFI;

            default:
                return 0;
        }
    }

    
    private int checkIsNetworkTypeAllowed(int networkType) {
        return translateNetworkTypeToApiFlag(networkType);
    }
    
    public Integer checkCanUseNetwork(Context context) {
    	RealSystemFacade mSystemFacade = new RealSystemFacade(context);
    	
        Integer networkType = mSystemFacade.getActiveNetworkType();
        if (networkType == null) {
        	return null ;
        }
        return checkIsNetworkTypeAllowed(networkType);
    }

    
   public void resetAllowedNetworkType(Context context ,int networkType) {
    	String whereClause = null ;
    	String[] whereArgs =null ;
    	ContentValues values = new ContentValues();
    	values.put(Downloads.Impl.COLUMN_ALLOWED_NETWORK_TYPES, networkType);
    	Integer network = checkCanUseNetwork(context);
    	mResolver.update(mBaseUri, values, whereClause,whereArgs);
    	if ((networkType & DownloadManager.Request.NETWORK_MOBILE) == 0) {
    		//only wifi ���ǰ��mobile��������״̬����Ϊ�ȴ�����(�������⣬��Ϊ����Thread���ڼ���,����ֹͣ)
    		if(network != null && network == DownloadManager.Request.NETWORK_MOBILE){
    			 values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_PAUSED_BY_APP);
    		     values.put(Downloads.Impl.COLUMN_CONTROL, Downloads.Impl.CONTROL_PAUSED);
    		}
    		whereClause = Downloads.Impl.COLUMN_STATUS + " != ?";
    		whereArgs = new String[] { String.valueOf(Downloads.Impl.STATUS_SUCCESS),
                    };
		} else {
			//wifi and mobile
			//values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_PENDING);
			whereClause = Downloads.Impl.COLUMN_STATUS + " != ?";
    		whereArgs = new String[] { String.valueOf(Downloads.Impl.STATUS_SUCCESS)
                    };
		}
    	mResolver.update(mBaseUri, values, whereClause,whereArgs);
    }
    
    public void resetAllowedNetworkType2(Context context ,int networkType) {
    	String whereClause = null ;
    	String[] whereArgs =null ;
    	ContentValues values = new ContentValues();
    	values.put(Downloads.Impl.COLUMN_ALLOWED_NETWORK_TYPES, networkType);
    	mResolver.update(mBaseUri, values, whereClause,whereArgs);
    	
    	
  }

    /**
     * �ж�appitem�Ƿ�������ݿ��¼����ͣ״̬
     * 
     * @param context
     *            Context
     * @param itemID
     *            ���uri
     * @return true������ͣ��false:δ��ͣ
     */
    public boolean isAppItemPaused(Context context, String itemID) {
        Cursor cursor = query(new Query().setFilterById(ContentUris.parseId(Uri.parse(itemID))));
        cursor.moveToFirst();
        // TODO �пգ��ж���Чֵ
        return cursor.getInt(cursor.getColumnIndex(Downloads.Impl.COLUMN_CONTROL)) == Downloads.CONTROL_PAUSED;
    }
    /**
     * ������������
     * 
     * @param context
     *            Context
     */
    public void restartAllPausedDownload(Context context) {
        restartDownload(context, null);
    }
    /**
     * Get the DownloadProvider URI for the download with the given ID.
     * @param id id
     * @return download uri
     */
    Uri getDownloadUri(long id) {
        return ContentUris.withAppendedId(mBaseUri, id);
    }

    /**
     * Get a parameterized SQL WHERE clause to select a bunch of IDs.
     * @param ids ids
     * @return getWhereClauseForIds
     */
    static String getWhereClauseForIds(long[] ids) {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append("(");
        for (int i = 0; i < ids.length; i++) {
            if (i > 0) {
                whereClause.append("OR ");
            }
            whereClause.append(Downloads.Impl._ID);
            whereClause.append(" = ? ");
        }
        whereClause.append(")");
        return whereClause.toString();
    }

    /**
     * Get the selection args for a clause returned by {@link #getWhereClauseForIds(long[])}.
     * @param ids ids.
     * @return getWhereArgsForIds
     */
    static String[] getWhereArgsForIds(long[] ids) {
        String[] whereArgs = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            whereArgs[i] = Long.toString(ids[i]);
        }
        return whereArgs;
    }

    /**
     * This class wraps a cursor returned by DownloadProvider -- the "underlying cursor" -- and
     * presents a different set of columns, those defined in the DownloadManager.COLUMN_* constants.
     * Some columns correspond directly to underlying values while others are computed from
     * underlying data.
     */
    private static class CursorTranslator extends CursorWrapper {
        /** base uri. */
        private Uri mBaseUri;

        /**
         * CursorTranslator.
         * @param cursor Cursor
         * @param baseUri Uri
         */
        public CursorTranslator(Cursor cursor, Uri baseUri) {
            super(cursor);
            mBaseUri = baseUri;
        }

        @Override
        public int getColumnIndex(String columnName) {
            return Arrays.asList(COLUMNS).indexOf(columnName);
        }

        @Override
        public int getColumnIndexOrThrow(String columnName) {
            int index = getColumnIndex(columnName);
            if (index == -1) {
                throw new IllegalArgumentException("No such column: " + columnName);
            }
            return index;
        }

        @Override
        public String getColumnName(int columnIndex) {
            int numColumns = COLUMNS.length;
            if (columnIndex < 0 || columnIndex >= numColumns) {
                throw new IllegalArgumentException("Invalid column index " + columnIndex + ", "
                                                   + numColumns + " columns exist");
            }
            return COLUMNS[columnIndex];
        }

        @Override
        public String[] getColumnNames() {
            String[] returnColumns = new String[COLUMNS.length];
            System.arraycopy(COLUMNS, 0, returnColumns, 0, COLUMNS.length);
            return returnColumns;
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public byte[] getBlob(int columnIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getDouble(int columnIndex) {
            return getLong(columnIndex);
        }
        
        /**
         * isLongColumn 
         * @param column column
         * @return long column return true
         */
        private boolean isLongColumn(String column) {
            return LONG_COLUMNS.contains(column);
        }

        @Override
        public float getFloat(int columnIndex) {
            return (float) getDouble(columnIndex);
        }

        @Override
        public int getInt(int columnIndex) {
            return (int) getLong(columnIndex);
        }

        @Override
        public long getLong(int columnIndex) {
            return translateLong(getColumnName(columnIndex));
        }

        @Override
        public short getShort(int columnIndex) {
            return (short) getLong(columnIndex);
        }

        @Override
        public String getString(int columnIndex) {
            return translateString(getColumnName(columnIndex));
        }
        /**
         * translateString
         * @param column column name
         * @return translated string
         */
        private String translateString(String column) {
            if (isLongColumn(column)) {
                return Long.toString(translateLong(column));
            }
            if (column.equals(COLUMN_TITLE)) {
                return getUnderlyingString(Downloads.COLUMN_TITLE);
            }
            if (column.equals(COLUMN_DESCRIPTION)) {
                return getUnderlyingString(Downloads.COLUMN_DESCRIPTION);
            }
            if (column.equals(COLUMN_URI)) {
                return getUnderlyingString(Downloads.COLUMN_URI);
            }
            if (column.equals(COLUMN_MEDIA_TYPE)) {
                return getUnderlyingString(Downloads.COLUMN_MIME_TYPE);
            }
            if (column.equals(COLUMN_MEDIAPROVIDER_URI)) {
                return getUnderlyingString(Downloads.Impl.COLUMN_MEDIAPROVIDER_URI);
            }
            if (column.equals(COLUMN_EXTRA)) {
            	return getUnderlyingString(Downloads.Impl.COLUMN_APP_DATA);
            }

            assert column.equals(COLUMN_LOCAL_URI);
            return getLocalUri();
        }

        /**
         * get local uri.
         * @return uri.
         */
        private String getLocalUri() {
            long destinationType = getUnderlyingLong(Downloads.Impl.COLUMN_DESTINATION);
            if (destinationType == Downloads.Impl.DESTINATION_FILE_URI) {
                // return client-provided file URI for external download
                return getUnderlyingString(Downloads.Impl.COLUMN_FILE_NAME_HINT);
            }

            if (destinationType == Downloads.Impl.DESTINATION_EXTERNAL) {
                // return stored destination for legacy external download
                String localPath = getUnderlyingString(Downloads.Impl.DATA);
                if (localPath == null) {
                    return null;
                }
                return Uri.fromFile(new File(localPath)).toString();
            }

            // return content URI for cache download
            long downloadId = getUnderlyingLong(Downloads.Impl._ID);
            return ContentUris.withAppendedId(mBaseUri, downloadId).toString();
        }

        /**
         * translate long.
         * @param column column name.
         * @return translated
         */
        private long translateLong(String column) {
            if (!isLongColumn(column)) {
                // mimic behavior of underlying cursor -- most likely, throw NumberFormatException
                return Long.valueOf(translateString(column));
            }

            if (column.equals(COLUMN_ID)) {
                return getUnderlyingLong(Downloads.Impl._ID);
            }
            if (column.equals(COLUMN_TOTAL_SIZE_BYTES)) {
                return getUnderlyingLong(Downloads.COLUMN_TOTAL_BYTES);
            }
            if (column.equals(COLUMN_STATUS)) {
                return translateStatus((int) getUnderlyingLong(Downloads.COLUMN_STATUS));
            }
            if (column.equals(COLUMN_REASON)) {
                return getReason((int) getUnderlyingLong(Downloads.COLUMN_STATUS));
            }
            if (column.equals(COLUMN_BYTES_DOWNLOADED_SO_FAR)) {
                return getUnderlyingLong(Downloads.COLUMN_CURRENT_BYTES);
            }
            if (column.equals(COLUMN_ORIGINAL_STATUS)) {
            	return getUnderlyingLong(Downloads.COLUMN_STATUS);
            }
            assert column.equals(COLUMN_LAST_MODIFIED_TIMESTAMP);
            return getUnderlyingLong(Downloads.COLUMN_LAST_MODIFICATION);
        }
        
        /**
         * get reason from status .
         * @param status status
         * @return reason
         */
        private long getReason(int status) {
            switch (translateStatus(status)) {
                case STATUS_FAILED:
                    return getErrorCode(status);

                case STATUS_PAUSED:
                    return getPausedReason(status);

                default:
                    return 0; // arbitrary value when status is not an error
            }
        }
        
        /**
         * getPausedReason.
         * @param status status
         * @return reason
         */
        private long getPausedReason(int status) {
            switch (status) {
                case Downloads.Impl.STATUS_WAITING_TO_RETRY:
                    return PAUSED_WAITING_TO_RETRY;

                case Downloads.Impl.STATUS_WAITING_FOR_NETWORK:
                    return PAUSED_WAITING_FOR_NETWORK;

                case Downloads.Impl.STATUS_QUEUED_FOR_WIFI:
                    return PAUSED_QUEUED_FOR_WIFI;
                case Downloads.Impl.STATUS_PAUSED_BY_APP:
                	return PAUSED_BY_APP;

                default:
                    return PAUSED_UNKNOWN;
            }
        }
        /**
         * getErrorCode .
         * @param status status
         * @return error code
         */
        private long getErrorCode(int status) {
            if ((400 <= status && status < Downloads.Impl.MIN_ARTIFICIAL_ERROR_STATUS) // SUPPRESS CHECKSTYLE
                    || (500 <= status && status < 600)) { // SUPPRESS CHECKSTYLE
                // HTTP status code
                return status;
            }

            switch (status) {
                case Downloads.STATUS_FILE_ERROR:
                    return ERROR_FILE_ERROR;

                case Downloads.STATUS_UNHANDLED_HTTP_CODE:
                case Downloads.STATUS_UNHANDLED_REDIRECT:
                    return ERROR_UNHANDLED_HTTP_CODE;

                case Downloads.STATUS_HTTP_DATA_ERROR:
                    return ERROR_HTTP_DATA_ERROR;

                case Downloads.STATUS_TOO_MANY_REDIRECTS:
                    return ERROR_TOO_MANY_REDIRECTS;

                case Downloads.STATUS_INSUFFICIENT_SPACE_ERROR:
                    return ERROR_INSUFFICIENT_SPACE;

                case Downloads.STATUS_DEVICE_NOT_FOUND_ERROR:
                    return ERROR_DEVICE_NOT_FOUND;

                case Downloads.Impl.STATUS_CANNOT_RESUME:
                    return ERROR_CANNOT_RESUME;

                case Downloads.Impl.STATUS_FILE_ALREADY_EXISTS_ERROR:
                    return ERROR_FILE_ALREADY_EXISTS;

                default:
                    return ERROR_UNKNOWN;
            }
        }
        
       
        /**
         * getUnderlyingString 
         * @param column column name
         * @return getUnderlyingString
         */
        private long getUnderlyingLong(String column) {
            return super.getLong(super.getColumnIndex(column));
        }
        
        /**
         * getUnderlyingString 
         * @param column column name
         * @return getUnderlyingString
         */
        private String getUnderlyingString(String column) {
            return super.getString(super.getColumnIndex(column));
        }
        
        /**
         * translateStatus
         * @param status status
         * @return translateStatus
         */
        private int translateStatus(int status) {
            switch (status) {
                case Downloads.STATUS_PENDING:
                    return STATUS_PENDING;

                case Downloads.STATUS_RUNNING:
                    return STATUS_RUNNING;

                case Downloads.Impl.STATUS_PAUSED_BY_APP:
                case Downloads.Impl.STATUS_WAITING_TO_RETRY:
                case Downloads.Impl.STATUS_WAITING_FOR_NETWORK:
                case Downloads.Impl.STATUS_QUEUED_FOR_WIFI:
                    return STATUS_PAUSED;

                case Downloads.STATUS_SUCCESS:
                    return STATUS_SUCCESSFUL;

                default:
                    assert Downloads.isStatusError(status);
                    return STATUS_FAILED;
            }
        }
    }
 
    
    
}
