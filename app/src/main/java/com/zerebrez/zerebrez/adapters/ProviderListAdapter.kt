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

package com.zerebrez.zerebrez.adapters

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

/**
 * Created by Jorge Zepeda Tinoco on 03/06/18.
 * jorzet.94@gmail.com
 */

class ProviderListAdapter(providers: List<Provider>, context: Context, fragment: ProvidersFragment):
        RecyclerView.Adapter<ViewHolder>(), ViewHolder.OnButtonClickListener {

    private var providers: List<Provider> = providers
    private var mContext: Context = context
    private var mProvidersFragment: ProvidersFragment = fragment


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder != null) {
            if (providers != null && providers.isNotEmpty()) {
                val item = providers.get(position)
                holder.bind(item, mContext)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.providers_card, parent, false),this)
    }

    override fun getItemCount(): Int {
        if (providers != null && providers.isNotEmpty()) {
            return providers.size
        } else {
            return 0
        }
    }

    override fun onButtonClicked(position: Int) {
        val bundle = Bundle()
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