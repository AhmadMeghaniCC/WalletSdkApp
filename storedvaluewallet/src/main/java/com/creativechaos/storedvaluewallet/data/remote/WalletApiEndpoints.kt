package com.creativechaos.storedvaluewallet.data.remote

object WalletApiEndpoints {

    var BASE_URL = ""
    const val CASH_IN = "/cash_in"
    const val CASH_OUT = "/cash_out"
    const val ADJUSTMENT = "/adjustment"
    const val REVERSAL = "/reversal"
    const val RESET_CARD = "/resetcard"
    const val TRANSACTION = "/transactions"
    const val GET_BALANCE = "/check_balance"
    var CARD = "/card"
    var LINK = "/link"
    var CONFIRM_CARD_ACTIVATION = "/card_activation_confirm"
    var CARD_ACTIVATION = "/card_activation"
    var LOGIN = "/sdklogin"
    var WALLET_STRUCTURE = "/wallet_app_structure"
    var WALLET_DETAIL = "/svcwallet"
    var SIGNUP = "/signup"
    var CONFIRM_SIGNUP = "/signupconfirm"
    var TOKEN = ""

//    private const val BASE_URL = "http://qa.opentransit.co/backend/web/svcwallet/v1_0"
//    const val CASH_IN = "$BASE_URL/cash_in"
//    const val CASH_OUT = "$BASE_URL/cash_out"
//    const val ADJUSTMENT = "$BASE_URL/adjustment"
//    const val REVERSAL = "$BASE_URL/reversal"
//    const val RESET_CARD = "$BASE_URL/resetcard"
//    const val CARD = "$BASE_URL/card"
//    const val LINK = "$BASE_URL/link"
//    const val CONFIRM_CARD_ACTIVATION = "$BASE_URL/card_activation_confirm"
//    const val CARD_ACTIVATION = "$BASE_URL/card_activation"
//    const val LOGIN = "$BASE_URL/sdklogin"
//    const val WALLET_STRUCTURE = "$BASE_URL/wallet_app_structure"
//    const val WALLET_DETAIL = "$BASE_URL/svcwallet"
//    const val SIGNUP = "$BASE_URL/signup"
//    const val CONFIRM_SIGNUP = "$BASE_URL/signupconfirm"
//    var TOKEN =
//        "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzdmNfd2FsbGV0X2FwcF8xLjAiLCJhdWQiOiJjMnBheSIsImlhdCI6MTcwNjU1MDgyMiwiZXhwIjoxNzM4MDg2ODIyLCJhdXRoX3RpbWUiOjE3MDY1NTA4MjIsImVudGl0eSI6IjEwMDA1NiIsInQiOjUwfQ.NFO8kYtXK8FXA-xyact8GjcfprFfRr9XlrYvW8sUB7BibGGOpt8Kl8PXLS3LBxGfm7E4nvm3rISRwy7-bwQsSvBfmORPZ_9LELGYbRuZy_E6BPZKkwtDDw59bTa6iMd892TnUYkoL163AYm1Pdw6-jecWEJ1H5LFmTPTWe_8y2o2aFnwkNq1x6Q2XyDiRUosB1zoJblCz2Ku-PlKLJnbYFUEQxUJeI7_zIhEeQJDuTszoZrvl-EsWpZHd27lK0HdhbOoWYmRnrQ5A3gEp_Nc3djfyw6bq_Tq4G8y1sglWzQftjKbYNDZt2zHnuw-yLKxTuURB4MR55VLfZcZGG661Q"
//    var TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJjMnBheSIsImF1ZCI6InN2Y193YWxsZXRfcHBfMS4wIiwiaWF0IjoxNzAzMTQ2MDE3LCJleHAiOjE3MzQ2ODIwMTcsImF1dGhfdGltZSI6MTcwMzE0NjAxNywicGhvbmUiOiIwMzA1NzYwMTM4OCJ9.Wemkzw6qSwfCbeOlSlrj1UhxtXlcmhETiCt6FvbNp6J09OjUwFZ6PgVThIBeZvxBFwknkqlsOgCQxdj6gg0ZLrpysrXCCUC9PqBXUjhrF31gtL5mkTv8S1LJ5sWQ3tqVuIw1fS1crd8IaQQg_2RDUCN9OZtuOuOnTQMJjjjlSwSn52Ph_kYh1jZyWigo9uxy2_F0NbMLCFJRCIMzmx9-bTVtH9QwwKia0bFxEqRjFGpRfHexCFEWCHe0FyH8YVHRPDhKChNVOiPbw1Vu8IkaA3VuIV3GUjbEFYq5gvENppllW8l578gVbOU26fpPkJx95XWI1vVvtLELurEvIJDveA"
//    const val TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJjMnBheSIsImF1ZCI6InN2Y193YWxsZXRfcHBfMS4wIiwiaWF0IjoxNzAyNTU2OTA3LCJleHAiOjE3MzQwOTI5MDcsImF1dGhfdGltZSI6MTcwMjU1NjkwNywicGhvbmUiOiIwMzIxMTIzNDU2NyJ9.ePlS0VaaGI5MdfO5ORhRPImJNGYi1ICWmGHL1lrMBFw3KCQvav98mz9xL2o-5oA96jEUuuPXeLNupM3PC8DQp8F1IRTOGGz_0-DFPK1lzgGo6eOYLdaw2EJl02XIQ4csQOicSiLvLntpB4nf2QzJsAlL81KFNp1Vsa1SaGp33uAE00Ds4FRCaJG9m-YatIR6oZobr3kIdPuU3TtPtl57zPRpMY-LQCozCv5tWC61buEq5cF2SZ8HP9gWvnbFGCQ_hFDLYWdJmvhexK0DlzX-pMSedV8FIKiMoSnVyHD-FXhVvstQaIdQv_5hYZsBD7S7uN7rIiJfmbFgKrzDcJBIjg"
}