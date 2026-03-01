package com.example.smartbite.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartbite.model.MenuItemModel
import com.example.smartbite.repo.MenuRepo
import com.example.smartbite.repo.MenuRepoImpl

class MenuViewModel(
    private val repo: MenuRepo = MenuRepoImpl()
) : ViewModel() {

    private val _menu = MutableLiveData<MenuItemModel?>()
    val menu: MutableLiveData<MenuItemModel?> get() = _menu

    private val _menuList = MutableLiveData<List<MenuItemModel>>(emptyList())
    val menuList: MutableLiveData<List<MenuItemModel>> get() = _menuList

    fun addMenu(menu: MenuItemModel, callback: (Boolean, String) -> Unit) {
        repo.addMenu(menu, callback)
    }

    fun updateMenu(menu: MenuItemModel, callback: (Boolean, String) -> Unit) {
        repo.updateMenu(menu, callback)
    }

    fun deleteMenu(menuId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteMenu(menuId, callback)
    }

    fun getMenuById(menuId: String) {
        repo.getMenuById(menuId) { success, _, data ->
            if (success) _menu.postValue(data)
        }
    }

    fun getAllMenus() {
        repo.getAllMenus { success, _, data ->
            if (success) _menuList.postValue(data)
            else _menuList.postValue(emptyList())
        }
    }

    fun toggleMenuAvailability(menuId: String, currentStatus: Boolean) {
        val newStatus = !currentStatus

        // 1. Get the current list (handle null with emptyList)
        val currentList = _menuList.value ?: emptyList()

        // 2. Create the new list where only the clicked item is toggled
        val updatedList = currentList.map {
            if (it.id == menuId) it.copy(isAvailable = newStatus) else it
        }

        // 3. Push the update to the UI immediately
        _menuList.value = updatedList

        // 4. Update the database (Firebase)
        repo.updateMenuAvailability(menuId, newStatus) { success, _ ->
            if (!success) {
                // If it fails, reload the original data from Firebase
                getAllMenus()
            }
        }
    }
}