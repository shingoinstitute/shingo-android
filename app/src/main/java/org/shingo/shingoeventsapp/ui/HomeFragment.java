package org.shingo.shingoeventsapp.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.ui.interfaces.NavigationInterface;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private NavigationInterface mNavigate;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Shingo App");
        ((MainActivity)getActivity()).toggleNavHeader(0);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        view.findViewById(R.id.events).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigate.navigateToId(R.id.nav_events);
            }
        });
        view.findViewById(R.id.model).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigate.navigateToId(R.id.nav_model);
            }
        });
        view.findViewById(R.id.affiliates).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigate.navigateToId(R.id.nav_affiliates);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationInterface)
            mNavigate = (NavigationInterface) context;
        else
            throw new RuntimeException(context.toString() + " must implement NavigationInterface");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mNavigate = null;
    }

}
