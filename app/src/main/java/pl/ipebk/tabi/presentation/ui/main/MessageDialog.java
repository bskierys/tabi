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
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.ipebk.tabi.R;
import pl.ipebk.tabi.presentation.ui.custom.SecondaryButton;

/**
 * Instance of {@link DialogFragment} that presents simple message
 */
public class MessageDialog extends DialogFragment {
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_BODY = "arg_body";
    private static final String ARG_BUTTON_TEXT = "arg_button_text";

    @BindView(R.id.txt_title) TextView titleView;
    @BindView(R.id.txt_body) TextView bodyView;
    @BindView(R.id.btn_confirm) SecondaryButton button;

    private String title;
    private String body;
    private String buttonText;

    public static MessageDialog newInstance(String title, String body, String buttonText) {
        MessageDialog dialog = new MessageDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_BODY, body);
        args.putString(ARG_BUTTON_TEXT, buttonText);
        dialog.setArguments(args);
        return dialog;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            body = getArguments().getString(ARG_BODY);
            buttonText = getArguments().getString(ARG_BUTTON_TEXT);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_message, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        ButterKnife.bind(this, v);

        titleView.setText(title);
        bodyView.setText(body);
        button.setText(buttonText);

        return v;
    }

    @OnClick(R.id.btn_confirm) public void onConfirm() {
        this.dismiss();
    }
}
