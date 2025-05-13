package org.gaze.vpc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.material.textfield.TextInputEditText;

import org.gaze.vpc.utils.SharedPreferencesUtils;
import org.gaze.tracker.core.GazeTracker;
import org.gaze.tracker.enumeration.InitializationErrors;
import org.gaze.tracker.ui.BaseActivity;


public class MainActivity extends BaseActivity implements GazeTracker.CalibrationUIExitCallback {

    private TextInputEditText editTextId, editTextAge;
    private AppCompatSpinner spinnerGender, spinnerGroup, spinnerTask;
    private Button buttonStartExperiment;
    private GazeTracker tracker;
    private Switch aSwitch;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化视图组件
        editTextId = findViewById(R.id.text_input_edit_text_id);
        editTextAge = findViewById(R.id.text_input_edit_text_age);
        spinnerGender = findViewById(R.id.spinner_gender);
        spinnerGroup = findViewById(R.id.spinner_group);
        spinnerTask = findViewById(R.id.spinner_task);
        buttonStartExperiment = findViewById(R.id.btn_start_exp);

        aSwitch = findViewById(R.id.debug_status_switch);

        aSwitch.setChecked(SharedPreferencesUtils.getBoolean("enable_debug", true));
        aSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            tracker.setErrorBarVisible(b);
            SharedPreferencesUtils.setBoolean("enable_debug", b);
        });

        ArrayAdapter<CharSequence> genderAdapter =
                ArrayAdapter.createFromResource(this, R.array.gender_array,
                        android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> groupAdapter =
                ArrayAdapter.createFromResource(this, R.array.group_array,
                        android.R.layout.simple_spinner_item);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroup.setAdapter(groupAdapter);

        ArrayAdapter<CharSequence> taskAdapter =
                ArrayAdapter.createFromResource(this, R.array.task_array,
                        R.layout.spinner_dropdown_item);
        groupAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTask.setAdapter(taskAdapter);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                 int position, long id) {
            }

            @Override public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                 int position, long id) {
            }

            @Override public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        buttonStartExperiment.setOnClickListener(v -> {
            // 设置点击事件
            // 获取EditText和Spinner的值
            String id = editTextId.getText().toString().trim(); // 去除空格
            String ageString = editTextAge.getText().toString().trim();
            String gender = spinnerGender.getSelectedItem().toString().trim();
            String group = spinnerGroup.getSelectedItem().toString().trim();
            String task = spinnerTask.getSelectedItem().toString().trim();

            // 检查EditText和Spinner的值是否为空
            if (id.isEmpty() || ageString.isEmpty() || gender.isEmpty() || group.isEmpty()) {
                // 如果有任何一个为空，显示提示消息
                Toast.makeText(MainActivity.this, "仔细检查所有的字段是否都填写了！",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查id是否包含文件路径不允许的字符
            if (!isValidId(id)) {
                Toast.makeText(MainActivity.this, "被试ID只允许存在字母、数字、下划线",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查年龄是否在0-200之间
            try {
                int age = Integer.parseInt(ageString);
                if (age < 0 || age > 200) {
                    // 年龄超出范围，显示提示消息
                    Toast.makeText(MainActivity.this, "年龄必须处于0-200", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            } catch (NumberFormatException e) {
                // 年龄不是有效的整数，显示提示消息
                Toast.makeText(MainActivity.this, "年龄的输入无效，请检查！", Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            SharedPreferencesUtils.setString("user_id", id);
            SharedPreferencesUtils.setString("subject_age", ageString);
            SharedPreferencesUtils.setString("subject_gender",
                    "男".equals(gender) ? "male" : "female");
            SharedPreferencesUtils.setString("subject_group", group);
            SharedPreferencesUtils.setString("task_name", task);

            tracker.setSessionName(id + "_" + task); // for calibration save
            tracker.drawCalibrationUI(this);

        });
    }

    // 验证id是否包含文件路径不允许的字符的方法
    private boolean isValidId(String id) {
        // 此正则表达式允许字母、数字、下划线
        String regex = "^[a-zA-Z0-9_]+$";
        return id.matches(regex);
    }

    @Override public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override public void onPermissionsSuccess() {
        super.onPermissionsSuccess();
        Log.i(TAG, "Tracker init");
        initGazeTracker();
    }

    private void initGazeTracker() {
        GazeTracker.create(this, (gazeTracker, initializationErrors) -> {
            if (initializationErrors == InitializationErrors.NONE) {
                tracker = gazeTracker;
            }
        });
    }

    @Override public void onExitUI() {
        Intent intent = new Intent(MyApp.getInstance(), ExperimentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApp.getInstance().startActivity(intent);
    }
}