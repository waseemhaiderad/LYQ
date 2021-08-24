package lnq.com.lnq.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.endpoints.EndpointKeys;
import lnq.com.lnq.endpoints.EndpointUrls;
import lnq.com.lnq.model.gson_converter_models.location.UpdateLocationData;

import static lnq.com.lnq.fragments.profile.ProgressDialogFragmentImageCrop.TAG;

public class ClusterRendererUtils extends DefaultClusterRenderer<UpdateLocationData> {
    private static Bitmap output;
    private String cachePath = "";
    private TransferUtility transferUtility;

    private static final String TAG = "ClusterRendererUtils";
    private final Context mContext;
    GoogleMap map;
    float currentZoom;
    private String type;
    private ClusterManager<UpdateLocationData> clusterManager;

    public ClusterRendererUtils(Context context, GoogleMap map, ClusterManager<UpdateLocationData> clusterManager, String type) {
        super(context, map, clusterManager);
        mContext = context;
        this.map = map;
        this.clusterManager = clusterManager;
        this.type = type;
        this.currentZoom = map.getCameraPosition().zoom;
        createTransferUtility();
        cachePath = context.getCacheDir().getAbsolutePath();
    }

    private void createTransferUtility() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                mContext.getApplicationContext(),
                Constants.COGNITO_POOL_ID,
                Regions.US_WEST_1
        );
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = new TransferUtility(s3Client, mContext.getApplicationContext());
    }

    @Override
    protected void onBeforeClusterItemRendered(UpdateLocationData item,
                                               MarkerOptions markerOptions) {
        View customMarkerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_custom_marker, null);
        AppCompatImageView markerImageView = customMarkerView.findViewById(R.id.mImgMarker);
        AppCompatImageView markerImageViewPic = customMarkerView.findViewById(R.id.mImgMarkerPic);
        TextView textMarkerUserName = customMarkerView.findViewById(R.id.textMarkerUserName);

        if (item.getUser_image() != null && !item.getUser_image().isEmpty()) {
            download(item.getUser_image(), markerImageViewPic);
        }

        String name = item.getUser_name();
        String lastName = "";
        String firstName = "";
        if (name.split("\\w+").length > 1) {

            lastName = name.substring(name.lastIndexOf(" ") + 1);
            firstName = name.substring(0, name.lastIndexOf(' '));
        } else {
            firstName = name;
        }
        textMarkerUserName.setText(firstName);

        if (item.getIs_favorite().equals(Constants.FAVORITE)) {
            if (item.getIs_connection().equals(Constants.CONNECTED)) {
                markerImageView.setImageResource(R.drawable.fav_pin_trans);
            } else if (item.getIs_connection().equals(Constants.CONTACTED)) {
                markerImageView.setImageResource(R.drawable.pendind_fav_pin_trans);
            } else if (item.getIs_connection().equals("")) {
                markerImageView.setImageResource(R.drawable.gray_fav_pin_trans);
            }
        } else {
            if (item.getIs_connection().equals(Constants.CONNECTED)) {
                markerImageView.setImageResource(R.drawable.pin_green_trans);
            } else if (item.getIs_connection().equals(Constants.CONTACTED)) {
                markerImageView.setImageResource(R.drawable.pin_blue_trans);
            } else if (item.getIs_connection().equals("")) {
                markerImageView.setImageResource(R.drawable.pin_gray_trans);
            }
        }

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(returnedBitmap));
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<UpdateLocationData> cluster, MarkerOptions markerOptions) {
        View customMarkerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_custom_cluster_marker, null);
        AppCompatImageView markerImageView = customMarkerView.findViewById(R.id.mImgMarker);
        TextView counter = customMarkerView.findViewById(R.id.textCounter);
        TextView textClusterUserName = customMarkerView.findViewById(R.id.textClusterUserName);
        counter.setText(String.valueOf(cluster.getSize()));

        Collection<Marker> markerCollection = clusterManager.getClusterMarkerCollection().getMarkers();
        ArrayList<Marker> markerArrayList = new ArrayList<>(markerCollection);
        Log.d(TAG, "Marker list size: " + markerArrayList.size());

        if (type.equals("green"))
            markerImageView.setImageResource(R.drawable.pin_green_trans);
        else if (type.equals("grey"))
            markerImageView.setImageResource(R.drawable.pin_gray_trans);
        else if (type.equals("blue")) {
            markerImageView.setImageResource(R.drawable.pin_blue_trans);
        }
        boolean isFavoriteConnected = false;
        boolean isFavoriteContacted = false;
        boolean isFavoriteLnq = false;
        boolean isConnected = false;
        boolean isContacted = false;
        boolean isLnq = false;

        for (UpdateLocationData updateLocationData : cluster.getItems()) {
            String name = updateLocationData.getUser_name();
            String lastName = "";
            String firstName = "";
            if (name.split("\\w+").length > 1) {

                lastName = name.substring(name.lastIndexOf(" ") + 1);
                firstName = name.substring(0, name.lastIndexOf(' '));
            } else {
                firstName = name;
            }
            textClusterUserName.setText(firstName + " + " + String.valueOf(cluster.getSize() - 1));
            if (updateLocationData.getIs_favorite().equals(Constants.FAVORITE) && updateLocationData.getIs_connection().equals(Constants.CONNECTED)) {
                isFavoriteConnected = true;
            }
            if (!isFavoriteConnected) {
                if (updateLocationData.getIs_favorite().equals(Constants.FAVORITE) && updateLocationData.getIs_connection().equals(Constants.CONTACTED)) {
                    isFavoriteContacted = true;
                }
            }
            if (!isFavoriteContacted) {
                if (updateLocationData.getIs_favorite().equals(Constants.FAVORITE) && updateLocationData.getIs_connection().equals("")) {
                    isFavoriteLnq = true;
                }
            }
            if (!isFavoriteLnq) {
                if (updateLocationData.getIs_favorite().equals("") && updateLocationData.getIs_connection().equals(Constants.CONNECTED)) {
                    isConnected = true;
                }
            }
            if (!isConnected) {
                if (updateLocationData.getIs_favorite().equals("") && updateLocationData.getIs_connection().equals(Constants.CONTACTED)) {
                    isContacted = true;
                }
            }
            if (!isContacted) {
                if (updateLocationData.getIs_favorite().equals("") && updateLocationData.getIs_connection().equals("")) {
                    isContacted = true;
                }
            }
        }

        if (isFavoriteConnected) {
            markerImageView.setImageResource(R.drawable.fav_pin_trans);
        } else if (isFavoriteContacted) {
            markerImageView.setImageResource(R.drawable.pendind_fav_pin_trans);
        } else if (isFavoriteLnq) {
            markerImageView.setImageResource(R.drawable.gray_fav_pin_trans);
        } else if (isConnected) {
            markerImageView.setImageResource(R.drawable.pin_green_trans);
        } else if (isContacted) {
            markerImageView.setImageResource(R.drawable.pin_blue_trans);
        } else {
            markerImageView.setImageResource(R.drawable.pin_gray_trans);
        }

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(returnedBitmap));
    }

