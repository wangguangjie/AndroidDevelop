package org.wangguangjie.crime.controler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import org.wangguangjie.crime.utils.PictureUtils;
import org.wangguangjie.headline.R;

import java.io.File;

/**
 * Created by wangguangjie on 2017/10/27.
 */

public class CrimePhotoDialogFragment extends DialogFragment {

    private static final String ARG_FILE="photo_file";

    public static CrimePhotoDialogFragment creatNewInstance(File file){
        CrimePhotoDialogFragment photoFragment=new CrimePhotoDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putString(ARG_FILE,file.getPath());
        photoFragment.setArguments(bundle);
        return photoFragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle onSaveInstanceState){
        Bundle bundle=getArguments();
        String filePath=bundle.getString(ARG_FILE);
        Bitmap bitmap= PictureUtils.getScaledBitmap(filePath,getActivity());
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.crime_dialog_image,null);
        ImageView imageView=(ImageView)view.findViewById(R.id.crime_dialog_picture);
        imageView.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity()).
                setView(imageView).setTitle("预览")
                .setPositiveButton(R.string.crime_button_positive,null).create();
    }
}
