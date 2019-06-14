package jp.ac.asojuku.st.noffication_de_study

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import jp.ac.asojuku.st.noffication_de_study.db.AnswersOpenHelper
import jp.ac.asojuku.st.noffication_de_study.db.QuestionsOpenHelper
import jp.ac.asojuku.st.noffication_de_study.db.UserAnswersOpenHelper
import kotlinx.android.synthetic.main.activity_question.*
import java.lang.Exception

class QuestionActivity : AppCompatActivity() {

    var user_id: Int = 12345678 //テスト用
    var examData: ExamData = intent.getSerializableExtra("ExamData") as ExamData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
//        atFirst()
        if (examData.question_current == 999) {
            printResult()
        }

        //ボタンの設定
        AA_Answer_0.setOnClickListener { choiceAnswer(0) }
        AA_Answer_1.setOnClickListener { choiceAnswer(1) }
        AA_Answer_2.setOnClickListener { choiceAnswer(2) }
        AA_Answer_3.setOnClickListener { choiceAnswer(3) }

    }

//    //コンストラクタ
//    fun atFirst() {
//        //集計か出題かを判定？
//    }

    //次の問題設定
    fun choiceNextQuestion() {
        examData.question_current = examData.question_next
        try {
            //次の問題が存在するなら次の問題番号を入れる
            examData.question_list.get(examData.question_next + 1)
            examData.question_next++

        } catch (e: ArrayIndexOutOfBoundsException) {
            //次の問題が存在しない場合は次に999を設定する
            examData.question_next = 999
        }


    }

    //問題表示
    fun printQuestion() {
        val questions = SQLiteHelper(this)
        val db = questions.readableDatabase
        val QOH = QuestionsOpenHelper(db)
        var question_arr: ArrayList<String>? = QOH.find_question(examData.question_current)
        var question_str: String
        if (question_arr == null) {
            question_str = "問題文がありません"
        } else {
            question_str = question_arr[0]
        }

        textView4.setText(question_str)

    }

    //解答選択
    fun choiceAnswer(choice_number: Int) {
        //登録処理
        regAnswer(choice_number)
        //画面遷移
        val intent = Intent(this, AnswerActivity::class.java)
        startActivity(intent)
    }

    //スキップ
    fun skipQuestion() {
        //DBにスキップしたとして999を登録して次の問題画面に画面遷移
        regAnswer(999)
        val intent = Intent(this, QuestionActivity::class.java)
        startActivity(intent)
    }

    //解答登録
    fun regAnswer(choice_number: Int) {
        //自分の解答を登録
        examData.answered_list.add(choice_number)
        //正解をDBから取得
        val answers = SQLiteHelper(this)
        val db = answers.readableDatabase
        val AOH = AnswersOpenHelper(db)
        var answer = AOH.find_answers(examData.question_current)?.get(1) //正しい正解
        var isCorrected = false //正解だった場合にtrueにする
        if (choice_number == answer) {
            isCorrected = true
        }
        examData.isCorrect_list.add(isCorrected)//解いた問題が正解だったかどうかがBoolean型で入る


    }

//    なんか使わないっぽい？（クラス一覧参照）
//    //結果集計
//    fun collectResult() {
//
//    }

    //結果登録
    fun regResult() {
        val userAnswers = SQLiteHelper(this)
        val db = userAnswers.readableDatabase
// 解答の登録
//        val UA = UserAnswersOpenHelper(db)
//        var question_arr:ArrayList<String>? = UA.add_record()
    }

    //結果表示
    fun printResult() {
        //解いた問題数を取得
        var answerCount = examData.isCorrect_list.size
        //正解数を取得
        var correctedCount = examData.isCorrect_list.filter { it == true }.count()
        //正答率を計算
        var answerRate = correctedCount / answerCount * 100

        //ポップアップ用のビルダー
        val builder = AlertDialog.Builder(this)
        if (answerRate < 100) { //ミスがある場合、間違った問題を解くボタンを表示させる
            builder.setMessage("正答率:" + answerRate + "%")
                .setCancelable(false)//範囲外タップによるキャンセルを不可にする
                .setNeutralButton("間違った問題を解く") { dialog, which ->
                    //間違った問題のリストを用意して問題解答画面に遷移する処理
                    var tempQuestionList = examData.question_list
                    examData.question_list.clear() //問題リストの初期化
                    examData.answered_list.clear() //解答リストの初期化
                    for (i in 0..tempQuestionList.size) {
                        var isMistake = examData.isCorrect_list.get(i)
                        if (isMistake) {
                            examData.question_list.add(tempQuestionList.get(i)) //間違ったquestion_idを詰め込んでいく
                        }
                    }
                    val intent = Intent(this, AnswerActivity::class.java)
                    startActivity(intent)
                    finish()

                }
                .setPositiveButton("終了") { dialog, which ->
                    //タイトル画面に戻る処理
                    examData.question_list.clear() //問題リストの初期化
                    examData.answered_list.clear() //解答リストの初期化
                    val intent = Intent(this, TitleActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .show()
        } else {
            builder.setMessage("正答率:100%!!!")
                .setCancelable(false)//範囲外タップによるキャンセルを不可にする
                .setNeutralButton("おめでとう！！") { dialog, which ->
                    //タイトル画面に戻る処理
                    examData.question_list.clear() //問題リストの初期化
                    examData.answered_list.clear() //解答リストの初期化
                    val intent = Intent(this, TitleActivity::class.java)
                    startActivity(intent)
                    finish()
                }.show()
        }


    }

}

