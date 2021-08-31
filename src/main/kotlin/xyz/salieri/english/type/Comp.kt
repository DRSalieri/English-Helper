package xyz.salieri.english.type

import kotlinx.coroutines.delay
import net.mamoe.mirai.event.events.GroupMessageEvent
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.nextEventOrNull
import xyz.salieri.mirai.plugin.Util
import xyz.salieri.mirai.plugin.getBooks
import xyz.salieri.mirai.plugin.randomword
import xyz.salieri.mirai.plugin.wordToQuestion
import java.util.*

val STATE_SLEEP = 0             // 等待设置
val STATE_READY = 1             // 等待启动
val STATE_RUNNING = 2           // 正在运行

val timesCeil = 30
val timesFloor = 5
val timesDefault = 10

val timeLimit: Long = 20_000
val tipsLimit: Long = 10_000
var secondLimit: Long = timeLimit - tipsLimit
var tipsNum: Int = 3
val waitLimit: Long = 1_000

data class HintTask(
    val group: Long,
    val str: String,
): TimerTask() {
    override fun run() {
        runBlocking {
            Util.sendGroupMsg(group, str)
        }
    }
}

class Comp(number: Long){
    var quesnum: Int = 0
    var state: Int = STATE_SLEEP
    var groupnum: Long = number
    var book: String = "CET4"

    var msg: String = ""
    var score: MutableMap<Long, Int> = mutableMapOf<Long, Int>().withDefault { 0 }
    var timelim: Long = timeLimit

    fun at(num: Long): String{
        return "[mirai:at:$num]"
    }


    suspend fun sendMsg(){
        Util.sendGroupMsg(this.groupnum,msg)
        msg = ""
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun set(msginput: String, groupnum: Long) {
        // 初始化分数
        score = mutableMapOf<Long, Int>().withDefault { 0 }

        // 群号
        this.groupnum = groupnum
        val msgs = msginput.split(" ")
        val bookname = msgs[1].trim()

        try {
            randomword(bookname)
        } catch (e: java.io.FileNotFoundException) {
            println(e)
            this.msg += "不存在${bookname}这本单词书，目前存在的单词书有：\n" + getBooks()
            return
        }
        // 存在这本书
        this.book = bookname
        // 次数

        var num = msgs.elementAtOrNull(2)?.trim()?.toIntOrNull()
        if( num == null || num > timesCeil || num < timesFloor){
            this.msg += "不合法的题目数量（合法范围为[${timesFloor},${timesCeil}]），设置为默认数量${timesDefault}\n"
            num = timesDefault
        }
        this.quesnum = num
        // 状态
        this.state = STATE_READY
        // 时间限制
        this.timelim = timeLimit
        // 输出信息
        this.msg += """
        群${this.groupnum}的英语竞赛设定完成！
        单词书为：${this.book}
        题目数为：${this.quesnum}
        时间限制：${this.timelim / 1000}
        请输入"开始"启动竞赛！""".trimIndent()
    }


    @OptIn(ExperimentalStdlibApi::class)
    suspend fun run(){
        this.state = STATE_RUNNING
        var words = mutableSetOf<Word>()
        var hardWords = mutableSetOf<Word>()
        while (words.size < quesnum) {
            words.add(randomword(this.book))
        }

        suspend fun listenFor(timeoutMillis: Long, expects: String): GroupMessageEvent? {
            return nextEventOrNull<GroupMessageEvent>(timeoutMillis) {
                expects.split("/").any {expect ->
                    it.message.contentToString().trim().equals(expect, ignoreCase = true)       // 用"/"分割标准答案，只要答对一个就正确
                } && it.group.id == this.groupnum                                               // 过滤本群消息
            }
        }
        while (words.size > 0) {
            for ((index, obj) in words.withIndex()) {
                // 产生随机一道题目，打印信息
                this.msg = wordToQuestion(index + 1, words.size, obj, this.timelim)
                this.sendMsg()
                val t = Timer()
                val tipsNum = obj.word.split("/")[0].length / 2
                t.schedule(HintTask(groupnum,
                    "${tipsLimit / 1000}s内没人猜出来哦，给你们个小提示\n"+
                        if( tipsNum > 0 ){
                            // 满足条件，进行提示
                            "这个单词的前${tipsNum}个字母是${obj.word.substring(0,tipsNum)}"
                        } else {
                            // 否则给出首字母
                            "这个单词的首字母是${obj.word[0]}"
                        }
                ), tipsLimit)
                val objEvent: GroupMessageEvent? = listenFor(20_000L, obj.word)
                t.cancel()

                if(objEvent == null) {
                    // 没有人回答出来
                    msg += "时间到，很可惜没有人答对。\n"
                    hardWords.add(obj)
                } else {
                    // 有人回答出来了，加分
                    val answered = mutableSetOf<Long>(objEvent.sender.id)                               // 回答者列表
                    val first = objEvent.sender.id                                                      // 首先回答的人
                    val timeOutMills = waitLimit                                                        // 同时回答的时限
                    score[objEvent.sender.id] = 2 + score.getValue(objEvent.sender.id)                  // 首先回答者加2分

                    val listening = GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
                        if (group.id == groupnum) {
                            if (obj.word.split("/").any {expect ->                                // 用"/"分割标准答案，只要答对一个就正确
                                    it.message.contentToString().trim().equals(expect, ignoreCase = true)
                                }) {
                                if (!answered.contains(it.sender.id)) { // sender的id不被包括在里面
                                    answered.add(it.sender.id)
                                    score[it.sender.id] = 1 + score.getValue(it.sender.id)
                                }
                            }
                        }
                    }
                    delay(1000L)
                    listening.cancel()
                    msg += "${at(first)}首先回答正确，获得2分，当前积分${score[first]}。"
                    val others = (answered - first).joinToString("\n"){
                        "${at(it)}获得1分，当前积分${score[it]}分。"
                    }
                    if (others != ""){
                        msg += "\n其他在${timeOutMills}ms内回答正确的有：\n"
                        msg += others
                    }
                }

                // 录入正确答案
                msg += "正确答案：${obj}\n"
                if( index != words.size - 1)msg += "\n3s后继续，输入\"gkd\"立即开始下一题哦"
                else if(hardWords.size == 0) msg += "\n3s后公布结果，输入\"gkd\"立即公布哦"
                else msg += "\n这一轮中还有${hardWords.size}个单词没有答对哦，将在下一轮对这些单词进行复习"

                this.sendMsg()
                // 等待3s
                listenFor(3_000, "gkd")
            }
            words = hardWords
            hardWords = mutableSetOf()
        }

        // 结束，打印玩家列表
        msg += "游戏结束！本局游戏得分如下：\n" +
        score.entries.sortedByDescending { it.value }.joinToString("\n") { (playerId, score) ->
            "${score}   ${this.at(playerId)}"
            // EnglishUserData.coin[it.key] += it.value
        }
        this.sendMsg()
        this.state = STATE_SLEEP
    }

