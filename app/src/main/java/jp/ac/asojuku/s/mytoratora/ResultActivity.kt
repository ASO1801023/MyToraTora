package jp.ac.asojuku.s.mytoratora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    val gu = 0
    val choki = 1
    val pa = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val id = intent.getIntExtra("MY_HAND",0)

        val myHand: Int
        myHand = when(id){
            R.id.gu -> { myHandImage.setImageResource(R.drawable.tora)
                gu
            }
            R.id.choki -> {
                myHandImage.setImageResource(R.drawable.tue)
                choki
            }
            R.id.pa -> {
                myHandImage.setImageResource(R.drawable.hito)
                pa
            }
            else -> gu
            }

        //コンピュータの手を決める
        val comHand = getHand()
        when (comHand) {
            gu -> comHandImage.setImageResource(R.drawable.tora)
            choki -> comHandImage.setImageResource(R.drawable.tue)
            pa -> comHandImage.setImageResource(R.drawable.hito)
        }

        //勝敗を判定する
        val gameResult = (comHand - myHand + 3) % 3
        when (gameResult) {
            0 -> resultLabel.setText(R.string.result_draw) //引き分け
            1 -> resultLabel.setText(R.string.result_win) //勝った場合
            2 -> resultLabel.setText(R.string.result_lose) //負けた場合
        }
        backButton.setOnClickListener{this.finish()}
        saveData(myHand,comHand,gameResult)
        }
    private fun saveData(myHand: Int, comHand: Int,gameResult: Int){
        //共有プリファレンスを使う　１、インスタンスを取得
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        //２、値を取得する；キーを指定して値を取得する。該当するものがなければデフォルト値が返る
        val gameCount = pref.getInt("GAME_COUNT",0)//勝負数
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0)//連勝数
        val lastComHand = pref.getInt("LAST_COM_HAND",0)//前回のコンピュータの手
        val lastGameResult = pref.getInt("GAME_RESULT",-1)//前回の勝敗
        //保存を始めていく、まずは値を組み立てる
        //連勝数
        val edtWinningStreskCount: Int =
            when{
                //前回勝って今回も勝ったら連勝＋１を返す
                lastGameResult == 2 && gameResult == 2 ->
                    winningStreakCount + 1
                else ->
                    0//それ以外は連勝数０を返す
            }
        //共有プリファレンスの編集モードを取得
        val editor = pref.edit()
        //editorのメソッドをメソッドチェーンで呼び出し
        editor.putInt("GAME_COUNT",gameCount+1)//勝負数
            .putInt("WINNING_STREAK_COUNT",edtWinningStreskCount)//連勝数
            .putInt("LAST_MY_HAND",myHand)//ユーザーの前の手
            .putInt("LAST_COM_HAND",comHand)//コンピュータの前の手
            .putInt("BEFORE_LAST_COM_HAND",lastComHand)//コンピュータの前々回の手
            .putInt("GAME_RESULT",gameResult)//勝敗
            .apply()//編集モードを確定して閉じる
    }

    private fun getHand() : Int {
        var hand = (Math.random() * 3).toInt()
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT",0)
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0)
        val lastMyHand = pref.getInt("LAST_MY_HAND",0)
        val lastComHand = pref.getInt("LAST_COM_HAND",0)
        val beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND",0)
        val gameResult = pref.getInt("GAME_RESULT",-1)

        if(gameCount == 1){
            if(gameResult == 2){
                //前回の勝負が一回目で、コンピュータが勝った場合、
                //コンピュータは次に出す手を変える
                while(lastComHand == hand){
                    hand = (Math.random() * 3).toInt()
                }
            } else if (gameResult == 1){
                //前回の勝負で１回目でコンピュータが負けた場合
                //相手が出した手に勝つ手を出す
                hand = (lastMyHand - 1 + 3) % 3
            }
        } else if(winningStreakCount > 0){
            if(beforeLastComHand == lastComHand){
                //同じ手で連勝いた場合は手を変える
                while (lastComHand == hand){
                    hand = (Math.random() * 3).toInt()
                }
            }
        }
        return hand
    }

}

