package com.ashok.skycoreassigment.ui.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.ashok.skycoreassigment.databinding.FragmentHomeBinding
import com.ashok.skycoreassigment.ui.adapters.NearbyRestaurentsAdapter
import com.ashok.skycoreassigment.ui.viewmodel.HomeViewModel
import com.ashok.skycoreassigment.utils.LayoutManagerUtil
import com.google.android.gms.location.*

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!

     lateinit var nearbyRestaurentsAdapter: NearbyRestaurentsAdapter
    private val homeViewModel: HomeViewModel by activityViewModels()




    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    lateinit var loacationRequest: LocationRequest

    lateinit var locationCallback: LocationCallback

    lateinit var location: Location

    val REQUEST_CODE = 1000

    private lateinit var latitude : String
    private lateinit var longitude : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nearbyRestaurentsAdapter = NearbyRestaurentsAdapter()

        setupRecyclerViews()
        observeViewModels()
        binding.swipeRefreshLayout.setOnRefreshListener {
            getNearByRestaurentsData()
        }

        binding.btnSubmitLocation.setOnClickListener {
            getNearByRestaurentsData()
        }

        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission
                .ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION
            ),REQUEST_CODE)
        else
        {
            buildLocationRequest()
            buildLocationCallback()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            if(ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission
                    .ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED   &&   ActivityCompat
                    .checkSelfPermission(requireContext(),android.Manifest.permission
                        .ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED )
            {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission
                    .ACCESS_FINE_LOCATION),REQUEST_CODE)
            }
            fusedLocationProviderClient.requestLocationUpdates(loacationRequest,locationCallback, Looper.myLooper())
        }


        binding.seekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {

                binding.txtSeekProgress.text = seek.progress.toString()

            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                    getNearByRestaurentsData()
            }
        })

    }
    private fun setupRecyclerViews() {
        binding.apply {
            recyclerRestaurents.layoutManager =
                LayoutManagerUtil.getVerticalLayoutManager(requireContext())

            recyclerRestaurents.adapter = nearbyRestaurentsAdapter
        }

    }

    private fun observeViewModels()
    {
        binding.swipeRefreshLayout.isRefreshing = true

        nearbyRestaurentsAdapter.addLoadStateListener { loadState ->

            if (loadState.refresh is LoadState.Loading) {

                binding.swipeRefreshLayout.isRefreshing = true
            }
            else {
                binding.swipeRefreshLayout.isRefreshing = false
                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> {
                        loadState.refresh as LoadState.Error
                    }
                    else -> null
                }
                errorState?.let {
                }
            }
        }



    }

    override fun onResume() {
        super.onResume()


        buildLocationRequest()
        buildLocationCallback()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if(ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission
                .ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED   &&   ActivityCompat
                .checkSelfPermission(requireContext(),android.Manifest.permission
                    .ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission
                .ACCESS_FINE_LOCATION),REQUEST_CODE)
        }
        fusedLocationProviderClient.requestLocationUpdates(loacationRequest,locationCallback, Looper.myLooper())

    }

    @SuppressLint("RestrictedApi")
    private fun buildLocationRequest() {

        loacationRequest = LocationRequest()
        loacationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        loacationRequest.interval = 5000
        loacationRequest.fastestInterval=3000
        loacationRequest.smallestDisplacement = 10f
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode)
        {
            REQUEST_CODE->{
                if (grantResults.size > 0)
                {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(requireContext(),"Permission Granted" , Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(requireContext(),"Permission Denied" , Toast.LENGTH_SHORT).show()

                }
            }
        }
    }


    private fun buildLocationCallback() {

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult?) {
                location = p0!!.locations.get(p0!!.locations.size - 1) //Get last Location
                Log.i("lattitude", location.latitude.toString() + "  " + location.longitude.toString())

                latitude = location.latitude.toString()
                longitude = location.longitude.toString()

                getNearByRestaurentsData()

            }
        }
    }

    fun getNearByRestaurentsData()
    {
        lifecycleScope.launch {
            homeViewModel.getDataRestaurents(latitude, longitude,
                15,0,binding.seekBar.progress,binding.edtLocation.text.toString()).collectLatest {
                nearbyRestaurentsAdapter.submitData(it)
            }
        }
    }

}