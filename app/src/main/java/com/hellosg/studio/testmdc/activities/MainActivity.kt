package com.hellosg.studio.testmdc.activities

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.hellosg.studio.testmdc.GlideApp
import com.hellosg.studio.testmdc.GlobalState
import com.hellosg.studio.testmdc.R
import com.hellosg.studio.testmdc.adapters.AddedDeviceAdapter
import com.hellosg.studio.testmdc.configs.Configs
import com.hellosg.studio.testmdc.fragments.AddDeviceBottomSheet
import com.hellosg.studio.testmdc.fragments.UserBottomSheet
import com.hellosg.studio.testmdc.models.Device
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity(), UserBottomSheet.OnClickListener {

    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var account: GoogleSignInAccount? = null
    private var hubId = ""
    private lateinit var adapter: AddedDeviceAdapter
    private var mDevices = arrayListOf<Device>()
    private lateinit var queue: RequestQueue
    private lateinit var loadDeviceRequest: JsonObjectRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }

        setupGoogleAccount()
        hubId = getSharedPreferences(Configs.key_pref, Context.MODE_PRIVATE)
                .getString(Configs.key_pref_device_id, "no_device")!!
        addEvents()
        queue = Volley.newRequestQueue(this)

        setSupportActionBar(bottom_app_bar)
        fab_add.setOnClickListener {
            val bottomSheet = AddDeviceBottomSheet()
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }
    }

    override fun onResume() {
        super.onResume()

        reloadDeviceList()
    }

    private fun reloadDeviceList() {
        loadDeviceRequest = JsonObjectRequest(Request.Method.GET, "${Configs.GET_DEVICES_URL}/$hubId", null,
                { response ->
                    pb_loading.visibility = View.GONE
                    val devicesArray = response.getJSONArray("devices")
                    val newDeviceList = arrayListOf<Device>()
                    for (i in 0..(devicesArray.length()-1)) {
                        val deviceObject = devicesArray.getJSONObject(i)
                        val name = deviceObject.getString("name")
                        val type = deviceObject.getInt("type")
                        val buttonMap = hashMapOf<String, String>()
                        val buttonArray = deviceObject.getJSONArray("buttons")
                        for (j in 0..(buttonArray.length() - 1)){
                            val buttonObject = buttonArray.getJSONObject(j)
                            buttonMap.put(buttonObject.getString("tag"), buttonObject.getString("code"))
                        }
                        newDeviceList.add(Device(name, type, buttonMap))
                        (applicationContext as GlobalState).currentDevicesList = newDeviceList

                        adapter.updateData(newDeviceList)
                    }
                    if (newDeviceList.size == 0) {
                        layout_empty.visibility = View.VISIBLE
                        layout_content.visibility = View.INVISIBLE
                    } else {
                        layout_empty.visibility = View.INVISIBLE
                        layout_content.visibility = View.VISIBLE
                    }
                },
                { error ->
                    pb_loading.visibility = View.GONE
                    Toast.makeText(this@MainActivity, R.string.unkown_error, Toast.LENGTH_SHORT).show()
                })
        loadDeviceRequest.tag = "LOAD_DEVICES"

        pb_loading.visibility = View.VISIBLE
        queue.add(loadDeviceRequest)
    }

    override fun onStop() {
        super.onStop()
        queue.cancelAll("LOAD_DEVICES")
    }

    private fun addEvents() {
        adapter = AddedDeviceAdapter(mDevices, this@MainActivity) {device ->
            val state = applicationContext as GlobalState
            state.currentDevice = device
            startActivity(intentFor<RemoteActivity>())
        }

        rv_list_device.adapter = adapter
        rv_list_device.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_list_device.setHasFixedSize(false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rv_list_device.setOnScrollChangeListener { _, _, _, _, _ ->
                header.isSelected = rv_list_device.canScrollVertically(-1)
            }
        }

        img_reload.setOnClickListener {
            reloadDeviceList()
        }

    }

    private fun setupGoogleAccount() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this@MainActivity, gso)

        account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            //We do nothing for now

//            GlideApp.with(this)
//                    .load(account!!.photoUrl)
//                    .apply(RequestOptions.circleCropTransform())
//                    .placeholder(R.drawable.user)
//                    .into(img_reload)
        } else {
            val builder = AlertDialog.Builder(this)
                    .setTitle(R.string.session_expired)
                    .setMessage(R.string.session_expired_detail)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        startActivity(intentFor<LoginActivity>())
                        finish()
                    }
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onLogout() {
        if (account != null) {

            val builder = AlertDialog.Builder(this)
                    .setTitle(R.string.signout_confirm)
                    .setMessage(R.string.signout_confirm_detail)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        mGoogleSignInClient.signOut()
                                .addOnCompleteListener {
                                    startActivity(intentFor<LoginActivity>())
                                    finish()
                                }
                    }
                    .setNegativeButton(R.string.cancel) { dialog, which ->
                        dialog.dismiss()
                    }
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_switch_hub -> {
                startActivity(intentFor<ConnectActivity>())
            }
            android.R.id.home -> {
                val bottomSheet = UserBottomSheet.newInstance(
                        account?.displayName,
                        account?.email,
                        account?.photoUrl
                )
                bottomSheet.show(
                        supportFragmentManager,
                        bottomSheet.tag
                )
            }
        }
        return true
    }
}
