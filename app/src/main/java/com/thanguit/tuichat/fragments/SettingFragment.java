package com.thanguit.tuichat.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.database.DataLocalManager;
import com.thanguit.tuichat.databinding.FragmentSettingBinding;
import com.thanguit.tuichat.databinding.LayoutBottomSheetSettingLanguageBinding;
import com.thanguit.tuichat.utils.MyToast;

public class SettingFragment extends Fragment {
    private FragmentSettingBinding fragmentSettingBinding;
    private BottomSheetDialog bottomSheetDialog;

    private FirebaseAuth firebaseAuth;

    private final Handler handler = new Handler();

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
        DataLocalManager.init(getContext());

        initializeViews();
        listeners();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (DataLocalManager.getTheme()) {
            fragmentSettingBinding.btnSwitchTheme.setMinAndMaxProgress(0.5f, 1.0f); // Tối
        } else {
            fragmentSettingBinding.btnSwitchTheme.setMinAndMaxProgress(0.1f, 0.5f); // Sáng
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentSettingBinding = null;
    }

    private void initializeViews() {
        if (DataLocalManager.getTheme()) {
            fragmentSettingBinding.btnSwitchTheme.setMinAndMaxProgress(0.5f, 1.0f); // Tối
        } else {
            fragmentSettingBinding.btnSwitchTheme.setMinAndMaxProgress(0.1f, 0.5f); // Sáng
        }
    }

    private void listeners() {
        fragmentSettingBinding.btnSwitchTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheme();
            }
        });

        fragmentSettingBinding.rlSettingTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheme();
            }
        });

        fragmentSettingBinding.rlSettingLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLanguageDialog();
            }
        });
    }

    private void setTheme() {
        if (DataLocalManager.getTheme()) {
            DataLocalManager.setTheme(false);
            fragmentSettingBinding.btnSwitchTheme.setClickable(false);
            fragmentSettingBinding.rlSettingTheme.setClickable(false);
            fragmentSettingBinding.btnSwitchTheme.setMinAndMaxProgress(0.65f, 1.0f); // Sáng
            fragmentSettingBinding.btnSwitchTheme.playAnimation();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    fragmentSettingBinding.btnSwitchTheme.setClickable(true);
                    fragmentSettingBinding.rlSettingTheme.setClickable(true);
                }
            }, 1800);
        } else {
            DataLocalManager.setTheme(true);
            fragmentSettingBinding.btnSwitchTheme.setClickable(false);
            fragmentSettingBinding.rlSettingTheme.setClickable(false);
            fragmentSettingBinding.btnSwitchTheme.setMinAndMaxProgress(0.1f, 0.5f); // Tối
            fragmentSettingBinding.btnSwitchTheme.playAnimation();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    fragmentSettingBinding.btnSwitchTheme.setClickable(true);
                    fragmentSettingBinding.rlSettingTheme.setClickable(true);
                }
            }, 1800);
        }
        MyToast.makeText(getContext(), MyToast.INFORMATION, "Theme" + DataLocalManager.getTheme(), MyToast.SHORT).show();
    }

    private void openLanguageDialog() {
        bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.layout_bottom_sheet_setting_language, null);
        LayoutBottomSheetSettingLanguageBinding binding = LayoutBottomSheetSettingLanguageBinding.bind(view);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);

        binding.tvVietnamFlag.setSelected(true);
        binding.tvEnglandFlag.setSelected(true);

        bottomSheetDialog.show();
    }
}