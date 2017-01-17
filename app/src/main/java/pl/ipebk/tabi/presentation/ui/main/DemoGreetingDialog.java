/*
* author: Bartlomiej Kierys
* date: 2017-01-15
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;

/**
 * TODO: Generic description. Replace with real one.
 */
public class DemoGreetingDialog extends DialogFragment {
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_demo_greeting, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.btn_demo_confirm)
    public void onConfirm() {
        this.dismiss();
    }
}
