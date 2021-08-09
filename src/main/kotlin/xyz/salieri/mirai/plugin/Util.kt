package xyz.salieri.mirai.plugin

object Util {
    var AC: Int = 0
    suspend fun sendGroupMsg(groupid: Long, msg: String) {
        CQ_sendGroupMsg(Util.AC, groupid, msg);
    }
}