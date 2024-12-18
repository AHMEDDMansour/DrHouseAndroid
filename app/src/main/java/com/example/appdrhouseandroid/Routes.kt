package com.example.appdrhouseandroid

sealed class Routes(val route: String){

    object Home :     Routes("home")
    object AIscreen : Routes("AIscreen")
    object Favorite : Routes("Favorite")
    object Reminder : Routes("Reminder")
    object Screen1 : Routes("Screen1")
    object LunchScreen : Routes("LunchScreen")
    object Login : Routes ("Login")
    object SignUp : Routes ("SignUp")
    object ForgetPassword : Routes ("ForgetPassword")
    object ConfirmCode : Routes ("ConfirmCode")
    object ResetPass : Routes ("ResetPassword")
    object OCRScreen : Routes ("OCRScreen   ")
    object ProductView : Routes ("ProductView   ")
    object CheckoutScreen : Routes ("checkout")
    object  OrderConfirmation : Routes ("order_confirmation")





}