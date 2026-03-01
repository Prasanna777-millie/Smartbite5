
package com.example.smartbite

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.smartbite.model.UserModel
import com.example.smartbite.repository.UserRepo
import com.example.smartbite.viewmodel.UserViewModel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class UserViewModelTest {

    // ✅ Fixes: "Method getMainLooper in android.os.Looper not mocked"
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun login_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer {
            val callback = it.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Login success")
            null
        }.`when`(repo).login(eq("test@gmail.com"), eq("123456"), any())

        var successResult = false
        var messageResult = ""

        viewModel.login("test@gmail.com", "123456") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Login success", messageResult)
        verify(repo).login(eq("test@gmail.com"), eq("123456"), any())
    }

    @Test
    fun login_fail_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer {
            val callback = it.getArgument<(Boolean, String) -> Unit>(2)
            callback(false, "Invalid credentials")
            null
        }.`when`(repo).login(eq("test@gmail.com"), eq("wrongpass"), any())

        var successResult = true
        var messageResult = ""

        viewModel.login("test@gmail.com", "wrongpass") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertFalse(successResult)
        assertEquals("Invalid credentials", messageResult)
        verify(repo).login(eq("test@gmail.com"), eq("wrongpass"), any())
    }

    @Test
    fun register_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer {
            val callback = it.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(true, "Registration success", "user123")
            null
        }.`when`(repo).register(eq("test@gmail.com"), eq("123456"), any())

        var successResult = false
        var messageResult = ""
        var userIdResult = ""

        viewModel.register("test@gmail.com", "123456") { success, msg, userId ->
            successResult = success
            messageResult = msg
            userIdResult = userId
        }

        assertTrue(successResult)
        assertEquals("Registration success", messageResult)
        assertEquals("user123", userIdResult)
        verify(repo).register(eq("test@gmail.com"), eq("123456"), any())
    }

    @Test
    fun register_fail_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer {
            val callback = it.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(false, "Email already exists", "")
            null
        }.`when`(repo).register(eq("test@gmail.com"), eq("123456"), any())

        var successResult = true
        var messageResult = ""
        var userIdResult = "not-empty"

        viewModel.register("test@gmail.com", "123456") { success, msg, userId ->
            successResult = success
            messageResult = msg
            userIdResult = userId
        }

        assertFalse(successResult)
        assertEquals("Email already exists", messageResult)
        assertEquals("", userIdResult)
        verify(repo).register(eq("test@gmail.com"), eq("123456"), any())
    }

    @Test
    fun addUserToDatabase_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val userId = "user123"
        val user = UserModel(
            id = userId,
            firstName = "Test",
            lastName = "User",
            gender = "Male",
            dob = "2000-01-01",
            email = "test@gmail.com"
        )

        doAnswer {
            val callback = it.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Registration success")
            null
        }.`when`(repo).addUserToDatabase(eq(userId), eq(user), any())

        var successResult = false
        var messageResult = ""

        viewModel.addUserToDatabase(userId, user) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Registration success", messageResult)
        verify(repo).addUserToDatabase(eq(userId), eq(user), any())
    }

    @Test
    fun forgetPassword_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer {
            val callback = it.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Reset email sent to test@gmail.com")
            null
        }.`when`(repo).forgetPassword(eq("test@gmail.com"), any())

        var successResult = false
        var messageResult = ""

        viewModel.forgetPassword("test@gmail.com") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Reset email sent to test@gmail.com", messageResult)
        verify(repo).forgetPassword(eq("test@gmail.com"), any())
    }

    @Test
    fun deleteAccount_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer {
            val callback = it.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Account deleted successfully")
            null
        }.`when`(repo).deleteAccount(eq("user123"), any())

        var successResult = false
        var messageResult = ""

        viewModel.deleteAccount("user123") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Account deleted successfully", messageResult)
        verify(repo).deleteAccount(eq("user123"), any())
    }

    @Test
    fun editProfile_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val userId = "user123"
        val updated = UserModel(
            id = userId,
            firstName = "Updated",
            lastName = "User",
            gender = "Male",
            dob = "2000-01-01",
            email = "updated@gmail.com"
        )

        doAnswer {
            val callback = it.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Profile updated success")
            null
        }.`when`(repo).editProfile(eq(userId), eq(updated), any())

        var successResult = false
        var messageResult = ""

        viewModel.editProfile(userId, updated) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Profile updated success", messageResult)
        verify(repo).editProfile(eq(userId), eq(updated), any())
    }

    @Test
    fun getUserById_posts_value_to_liveData() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val userId = "user123"
        val user = UserModel(
            id = userId,
            firstName = "Test",
            lastName = "User",
            gender = "Male",
            dob = "2000-01-01",
            email = "test@gmail.com"
        )

        doAnswer {
            val callback = it.getArgument<(Boolean, String, UserModel?) -> Unit>(1)
            callback(true, "Profile fetched", user)
            null
        }.`when`(repo).getUserById(eq(userId), any())

        viewModel.getUserById(userId)

        assertEquals(user, viewModel.users.value)
        verify(repo).getUserById(eq(userId), any())
    }

    @Test
    fun getAllUser_posts_list_to_liveData() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        val list = listOf(
            UserModel(id = "1", firstName = "A", email = "a@gmail.com"),
            UserModel(id = "2", firstName = "B", email = "b@gmail.com")
        )

        doAnswer {
            val callback = it.getArgument<(Boolean, String, List<UserModel>?) -> Unit>(0)
            callback(true, "data fetched", list)
            null
        }.`when`(repo).getAllUser(any())

        viewModel.getAllUser()

        assertEquals(list, viewModel.allUsers.value)
        verify(repo).getAllUser(any())
    }
}