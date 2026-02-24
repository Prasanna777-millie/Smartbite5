package com.example.smartbite.Repository


import com.example.smartbite.model.MenuItemModel

interface MenuRepo {

    fun addMenu(menu: MenuItemModel, callback: (Boolean, String) -> Unit)

    fun getMenuById(menuId: String, callback: (Boolean, String, MenuItemModel?) -> Unit)

    fun getAllMenus(callback: (Boolean, String, List<MenuItemModel>) -> Unit)

    fun updateMenu(menu: MenuItemModel, callback: (Boolean, String) -> Unit)

    fun deleteMenu(menuId: String, callback: (Boolean, String) -> Unit)
}