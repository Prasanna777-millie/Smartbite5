package com.example.smartbite.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartbite.Repository.MenuRepo
import com.example.smartbite.model.MenuItemModel


class MenuViewModel(private val repo: MenuRepo) : ViewModel() {

    // LiveData for a single menu
    private val _menu = MutableLiveData<MenuItemModel?>()
    val menu: MutableLiveData<MenuItemModel?> get() = _menu

    // LiveData for all menus
    private val _menuList = MutableLiveData<List<MenuItemModel>>()
    val menuList: MutableLiveData<List<MenuItemModel>> get() = _menuList

    // Add menu
    fun addMenu(menu: MenuItemModel, callback: (Boolean, String) -> Unit) {
        repo.addMenu(menu, callback)
    }

    // Update menu
    fun updateMenu(menu: MenuItemModel, callback: (Boolean, String) -> Unit) {
        repo.updateMenu(menu, callback)
    }

    // Delete menu
    fun deleteMenu(menuId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteMenu(menuId, callback)
    }

    // Get menu by ID
    fun getMenuById(menuId: String) {
        repo.getMenuById(menuId) { success, _, data ->
            if (success) _menu.postValue(data)
        }
    }

    // Get all menus
    fun getAllMenus() {
        repo.getAllMenus { success, _, data ->
            if (success) _menuList.postValue(data)
        }
    }
}