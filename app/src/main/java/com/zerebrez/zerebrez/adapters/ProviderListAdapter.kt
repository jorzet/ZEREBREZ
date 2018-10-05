package com.zerebrez.zerebrez.adapters

import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.payment.ConfirmOrderFragment
import com.zerebrez.zerebrez.fragments.payment.ProvidersFragment
import com.zerebrez.zerebrez.models.Provider
import java.io.Serializable

//import java.util.List

class ProviderListAdapter(providers: List<Provider>, context: Context, fragment: ProvidersFragment) : RecyclerView.Adapter<ViewHolder>(), ViewHolder.OnButtonClickListener {

    var providers: List<Provider> = providers
    var context: Context = context
    var mProvidersFragment: ProvidersFragment = fragment


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = providers.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.providers_card, parent, false),this)
    }

    override fun getItemCount(): Int {
        return providers.size
    }

    override fun onButtonClicked(position: Int) {
        var bundle: Bundle = Bundle()
        bundle.putSerializable("Provider",providers.get(position) as Serializable)
        mProvidersFragment.ShowConfirmOrderFragment(bundle)
    }
}

class ViewHolder(view: View, clickListener: OnButtonClickListener ) : RecyclerView.ViewHolder(view) {
    val mProviderImageView = view.findViewById(R.id.iv_provider_icon) as ImageView
    val mComisionTextView = view.findViewById(R.id.tv_providers_comision) as TextView
    val clickListener = clickListener
    interface OnButtonClickListener {
        fun onButtonClicked(position: Int)
    }

    fun bind(provider:Provider, context: Context) {
        mComisionTextView.text = "Comisión: $${provider.commission.toFloat()}"
        mProviderImageView.loadUrl(provider.image_small)
        itemView.setOnClickListener(View.OnClickListener { clickListener.onButtonClicked(adapterPosition) })
    }
    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }
}