package com.smehsn.compassinsurance.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.smehsn.compassinsurance.R;
import com.smehsn.compassinsurance.dao.Dealer;
import com.smehsn.compassinsurance.parser.FormParser;
import com.smehsn.compassinsurance.parser.FormValidationException;
import com.smehsn.compassinsurance.parser.fragment.FormHostingFragment;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Sam
 */
public class DealerRegistrationFragment extends FormHostingFragment {
    private View rootView;
    private FormParser formParser;
    private Dealer dealer;

    @BindView(R.id.dealerName)
    EditText dealerName;

    @BindView(R.id.dealerEmail)
    EditText dealerEmail;

    @BindView(R.id.dealerFax)
    EditText dealerFax;

    @BindView(R.id.dealerAddress)
    EditText dealerAddress;

    public static DealerRegistrationFragment newInstance(){
        return new DealerRegistrationFragment();
    }

    @OnClick(R.id.saveDealer)
    void saveDealer(){
        try{
            parseForm();
            dealer.setAddress(dealerAddress.getText().toString());
            dealer.setEmail(dealerEmail.getText().toString());
            dealer.setFax(dealerFax.getText().toString());
            dealer.setName(dealerName.getText().toString());
        } catch (FormValidationException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dealer = Dealer.getInstance(getContext());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.config_dealer, container, false);
        ButterKnife.bind(this, rootView);
        formParser = new FormParser(getContext(), rootView);
        return rootView;
    }

    @Override
    public Map<String, String> parseForm() throws FormValidationException {
        formParser.parse();
        return null;
    }

    @Override
    public String getFormTitle() {
        return null;
    }
}
