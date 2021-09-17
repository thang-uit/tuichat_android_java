package com.thanguit.tuichat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.thanguit.tuichat.R;
import com.thanguit.tuichat.activities.MainActivity;
import com.thanguit.tuichat.database.DataLocalManager;
import com.thanguit.tuichat.databinding.FragmentSettingBinding;
import com.thanguit.tuichat.databinding.LayoutBottomSheetSettingLanguageBinding;
import com.thanguit.tuichat.utils.SettingLanguage;

public class SettingFragment extends Fragment {
    private FragmentSettingBinding fragmentSettingBinding;
    private BottomSheetDialog bottomSheetDialog;

    private SettingLanguage settingLanguage;
    private final Handler handler = new Handler();

    private final String VIETNAMESE = "vi";
    private final String ENGLISH = "en";

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

        DataLocalManager.init(getContext());
        settingLanguage = SettingLanguage.getInstance();

        initializeViews();
        listeners();
    }

    @Override
    public void onResume() {
        super.onResume();
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

        if (DataLocalManager.getLanguage().equals(VIETNAMESE)) {
            fragmentSettingBinding.tvSettingLanguage.setText(getResources().getString(R.string.tvVietnamFlag));
        } else {
            fragmentSettingBinding.tvSettingLanguage.setText(getResources().getString(R.string.tvEnglandFlag));
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
    }

    private void openLanguageDialog() {
        bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.layout_bottom_sheet_setting_language, null);
        LayoutBottomSheetSettingLanguageBinding binding = LayoutBottomSheetSettingLanguageBinding.bind(view);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);

        binding.tvVietnamFlag.setSelected(true);
        binding.tvEnglandFlag.setSelected(true);

        if (DataLocalManager.getLanguage().equals(VIETNAMESE)) {
            binding.ivVietnamChecked.setVisibility(View.VISIBLE);
            binding.ivEnglandChecked.setVisibility(View.GONE);
        } else {
            binding.ivEnglandChecked.setVisibility(View.VISIBLE);
            binding.ivVietnamChecked.setVisibility(View.GONE);
        }

        binding.rlVietnamFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingLanguage.changeLanguage(getContext(), VIETNAMESE);
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        binding.rlEnglandFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingLanguage.changeLanguage(getContext(), ENGLISH);
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        bottomSheetDialog.show();
    }
}