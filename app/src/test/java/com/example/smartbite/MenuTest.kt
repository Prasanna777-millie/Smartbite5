package com.example.smartbite

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.smartbite.model.MenuItemModel
import com.example.smartbite.repo.MenuRepo
import com.example.smartbite.viewmodel.MenuViewModel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class MenuTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun addMenu_success_test() {
        val repo = mock<MenuRepo>()
        val vm = MenuViewModel(repo)

        val item = MenuItemModel(
            id = "1",
            name = "Cappuccino",
            desc = "Hot coffee",
            price = 200,
            imageRes = 0,
            category = "Coffee",
            isAvailable = true,
            imageUrl = "http://img.com/a.jpg"
        )

        doAnswer {
            val cb = it.getArgument<(Boolean, String) -> Unit>(1)
            cb(true, "Menu added")
            null
        }.`when`(repo).addMenu(eq(item), any())

        var ok = false
        var msg = ""

        vm.addMenu(item) { success, message ->
            ok = success
            msg = message
        }

        assertTrue(ok)
        assertEquals("Menu added", msg)
        verify(repo).addMenu(eq(item), any())
    }

    @Test
    fun updateMenu_success_test() {
        val repo = mock<MenuRepo>()
        val vm = MenuViewModel(repo)

        val item = MenuItemModel(
            id = "2",
            name = "Burger",
            desc = "Cheese burger",
            price = 350,
            imageRes = 0,
            category = "Food",
            isAvailable = true,
            imageUrl = null
        )

        doAnswer {
            val cb = it.getArgument<(Boolean, String) -> Unit>(1)
            cb(true, "Menu updated")
            null
        }.`when`(repo).updateMenu(eq(item), any())

        var ok = false
        var msg = ""

        vm.updateMenu(item) { success, message ->
            ok = success
            msg = message
        }

        assertTrue(ok)
        assertEquals("Menu updated", msg)
        verify(repo).updateMenu(eq(item), any())
    }

    @Test
    fun deleteMenu_success_test() {
        val repo = mock<MenuRepo>()
        val vm = MenuViewModel(repo)

        doAnswer {
            val cb = it.getArgument<(Boolean, String) -> Unit>(1)
            cb(true, "Menu deleted")
            null
        }.`when`(repo).deleteMenu(eq("1"), any())

        var ok = false
        var msg = ""

        vm.deleteMenu("1") { success, message ->
            ok = success
            msg = message
        }

        assertTrue(ok)
        assertEquals("Menu deleted", msg)
        verify(repo).deleteMenu(eq("1"), any())
    }

    @Test
    fun getMenuById_posts_value_to_liveData() {
        val repo = mock<MenuRepo>()
        val vm = MenuViewModel(repo)

        val menuId = "10"
        val item = MenuItemModel(
            id = menuId,
            name = "Latte",
            desc = "Milk coffee",
            price = 250,
            imageRes = 0,
            category = "Coffee",
            isAvailable = true,
            imageUrl = null
        )

        doAnswer {
            val cb = it.getArgument<(Boolean, String, MenuItemModel?) -> Unit>(1)
            cb(true, "Menu fetched", item)
            null
        }.`when`(repo).getMenuById(eq(menuId), any())

        vm.getMenuById(menuId)

        assertEquals(item, vm.menu.value)
        verify(repo).getMenuById(eq(menuId), any())
    }

    @Test
    fun getAllMenus_posts_list_to_liveData_success() {
        val repo = mock<MenuRepo>()
        val vm = MenuViewModel(repo)

        val list = listOf(
            MenuItemModel(id = "1", name = "A", desc = "d", price = 10, imageRes = 0, category = "Coffee", isAvailable = true, imageUrl = null),
            MenuItemModel(id = "2", name = "B", desc = "d", price = 20, imageRes = 0, category = "Food", isAvailable = false, imageUrl = null)
        )

        doAnswer {
            val cb = it.getArgument<(Boolean, String, List<MenuItemModel>) -> Unit>(0)
            cb(true, "Menus fetched", list)
            null
        }.`when`(repo).getAllMenus(any())

        vm.getAllMenus()

        assertEquals(list, vm.menuList.value)
        verify(repo).getAllMenus(any())
    }

    @Test
    fun getAllMenus_posts_empty_list_on_fail() {
        val repo = mock<MenuRepo>()
        val vm = MenuViewModel(repo)

        vm.menuList.value = listOf(
            MenuItemModel(id = "x", name = "Old", desc = "Old", price = 1, imageRes = 0, category = "Coffee", isAvailable = true)
        )

        doAnswer {
            val cb = it.getArgument<(Boolean, String, List<MenuItemModel>) -> Unit>(0)
            cb(false, "Error", emptyList())
            null
        }.`when`(repo).getAllMenus(any())

        vm.getAllMenus()

        assertEquals(emptyList<MenuItemModel>(), vm.menuList.value)
        verify(repo).getAllMenus(any())
    }

    @Test
    fun toggleMenuAvailability_updates_list_immediately_and_calls_repo() {
        val repo = mock<MenuRepo>()
        val vm = MenuViewModel(repo)

        val menuId = "1"
        vm.menuList.value = listOf(
            MenuItemModel(id = menuId, name = "A", desc = "d", price = 10, imageRes = 0, category = "Coffee", isAvailable = true),
            MenuItemModel(id = "2", name = "B", desc = "d", price = 20, imageRes = 0, category = "Food", isAvailable = false)
        )

        doAnswer {
            val cb = it.getArgument<(Boolean, String) -> Unit>(2)
            cb(true, "Successfully updated")
            null
        }.`when`(repo).updateMenuAvailability(eq(menuId), eq(false), any())

        vm.toggleMenuAvailability(menuId, currentStatus = true)

        val updated = vm.menuList.value ?: emptyList()
        assertEquals(false, updated.first { it.id == menuId }.isAvailable)

        verify(repo).updateMenuAvailability(eq(menuId), eq(false), any())
        verify(repo, never()).getAllMenus(any())
    }

    @Test
    fun toggleMenuAvailability_on_repo_fail_reload_from_firebase() {
        val repo = mock<MenuRepo>()
        val vm = MenuViewModel(repo)

        val menuId = "1"
        vm.menuList.value = listOf(
            MenuItemModel(id = menuId, name = "A", desc = "d", price = 10, imageRes = 0, category = "Coffee", isAvailable = true)
        )

        doAnswer {
            val cb = it.getArgument<(Boolean, String) -> Unit>(2)
            cb(false, "Failed to update")
            null
        }.`when`(repo).updateMenuAvailability(eq(menuId), eq(false), any())

        val reloadedList = listOf(
            MenuItemModel(id = menuId, name = "A", desc = "d", price = 10, imageRes = 0, category = "Coffee", isAvailable = true)
        )

        doAnswer {
            val cb = it.getArgument<(Boolean, String, List<MenuItemModel>) -> Unit>(0)
            cb(true, "Menus fetched", reloadedList)
            null
        }.`when`(repo).getAllMenus(any())

        vm.toggleMenuAvailability(menuId, currentStatus = true)

        val finalList = vm.menuList.value ?: emptyList()
        assertEquals(true, finalList.first { it.id == menuId }.isAvailable)

        verify(repo).updateMenuAvailability(eq(menuId), eq(false), any())
        verify(repo).getAllMenus(any())
    }
}