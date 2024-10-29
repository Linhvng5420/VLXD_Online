package com.tdc.vlxdonline.Activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.tdc.vlxdonline.R;

public class InputFragment extends Fragment {
    private static final String ARG_LABEL = "label";
    private static final String ARG_HINT = "hint";
    private static final String ARG_IS_PASSWORD = "isPassword";

    public static InputFragment newInstance(String label, String hint, boolean isPassword) {
        InputFragment fragment = new InputFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LABEL, label);
        args.putString(ARG_HINT, hint);
        args.putBoolean(ARG_IS_PASSWORD, isPassword);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, container, false);

        TextView tvLabel = view.findViewById(R.id.tv_label);
        EditText edtInput = view.findViewById(R.id.edt_input);
        boolean isPassword = getArguments().getBoolean(ARG_IS_PASSWORD);

        // Nhận và hiển thị giá trị label
        if (getArguments() != null) {
            String label = getArguments().getString(ARG_LABEL);
            String hint = getArguments().getString(ARG_HINT);
            tvLabel.setText(label);
            edtInput.setHint(hint);
            if (isPassword) {
                edtInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                edtInput.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        }

        return view;
    }

    public EditText getEditText() {
        if (getView() != null) {
            return getView().findViewById(R.id.edt_input);
        }
        return null;
    }

    public void showPassword(boolean show) {
        if (getView() != null) {
            EditText edtInput = getView().findViewById(R.id.edt_input);
            if (show) {
                edtInput.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                edtInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // Đặt lại con trỏ để giữ vị trí của nó
            edtInput.setSelection(edtInput.getText().length());
        }
    }
}