    suspend fun intro() {
        msg += """
        背单词插件，作者：salieri
        项目地址：https://github.com/DRSalieri/miraitest
        ===========================
        请输入"背单词 <book> [times]"
        进行设定
        目前支持的单词书有：
        ${getBooks()}。
        
        次数限制为[${timesFloor},${timesCeil}]，默认为${timesDefault}次
        """.trimIndent()
        this.sendMsg()
    }

    // 做一道题
    /*
    @OptIn(ExperimentalStdlibApi::class)
    fun question(obj: Word){
        var solved: Boolean = false
        var solveid: Long = 0
        // 做题
        val chan = GlobalEventChannel.filter { it is GroupMessageEvent && it.group.id == this.groupnum }
        chan.subscribeAlways<GroupMessageEvent> {
            whileSelectMessages {
                obj.word {
                    solved = true
                    solveid = sender.id
                    false
                }
                (obj.word.uppercase()){
                    solved = true
                    solveid = sender.id
                    false
                }
                (obj.word.lowercase()){
                    solved = true
                    solveid = sender.id
                    false
                }
                timeout(timelim){
                    solved = false
                    false
                }
            }
        }

        // 这题的总结
        if(solved == false){
            msg += "时间到！没有人答出来\n"
        } else {
            msg += this.at(solveid)
            var temp = score[solveid]
            if(temp == null)temp = 0
            score.put(solveid, temp + 1)
            msg += " 恭喜你回答正确！您目前的分数是${score[solveid]}分\n"
        }
        msg += (obj.word + "\n")
        obj.trans.forEach{
            msg += "[${it.pos}] ${it.tran}\n"
        }


        // this.sendMsg()



    }

     */

}
