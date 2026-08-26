package com.creacionesnormita.mobile.features.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creacionesnormita.mobile.core.model.Perfil
import com.creacionesnormita.mobile.core.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object SignUpSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun signUp(
        email: String,
        password: String,
        nombre: String,
        fechaNacimiento: String,
        celular: String,
        otroContacto: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                val user = response
                
                if (user != null) {
                    val perfil = Perfil(
                        id = user.id,
                        nombre = nombre,
                        fecha_nacimiento = fechaNacimiento,
                        celular = celular,
                        otro_contacto = otroContacto.ifBlank { null }
                    )
                    SupabaseClient.client.postgrest["profiles"].insert(perfil)
                    _authState.value = AuthState.SignUpSuccess
                } else {
                    _authState.value = AuthState.Error("Error al crear usuario")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error(e.message ?: "Error al registrarse")
            }
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
