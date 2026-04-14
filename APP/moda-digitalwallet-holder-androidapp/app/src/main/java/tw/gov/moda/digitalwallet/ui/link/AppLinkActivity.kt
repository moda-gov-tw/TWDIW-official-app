package tw.gov.moda.digitalwallet.ui.link

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.ui.main.MainActivity
import tw.gov.moda.diw.R

class AppLinkActivity: Activity() {
    companion object{
        const val INTENT_ACTION = "APPLINK_TO_MAIN"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startMainActivity(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        startMainActivity(intent)
    }


    private fun startMainActivity(intent: Intent?) {
        MainScope().launch {
            val routeIntent = Intent(this@AppLinkActivity, MainActivity::class.java).apply {
                // 依需求帶原始 deep link 資訊
                data = intent?.data
                putExtras(intent ?: Intent())
                action = INTENT_ACTION

                // 確保進入「自己的 Task」，並清理舊Task
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(routeIntent)
            // 立刻結束跳板，避免在 Recents 留下任何縮圖
            finish()
        }
    }
}