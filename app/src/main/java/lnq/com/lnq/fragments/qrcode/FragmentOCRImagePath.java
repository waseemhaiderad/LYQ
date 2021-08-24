package lnq.com.lnq.fragments.qrcode;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lnq.com.lnq.MainActivity;
import lnq.com.lnq.R;
import lnq.com.lnq.application.LnqApplication;
import lnq.com.lnq.common.Constants;
import lnq.com.lnq.databinding.FragmentOCRImagePathBinding;
import lnq.com.lnq.model.event_bus_models.EventBuSetDataRecipentFragment;
import lnq.com.lnq.model.event_bus_models.EventBusOpenCameraSecondaryProfile;
import lnq.com.lnq.model.event_bus_models.EventBusOpenRecipentFragment;
import lnq.com.lnq.utils.ValidUtils;

import static android.app.Activity.RESULT_OK;
import static lnq.com.lnq.fragments.qrcode.FragShareQrCode.RC_TAKE_PICTURE;

public class FragmentOCRImagePath extends Fragment implements View.OnClickListener {
    public enum Direction {VERTICAL, HORIZONTAL}

    FragmentOCRImagePathBinding binding;
    private Bitmap mBitmap;
    private PhotoView imageViewOCR;
    Bitmap bmp;
    String fname, lname, email, phone;
    private AppCompatImageView imageViewBackTopBar, imageViewSearchTopBar, imageViewDropdownContacts;
    CardView topBarLayout;
    TextView textViewHeading;

    public FragmentOCRImagePath() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_o_c_r_image_path, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.GONE);

        topBarLayout = binding.topBarContact.topBarCardView;
        imageViewBackTopBar = topBarLayout.findViewById(R.id.imageViewBackTopBar);
        imageViewSearchTopBar = topBarLayout.findViewById(R.id.imageViewSearchTopBar);
        imageViewDropdownContacts = topBarLayout.findViewById(R.id.imageViewDropdownContacts);
        textViewHeading = topBarLayout.findViewById(R.id.textViewUserNameTopBar);
        textViewHeading.setText(R.string.ocr);
        ValidUtils.textViewGradientColor(textViewHeading);
        imageViewBackTopBar.setVisibility(View.VISIBLE);
        imageViewSearchTopBar.setVisibility(View.GONE);
        imageViewDropdownContacts.setVisibility(View.GONE);
        imageViewBackTopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        imageViewOCR = view.findViewById(R.id.imageViewOCR);
        binding.btnTextGenerate.setOnClickListener(this);

        if (getArguments() != null) {
            byte[] byteArray = getArguments().getByteArray("ocrBitmap");
            bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imageViewOCR.setImageBitmap(bmp);
        }
    }

    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTextGenerate:
                binding.btnTextGenerate.setVisibility(View.INVISIBLE);
                binding.clearTextViewLogin2.setVisibility(View.VISIBLE);
                binding.clearTextViewLogin2.startAnimation();
                if (bmp != null) {
                    runTextRecognition();
                }
                break;
        }
    }

    private void runTextRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText texts) {
                processTextRecognitionResult(texts);
                binding.clearTextViewLogin2.stopAnimation();
                binding.clearTextViewLogin2.revertAnimation();
                binding.btnTextGenerate.setVisibility(View.VISIBLE);
                binding.clearTextViewLogin2.setVisibility(View.GONE);
                if (phone != null && email != null) {
                    EventBus.getDefault().post(new EventBuSetDataRecipentFragment(fname, lname, email, phone));
                }else {
                    ValidUtils.showCustomToast(getContext(), "Contact information not detected properly. Please try again.");
                    getActivity().onBackPressed();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                ValidUtils.showCustomToast(getContext(), "Contact information not detected properly. Please try again.");
                getActivity().onBackPressed();
            }
        });
    }

    private void processTextRecognitionResult(FirebaseVisionText firebaseVisionText) {
        if (firebaseVisionText.getTextBlocks().size() == 0) {
            ValidUtils.showCustomToast(getContext(), "Contact information not detected properly. Please try again.");
            return;
        }
        List<String> ocrDataList = new ArrayList<>();
        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
            //In case you want to extract each line
            for (FirebaseVisionText.Line line : block.getLines()) {
                for (FirebaseVisionText.Element element : line.getElements()) {
                    ocrDataList.add(element.getText());
                }
            }
        }
            for (int i = 0; i < ocrDataList.size(); i++) {
                fname = ocrDataList.get(0);
                lname = ocrDataList.get(1);
                if (ocrDataList.get(i).contains("00")) {
                    phone = ocrDataList.get(1);
                }
                if (ocrDataList.get(i).contains("@")) {
                    email = ocrDataList.get(i);
                }
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).mBind.mBottomBar.setVisibility(View.GONE);
    }
}