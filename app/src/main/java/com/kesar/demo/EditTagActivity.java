package com.kesar.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.kesar.demo.domain.Tag;

import org.kesar.lazy.lazydb.LazyDB;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 编辑tag
 * Created by kesar on 2016/10/28 0028.
 */
public class EditTagActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 2;
    public static final String Extra_Position = "mPosition";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.etContent)
    EditText mEtContent;

    private LazyDB mLazyDB;
    private Tag mTag;
    private int mPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tag);
        ButterKnife.bind(this);

        mLazyDB = LazyDBFactory.createDB(getApplicationContext());
        mTag = (Tag) getIntent().getSerializableExtra(Tag.class.getName());
        mPosition = getIntent().getIntExtra(Extra_Position, 0);
        initView();
    }

    private void initView() {
        // mToolbar
        setSupportActionBar(mToolbar);
        mEtContent.setText(mTag.getText());
    }

    @OnClick({R.id.tvCancel, R.id.tvFinish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCancel:
                onBackPressed();
                break;
            case R.id.tvFinish:
                try {
                    String text = mEtContent.getText().toString();
                    mTag.setText(text);
                    mLazyDB.update(mTag);
                    Intent data = new Intent();
                    data.putExtra(Extra_Position, mPosition);
                    data.putExtra(Tag.class.getName(), mTag);
                    setResult(RESULT_OK, data);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}