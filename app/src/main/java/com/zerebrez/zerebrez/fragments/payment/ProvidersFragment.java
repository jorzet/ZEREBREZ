/*
 * Copyright [2019] [Jorge Zepeda Tinoco]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zerebrez.zerebrez.fragments.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.zerebrez.zerebrez.R;
import com.zerebrez.zerebrez.adapters.ProviderListAdapter;
import com.zerebrez.zerebrez.fragments.content.BaseContentDialogFragment;
import com.zerebrez.zerebrez.models.Provider;
import com.zerebrez.zerebrez.models.enums.DialogType;
import com.zerebrez.zerebrez.services.compropago.ComproPagoManager;
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog;

import java.util.List;
import retrofit2.Response;

/**
 * Created by Jesus Campos on 05/09/18.
 * jcampos.jc38@gmail.com
 */

public class ProvidersFragment extends BaseContentDialogFragment implements ErrorDialog.OnErrorDialogListener {
    private static final String TAG = "ProvidersFragment";
    private static final String DIALOG_TAG = "dialog";

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingView;
    private RelativeLayout mCloseContainer;

    private ComproPagoManager mComproPagoManager;
    private ProviderListAdapter mProviderAdapter;

    private ConfirmOrderFragment mConfirmOrderFragment=null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.providers_fragment, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.rv_providers_list);
        mLoadingView = (ProgressBar) root.findViewById(R.id.pb_providers);
        mCloseContainer = (RelativeLayout) root.findViewById(R.id.rl_close_providers);

        mCloseContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        setWaitScreen(true);

        mComproPagoManager = new ComproPagoManager();
        getProvidersList();
        return root;
    }


    private void setWaitScreen(boolean set) {
        mRecyclerView.setVisibility(set ? View.GONE : View.VISIBLE);
        mLoadingView.setVisibility(set ? View.VISIBLE : View.GONE);
    }

    private void getProvidersList(){
        mComproPagoManager.ListProviders(new ComproPagoManager.OnListProvidersListener(){
            @Override
            public void onListProvidersResponse(Response<List<Provider>> response) {
                onListProvidersSuccess(response);
            }

            @Override
            public void onListProvidersFailure(Throwable throwable) {
                onListProvidersError(throwable);
            }
        });
    }

    void onListProvidersSuccess(Response<List<Provider>> response){
        if(response!=null){
            if(response.code()<300 && response.code()>199){
                if(response.body()!=null)
                    setmRecyclerView(response.body());
                else
                    SendFailedMessage();
            }else
                SendFailedMessage();
        }else
            SendFailedMessage();
    }

    void onListProvidersError(Throwable throwable){
        Log.e(TAG,"GetProvidersFailure: "+throwable.getLocalizedMessage());
        SendFailedMessage();
    }

    private void setmRecyclerView(List<Provider> providers){
        if (mRecyclerView != null && getActivity() != null) {
            mProviderAdapter = new ProviderListAdapter(providers, getActivity(), this);
            if (mRecyclerView.getAdapter() == null) {
                mRecyclerView.setAdapter(mProviderAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                setWaitScreen(false);
            }
        }
    }

    public void ShowConfirmOrderFragment(Bundle bundle){

        if (mConfirmOrderFragment==null) {
            mConfirmOrderFragment = new  ConfirmOrderFragment();
        }
        mConfirmOrderFragment.setArguments(bundle);
        if (!isConfirmOrderFragmentShown()) {
            mConfirmOrderFragment.show(getFragmentManager(),DIALOG_TAG);
        }
    }

    private boolean isConfirmOrderFragmentShown() {
        return mConfirmOrderFragment != null && mConfirmOrderFragment.isVisible();
    }

    private void SendFailedMessage(){
        ErrorDialog.Companion.newInstance("Algo salió mal...", "Asegurate de tener una conexión a internet.",
                DialogType.OK_DIALOG, this).show(getFragmentManager(), "networkError");
    }

    @Override
    public void onConfirmationCancel() {

    }

    @Override
    public void onConfirmationNeutral() {
        getActivity().onBackPressed();
    }

    @Override
    public void onConfirmationAccept() {

    }
}
