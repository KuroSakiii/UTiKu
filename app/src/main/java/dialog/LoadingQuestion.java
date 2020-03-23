package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.administrator.aninterface.R;

/**
 * 公共组件--loading对话框
 */
public class LoadingQuestion extends Dialog {
    @SuppressWarnings("unused")
    private Context ctx;
    private TextView mTxtMsg;
    private String mMessage;
    private boolean mCancelable = true;
    private OnCancelListener mOnCancelListener;

    public LoadingQuestion(Context context) {
        super(context, R.style.task_dialog);
        ctx = context;
    }

    public LoadingQuestion(Context context, OnCancelListener onCancelListener) {
        super(context, R.style.task_dialog);
        this.mOnCancelListener = onCancelListener;
        ctx = context;
    }

    public void setCancelable(boolean val) {
        this.mCancelable = val;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mMessage != null) {
            mTxtMsg.setText(mMessage);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (this.mCancelable) {
            if (this.mOnCancelListener != null) {
                this.mOnCancelListener.onDialogCancel();
            }
            this.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_question);
        setCancelable(mCancelable);
        setCanceledOnTouchOutside(false);
        mTxtMsg = findViewById(R.id.load_question);
    }

    public interface OnCancelListener {
        void onDialogCancel();
    }
}
