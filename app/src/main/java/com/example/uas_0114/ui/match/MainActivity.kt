package com.example.uas_0114.ui.match

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas_0114.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.example.uas_0114.model.EventItem
import com.example.uas_0114.model.LeagueItem
import com.example.uas_0114.ui.detail.DetailActivity
import com.example.uas_0114.ui.detail.INTENT_DETAIL
import com.example.uas_0114.utils.ServerCallback
import com.example.uas_0114.utils.gone
import com.example.uas_0114.utils.invisible
import com.example.uas_0114.utils.visible
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.ctx
import org.json.JSONObject

class MainActivity : AppCompatActivity(), MatchView {
    private var listLeageu = ArrayList<LeagueItem>()
    private var menuItem: Int = 1
    private var idSpinner: String = ""
    private val presenter = MatchPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        getlistLeageu()
        setSpinner(menuItem)
        initBottomNavigationContainer()
        initContainer()

        refreshButton.setOnClickListener {
            if (presenter.isNetworkAvailable(this)) {
                this@MainActivity.finish()
                this@MainActivity.startActivity(this@MainActivity.intent)
            } else {
                Snackbar.make(main_activity, getString(R.string.turnOn_internet)
                    , Snackbar.LENGTH_LONG).show()
            }
        }

        swipeRefresh.setOnRefreshListener {
            if (swipeRefresh.isRefreshing) {
                swipeRefresh.isRefreshing = false
                setSpinner(menuItem)
                setDataToContainer(idSpinner, menuItem)
            }

        }
    }

    private fun initLayout() {
        setContentView(R.layout.activity_main)
    }

    private fun initBottomNavigationContainer() {
        navigation.setOnNavigationItemSelectedListener(bottomNavigationListener)
    }

    private val bottomNavigationListener by lazy {
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main_menu_match_prev -> {
                    spinner.visibility = View.VISIBLE
                    menuItem = 1
                    title = getString(R.string.prev)
                    setSpinner(menuItem)
                    setDataToContainer(idSpinner, menuItem)
                    Log.d("TAG", "ini prev")
                    true
                }
                R.id.main_menu_match_next -> {
                    spinner.visibility = View.VISIBLE
                    menuItem = 2
                    title = getString(R.string.next)
                    setSpinner(menuItem)
                    setDataToContainer(idSpinner, menuItem)
                    Log.d("TAG", "ini next")
                    true
                }
                R.id.main_menu_match_favorites -> {
                    menuItem = 3
                    title = getString(R.string.favorite)
                    setSpinner(menuItem)
                    spinner.visibility = View.GONE
                    setDataToContainer("0", menuItem)
                    Log.d("TAG", "ini fav")
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    override fun showLoading() {
        progressbar.visible()
        recyclerview.invisible()
        emptyDataView.invisible()
        noconectionView.invisible()
    }

    override fun hideLoading() {
        progressbar.gone()
        recyclerview.visible()
        emptyDataView.invisible()
        noconectionView.invisible()
    }

    override fun showEmptyData() {
        progressbar.gone()
        recyclerview.invisible()
        if (presenter.isNetworkAvailable(this)) {
            emptyDataView.visible()
        } else {
            noconectionView.visible()

        }
    }

    private fun getlistLeageu() {
        if (presenter.isNetworkAvailable(this)) {
            presenter.getSpinnerData(this, object : ServerCallback {
                override fun onSuccess(response: String) {
                    if (presenter.isSuccess(response)) {
                        try {
                            val jsonObject = JSONObject(response)
                            var numData = 0
                            var numDataSize = 0
                            val message = jsonObject.getJSONArray("leagues")
                            val soccerData = arrayOfNulls<String>(message.length())
                            val idLeague: ArrayList<String> = ArrayList()
                            val leagueName: ArrayList<String> = ArrayList()
                            Log.d("TAG MESSAGE", message.length().toString())
                            if (message.length() > 0) {
                                for (i in 0 until message.length()) {
                                    val dataAll = message.getJSONObject(i)
                                    soccerData[i] = dataAll.getString("strSport")
                                    if (soccerData[i].toString() == "Soccer") {
                                        idLeague.add(dataAll.getString("idLeague"))
                                        leagueName.add(dataAll.getString("strLeague"))
                                        listLeageu.add(LeagueItem(idLeague[numData], leagueName[numData]))
                                        numData += 1
                                    } else {
                                        numDataSize += 1
                                    }
                                }
                                Log.d("TAG NUMDATA", numData.toString())
                                Log.d("TAG NUMDATASIZE", numDataSize.toString())
                                spinner.adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, leagueName)
                            } else {
                            }
                        } catch (e: NullPointerException) {
                            showEmptyData()
                        }
                    }
                }

                override fun onFailed(response: String) {
                }

                override fun onFailure(throwable: Throwable) {
                }
            })
        } else {
            showEmptyData()
            Snackbar.make(main_activity, getString(R.string.turnOn_internet)
                , Snackbar.LENGTH_LONG).show()
        }

    }

    private fun setSpinner(menu: Int) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                idSpinner = listLeageu[position].idLeague
                when (menu) {
                    1 -> setDataToContainer(idSpinner, 1)
                    2 -> setDataToContainer(idSpinner, 2)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun initContainer() {
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = MatchAdapter { posistionData ->
            val dataIntent = getListAdapter()?.getDataAt(posistionData)
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(INTENT_DETAIL, dataIntent)
            intent.putExtra(getString(R.string.menuItem), menuItem.toString())
            startActivity(intent)
        }
    }

    private fun setDataToContainer(id: String, menu: Int) {
        var data: MutableList<EventItem>
        if (presenter.isNetworkAvailable(this)) {
            if (menu == 1) {
                showLoading()
                presenter.getPrevMatch(this, id, object : ServerCallback {
                    override fun onSuccess(response: String) {
                        if (presenter.isSuccess(response)) {
                            try {
                                if (presenter.isSuccess(response)) {
                                    data = presenter.parsingData(this@MainActivity, response)
                                    if (data.size < 1) {
                                        showEmptyData()
                                    } else {
                                        getListAdapter()?.setData(data.toMutableList())
                                        hideLoading()
                                    }
                                }
                            } catch (e: NullPointerException) {
                                showEmptyData()
                            }
                        }
                    }

                    override fun onFailed(response: String) {
                        showEmptyData()
                    }

                    override fun onFailure(throwable: Throwable) {
                        showEmptyData()
                    }
                })
            } else if (menu == 2) {
                showLoading()
                presenter.getNextMatch(this, id, object : ServerCallback {
                    override fun onSuccess(response: String) {
                        if (presenter.isSuccess(response)) {
                            try {
                                if (presenter.isSuccess(response)) {
                                    data = presenter.parsingData(this@MainActivity, response)
                                    if (data.size < 1) {
                                        showEmptyData()
                                    } else {
                                        getListAdapter()?.setData(data.toMutableList())
                                        hideLoading()
                                    }
                                }
                            } catch (e: NullPointerException) {
                                showEmptyData()
                            }
                        }
                    }

                    override fun onFailed(response: String) {
                        showEmptyData()
                    }

                    override fun onFailure(throwable: Throwable) {
                        showEmptyData()
                    }
                })
            } else if (menu == 3) {
                var data: MutableList<EventItem>
                data = presenter.getFavoritesAll(ctx)
                if (data.size < 1) {
                    showEmptyData()
                } else {
                    getListAdapter()?.setData(data.toMutableList())
                }

            } else {
                showEmptyData()
            }
        } else {
            showEmptyData()
            Snackbar.make(main_activity, getString(R.string.turnOn_internet)
                , Snackbar.LENGTH_LONG).show()
        }
    }

    private fun getListAdapter(): MatchAdapter? = recyclerview?.adapter as? MatchAdapter
}

