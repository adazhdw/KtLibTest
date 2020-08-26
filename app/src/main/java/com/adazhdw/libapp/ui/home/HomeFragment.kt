package com.adazhdw.libapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adazhdw.ktlib.ext.logD
import com.adazhdw.ktlib.http.kthttp.KParams
import com.adazhdw.ktlib.http.kthttp.khttp
import com.adazhdw.libapp.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView: TextView = view.findViewById(R.id.text_home)
        khttp.get<String>(
            url = "https://wanandroid.com/wxarticle/list/408/1/json",
            param = KParams.Builder().setHeaders(mapOf("k" to "Android")).build(),
            onSuccess = { result ->
                result.logD("HomeFragment")
                textView.text = result
            }
        )
    }
}
