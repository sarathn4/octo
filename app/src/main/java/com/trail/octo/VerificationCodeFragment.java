package com.trail.octo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class VerificationCodeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaction_frag_verification_code, null);
        LinearLayout linearLayout_pt = (LinearLayout) view.findViewById(R.id.fragment_vc_pt);
        LinearLayout linearLayout_uts = (LinearLayout) view.findViewById(R.id.fragment_vc_uts);
        LinearLayout linearLayout_st = (LinearLayout) view.findViewById(R.id.fragment_vc_st);

        linearLayout_pt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_code_pt = new Intent(getActivity(), ListOfTickets.class);
                intent_code_pt.putExtra("validity", "verification_code");
                intent_code_pt.putExtra("type", "Platform");
                startActivity(intent_code_pt);
            }
        });
        linearLayout_uts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_code_uts = new Intent(getActivity(),ListOfTickets.class);
                intent_code_uts.putExtra("validity","verification_code");
                intent_code_uts.putExtra("type","Unreserved Ticket");
                startActivity(intent_code_uts);
            }
        });
        linearLayout_st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_code_st = new Intent(getActivity(),ListOfTickets.class);
                intent_code_st.putExtra("validity","verification_code");
                intent_code_st.putExtra("type","MonthlyPass");
                startActivity(intent_code_st);
            }
        });
        return view;
    }
}
