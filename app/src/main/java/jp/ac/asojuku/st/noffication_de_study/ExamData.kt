package jp.ac.asojuku.st.noffication_de_study

import java.io.Serializable

class ExamData (id:Int, name:String, number:String):Serializable{
    ///出題id ?? よく分からん。なんでつけたんだっけか・・・
    val id = id
    //試験名 FEとか
    val name = name
    //試験番号　FE2019Sとか
    val number = number

    //問題番号リスト
    var question_list = ArrayList<Int>()
    //回答番号リスト　未回答:999
    var answered_list = ArrayList<Int>()

    //今の問題番号　AnswerActivityで解いた問題を識別するために使用。　QuestionActivityの起動時処理でquestion_nextの値を代入
    var question_current = 0

    //次の問題番号　QuestionActivityの起動時処理で変更
    var question_next = 0

    // 完成した問題リストを受け取り、それに対応する解答リストを999(未解答)で初期化する
    public fun set_list_data(list:ArrayList<Int>){
        this.question_list = list
        for(i in 1..list.size){
            answered_list.add(999)
        }
    }

}