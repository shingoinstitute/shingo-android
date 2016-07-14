package org.shingo.shingoapp.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.shingo.shingoapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModelFragment extends Fragment {

    public ModelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ModelFragment.
     */
    public static ModelFragment newInstance() {
        return new ModelFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_model, container, false);
        getActivity().setTitle("Shingo Model");
        TouchImageView img = (TouchImageView)view.findViewById(R.id.model);
        img.setImageResource(R.drawable.model);
        return view;
    }

}
