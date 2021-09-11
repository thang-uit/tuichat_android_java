package com.thanguit.tuichat.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.databinding.FragmentSettingBinding;
import com.thanguit.tuichat.databinding.LayoutBottomSheetSettingLanguageBinding;

import java.util.Objects;

public class SettingFragment extends Fragment {
    private FragmentSettingBinding fragmentSettingBinding;
    private BottomSheetDialog bottomSheetDialog;

    private FirebaseAuth firebaseAuth;

    public SettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentSettingBinding = FragmentSettingBinding.inflate(inflater, container, false);
        return fragmentSettingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        initializeViews();
        listeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentSettingBinding = null;
    }

    private void initializeViews() {
    }

    private void listeners() {
        fragmentSettingBinding.btnSwitchTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fragmentSettingBinding.rlSettingLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLanguageDialog();
            }
        });
    }

    private void openLanguageDialog() {
        bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.layout_bottom_sheet_setting_language, null);
        LayoutBottomSheetSettingLanguageBinding binding = LayoutBottomSheetSettingLanguageBinding.bind(view);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
        }

        binding.tvVietnamFlag.setSelected(true);
        binding.tvEnglandFlag.setSelected(true);

        bottomSheetDialog.show();
    }
}