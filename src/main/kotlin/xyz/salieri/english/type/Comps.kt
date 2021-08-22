package xyz.salieri.english.type

object Comps{
    val comps: MutableList<Comp> = mutableListOf()              // 组织所有群的comp
    fun getComp(groupnum: Long): Int{                           // 判断群groupnum是否有comp
        return this.comps.indexOfFirst{it.groupnum == groupnum}
    }

    fun getOrCreatComp(groupnum: Long): Comp{
        val compIndex = getComp(groupnum)
        if(compIndex == -1){
            //create a comp
            val comp = Comp(groupnum)
            this.comps += comp
            return comp
        } else {
            return this.comps[compIndex]
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    suspend fun mainlogic(groupnum: Long, sender: Long, msginput: String){
        var msg = msginput.trim().uppercase()
        var comp = this.getOrCreatComp(groupnum)
        if( sender == 80000000L ){
            return;
        }

        if(msg.startsWith("背单词")){
            // 背单词 <book> <times>
            if(comp.state == STATE_RUNNING)
                return;
            else if(msg.split(" ").size == 1){
                comp.intro()
            }
            else {
                comp.set(msg, groupnum)
                comp.sendMsg()
            }
        } else if(msg == "开始"){
            if(comp.state == STATE_SLEEP){
                comp.msg += "还未完成设置，请通过\"背单词 <book> <times>\"命令完成设置"
                comp.sendMsg()
            } else if (comp.state == STATE_RUNNING) {
                return;
            } else {
                comp.run()
            }
        }


        // 输出信息


    }




}