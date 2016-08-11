package org.shingo.shingoeventsapp.ui;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.data.GetAsyncData;
import org.shingo.shingoeventsapp.data.OnTaskCompleteListener;
import org.shingo.shingoeventsapp.ui.interfaces.NavigationInterface;
import org.shingo.shingoeventsapp.ui.interfaces.OnErrorListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFragment extends Fragment implements OnTaskCompleteListener {

    private static final String EMAIL_PARAM = "email";
    private static final String DESC_PARAM = "description";
    private static final String DEVICE_PARAM = "device";
    private static final String DETAIL_PARAM = "details";
    private static final String RATING_PARAM = "rating";

    private EditText mEmailView;
    private EditText mDescriptionView;
    private RatingBar mRatingBar;
    private ProgressDialog progress;

    private OnErrorListener mErrorListener;
    private NavigationInterface mNavigate;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BugFragment.
     */
    public static FeedbackFragment newInstance() {
        return new FeedbackFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        view.findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigate.navigateToId(R.id.nav_home);
            }
        });

        view.findViewById(R.id.action_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitFeedback();
            }
        });

        mEmailView = (EditText) view.findViewById(R.id.email);
        mDescriptionView = (EditText) view.findViewById(R.id.comment);
        mDescriptionView.setSingleLine(true);
        mDescriptionView.setLines(10);
        mDescriptionView.setHorizontallyScrolling(false);
        mDescriptionView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == R.id.submit || i == EditorInfo.IME_ACTION_SEND){
                    submitFeedback();
                    return true;
                }

                return false;
            }
        });
        mRatingBar = (RatingBar) view.findViewById(R.id.rating);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationInterface)
            mNavigate = (NavigationInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement NavigationInterface");

        if (context instanceof OnErrorListener)
            mErrorListener = (OnErrorListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnErrorListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mNavigate = null;
    }

    private boolean isEmailValid(String email) {
        return TextUtils.isEmpty(email) || email.contains("@");
    }

    private void submitFeedback(){
        closeKeyboard();
        String email = mEmailView.getText().toString();
        String description = mDescriptionView.getText().toString();
        String rating = String.valueOf(mRatingBar.getRating());

        boolean cancel = false;
        View focusView = null;
        // Check for a valid email address.
        if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(description)){
            mDescriptionView.setError(getString(R.string.error_field_required));
            focusView = mDescriptionView;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        } else {
            GetAsyncData createFeedbackAsync = new GetAsyncData(this);
            String postData = EMAIL_PARAM + "=" + email;
            postData += "&" + DESC_PARAM + "=" + description;
            postData += "&" + DEVICE_PARAM + "=" + Build.DEVICE;
            postData += "&" + DETAIL_PARAM + "=" + getDetails();
            postData += "&" + RATING_PARAM + "=" + rating;
            createFeedbackAsync.execute("/support/feedback", "", postData);
            progress = ProgressDialog.show(getContext(), "Thanks for the feedback!", "Submitting...");
        }
    }

    private String getDetails() {
        return "OS: " + System.getProperty("os.version") + "\n" +
                "API: " + Build.VERSION.SDK_INT + "\n" +
                "Device: " + Build.DEVICE + "\n" +
                "Model: " + Build.MANUFACTURER + " - " + Build.MODEL + "\n" +
                "Product: " + Build.PRODUCT;
    }

    @Override
    public void onTaskComplete(String response) {
        try {
            JSONObject result = new JSONObject(response);
            if(result.getBoolean("success")){
                progress.dismiss();
                Snackbar.make(getView(), "Feedback submitted", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Done", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mNavigate.navigateToId(R.id.nav_home);
                            }
                        }).show();
            } else {
                throw new JSONException("Request was unsuccessful");
            }
        } catch (Exception e) {
            e.printStackTrace();
            onTaskError("");
        }
    }

    @Override
    public void onTaskError(String error) {
        progress.dismiss();
        if(mErrorListener != null)
            mErrorListener.handleError("Well this is embarrassing... We had trouble submitting your feedback. Please let us know at shingo.development@usu.edu!");
    }

    private void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
