package xyz.salieri.english.type

import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.GroupMessageEvent
import javax.swing.GroupLayout
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.getMember
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.nextMessageOrNull
import xyz.salieri.mirai.plugin.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.mamoe.mirai.event.*
import xyz.salieri.mirai.plugin.EnglishUserData

val STATE_SLEEP = 0             // 等待设置
val STATE_READY = 1             // 等待启动
val STATE_RUNNING = 2           // 正在运行

val timesCeil = 30
val timesFloor = 5
val timesDefault = 10

val timeLimit: Long = 20_000
val timeDelay: Long = 5_000

class Comp(number: Long){
    var quesnum: Int = 0
    var quesindex: Int = 0
    var state: Int = STATE_SLEEP
    var groupnum: Long = number
    var book: String = "CET4"

    var msg: String = ""
    var score: MutableMap<Long, Int> = mutableMapOf()
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
        score = mutableMapOf()

        // 群号
        this.groupnum = groupnum
        val msgs = msginput.split(" ")
        // 检测是否有这本书
        val bookname = msgs[1].trim().uppercase()
        if( BookList.contains(bookname) ){
            // 存在这本书
            this.book = bookname
        } else {
            this.msg += "不存在${bookname}这本单词书，目前存在的单词书有：\n"
            BookList.forEach{
                this.msg += it
                this.msg += "  "
            }
            return;
        }
        // 次数
        var num = msgs[2].trim().toIntOrNull()
        if( num == null || num > timesCeil || num < timesFloor){
            this.msg += "不合法的题目数量（合法范围为[${timesFloor},${timesCeil}]），设置为默认数量${timesDefault}\n"
            num = timesDefault
        }
        this.quesindex = 1
        this.quesnum = num
        // 状态
        this.state = STATE_READY
        // 时间限制
        this.timelim = timeLimit
        // 输出信息
        this.msg += "群${this.groupnum}的英语竞赛设定完成！\n"
        this.msg += "单词书为：${this.book}\n"
        this.msg += "题目数为：${this.quesnum}\n"
        this.msg += "时间限制：${this.timelim / 1000}\n"
        this.msg += "请输入\"开始\"启动竞赛！"
    }


    @OptIn(ExperimentalStdlibApi::class)
    suspend fun run(){
        this.state = STATE_RUNNING
        val chan = GlobalEventChannel.filter{ it is GroupMessageEvent && it.group.id == this.groupnum } // 本群消息的信道
        while( this.quesindex <= quesnum ){
            // 产生随机一道题目，打印信息
            val obj: Word = randomword(this.book)
            this.msg = wordToQuestion(this.quesindex,this.quesnum,obj,this.timelim)
            this.sendMsg()
            // 建立【带超时的监听】，分别是5s（提示第一个字母），5s（提示前三个字母），10s
            var objEvent: GroupMessageEvent? = nextEventOrNull<GroupMessageEvent>(5_000){
                it.message.contentToString().trim().lowercase() == obj.word.trim().lowercase()
            }
            if(objEvent == null){
                // 第一个提示
                // 给出第一个字母
                msg += "5s内没人猜出来哦，给你们个小提示\n"
                msg += "这个单词的首字母是${obj.word[0]}"
                this.sendMsg()
                objEvent = nextEventOrNull<GroupMessageEvent>(5_000){
                    it.message.contentToString().trim().lowercase() == obj.word.trim().lowercase()
                }
                if(objEvent == null){
                    // 第二个提示，当单词长度大于3才给，给出第二个字母
                    if (obj.word.length > 3){
                        msg += "10s内没人猜出来啦，再给你们个提示\n"
                        msg += "这个单词的前三个字母是${obj.word[0]}${obj.word[1]}${obj.word[2]}"
                        this.sendMsg()
                    }
                    objEvent = nextEventOrNull<GroupMessageEvent>(10_000){
                        it.message.contentToString().trim().lowercase() == obj.word.trim().lowercase()
                    }
                }
            }

            if(objEvent == null){
                // 没有人回答出来
                msg += "时间到，很可惜没有人答对。\n"
            } else {
                // 有人回答出来了，加分
                var temp = score[objEvent.sender.id]
                if(temp == null)temp = 0
                score.put(objEvent.sender.id, temp + 1)

                msg += (at(objEvent.sender.id) + "\n")
                msg += "恭喜你回答正确，获得1分！您目前的分数为${score[objEvent.sender.id]}\n"
            }

            // 录入正确答案
            msg += "正确答案：${obj.word}\n"
            obj.trans.forEach{
                msg += "[${it.pos}] ${it.tran}\n"
            }

            if( quesindex != quesnum )msg += "3s后继续，输入\"gkd\"立即开始下一题哦"
            else msg += "3s后公布结果，输入\"gkd\"立即公布哦"

            this.quesindex ++

            this.sendMsg()

            // 等待5s
            objEvent = nextEventOrNull<GroupMessageEvent>(3_000){
                it.message.contentToString().trim().lowercase() == "gkd"
            }


        }
        // 结束，打印玩家列表
        msg += "游戏结束！本局游戏得分如下：\n"
        (score.entries.sortedByDescending { it.value }.associateBy ( {it.key}, {it.value} ) ).forEach{
            msg += "${it.value}   ${this.at(it.key)}\n"
            // EnglishUserData.coin[it.key] += it.value
        }
        this.sendMsg()
        this.state = STATE_SLEEP

    }

    suspend fun intro(){
        msg += "背单词插件，作者：salieri\n"
        msg += "项目地址：https://github.com/DRSalieri/miraitest\n"
        msg += "===========================\n"
        msg += "请输入\"背单词 <book> <times>\"\n进行设定\n"
        msg += "目前支持的单词书有：\n"
        BookList.forEach{
            this.msg += it
            this.msg += "  "
        }
        msg += "\n"
        msg += "次数限制为[${timesFloor},${timesCeil}]，默认为${timesDefault}次"
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