package com.sun.chat_04.ui.discovery

import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sun.chat_04.R
import com.sun.chat_04.data.model.User
import com.sun.chat_04.data.remote.UserRemoteDataSource
import com.sun.chat_04.data.repositories.UserRepository
import com.sun.chat_04.ui.frienddetail.FriendDetailFragment
import com.sun.chat_04.util.Constants
import com.sun.chat_04.util.Global
import kotlinx.android.synthetic.main.fragment_discovery.groupAroundHere
import kotlinx.android.synthetic.main.fragment_discovery.imageAroundHere
import kotlinx.android.synthetic.main.fragment_discovery.progressLoading
import kotlinx.android.synthetic.main.fragment_discovery.recyclerDiscovery
import kotlinx.android.synthetic.main.fragment_discovery.searchDiscovery

class DiscoveryFragment : Fragment(), DiscoveryContract.View {

    private lateinit var presenter: DiscoveryContract.Presenter
    private var users = ArrayList<User>()
    private lateinit var adapter: DiscoveryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discovery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initComponents()
        initPresenter()
        handleFindUserByName()
        handleSearchAroundHere()
    }

    override fun onFindUsersSuccess(users: List<User>) {
        ::adapter.isInitialized.let {
            adapter.refreshUsers(users)
        }
        hideProgress()
        hideIconSearchAroundHere()
    }

    override fun onGetInfoUserSuccess(user: User) {
        val locationUser = Location("")
        locationUser.latitude = user.lat
        locationUser.longitude = user.lgn
        ::presenter.isInitialized.let { presenter.findUserAroundHere(locationUser) }
    }

    override fun onFindUserFailure(exception: Exception?) {
        hideProgress()
        Global.showMessage(context, resources.getString(R.string.user_not_found))
    }

    private fun initComponents() {
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.dp_4)
        adapter = DiscoveryAdapter(users) { user -> userClickListener(user) }
        recyclerDiscovery.layoutManager = GridLayoutManager(context, Constants.COLUMN)
        recyclerDiscovery.addItemDecoration(SpacesItemDecoration(spacingInPixels))
        ::adapter.isInitialized.let {
            recyclerDiscovery.adapter = adapter
        }
    }

    private fun initPresenter() {
        presenter =
            DiscoveryPresenter(
                this,
                UserRepository(
                    UserRemoteDataSource(
                        Global.firebaseAuth,
                        Global.firebaseDatabase,
                        Global.firebaseStorage
                    )
                )
            )
    }

    private fun handleFindUserByName() {
        searchDiscovery.setIconifiedByDefault(false)
        searchDiscovery.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(string: String?): Boolean {
                showProgress()
                hideIconSearchAroundHere()
                string?.let { ::presenter.isInitialized.let { presenter.findUserByName(string) } }
                return true
            }

            override fun onQueryTextChange(string: String?): Boolean {
                return true
            }
        })
    }

    private fun handleSearchAroundHere() {
        imageAroundHere.setOnClickListener {
            checkPermissions()
        }
    }

    private fun userClickListener(user: User) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(R.id.parentLayout, FriendDetailFragment.newInstance(user))
            ?.addToBackStack("")
            ?.commit()
    }

    private fun checkPermissions() {
        context?.let {
            if (Global.checkGrantedPermission(it, Constants.INDEX_PERMISSION_ACCESS_COARSE_LOCATION) &&
                Global.checkGrantedPermission(it, Constants.INDEX_PERMISSION_ACCESS_FINE_LOCATION)
            ) {
                hideIconSearchAroundHere()
                showProgress()
                ::presenter.isInitialized.let { presenter.getUserInfo() }
            } else {
                activity?.let {
                    ActivityCompat.requestPermissions(it, Global.PERMISSIONS, Constants.REQUEST_PERMISSION_CODE)
                }
            }
        }
    }

    private fun showProgress() {
        progressLoading?.let {
            progressLoading.visibility = View.VISIBLE
        }
    }

    private fun hideProgress() {
        progressLoading?.let {
            progressLoading.visibility = View.GONE
        }
    }

    private fun showIconSearchAroundHere() {
        groupAroundHere?.let {
            groupAroundHere.visibility = View.VISIBLE
        }
    }

    private fun hideIconSearchAroundHere() {
        groupAroundHere?.let {
            groupAroundHere.visibility = View.GONE
        }
    }
}
