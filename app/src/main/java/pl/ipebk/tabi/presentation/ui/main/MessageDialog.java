/*
* author: Bartlomiej Kierys
* date: 2017-01-15
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.presentation.ui.main;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
    private View.OnClickListener defaultClickListener = v -> onConfirm();

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

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_message, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        ButterKnife.bind(this, view);

        titleView.setText(title);
        bodyView.setText(body);
        button.setText(buttonText);
        button.setOnClickListener(defaultClickListener);

        return view;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        if(button!=null) {
            button.setOnClickListener(v -> {
                listener.onClick(v);
                onConfirm();
            });
        } else {
            defaultClickListener = v -> {
                listener.onClick(v);
                onConfirm();
            };
        }
    }

    private void onConfirm() {
        this.dismiss();
    }
}
