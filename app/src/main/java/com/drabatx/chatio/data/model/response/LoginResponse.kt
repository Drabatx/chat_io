package com.drabatx.chatio.data.model.response

import com.drabatx.chatio.data.model.UserModel

data class LoginResponse(val message: String, val userModel: UserModel)