//    void download(String objectKey, ImageView imageView) {
//        final File fileDownload = new File(mContext.getCacheDir(), objectKey);
//
//        TransferObserver transferObserver = transferUtility.download(
//                Constants.BUCKET_NAME,
//                objectKey,
//                fileDownload
//        );
//        transferObserver.setTransferListener(new TransferListener() {
//
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                Log.d(TAG, "onStateChanged: " + state);
//                if (TransferState.COMPLETED.equals(state)) {
//                    Glide.with(mContext).
//                            load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
//                            apply(new RequestOptions().circleCrop()).
//                            apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar)).
//                            into(imageView);
//                }
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//                Log.e(TAG, "onError: ", ex);
//            }
//        });
//    }

    public static Bitmap getCircledBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        if (bitmap != null) {
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        }

        return output;
    }

    void download(String objectKey, ImageView imageView) {

        if (LnqApplication.getInstance().listImagePaths.contains(cachePath + "/" + objectKey)) {
//            Glide.with(mContext).
//                    load(BitmapFactory.decodeFile(cachePath + "/" + objectKey)).
//                    apply(new RequestOptions().circleCrop()).
//                    apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imageView);
            getCircledBitmap(BitmapFactory.decodeFile(cachePath + "/" + objectKey));
            imageView.setImageBitmap(output);
        } else {
            final File fileDownload = new File(cachePath, objectKey);

            TransferObserver transferObserver = transferUtility.download(
                    Constants.BUCKET_NAME,
                    objectKey,
                    fileDownload
            );
            transferObserver.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    Log.d(TAG, "onStateChanged: " + state);
                    if (TransferState.COMPLETED.equals(state)) {
                        LnqApplication.getInstance().listImagePaths.add(fileDownload.getAbsolutePath());
//                        Glide.with(mContext).
//                                load(BitmapFactory.decodeFile(fileDownload.getAbsolutePath())).
//                                apply(new RequestOptions().circleCrop()).
//                                apply(new RequestOptions().placeholder(R.drawable.ic_action_avatar))
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .into(imageView);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getCircledBitmap(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()));
                                imageView.setImageBitmap(output);
                            }
                        }, 200);

                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.e(TAG, "onError: ", ex);
                }
            });
        }
    }

    @Override
    protected void onClusterItemRendered(UpdateLocationData clusterItem, Marker marker) {
        marker.setTag(clusterItem);
    }

    @Override
    protected void onClusterRendered(Cluster<UpdateLocationData> cluster, Marker marker) {

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<UpdateLocationData> cluster) {
        if (currentZoom <= 4.99f) {
            return cluster.getSize() > 1;
        } else {
            return cluster.getSize() > 20;
        }
    }

}