package com.jntele.troy.jntelelte;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by lenovo on 2018/5/27.
 */

public class InfoDialogFragment extends DialogFragment {

    private DialogInterface.OnClickListener infoCallback;

    private String title;

    private String message;

    private String hint;

    public void show(String title, String message, String hint, DialogInterface.OnClickListener infoCallback,
                     FragmentManager fragmentManager) {
        this.title = title;
        this.message = message;
        this.hint = hint;
        this.infoCallback = infoCallback;
        show(fragmentManager, "InfoDialogFragment");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(hint, infoCallback);
        return builder.create();
    }
}
