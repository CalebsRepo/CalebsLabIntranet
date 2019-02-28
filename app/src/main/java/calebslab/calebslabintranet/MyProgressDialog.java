package calebslab.calebslabintranet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyProgressDialog extends Dialog {

    public static MyProgressDialog show(Context context, CharSequence title,
                                        CharSequence message) {
        return show(context, title, message, false);
    }

    public static MyProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static MyProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static MyProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable, OnCancelListener cancelListener) {
        MyProgressDialog dialog = new MyProgressDialog(context);
        dialog.setTitle(title);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);

        /* The next line will add the ProgressBar to the dialog. */

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(new ProgressBar(context));

        TextView textView1 = new TextView(context);
        TextView textView2 = new TextView(context);

        textView1.setText("CalebsLab Intranet");
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        textView1.setGravity(Gravity.CENTER_HORIZONTAL);
        textView1.setTextColor(context.getResources().getColor(R.color.colorGaleb));

        textView2.setText("일하러갈렙");
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        textView2.setTextColor(context.getResources().getColor(R.color.colorGaleb));
        textView2.setGravity(Gravity.CENTER_HORIZONTAL);
        textView2.setTypeface(textView2.getTypeface(), Typeface.BOLD);

        linearLayout.addView(textView1);
        linearLayout.addView(textView2);

        dialog.addContentView(linearLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();

        return dialog;
    }

    public MyProgressDialog(Context context) {
        super(context, R.style.NewDialog);
    }
}
