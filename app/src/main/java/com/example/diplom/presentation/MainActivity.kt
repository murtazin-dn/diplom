package com.example.diplom.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import com.bumptech.glide.Glide
import com.example.diplom.R
import com.example.diplom.data.network.chats.model.response.ChatResponse
import com.example.diplom.databinding.ActivityMainBinding
import com.example.diplom.presentation.common.toPhotoURL
import com.example.diplom.util.BASE_URL
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {

    var hideBottomNavMenu: HideBottomNavMenu? = null
    var showBottomNavMenu: ShowBottomNavMenu? = null
    var searchToolbarImpl: SearchToolbar? = null
    var newsNavigation: NewsNavigation? = null

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: MaterialToolbar
    private lateinit var chatToolbar: MaterialToolbar
    private lateinit var searchToolbar: MaterialToolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbar)
        chatToolbar = findViewById(R.id.chat_toolbar)
        searchToolbar = findViewById(R.id.search_toolbar)
        setSupportActionBar(toolbar)

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return false
            }
        })

        binding.searchToolbar.searchViewToolbar.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) if(searchToolbarImpl != null) searchToolbarImpl!!.invoke(newText)
                return false
            }

        })
    }

    fun setChatInfo(chat: ChatResponse){
        binding.chatToolbar.tvUserNameToolbar.text = "${chat.secondUser.name} ${chat.secondUser.surname}"
        if(!chat.secondUser.icon.isNullOrBlank()) {
            Glide
                .with(this)
                .load(chat.secondUser.icon.toPhotoURL())
                .into(binding.chatToolbar.imgUserIconToolbar)
        }else{
            Glide
                .with(this)
                .load(R.drawable.no_photo)
                .into(binding.chatToolbar.imgUserIconToolbar)
        }
    }

    fun hideChatToolbar(){
        showBottomMenu()
        chatToolbar.visibility = View.GONE
        toolbar.visibility = View.VISIBLE
        setSupportActionBar(toolbar)
        binding.chatToolbar.tvUserNameToolbar.text = ""
        Glide
            .with(this)
            .load(R.drawable.no_photo)
            .into(binding.chatToolbar.imgUserIconToolbar)

    }
    fun showChatToolbar(){
        hideBottomMenu()
        chatToolbar.visibility = View.VISIBLE
        toolbar.visibility = View.GONE
        setSupportActionBar(chatToolbar)
    }

    fun hideSearchToolbar(){
        searchToolbar.visibility = View.GONE
        toolbar.visibility = View.VISIBLE
        setSupportActionBar(toolbar)
    }
    fun showSearchToolbar(){
        searchToolbar.visibility = View.VISIBLE
        toolbar.visibility = View.GONE
        setSupportActionBar(chatToolbar)
    }


    fun hideBottomMenu() {
        hideBottomNavMenu?.invoke()
    }

    fun showBottomMenu() {
        showBottomNavMenu?.invoke()
    }

    fun showUpButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun hideUpButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    companion object{
        var currentChatId: Long? = null
    }
}

fun interface HideBottomNavMenu{
    fun invoke()
}
fun interface ShowBottomNavMenu{
    fun invoke()
}
fun interface SearchToolbar{
    fun invoke(text: String)
}

interface NewsNavigation{
    fun navigateToProfile(id: Long)
    fun navigateToPost(id: Long)
}